package org.automatas.engine;

public class StructReference implements Reference {
    private final StructInstance instance;
    private final String member;

    public StructReference(StructInstance instance, String member) {
        this.instance = instance;
        this.member = member;
    }

    public StructReference(Scalar object, String member) {
        this(object.toObject(), member);
    }

    @Override
    public Scalar getValue() {
        return instance.getValue(member);
    }

    @Override
    public void setValue(Scalar value) {
        instance.setValue(member, value);
    }

    @Override
    public void remove() {
        // TODO: no-op.
    }
}
