package org.automatas.language;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import org.automatas.engine.Scalar;

import java.io.Reader;
import java.util.Stack;

%%

%public
%class Lexer
%unicode
%cupsym Token
%cup
%char
%line
%column

%{
    private ComplexSymbolFactory factory;
    private StringBuilder string;
    private Stack<Character> braces;

    public Lexer(Reader reader, ComplexSymbolFactory factory) {
        this(reader);
        this.factory = factory;
        this.string = new StringBuilder();
        this.braces = new Stack<>();
    }

    private Symbol symbol(int type) {
        return symbol(Token.terminalNames[type], type);
    }

    private Symbol symbol(String name, int type) {
        return factory.newSymbol(
            name,
            type,
            new Location(yyline + 1, yycolumn + 1),
            new Location(yyline + 1, yycolumn + yylength())
        );
    }

    private Symbol symbol(int type, Object value) {
        return symbol(Token.terminalNames[type], type, value);
    }

    private Symbol symbol(String name, int type, Object value) {
        return factory.newSymbol(
            Token.terminalNames[type],
            type,
            new Location(yyline + 1, yycolumn + 1),
            new Location(yyline + 1, yycolumn + yylength()),
            value
        );
    }

    private Symbol bool(boolean value) {
        return symbol(value ? "true" : "false", Token.BOOL, Scalar.fromBoolean(value));
    }

    private Symbol integer(String text, int base) {
        if (base != 10) {
            // remove "0x", "0b", "0o"
            text = text.substring(2);
        }

        long number = Long.parseLong(text, base);
        Scalar result = Scalar.fromInteger(number);
        return symbol("integer", Token.INTEGER, result);
    }

    private Symbol decimal(String text) {
        double number = Double.parseDouble(text);
        Scalar result = Scalar.fromFloat(number);
        return symbol("float", Token.FLOAT, result);
    }

    private Symbol text(int kind, String value) {
        Scalar result = Scalar.fromString(value);
        return symbol(kind, result);
    }

    private Symbol beginBracket(String text) {
        assert text.length() == 1;
        char c = text.charAt(0);

        Symbol sym = null;
        braces.push(c);

        switch (c) {
            case '(':
                sym = symbol("(", Token.LPAREN);
                break;
            case '[':
                sym = symbol("[", Token.LBRACKET);
                break;
            case '{':
                sym = symbol("{", Token.LBRACE);
                break;
            default:
                assert false;
        }

        return sym;
    }

    private boolean isExpectedBracket(char bracket) {
        if (braces.size() > 0) {
            char peek = braces.pop();
            return peek == '(' && bracket == ')'
                || peek == '[' && bracket == ']'
                || peek == '{' && bracket == '}';
        }

        return false;
    }

    private Symbol endBracket(String text) {
        assert text.length() == 1;
        char c = text.charAt(0);

        if (!isExpectedBracket(c)) {
            String msg = String.format("Unmatched '%c' in line %d, column %d", c, yyline + 1, yycolumn + 1);
            throw new Error(msg);
        }

        Symbol sym = null;

        switch (c) {
            case ')':
                sym = symbol(")", Token.RPAREN);
                break;
            case ']':
                sym = symbol("]", Token.RBRACKET);
                break;
            case '}':
                sym = symbol("}", Token.RBRACE);
                break;
            default:
                assert false;
        }

        return sym;
    }
%}

%eofval{
    return symbol(Token.EOF);
%eofval}

/* Regular Expressions */
NewLine        = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {NewLine}|[ \t\f]
Identifier     = [:jletter:][:jletterdigit:]*
IntegerLiteral = 0|[1-9][0-9]*
DecimalLiteral = ({IntegerLiteral}?"."{IntegerLiteral})|({IntegerLiteral}"."{IntegerLiteral}?)
SciNotLiteral  = (({IntegerLiteral}|{DecimalLiteral})[eE][+-]?{IntegerLiteral})
HexNumLiteral  = "0x"[0-9a-fA-F]+
OctNumLiteral  = "0o"[0-7]+
BinNumLiteral  = "0b"[01]+

