package org.automatas.program;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.automatas.Ast;
import org.automatas.Lexer;
import org.automatas.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (var reader = new FileReader("./code.txt")) {
            var factory = new ComplexSymbolFactory();
            var lexer = new Lexer(reader, factory);
            var parser = new Parser(lexer, factory);

            Symbol result = parser.parse();
            ArrayList<Ast> astList = (ArrayList<Ast>) result.value;

            var executor = new Executor(astList);
            executor.execute();
        } catch (FileNotFoundException notFoundException) {
            System.err.println("File was not found!");
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
