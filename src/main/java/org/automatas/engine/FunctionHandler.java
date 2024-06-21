package org.automatas.engine;

import java.util.List;

@FunctionalInterface
public interface FunctionHandler {
    void call(Node result, List<Scalar> args);
}
