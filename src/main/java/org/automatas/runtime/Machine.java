package org.automatas.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class Machine {
    private final static int CMP_RESULT = 15;

    /* Virtual-ish registers */
    private final Object[] r = new Object[16];

    /* Register stack */
    private final Stack<Object> stack = new Stack<>();

    /* Virtual-ish memory */
    private final List<Object> heap = new ArrayList<>();

    private final Op[] instructions;

    private int pc = 0;

    public Machine(Op[] instructions) {
        this.instructions = instructions;
    }

    public void execute() {
        while (pc < instructions.length) {
            Op op = instructions[pc];
            pc++;

            switch (op.code) {
                case MOVE:
                    executeMove(op);
                    break;
                case LOAD:
                    executeLoad(op);
                    break;
                case STORE:
                    executeStore(op);
                    break;
                case PUSH:
                    executePush(op);
                    break;
                case POP:
                    executePop(op);
                    break;
                case PEEK:
                    executePeek(op);
                case COMPARE:
                    executeCompare(op);
                    break;
                case JMP:
                    executeJmp(op);
                    break;
                case JMP_EQ:
                    executeJmpEq(op);
                    break;
                case JMP_NEQ:
                    executeJmpNeq(op);
                    break;
                case JMP_LT:
                    executeJmpLt(op);
                    break;
                case JMP_GT:
                    executeJmpGt(op);
                    break;
                case INPUT:
                    executeInput(op);
                    break;
                case OUTPUT:
                    executeOutput(op);
                    break;
            }
        }
    }

    /**
     * Copies the value from one register to another.
     */
    private void executeMove(Op op) {
        r[op.op1] = r[op.op2];
    }

    /**
     * Loads data from memory to a register.
     */
    private void executeLoad(Op op) {
        //r[op.op1] = heap.get(op.op2);
    }

    /**
     * Stores data from a register to memory.
     */
    private void executeStore(Op op) {
        //heap.set(op.op1, r[op.op2]);
    }

    private void executePush(Op op) {
        int n = op.op1;
        stack.push(r[n]);
    }

    private void executePop(Op op) {
        int n = op.op1;
        r[n] = stack.pop();
    }

    private void executePeek(Op op) {
        int n = op.op1;
        r[n] = stack.peek();
    }

    @SuppressWarnings("unchecked")
    private void executeCompare(Op op) {
        Comparable<Object> a = (Comparable<Object>) r[op.op1];
        Comparable<Object> b = (Comparable<Object>) r[op.op2];
        r[CMP_RESULT] = a.compareTo(b);
    }

    private void executeJmp(Op op) {
        pc = op.op1;
    }

    private void executeJmpEq(Op op) {
        int cmp = (int) r[CMP_RESULT];

        if (cmp == 0) {
            pc = op.op1;
        }
    }

    private void executeJmpNeq(Op op) {
        int cmp = (int) r[CMP_RESULT];

        if (cmp != 0) {
            pc = op.op1;
        }
    }

    private void executeJmpLt(Op op) {
        int cmp = (int) r[CMP_RESULT];

        if (cmp < 0) {
            pc = op.op1;
        }
    }

    private void executeJmpGt(Op op) {
        int cmp = (int) r[CMP_RESULT];

        if (cmp > 0) {
            pc = op.op1;
        }
    }

    /**
     * Reads a string from STDIN and stores it into memory.
     */
    private void executeInput(Op op) {
        try {
            var reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            ArrayList<Object> val = (ArrayList<Object>) heap;
            val.ensureCapacity(op.op1);

            heap.set(op.op1, input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints a string from memory to STDOUT.
     */
    private void executeOutput(Op op) {
        Object value = heap.get(op.op1);
        System.out.print(value);
    }
}
