package org.automatas.program;

import org.automatas.Ast;
import org.automatas.AstKind;
import org.automatas.Scalar;

import java.util.ArrayList;
import java.util.HashMap;

public class Executor {
    private final ArrayList<Ast> astList;
    private final HashMap<String, Scalar> identifiers;

    public Executor(ArrayList<Ast> astList) {
        this.astList = astList;
        this.identifiers = new HashMap<>();
    }

    public void execute() {
        for (Ast ast : astList) {
            execute(ast, new OpResult());
        }
    }

    private void fatalError(String fmt, Object... args) {
        String message = String.format(fmt, args);
        throw new RuntimeException("Fatal Error: " + message);
    }

    private void execute(Ast ast, OpResult result) {
        switch (ast.kind) {
            case AST_STATEMENT_LIST:
                executeStatementList(ast, result);
                break;
            case AST_IDENTIFIER:
            case AST_STRING:
            case AST_INTEGER:
            case AST_FLOAT:
            case AST_BOOL:
                executeLiteral(ast, result);
                break;
            case AST_DECLARATION:
                executeVarDeclaration(ast, result);
                break;
            case AST_ASSIGN:
                executeVarAssign(ast, result);
                break;
            case AST_AND:
                executeAndOperator(ast, result);
                break;
            case AST_OR:
                executeOrOperator(ast, result);
                break;
            case AST_EQUALS:
            case AST_NOT_EQUALS:
            case AST_SMALLER:
            case AST_SMALLER_OR_EQUAL:
            case AST_GREATER:
            case AST_GREATER_OR_EQUAL:
                executeValueComparison(ast, result);
                break;
            case AST_ADD:
            case AST_MINUS:
            case AST_MULTIPLY:
            case AST_POW:
            case AST_DIVIDE:
            case AST_MODULUS:
                executeValueArithmetic(ast, result);
                break;
            case AST_PRE_DEC:
            case AST_PRE_INC:
                executePreDecInc(ast, result);
                break;
            case AST_POST_DEC:
            case AST_POST_INC:
                executePostDecInc(ast, result);
                break;
            case AST_PRINT:
                executePrint(ast, result);
                break;
            case AST_UNSET:
                executeVarUnset(ast, result);
                break;
            case AST_IF:
                executeIfStatement(ast, result);
                break;
            case AST_IF_ELSE:
                executeIfElseStatement(ast, result);
                break;
            case AST_FOR:
                executeForLoop(ast, result);
                break;
            case AST_WHILE:
                executeWhileLoop(ast, result);
                break;
        }
    }

