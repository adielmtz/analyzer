package org.automatas.engine;

public interface Reference {
    Scalar getValue();
    void setValue(Scalar value);
    void remove();
}
