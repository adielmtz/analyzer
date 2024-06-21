package org.automatas.engine;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.automatas.language.Lexer;
import org.automatas.language.Parser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Executor {
    private final ScopeManager scope = new ScopeManager();
    private HashMap<String, Ast> structs;
    private HashMap<String, UserFunction> userFunctions;
    private final HashMap<String, FunctionHandler> handlers;

    public Executor() {
        handlers = new HashMap<>();
        BuiltInFunctions.loadBuiltIns(handlers);
    }

    public void executeFile(String filename) {
        try (var reader = new FileReader(filename)) {
            var factory = new ComplexSymbolFactory();
            var lexer = new Lexer(reader, factory);
            var parser = new Parser(lexer, factory);

            Symbol result = parser.parse();
            Ast root = (Ast) result.value;
            structs = parser.getDeclaredStructs();
            userFunctions = parser.getDeclaredFunctions();
            execute(root, new Node());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fatalError(String fmt, Object... args) {
        String message = "Fatal Error: " + fmt.formatted(args);
        throw new RuntimeException(message);
    }

    private void execute(Ast ast, Node result) {
        switch (ast.kind) {
            case AST_STATEMENT_LIST:
                executeStatementList(ast, result);
                break;
            case AST_SCALAR:
                executeScalar(ast, result);
                break;
            case AST_ARRAY:
                executeArray(ast, result);
                break;
            case AST_DECLARATION:
                executeVarDeclaration(ast, result);
                break;
            case AST_ASSIGN:
                executeVarAssign(ast, result);
                break;
            case AST_IDENTIFIER:
                executeVarFetch(ast, result);
                break;
            case AST_ARRAY_ACCESS:
                executeArrayAccess(ast, result);
                break;
            case AST_STRUCT_ACCESS:
                executeStructAccess(ast, result);
                break;
            case AST_CALL:
                executeCall(ast, result);
                break;
            case AST_RETURN:
                executeReturn(ast, result);
                break;
            case AST_AND:
                executeLogicAnd(ast, result);
                break;
            case AST_OR:
                executeLogicOr(ast, result);
                break;
            case AST_EQUALS:
            case AST_NOT_EQUALS:
            case AST_SMALLER:
            case AST_SMALLER_OR_EQUAL:
            case AST_GREATER:
            case AST_GREATER_OR_EQUAL:
                executeScalarComparison(ast, result);
                break;
            case AST_BOOL_NOT:
                executeBoolNot(ast, result);
                break;
            case AST_ADD:
            case AST_SUBTRACT:
            case AST_MULTIPLY:
            case AST_POW:
            case AST_DIVIDE:
            case AST_MODULO:
                executeScalarOperations(ast, result);
                break;
            case AST_POST_INC:
            case AST_POST_DEC:
            case AST_PRE_INC:
            case AST_PRE_DEC:
                executeDecInc(ast, result);
                break;
            case AST_LEN:
                executeLen(ast, result);
                break;
            case AST_TYPEOF:
                executeTypeof(ast, result);
                break;
            case AST_AS:
                executeTypeCast(ast, result);
                break;
            case AST_IS:
                executeTypeCheck(ast, result);
                break;
            case AST_NEW:
                executeNewInstance(ast, result);
                break;
            case AST_UNSET:
                executeUnset(ast, result);
                break;
            case AST_IF:
                executeIfStatement(ast, result);
                break;
            case AST_IF_ELSE:
                executeIfElseStatement(ast, result);
                break;
            case AST_FOR:
                executeForStatement(ast, result);
                break;
            case AST_FOREACH:
                executeForeachStatement(ast, result);
                break;
            case AST_DO_WHILE:
                executeDoWhileStatement(ast, result);
                break;
            case AST_WHILE:
                executeWhileStatement(ast, result);
                break;
        }
    }

    private void executeStatementList(Ast ast, Node result) {
        assert ast.kind == AstKind.AST_STATEMENT_LIST;

        scope.beginBlock();

        for (Ast statement : ast.child) {
            var node = new Node();
            execute(statement, node);

            if (node.mustReturn()) {
                node.propagateTo(result);
                break;
            }
        }

        scope.endBlock();
    }

    private void executeScalar(Ast ast, Node result) {
        assert ast.value != null;
        assert ast.child.length == 0;

        Scalar value = Scalar.make(ast.value, ast.type);

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeArray(Ast ast, Node result) {
        assert ast.kind == AstKind.AST_ARRAY;

        List<Scalar> values = new ArrayList<>(ast.child.length);
        for (Ast child : ast.child) {
            var node = new Node();
            execute(child, node);
            values.add(node.getValue());
        }

        Scalar array = Scalar.makeArray(values);
        result.setType(NodeType.CONSTANT);
        result.setValue(array);
    }

    private void executeTypeof(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast expr = ast.child[0];

        var node = new Node();
        execute(expr, node);

        if (node.getType() != NodeType.CONSTANT) {
            fatalError("Cannot get type of non-scalar expression.");
        }

        Scalar typeName = Scalar.makeString(node.getValue().getType().toString());
        result.setType(NodeType.CONSTANT);
        result.setValue(typeName);
    }

    private void executeTypeCast(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast expr = ast.child[0];
        Ast type = ast.child[1];

        var node = new Node();
        execute(expr, node);

        if (node.getType() != NodeType.CONSTANT) {
            fatalError("Cannot type cast non-scalar expression.");
        }

        String typeName = type.value.toString();
        ScalarType target = ScalarType.getType(typeName);

        if (target == null) {
            fatalError("Cannot cast to unknown type '%s'.", type.value);
            return;
        }

        Scalar original = node.getValue();
        Scalar casted = switch (target) {
            case ARRAY -> Scalar.makeArray(original.toList());
            case BOOL -> Scalar.makeBool(original.toBoolean());
            case FLOAT -> Scalar.makeFloat(original.toDouble());
            case INT -> Scalar.makeInt(original.toLong());
            case STRING -> Scalar.makeString(original.toString());
            case OBJECT -> null;
        };

        if (casted == null) {
            fatalError("Cannot cast to object.");
        }

        result.setType(NodeType.CONSTANT);
        result.setValue(casted);
    }

    private void executeTypeCheck(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast expr = ast.child[0];
        Ast type = ast.child[1];

        var node = new Node();
        execute(expr, node);

        if (node.getType() != NodeType.CONSTANT) {
            fatalError("Cannot type cast non-scalar expression.");
        }

        String typeName = type.value.toString();
        ScalarType target = ScalarType.getType(typeName);

        if (target == null) {
            fatalError("Unknown type '%s'.", type.value);
            return;
        }

        Scalar value = node.getValue();
        Scalar isSameType = Scalar.makeBool(value.getType() == target);

        result.setType(NodeType.CONSTANT);
        result.setValue(isSameType);
    }

    private void executeNewInstance(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast identifier = ast.child[0];
        String name = identifier.value.toString();

        if (!structs.containsKey(name)) {
            fatalError("Cannot instantiate undefined struct '%s'.", name);
        }

        Ast[] statementList = structs.get(name).child;
        HashMap<String, Scalar> members = HashMap.newHashMap(statementList.length);

        for (Ast mem : statementList) {
            String memName = mem.value.toString();
            members.put(memName, null);
        }

        Scalar object = Scalar.makeObject(name, members);
        result.setType(NodeType.CONSTANT);
        result.setValue(object);
    }

    private void executeVarDeclaration(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        if (var.kind != AstKind.AST_IDENTIFIER) {
            fatalError("Illegal array access on left side of var declaration.");
        }

        String name = var.value.toString();
        if (scope.hasLocalSymbol(name)) {
            fatalError("'%s' is already defined.", name);
        }

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' cannot be assigned to variable '%s'.", expr.kind, name);
        }

        Scalar value = exprNode.getValue();
        scope.addSymbol(name, value);
        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeVarAssign(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        if (var.kind == AstKind.AST_ARRAY_ACCESS) {
            executeArrayAssign(ast, result);
            return;
        }

        if (var.kind == AstKind.AST_STRUCT_ACCESS) {
            executeStructAssign(ast, result);
            return;
        }

        String name = var.value.toString();
        if (!scope.hasSymbol(name)) {
            fatalError("undefined variable '%s'.", name);
        }

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' cannot be assigned to variable '%s'.", expr.kind, name);
        }

        Scalar value = exprNode.getValue();
        scope.setSymbol(name, value);
        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeArrayAssign(Ast ast, Node result) {
        assert ast.child.length == 2;
        assert ast.child[0].kind == AstKind.AST_ARRAY_ACCESS;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        var varNode = new Node();
        execute(var, varNode);

        if (!varNode.hasReference()) {
            fatalError("Cannot assign to non-array value using array access syntax.");
        }

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' cannot be assigned as value.", expr.kind);
        }

        Reference reference = varNode.getReference();
        Scalar value = exprNode.getValue();
        reference.setValue(value);

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeStructAssign(Ast ast, Node result) {
        assert ast.child.length == 2;
        assert ast.child[0].kind == AstKind.AST_STRUCT_ACCESS;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];

        var varNode = new Node();
        execute(var, varNode);

        if (!varNode.hasReference()) {
            fatalError("Unexpected error: unable to compile struct access operation.");
        }

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' cannot be assigned as value.", expr.kind);
        }

        Reference reference = varNode.getReference();
        Scalar value = exprNode.getValue();
        reference.setValue(value);

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeVarFetch(Ast ast, Node result) {
        assert ast.child.length == 0;

        String name = ast.value.toString();
        if (!scope.hasSymbol(name)) {
            fatalError("undefined variable '%s'.", name);
        }

        result.setType(NodeType.CONSTANT);
        result.setValue(scope.getSymbol(name));
    }

    private void executeArrayAccess(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast var = ast.child[0];
        Ast idx = ast.child[1];

        var varNode = new Node();
        execute(var, varNode);
        Scalar array = varNode.getValue();

        if (!array.isArray()) {
            fatalError("Cannot use array access on non array value.");
        }

        if (idx == null) {
            // Add new "empty" space and return (expecting "arr[] = expr")
            List<Scalar> list = array.toList();
            list.add(null);

            Scalar newIndex = Scalar.makeInt(list.size() - 1);

            result.setType(NodeType.NONE);
            result.setValue(null);
            result.setReference(new ArrayReference(array, newIndex));
            return;
        }

        // Existing index within array (expecting "arr[index]")
        var idxNode = new Node();
        execute(idx, idxNode);
        Scalar index = idxNode.getValue();

        var reference = new ArrayReference(array, index);
        Scalar value = reference.getValue();

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
        result.setReference(reference);
    }

    private void executeStructAccess(Ast ast, Node result) {
        assert ast.child.length == 2;
        assert ast.child[1].kind == AstKind.AST_IDENTIFIER;

        Ast var = ast.child[0];
        Ast mem = ast.child[1];

        var varNode = new Node();
        execute(var, varNode);

        Scalar object = varNode.getValue();
        String member = mem.value.toString();

        if (!object.isObject()) {
            fatalError("Attempt to assign property '%s' on non-object value.", member);
        }

        StructInstance instance = object.toObject();
        assert instance != null;

        if (!instance.hasProperty(member)) {
            fatalError("Undefined property %s::%s.", instance.getStructName(), member);
        }

        var reference = new StructReference(instance, member);
        Scalar value = reference.getValue();

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
        result.setReference(reference);
    }

    private void executeCall(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast funcName = ast.child[0];
        Ast funcArgs = ast.child[1];

        String name = funcName.value.toString();

        var arguments = new Node();
        execute(funcArgs, arguments);

        if (arguments.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' is an invalid argument list.", funcArgs.kind);
        }

        List<Scalar> argList = arguments.getValue().toList();
        var callResult = new Node();

        if (handlers.containsKey(name)) {
            FunctionHandler handler = handlers.get(name);
            handler.call(callResult, argList);
        } else if (userFunctions.containsKey(name)) {
            UserFunction func = userFunctions.get(name);

            // Validate argument count
            String[] params = func.getParameters();
            if (argList.size() < params.length) {
                fatalError("Too few arguments: %s() expects %d arguments, %d provided.", name, params.length, argList.size());
                return;
            }

            // Create stack frame for the function
            scope.push();

            // Pass arguments
            for (int i = 0; i < params.length; i++) {
                scope.addSymbol(params[i], argList.get(i));
            }

            // Do call
            Ast body = func.getBody();
            execute(body, callResult);

            // Restore stack
            scope.pop();
        } else {
            fatalError("Call to undefined function '%s'.", name);
            return;
        }

        if (callResult.hasError()) {
            String error = callResult.getValue().toString();
            fatalError(error);
            return;
        }

        result.setType(NodeType.NONE);
        result.setValue(null);

        if (callResult.getType() == NodeType.RETURN && callResult.hasValue()) {
            result.setType(NodeType.CONSTANT);
            result.setValue(callResult.getValue());
        }
    }

    private void executeReturn(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast expr = ast.child[0];
        Scalar value = null;

        /* TODO: Add callstack validation logic */

        if (expr != null) {
            var exprNode = new Node();
            execute(expr, exprNode);

            if (exprNode.getType() != NodeType.CONSTANT) {
                fatalError("Cannot return non-constant expression %s.", expr.kind);
            }

            value = exprNode.getValue();
        }

        result.fnReturn(value);
    }

    private void executeLogicAnd(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var lhsNode = new Node();
        execute(lhs, lhsNode);

        if (lhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in left-hand side operand.");
        }

        if (!lhsNode.getValue().toBoolean()) {
            // false && ??
            result.setType(NodeType.CONSTANT);
            result.setValue(Scalar.makeBool(false));
            return;
        }

        var rhsNode = new Node();
        execute(rhs, rhsNode);

        if (rhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in right-hand side operand.");
        }

        Scalar value = Scalar.makeBool(rhsNode.getValue().toBoolean());

        // true && ??
        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeLogicOr(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var lhsNode = new Node();
        execute(lhs, lhsNode);

        if (lhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in left-hand side operand.");
        }

        if (lhsNode.getValue().toBoolean()) {
            // true || ??
            result.setType(NodeType.CONSTANT);
            result.setValue(Scalar.makeBool(true));
            return;
        }

        var rhsNode = new Node();
        execute(rhs, rhsNode);

        if (rhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in right-hand side operand.");
        }

        Scalar value = Scalar.makeBool(rhsNode.getValue().toBoolean());

        // false || ??
        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeScalarComparison(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var lhsNode = new Node();
        execute(lhs, lhsNode);

        if (lhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in left-hand side operand.");
        }

        var rhsNode = new Node();
        execute(rhs, rhsNode);

        if (rhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in right-hand side operand.");
        }

        Scalar a = lhsNode.getValue();
        Scalar b = rhsNode.getValue();

        boolean order = switch (ast.kind) {
            case AST_EQUALS -> a.equals(b);
            case AST_NOT_EQUALS -> !a.equals(b);
            case AST_SMALLER -> a.compareTo(b) < 0;
            case AST_SMALLER_OR_EQUAL -> a.compareTo(b) <= 0;
            case AST_GREATER -> a.compareTo(b) > 0;
            case AST_GREATER_OR_EQUAL -> a.compareTo(b) >= 0;
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        Scalar value = Scalar.makeBool(order);
        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeBoolNot(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast expr = ast.child[0];

        var node = new Node();
        execute(expr, node);

        if (node.getType() != NodeType.CONSTANT) {
            fatalError("Cannot negate non-boolean expression.");
        }

        boolean original = node.getValue().toBoolean();
        Scalar negated = Scalar.makeBool(!original);

        result.setType(NodeType.CONSTANT);
        result.setValue(negated);
    }

    private void executeScalarOperations(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast lhs = ast.child[0];
        Ast rhs = ast.child[1];

        var lhsNode = new Node();
        execute(lhs, lhsNode);

        if (lhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in left-hand side operand.");
        }

        var rhsNode = new Node();
        execute(rhs, rhsNode);

        if (rhsNode.getType() != NodeType.CONSTANT) {
            fatalError("Invalid expression in right-hand side operand.");
        }

        Scalar a = lhsNode.getValue();
        Scalar b = rhsNode.getValue();

        Scalar value = switch (ast.kind) {
            case AST_ADD -> ScalarOperation.add(a, b);
            case AST_SUBTRACT -> ScalarOperation.subtract(a, b);
            case AST_MULTIPLY -> ScalarOperation.multiply(a, b);
            case AST_POW -> ScalarOperation.pow(a, b);
            case AST_DIVIDE -> ScalarOperation.divide(a, b);
            case AST_MODULO -> ScalarOperation.modulo(a, b);
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        result.setType(NodeType.CONSTANT);
        result.setValue(value);
    }

    private void executeDecInc(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast var = ast.child[0];

        var varNode = new Node();
        execute(var, varNode);

        Scalar original = varNode.getValue();

        if (original.isArray() || original.isString() || original.isObject()) {
            fatalError("Cannot increment a value of type '%s'.", original.getType());
        }

        Scalar modified = switch (ast.kind) {
            case AST_POST_DEC, AST_PRE_DEC -> ScalarOperation.subtract(original, Scalar.makeInt(1));
            case AST_POST_INC, AST_PRE_INC -> ScalarOperation.add(original, Scalar.makeInt(1));
            default -> throw new IllegalStateException("Unexpected value: " + ast.kind);
        };

        // Update value
        if (varNode.hasReference()) {
            Reference reference = varNode.getReference();
            reference.setValue(modified);
        } else {
            String name = var.value.toString();
            scope.setSymbol(name, modified);
        }

        result.setType(NodeType.CONSTANT);

        if (ast.kind == AstKind.AST_PRE_DEC || ast.kind == AstKind.AST_PRE_INC) {
            result.setValue(modified);
        } else {
            result.setValue(original);
        }
    }

    private void executeLen(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast expr = ast.child[0];

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression of type '%s' cannot be used as argument.", expr.kind);
        }

        Scalar value = exprNode.getValue();
        Scalar length;

        if (value.isArray()) {
            length = Scalar.makeInt(value.toList().size());
        } else if (value.isString()) {
            length = Scalar.makeInt(value.toString().length());
        } else {
            fatalError("Type '%s' cannot be used as len() argument.", value.getType());
            return;
        }

        result.setType(NodeType.CONSTANT);
        result.setValue(length);
    }

    private void executeUnset(Ast ast, Node result) {
        assert ast.child.length == 1;

        Ast var = ast.child[0];

        if (var.kind == AstKind.AST_ARRAY_ACCESS || var.kind == AstKind.AST_STRUCT_ACCESS) {
            var varNode = new Node();
            execute(var, varNode);

            Reference reference = varNode.getReference();
            reference.remove();
        } else {
            String name = var.value.toString();
            scope.removeSymbol(name);
        }

        result.setType(NodeType.NONE);
        result.setValue(null);
    }

    private void executeIfStatement(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast cond = ast.child[0];
        Ast stmt = ast.child[1];

        var condNode = new Node();
        execute(cond, condNode);

        boolean executed = false;

        if (condNode.getValue().toBoolean()) {
            scope.beginBlock();

            var node = new Node();
            execute(stmt, node);

            scope.endBlock();
            executed = true;

            if (node.mustReturn()) {
                node.propagateTo(result);
                return;
            }
        }

        result.setType(NodeType.TMP_VALUE);
        result.setValue(Scalar.makeBool(executed));
    }

    private void executeIfElseStatement(Ast ast, Node result) {
        assert ast.child.length == 2;
        assert ast.child[0].kind == AstKind.AST_IF;

        Ast ifstmt = ast.child[0];
        Ast elstmt = ast.child[1];

        var ifstmtNode = new Node();
        execute(ifstmt, ifstmtNode);

        if (!ifstmtNode.getValue().toBoolean()) {
            // execute 'else' block if needed
            scope.beginBlock();

            var node = new Node();
            execute(elstmt, node);

            scope.endBlock();

            if (node.mustReturn()) {
                node.propagateTo(result);
                return;
            }
        }

        if (ifstmtNode.mustReturn()) {
            ifstmtNode.propagateTo(result);
        }
    }

    private void executeForStatement(Ast ast, Node result) {
        assert ast.child.length == 4;

        Ast decl = ast.child[0];
        Ast cond = ast.child[1];
        Ast step = ast.child[2];
        Ast stmt = ast.child[3];

        var declOp = new Node();
        var condOp = new Node();
        var stepOp = new Node();
        var stmtOp = new Node();

        execute(decl, declOp);
        execute(cond, condOp);

        while (condOp.getValue().toBoolean()) {
            scope.beginBlock();
            execute(stmt, stmtOp);
            scope.endBlock();

            if (stmtOp.mustReturn()) {
                stmtOp.propagateTo(result);
                return;
            }

            execute(step, stepOp);
            execute(cond, condOp);
        }

        result.setType(NodeType.NONE);
        result.setValue(null);
    }

    private void executeForeachStatement(Ast ast, Node result) {
        assert ast.child.length == 3;

        Ast var = ast.child[0];
        Ast expr = ast.child[1];
        Ast stmt = ast.child[2];

        var exprNode = new Node();
        execute(expr, exprNode);

        if (exprNode.getType() != NodeType.CONSTANT) {
            fatalError("Expression '%s' cannot be iterated.", expr.kind);
        }

        Scalar iterable = exprNode.getValue();
        if (!iterable.isArray()) {
            fatalError("Cannot iterate non-array value '%s'.", iterable.getType());
        }

        // Get var identifier
        String name = var.value.toString();
        List<Scalar> array = iterable.toList();

        // Create local var if needed
        if (!scope.hasSymbol(name)) {
            scope.addSymbol(name, null);
        }

        for (Scalar value : array) {
            scope.beginBlock();
            scope.setSymbol(name, value);

            var node = new Node();
            execute(stmt, node);

            scope.endBlock();

            if (node.mustReturn()) {
                node.propagateTo(result);
                return;
            }
        }

        result.setType(NodeType.NONE);
        result.setValue(null);
    }

    private void executeDoWhileStatement(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast stmt = ast.child[0];
        Ast expr = ast.child[1];

        var stmtOp = new Node();
        var exprOp = new Node();

        do {
            scope.beginBlock();
            execute(stmt, stmtOp);
            scope.endBlock();

            if (stmtOp.mustReturn()) {
                stmtOp.propagateTo(result);
                return;
            }

            execute(expr, exprOp);
        } while (exprOp.getValue().toBoolean());

        result.setType(NodeType.NONE);
        result.setValue(null);
    }

    private void executeWhileStatement(Ast ast, Node result) {
        assert ast.child.length == 2;

        Ast expr = ast.child[0];
        Ast stmt = ast.child[1];

        var exprOp = new Node();
        var stmtOp = new Node();
        execute(expr, exprOp);

        while (exprOp.getValue().toBoolean()) {
            scope.beginBlock();
            execute(stmt, stmtOp);
            scope.endBlock();

            if (stmtOp.mustReturn()) {
                stmtOp.propagateTo(result);
                return;
            }

            execute(expr, exprOp);
        }

        result.setType(NodeType.NONE);
        result.setValue(null);
    }
}
