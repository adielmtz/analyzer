package org.automatas;

public enum AstKind {
    /* Parser Special Ast */
    AST_STATEMENT_LIST,

    /* Literals */
    AST_IDENTIFIER,
    AST_STRING,
    AST_INTEGER,
    AST_FLOAT,
    AST_BOOL,

    /* Logic operators */
    AST_DECLARATION,
    AST_ASSIGN,
    AST_AND,
    AST_OR,
    AST_EQUALS,
    AST_NOT_EQUALS,
    AST_SMALLER,
    AST_SMALLER_OR_EQUAL,
    AST_GREATER,
    AST_GREATER_OR_EQUAL,

    /* Arithmetic operators */
    AST_ADD,
    AST_MINUS,
    AST_MULTIPLY,
    AST_POW,
    AST_DIVIDE,
    AST_MODULUS,

    /* Increment/Decrement */
    AST_POST_INC,
    AST_POST_DEC,
    AST_PRE_INC,
    AST_PRE_DEC,

    /* Keywords */
    AST_PRINT,
    AST_UNSET,
    AST_IF,
    AST_IF_ELSE,
    AST_FOR,
    AST_WHILE,
}
