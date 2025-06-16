package com.game.protocol;

import java.util.List;
import java.util.ArrayList;

public class MessageType {
    private final String name;
    private final List<FieldDescriptor> fields;
    private final BinaryRuntime runtime;

    public MessageType(String name, List<FieldDescriptor> fields, BinaryRuntime runtime) {
        this.name = name;
        this.fields = fields;
        this.runtime = runtime;
    }

    public String getName() { return name; }
    public List<FieldDescriptor> getFields() { return fields; }
    public BinaryRuntime getRuntime() { return runtime; }

    public List<FieldDescriptor> byNumber() {
        List<FieldDescriptor> sorted = new ArrayList<>(fields);
        sorted.sort((a, b) -> Integer.compare(a.getNumber(), b.getNumber()));
        return sorted;
    }
}