    private void executeStatementList(Ast ast, OpResult result) {
        assert ast.kind == AstKind.AST_STATEMENT_LIST;

        for (Ast child : ast.child) {
            execute(child, new OpResult());
        }

        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeLiteral(Ast ast, OpResult result) {
        assert ast.child.length == 0;

        if (ast.kind == AstKind.AST_IDENTIFIER) {
            executeVarFetch(ast, result);
        } else {
            result.setType(OpResult.ResultType.CONSTANT);
            result.setValue(ast.value);
        }
    }

    private void executeVarFetch(Ast ast, OpResult result) {
        assert ast.child.length == 0;
        assert ast.kind == AstKind.AST_IDENTIFIER;

        String id = ast.value.asString();
        if (!identifiers.containsKey(id)) {
            fatalError("Unkown identifier '%s'.", id);
        }

        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(identifiers.get(id));
    }

    private void executeVarDeclaration(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Expression '%s' cannot be declared as variable name.", var.kind);
        }

        String id = var.value.asString();
        if (identifiers.containsKey(id)) {
            fatalError("Variable '%s' is already defined", id);
        }

        var op = new OpResult();
        execute(expr, op);

        if (op.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Expression '%s' cannot be assigned to a variable.", expr.kind);
        }

        // Everything ok!
        identifiers.put(id, op.getValue());
        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeVarAssign(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Expression '%s' is not a valid variable name.", var.kind);
        }

        String id = var.value.asString();
        if (!identifiers.containsKey(id)) {
            fatalError("Variable '%s' is not defined.", id);
        }

        var op = new OpResult();
        execute(expr, op);

        if (op.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Expression '%s' cannot be assigned to a variable.", expr.kind);
        }

        identifiers.put(id, op.getValue());
        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeAndOperator(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var op1 = new OpResult();
        execute(lhs, op1);

        if (op1.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", lhs.kind);
        }

        if (!op1.getValue().asBool()) {
            // false && ??
            // expression is evaluates to false
            result.setType(OpResult.ResultType.CONSTANT);
            result.setValue(Scalar.fromBool(false));
            return;
        }

        var op2 = new OpResult();
        execute(rhs, op2);

        if (op2.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", rhs.kind);
        }

        // true && ??
        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(Scalar.fromBool(op2.getValue().asBool()));
    }

    private void executeOrOperator(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var op1 = new OpResult();
        execute(lhs, op1);

        if (op1.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", lhs.kind);
        }

        if (op1.getValue().asBool()) {
            // true || ??
            // expression is evaluates to true
            result.setType(OpResult.ResultType.CONSTANT);
            result.setValue(Scalar.fromBool(true));
            return;
        }

        var op2 = new OpResult();
        execute(rhs, op2);

        if (op2.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", rhs.kind);
        }

        // false || ??
        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(Scalar.fromBool(op2.getValue().asBool()));
    }

    private void executeValueComparison(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var op1 = new OpResult();
        var op2 = new OpResult();
        execute(lhs, op1);
        execute(rhs, op2);

        if (op1.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", lhs.kind);
        }

        if (op2.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", rhs.kind);
        }

        Scalar val1 = op1.getValue();
        Scalar val2 = op2.getValue();

        boolean ret = switch (ast.kind) {
            case AST_EQUALS -> val1.equals(val2);
            case AST_NOT_EQUALS -> !val1.equals(val2);
            case AST_SMALLER -> val1.compareTo(val2) < 0;
            case AST_SMALLER_OR_EQUAL -> val1.compareTo(val2) <= 0;
            case AST_GREATER -> val1.compareTo(val2) > 0;
            case AST_GREATER_OR_EQUAL -> val1.compareTo(val2) >= 0;
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(Scalar.fromBool(ret));
    }

    private Scalar scalarAdd(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            // concatenate
            return Scalar.fromString(a.asString() + b.asString());
        }

        return switch (a.getType()) {
            case IS_FLOAT -> Scalar.fromFloat(a.asFloat() + b.asFloat());
            case IS_BOOL, IS_INTEGER -> Scalar.fromInteger(a.asInteger() + b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private Scalar scalarSubtract(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            fatalError("Incompatible type string cannot be used as operand.");
        }

        return switch (a.getType()) {
            case IS_FLOAT -> Scalar.fromFloat(a.asFloat() - b.asFloat());
            case IS_BOOL, IS_INTEGER -> Scalar.fromInteger(a.asInteger() - b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private Scalar scalarMultiply(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            fatalError("Incompatible type string cannot be used as operand.");
        }

        return switch (a.getType()) {
            case IS_FLOAT -> Scalar.fromFloat(a.asFloat() * b.asFloat());
            case IS_BOOL, IS_INTEGER -> Scalar.fromInteger(a.asInteger() * b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private Scalar scalarPow(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            fatalError("Incompatible type string cannot be used as operand.");
        }

        double result = Math.pow(a.asFloat(), b.asFloat());
        if (a.isFloat() || b.isFloat()) {
            return Scalar.fromFloat(result);
        }

        // Bool or int
        return Scalar.fromInteger((long) result);
    }

    private Scalar scalarDivide(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            fatalError("Incompatible type string cannot be used as operand");
        }

        return switch (a.getType()) {
            case IS_FLOAT -> Scalar.fromFloat(a.asFloat() / b.asFloat());
            case IS_BOOL, IS_INTEGER -> Scalar.fromInteger(a.asInteger() / b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private Scalar scalarModulus(Scalar a, Scalar b) {
        if (a.isString() || b.isString()) {
            fatalError("Incompatible type string cannot be used as operand.");
        }

        return switch (a.getType()) {
            case IS_FLOAT -> Scalar.fromFloat(a.asFloat() % b.asFloat());
            case IS_BOOL, IS_INTEGER -> Scalar.fromInteger(a.asInteger() % b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private void executeValueArithmetic(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var op1 = new OpResult();
        var op2 = new OpResult();
        execute(lhs, op1);
        execute(rhs, op2);

        if (op1.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", lhs.kind);
        }

        if (op2.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Unexpected operand '%s'.", rhs.kind);
        }

        Scalar val1 = op1.getValue();
        Scalar val2 = op2.getValue();

        Scalar ret = switch (ast.kind) {
            case AST_ADD -> scalarAdd(val1, val2);
            case AST_MINUS -> scalarSubtract(val1, val2);
            case AST_MULTIPLY -> scalarMultiply(val1, val2);
            case AST_POW -> scalarPow(val1, val2);
            case AST_DIVIDE -> scalarDivide(val1, val2);
            case AST_MODULUS -> scalarModulus(val1, val2);
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(ret);
    }

    private void executePreDecInc(Ast ast, OpResult result) {
        assert ast.child.length == 1;

        Ast var = ast.child[0];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Cannot increment/decrement Expression '%s'.", var.kind);
        }

        var op = new OpResult();
        executeVarFetch(var, op);

        Scalar val = op.getValue();
        if (!val.isFloat() && !val.isInteger()) {
            fatalError("Value of type '%' cannot be incremented/decremented.", val.getType());
        }

        Scalar ret = switch (ast.kind) {
            case AST_PRE_DEC -> scalarSubtract(val, Scalar.fromInteger(1));
            case AST_PRE_INC -> scalarAdd(val, Scalar.fromInteger(1));
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        identifiers.put(var.value.asString(), ret);
        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(ret); // return new value
    }

    private void executePostDecInc(Ast ast, OpResult result) {
        assert ast.child.length == 1;

        Ast var = ast.child[0];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Cannot increment/decrement Expression '%s'.", var.kind);
        }

        var op = new OpResult();
        executeVarFetch(var, op);

        Scalar val = op.getValue();
        if (!val.isFloat() && !val.isInteger()) {
            fatalError("Value of type '%' cannot be incremented/decremented.", val.getType());
        }

        Scalar ret = switch (ast.kind) {
            case AST_POST_DEC -> scalarSubtract(val, Scalar.fromInteger(1));
            case AST_POST_INC -> scalarAdd(val, Scalar.fromInteger(1));
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        identifiers.put(var.value.asString(), ret);
        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(val); // return old value
    }

    private void executePrint(Ast ast, OpResult result) {
        assert ast.child.length == 1;

        Ast expr = ast.child[0];

        var op = new OpResult();
        execute(expr, op);

        if (op.getType() != OpResult.ResultType.CONSTANT) {
            fatalError("Expression '%s' cannot be printed.", expr.kind);
        }

        Scalar value = op.getValue();
        System.out.print(value.toString());

        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeVarUnset(Ast ast, OpResult result) {
        assert ast.child.length == 1;

        Ast var = ast.child[0];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Expression '%s' cannot be unset.", var.kind);
        }

        String id = var.value.asString();
        boolean ret = identifiers.remove(id) != null;

        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(Scalar.fromBool(ret));
    }

    private void executeIfStatement(Ast ast, OpResult result) {
        assert ast.child.length == 2;
        assert ast.kind == AstKind.AST_IF;

        Ast expr = ast.child[0];
        Ast stmt = ast.child[1];

        var exprOp = new OpResult();
        execute(expr, exprOp);

        boolean executed = false;

        if (exprOp.getValue().asBool()) {
            var stmtOp = new OpResult();
            execute(stmt, stmtOp);
            executed = true;
        }

        result.setType(OpResult.ResultType.CONSTANT);
        result.setValue(Scalar.fromBool(executed));
    }

    private void executeIfElseStatement(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast ifstmt = ast.child[0];
        Ast elstmt = ast.child[1];

        var ifOp = new OpResult();
        execute(ifstmt, ifOp);
        assert ifOp.getType() == OpResult.ResultType.CONSTANT;

        if (!ifOp.getValue().asBool()) {
            var elseOp = new OpResult();
            execute(elstmt, elseOp);
        }

        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeForLoop(Ast ast, OpResult result) {
        assert ast.child.length == 4;

        Ast decl = ast.child[0];
        Ast cond = ast.child[1];
        Ast step = ast.child[2];
        Ast stmt = ast.child[3];

        var declOp = new OpResult();
        var condOp = new OpResult();
        var stepOp = new OpResult();
        var stmtOp = new OpResult();

        execute(decl, declOp);
        execute(cond, condOp);

        while (condOp.getValue().asBool()) {
            execute(stmt, stmtOp);
            execute(step, stepOp);
            execute(cond, condOp);
        }

        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }

    private void executeWhileLoop(Ast ast, OpResult result) {
        assert ast.child.length == 2;

        Ast expr = ast.child[0];
        Ast stmt = ast.child[1];

        var exprOp = new OpResult();
        var stmtOp = new OpResult();
        execute(expr, exprOp);

        while (exprOp.getValue().asBool()) {
            execute(stmt, stmtOp);
            execute(expr, exprOp);
        }

        result.setType(OpResult.ResultType.NONE);
        result.setValue(null);
    }
}
