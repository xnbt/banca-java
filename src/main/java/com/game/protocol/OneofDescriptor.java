package com.game.protocol;

import java.util.List;

public class OneofDescriptor {
    private final String name;
    private final List<FieldDescriptor> fields;

    public OneofDescriptor(String name, List<FieldDescriptor> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() { return name; }
    public List<FieldDescriptor> getFields() { return fields; }
}
