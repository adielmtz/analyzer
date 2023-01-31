package org.automatas.program;

import org.automatas.engine.Executor;

public class Main {
    public static void main(String[] args) {
        var engine = new Executor();
        engine.executeFile("./code.txt");
    }
}
