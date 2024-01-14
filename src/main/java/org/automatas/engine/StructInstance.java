package org.automatas.engine;

import java.util.HashMap;

public class StructInstance {
    private final String name;
    private final HashMap<String, Scalar> members;

    public StructInstance(String name, HashMap<String, Scalar> members) {
        this.name = name;
        this.members = members;
    }

    public Scalar getValue(String member) {
        return members.get(member);
    }

    public void setValue(String member, Scalar value) {
        members.put(member, value);
    }
}
