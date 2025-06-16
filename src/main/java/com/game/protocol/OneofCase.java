package com.game.protocol;

public class OneofCase {
    private final String fieldName;
    private final Object value;

    public OneofCase(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() { return fieldName; }
    public Object getValue() { return value; }
}
