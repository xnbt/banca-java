package com.game.protocol;

import com.game.protocol.message.BulletType;

import java.util.*;

public class BinaryRuntime {
    private static final BinaryRuntime INSTANCE = new BinaryRuntime();

    public static BinaryRuntime getInstance() {
        return INSTANCE;
    }

    public static class WriteOptions {
        private final boolean writeUnknownFields;
        private final WriterFactory writerFactory;

        public WriteOptions(boolean writeUnknownFields, WriterFactory writerFactory) {
            this.writeUnknownFields = writeUnknownFields;
            this.writerFactory = writerFactory;
        }

        public WriterFactory getWriterFactory() {
            return writerFactory;
        }
    }

    public interface WriterFactory {
        BinaryWriter create();
    }

    public WriteOptions makeWriteOptions(Map<String, Object> options) {
        boolean writeUnknownFields = options != null && Boolean.TRUE.equals(options.get("writeUnknownFields"));
        return new WriteOptions(writeUnknownFields, () -> new BinaryWriter());
    }

    public void writeMessage(Message message, BinaryWriter writer, WriteOptions options) {
        MessageType type = message.getMessageType();  // Updated here too
        for (FieldDescriptor field : type.byNumber()) {
            if (hasField(field, message)) {
                Object value = field.getOneof() != null ?
                        message.getOneofCase(field.getOneof()).getValue() :
                        message.getField(field.getName());
                writeField(field, value, writer, options);
            } else if (field.isRequired()) {
                throw new RuntimeException("Required field not set: " + field.getName());
            }
        }
    }

    private void writeField(FieldDescriptor field, Object value, BinaryWriter writer, WriteOptions options) {
        if (value == null) return;

        if (field.isRepeated()) {
            List<?> values = (List<?>) value;
            if (field.isPacked()) {
                writer.tag(field.getNumber(), WireType.LengthDelimited).fork();
                for (Object item : values) {
                    writeValue(field.getType(), item, writer);
                }
                writer.join();
            } else {
                for (Object item : values) {
                    writer.tag(field.getNumber(), getWireType(field.getType()));
                    writeValue(field.getType(), item, writer);
                }
            }
        } else {
            writer.tag(field.getNumber(), getWireType(field.getType()));
            writeValue(field.getType(), value, writer);
        }
    }

    private WireType getWireType(FieldType type) {
        switch (type) {
            case INT32:
            case INT64:
            case UINT32:
            case UINT64:
            case SINT32:
            case SINT64:
            case BOOL:
            case ENUM:
                return WireType.Varint;
            case FIXED64:
            case SFIXED64:
            case DOUBLE:
                return WireType.Bit64;
            case STRING:
            case BYTES:
            case MESSAGE:
                return WireType.LengthDelimited;
            case FIXED32:
            case SFIXED32:
            case FLOAT:
                return WireType.Bit32;
            default:
                throw new RuntimeException("Unknown field type: " + type);
        }
    }

    // BinaryRuntime.java
    private void writeValue(FieldType type, Object value, BinaryWriter writer) {
        if (value == null) return;

        switch (type) {
            case INT32:
            case INT64:
            case UINT32:
            case UINT64:
            case BOOL:
                writer.varint(((Number) value).longValue());
                break;
            case ENUM:
                // Direct handling of enum value
                if (value instanceof Enum<?>) {
                    if (value instanceof BulletType) {
                        writer.varint(((BulletType) value).getValue());
                    } else {
                        writer.varint(((Enum<?>) value).ordinal());
                    }
                } else {
                    throw new RuntimeException("Expected enum value but got: " + value.getClass());
                }
                break;
            case SINT32:
            case SINT64:
                writer.zigZag(((Number) value).longValue());
                break;
            case FIXED64:
            case SFIXED64:
            case DOUBLE:
                writer.fixed64(((Number) value).longValue());
                break;
            case STRING:
                writer.string((String) value);
                break;
            case BYTES:
                writer.bytes((byte[]) value);
                break;
            case MESSAGE:
                byte[] messageBytes = ((Message) value).toBinary(null);
                writer.bytes(messageBytes);
                break;
            case FIXED32:
            case SFIXED32:
            case FLOAT:
                writer.fixed32(((Number) value).intValue());
                break;
            default:
                throw new RuntimeException("Unknown field type: " + type);
        }
    }

    private boolean hasField(FieldDescriptor field, Message message) {
        if (field.isRepeated()) {
            List<?> list = (List<?>) message.getField(field.getName());
            return list != null && !list.isEmpty();
        }
        if (field.getOneof() != null) {
            OneofCase oneofCase = message.getOneofCase(field.getOneof());
            return oneofCase != null && oneofCase.getFieldName().equals(field.getName());
        }
        Object value = message.getField(field.getName());
        return value != null;
    }
}