/* Comments */
Comment              = {TraditionalComment}|{EndOfLineComment}|{DocumentationComment}
TraditionalComment   = "/*"[^*]~"*/"|"/*""*"+"/"
EndOfLineComment     = "//" {InputCharacter}*{NewLine}?
DocumentationComment = "/**"{CommentContent}"*"+"/"
CommentContent       = ([^*]|\*+[^/*])*

/* Special state when lexing "quoted strings" */
%state ST_IN_STRING

%%

<YYINITIAL> "true"           { return bool(true); }
<YYINITIAL> "false"          { return bool(false); }
<YYINITIAL> "len"            { return symbol("len", Token.LEN); }
<YYINITIAL> "typeof"         { return symbol("typeof", Token.TYPEOF); }
<YYINITIAL> "print"          { return symbol("print", Token.PRINT); }
<YYINITIAL> "println"        { return symbol("println", Token.PRINTLN); }
<YYINITIAL> "readline"       { return symbol("readline", Token.READLN); }
<YYINITIAL> "unset"          { return symbol("unset", Token.UNSET); }
<YYINITIAL> "if"             { return symbol("if", Token.IF); }
<YYINITIAL> "else"           { return symbol("else", Token.ELSE); }
<YYINITIAL> "for"            { return symbol("for", Token.FOR); }
<YYINITIAL> "do"             { return symbol("do", Token.DO); }
<YYINITIAL> "while"          { return symbol("while", Token.WHILE); }
<YYINITIAL> {Identifier}     { return text(Token.IDENTIFIER, yytext()); }

/* Literals */
<YYINITIAL> {IntegerLiteral} { return integer(yytext(), 10); }
<YYINITIAL> {DecimalLiteral}
           |{SciNotLiteral}  { return decimal(yytext()); }
<YYINITIAL> {HexNumLiteral}  { return integer(yytext(), 16); }
<YYINITIAL> {OctNumLiteral}  { return integer(yytext(), 8); }
<YYINITIAL> {BinNumLiteral}  { return integer(yytext(), 2); }
<YYINITIAL> \"               { string.setLength(0); yybegin(ST_IN_STRING); }

/* Symbols */
<YYINITIAL> ("("|"["|"{")    { return beginBracket(yytext()); }
<YYINITIAL> (")"|"]"|"}")    { return endBracket(yytext()); }
<YYINITIAL> ","              { return symbol(Token.COMMA); }
<YYINITIAL> ";"              { return symbol(Token.SEMICOLON); }

/* Logic Operators */
<YYINITIAL> ":="             { return symbol(":=", Token.DECLARATION); }
<YYINITIAL> "="              { return symbol("=", Token.ASSIGN); }
<YYINITIAL> "&&"             { return symbol("AND", Token.AND); }
<YYINITIAL> "||"             { return symbol("OR", Token.OR); }
<YYINITIAL> "=="             { return symbol("==", Token.EQUALS); }
<YYINITIAL> "!="             { return symbol("!=", Token.NOT_EQUALS); }
<YYINITIAL> "<"              { return symbol("<", Token.SMALLER); }
<YYINITIAL> "<="             { return symbol("<=", Token.SMALLER_OR_EQUAL); }
<YYINITIAL> ">"              { return symbol(">", Token.GREATER); }
<YYINITIAL> ">="             { return symbol(">=", Token.GREATER_OR_EQUAL); }

/* Arithmetic Operators */
<YYINITIAL> "+"              { return symbol("+", Token.PLUS); }
<YYINITIAL> "++"             { return symbol("++", Token.INCREMENT); }
<YYINITIAL> "-"              { return symbol("-", Token.MINUS); }
<YYINITIAL> "--"             { return symbol("--", Token.DECREMENT); }
<YYINITIAL> "*"              { return symbol("*", Token.MULTIPLY); }
<YYINITIAL> "**"             { return symbol("**", Token.POW); }
<YYINITIAL> "/"              { return symbol("/", Token.DIVIDE); }
<YYINITIAL> "%"              { return symbol("%", Token.MODULUS); }

/* Ignore comments & whitespace */
<YYINITIAL> {WhiteSpace}
           |{Comment}        { /* ignorar */ }


<ST_IN_STRING> \"            { yybegin(YYINITIAL);
                               return text(Token.STRING, string.toString()); }
<ST_IN_STRING> [^\n\r\"\\]+  { string.append(yytext()); }
<ST_IN_STRING> \\t           { string.append('\t'); }
<ST_IN_STRING> \\n           { string.append('\n'); }
<ST_IN_STRING> \\r           { string.append('\r'); }
<ST_IN_STRING> \\\"          { string.append('\"'); }
<ST_IN_STRING> \\            { string.append('\\'); }

/* Error fallback */
[^]                          { String message = String.format("Illegal input '%s' in line %d", yytext(), yyline);
                               throw new Error(message); }
