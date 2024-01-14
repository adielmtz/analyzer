package org.automatas.engine;

import java.util.HashMap;
import java.util.Set;

public class StructInstance {
    private final String name;
    private final HashMap<String, Scalar> members;

    public StructInstance(String name, HashMap<String, Scalar> members) {
        this.name = name;
        this.members = members;
    }

    public boolean hasProperty(String name) {
        return members.containsKey(name);
    }

    public String getStructName() {
        return name;
    }

    public Set<String> getPropertyNames() {
        return members.keySet();
    }

    public Scalar getPropertyValue(String member) {
        return members.get(member);
    }

    public void setPropertyValue(String member, Scalar value) {
        members.put(member, value);
    }
}
