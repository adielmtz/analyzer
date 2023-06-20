package org.automatas.engine;

/**
 * Represents all supported Ast types.
 */
public enum AstKind {
    /* Lists */
    AST_STATEMENT_LIST,

    /* Literals */
    AST_SCALAR,
    AST_IDENTIFIER,

    /* Arrays */
    AST_ARRAY,
    AST_ARRAY_ACCESS,

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
    AST_BOOL_NOT,

    /* Arithmetic operators */
    AST_ADD,
    AST_SUBTRACT,
    AST_MULTIPLY,
    AST_POW,
    AST_DIVIDE,
    AST_MODULO,

    /* Increment/Decrement */
    AST_POST_INC,
    AST_POST_DEC,
    AST_PRE_INC,
    AST_PRE_DEC,

    /* Keywords */
    AST_LEN,
    AST_TYPEOF,
    AST_AS,
    AST_IS,
    AST_PRINT,
    AST_PRINTLN,
    AST_INPUT,
    AST_UNSET,
    AST_IF,
    AST_IF_ELSE,
    AST_FOR,
    AST_FOREACH,
    AST_DO_WHILE,
    AST_WHILE,
}
