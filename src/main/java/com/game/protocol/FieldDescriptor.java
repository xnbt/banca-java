package com.game.protocol;

// FieldDescriptor.java
public class FieldDescriptor {
    private final int number;        // Field number in the protocol
    private final String name;       // Field name
    private final String jsonName;   // Name used in JSON (usually same as name)
    private final FieldType type;    // Field type (INT32, STRING, MESSAGE, etc)
    private final boolean repeated;  // If field is a repeated/array
    private final boolean required;  // If field is required
    private final boolean packed;    // If repeated field should be packed in binary format
    private final OneofDescriptor oneof;  // If field is part of a oneof group
    private final Object defaultValue;    // Default value for the field

    public FieldDescriptor(int number,
                           String name,
                           FieldType type,
                           boolean repeated,
                           boolean required,
                           boolean packed,
                           OneofDescriptor oneof) {
        this(number, name, name, type, repeated, required, packed, oneof, null);
    }

    public FieldDescriptor(int number,
                           String name,
                           String jsonName,
                           FieldType type,
                           boolean repeated,
                           boolean required,
                           boolean packed,
                           OneofDescriptor oneof,
                           Object defaultValue) {
        this.number = number;
        this.name = name;
        this.jsonName = jsonName;
        this.type = type;
        this.repeated = repeated;
        this.required = required;
        this.packed = packed;
        this.oneof = oneof;
        this.defaultValue = defaultValue;
    }

    // Getters
    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getJsonName() {
        return jsonName;
    }

    public FieldType getType() {
        return type;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isPacked() {
        return packed;
    }

    public OneofDescriptor getOneof() {
        return oneof;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    // Helper method to create a builder pattern
    public static Builder newBuilder() {
        return new Builder();
    }

    // Builder pattern for easier field creation
    public static class Builder {
        private int number;
        private String name;
        private String jsonName;
        private FieldType type;
        private boolean repeated;
        private boolean required;
        private boolean packed;
        private OneofDescriptor oneof;
        private Object defaultValue;

        public Builder setNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            this.jsonName = name; // Default jsonName to name
            return this;
        }

        public Builder setJsonName(String jsonName) {
            this.jsonName = jsonName;
            return this;
        }

        public Builder setType(FieldType type) {
            this.type = type;
            return this;
        }

        public Builder setRepeated(boolean repeated) {
            this.repeated = repeated;
            return this;
        }

        public Builder setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder setPacked(boolean packed) {
            this.packed = packed;
            return this;
        }

        public Builder setOneof(OneofDescriptor oneof) {
            this.oneof = oneof;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public FieldDescriptor build() {
            return new FieldDescriptor(
                    number,
                    name,
                    jsonName,
                    type,
                    repeated,
                    required,
                    packed,
                    oneof,
                    defaultValue
            );
        }
    }
}
