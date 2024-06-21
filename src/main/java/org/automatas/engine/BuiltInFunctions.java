package org.automatas.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class BuiltInFunctions {
    public static void loadBuiltIns(HashMap<String, FunctionHandler> handlers) {
        handlers.put("print", BuiltInFunctions::handlePrint);
        handlers.put("printf", BuiltInFunctions::handlePrintf);
        handlers.put("input", BuiltInFunctions::handleInput);
    }

    private static void handlePrint(Node result, List<Scalar> args) {
        if (args.isEmpty()) {
            result.raiseError("Too few arguments: print() requires 1 argument, 0 provided.");
            return;
        }

        String message = args.get(0).toString();
        System.out.println(message);

        result.fnReturn();
    }

    private static void handlePrintf(Node result, List<Scalar> args) {
        if (args.isEmpty()) {
            result.raiseError("Too few arguments: printf() requires at least 1 argument, 0 provided.");
            return;
        }

        String format = args.get(0).toString();
        Object[] values = new Object[args.size() - 1];

        for (int i = 1; i < args.size(); i++) {
            values[i - 1] = args.get(i).getRawValue();
        }

        System.out.printf(format, values);

        result.fnReturn();
    }

    private static void handleInput(Node result, List<Scalar> args) {
        if (!args.isEmpty()) {
            String prompt = args.get(0).toString();
            System.out.print(prompt);
        }

        try {
            var reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            Scalar value = Scalar.makeString(input);

            result.fnReturn(value);
        } catch (IOException e) {
            result.raiseError("Fatal OS Exception: stdin read failed.");
        }
    }

    /**
     * Static class.
     */
    private BuiltInFunctions() {
    }
}
