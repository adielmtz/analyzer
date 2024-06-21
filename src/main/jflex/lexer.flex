package org.automatas.language;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

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
    private Stack<Character> nesting;

    public Lexer(Reader reader, ComplexSymbolFactory factory) {
        this(reader);
        this.factory = factory;
        this.string = new StringBuilder();
        this.nesting = new Stack<>();
    }

    private Symbol symbol(String name, int type, Object value) {
        return factory.newSymbol(
            name, type,
            new Location(yyline + 1, yycolumn + 1),
            new Location(yyline + 1, yycolumn + yylength()),
            value
        );
    }

    private Symbol symbol(String name, int type) {
        return factory.newSymbol(
            name, type,
            new Location(yyline + 1, yycolumn + 1),
            new Location(yyline + 1, yycolumn + yylength())
        );
    }

    private Symbol symbol(int type) {
        return symbol(Token.terminalNames[type], type);
    }

    private Symbol bool(boolean value) {
        return symbol(Boolean.toString(value), Token.BOOL, value);
    }

    private Symbol integer(String text, int base) {
        if (base != 10) {
            // Strip '0x', '0b' or '0o' prefixes
            text = text.substring(2);
        }

        long value = Long.parseLong(text, base);
        return symbol("integer", Token.INTEGER, value);
    }

    private Symbol decimal(String text) {
        double value = Double.parseDouble(text);
        return symbol("float", Token.FLOAT, value);
    }

    private Symbol label(String value) {
        return symbol("label", Token.LABEL, value);
    }

    private Symbol string(String value) {
        return symbol("string", Token.STRING, value);
    }

    private Symbol beginNesting(String text) {
        assert text.length() == 1;
        char c = text.charAt(0);
        nesting.push(c);

        return switch (c) {
            case '(' -> symbol(Token.LPAREN);
            case '[' -> symbol(Token.LBRACKET);
            case '{' -> symbol(Token.LBRACE);
            default -> throw new IllegalStateException("Unexpected char value");
        };
    }

    private Symbol endNesting(String text) {
        assert text.length() == 1;
        char c = text.charAt(0);

        if (nesting.isEmpty()) {
            throw new RuntimeException("Unmatched '%c'".formatted(c));
        }

        char o = nesting.pop();
        if ((c == ')' && o != '(') || (c == ']' && o != '[') || (c == '}' && o != '{')) {
            throw new RuntimeException("'%c' does not match '%c'".formatted(c, o));
        }

        return switch (c) {
            case ')' -> symbol(Token.RPAREN);
            case ']' -> symbol(Token.RBRACKET);
            case '}' -> symbol(Token.RBRACE);
            default -> throw new IllegalStateException("Unexpected char value");
        };
    }
%}

%eofval{
    return symbol(Token.EOF);
%eofval}

/* Regular Expressions */
NewLine        = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {NewLine}|[ \t\f]
Label          = [:jletter:][:jletterdigit:]*
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

/* Keywords */
<YYINITIAL> "true"           { return bool(true); }
<YYINITIAL> "false"          { return bool(false); }
<YYINITIAL> "as"             { return symbol("as", Token.AS); }
<YYINITIAL> "is"             { return symbol("is", Token.IS); }
<YYINITIAL> "in"             { return symbol("in", Token.IN); }
<YYINITIAL> "len"            { return symbol("len", Token.LEN); }
<YYINITIAL> "typeof"         { return symbol("typeof", Token.TYPEOF); }
<YYINITIAL> "unset"          { return symbol("unset", Token.UNSET); }
<YYINITIAL> "if"             { return symbol("if", Token.IF); }
<YYINITIAL> "else"           { return symbol("else", Token.ELSE); }
<YYINITIAL> "for"            { return symbol("for", Token.FOR); }
<YYINITIAL> "foreach"        { return symbol("foreach", Token.FOREACH); }
<YYINITIAL> "do"             { return symbol("do", Token.DO); }
<YYINITIAL> "while"          { return symbol("while", Token.WHILE); }
<YYINITIAL> "struct"         { return symbol("struct", Token.STRUCT); }
<YYINITIAL> "pub"            { return symbol("pub", Token.PUB); }
<YYINITIAL> "new"            { return symbol("new", Token.NEW); }
<YYINITIAL> "fn"             { return symbol("fn", Token.FN); }
<YYINITIAL> "return"         { return symbol("return", Token.RETURN); }
<YYINITIAL> {Label}          { return label(yytext()); }

/* Symbols */
<YYINITIAL> "("|"["|"{"      { return beginNesting(yytext()); }
<YYINITIAL> ")"|"]"|"}"      { return endNesting(yytext()); }
<YYINITIAL> "!"              { return symbol(Token.EXCLAMATION); }
<YYINITIAL> ","              { return symbol(Token.COMMA); }
<YYINITIAL> "."              { return symbol(Token.DOT); }
<YYINITIAL> ";"              { return symbol(Token.SEMICOLON); }

/* Logic Operators */
<YYINITIAL> ":="             { return symbol(":=", Token.DECLARATION); }
<YYINITIAL> "="              { return symbol("=", Token.ASSIGN); }
<YYINITIAL> "&&"             { return symbol("&&", Token.AND); }
<YYINITIAL> "||"             { return symbol("||", Token.OR); }
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
<YYINITIAL> "%"              { return symbol("%", Token.MODULO); }

/* Ignore comments & whitespace */
<YYINITIAL> {WhiteSpace}
           |{Comment}        { /* ignorar */ }

/* Literals */
<YYINITIAL> {IntegerLiteral} { return integer(yytext(), 10); }
<YYINITIAL> {DecimalLiteral}
           |{SciNotLiteral}  { return decimal(yytext()); }
<YYINITIAL> {HexNumLiteral}  { return integer(yytext(), 16); }
<YYINITIAL> {OctNumLiteral}  { return integer(yytext(), 8); }
<YYINITIAL> {BinNumLiteral}  { return integer(yytext(), 2); }
<YYINITIAL> \"               { yybegin(ST_IN_STRING); string.setLength(0); }


<ST_IN_STRING> \"            { yybegin(YYINITIAL); return string(string.toString()); }
<ST_IN_STRING> [^\n\r\"\\]+  { string.append(yytext()); }
<ST_IN_STRING> \\t           { string.append('\t'); }
<ST_IN_STRING> \\n           { string.append('\n'); }
<ST_IN_STRING> \\r           { string.append('\r'); }
<ST_IN_STRING> \\\"          { string.append('\"'); }
<ST_IN_STRING> \\            { string.append('\\'); }

/* Error fallback */
[^]                          { String message = String.format("Illegal input '%s' in line %d", yytext(), yyline);
                               throw new Error(message); }
