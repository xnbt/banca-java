package com.game.protocol;

import java.util.Map;

public abstract class Message {
    private static final String UNKNOWN_FIELDS_KEY = "__unknown_fields";

    public abstract MessageType getMessageType();  // Changed from getType() to getMessageType()
    public abstract Object getField(String name);
    public abstract void setField(String name, Object value);
    public abstract OneofCase getOneofCase(OneofDescriptor oneof);

    public byte[] toBinary(Map<String, Object> options) {
        BinaryRuntime runtime = getMessageType().getRuntime();  // Updated here too
        BinaryRuntime.WriteOptions writeOptions = runtime.makeWriteOptions(options);
        BinaryWriter writer = writeOptions.getWriterFactory().create();
        runtime.writeMessage(this, writer, writeOptions);
        return writer.finish();
    }
}
