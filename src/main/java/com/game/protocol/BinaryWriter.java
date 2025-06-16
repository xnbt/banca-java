// BinaryWriter.java
package com.game.protocol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BinaryWriter {
    private ByteArrayOutputStream buffer;
    private List<ByteArrayOutputStream> forks;

    // Add this method to write byte arrays
    public void write(byte[] bytes) {
        try {
            buffer.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write bytes", e);
        }
    }

    // Add this method to write a specific range of bytes
    public void write(byte[] bytes, int offset, int length) {
        try {
            buffer.write(bytes, offset, length);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write bytes", e);
        }
    }

    public void writeDouble(double value) {
        byte[] bytes = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putDouble(value)
                .array();
        buffer.write(bytes, 0, bytes.length);
    }

    public void writeInt32(int value) {
        // Write in varint format
        while ((value & ~0x7F) != 0) {
            buffer.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buffer.write(value & 0x7F);
    }

    public void writeTag(int fieldNumber, int wireType) {
        int tag = (fieldNumber << 3) | wireType;
        writeInt32(tag);
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

    public BinaryWriter() {
        this.buffer = new ByteArrayOutputStream();
        this.forks = new ArrayList<>();
    }

    public BinaryWriter tag(int fieldNumber, WireType wireType) {
        int tag = (fieldNumber << 3) | wireType.getValue();
        writeVarint(tag);
        return this;
    }

    // Add this method for writing varints
    private void writeVarint(long value) {
        // Handle negative values for signed integers
        if (value < 0) {
            value = (value << 1) ^ (value >> 63);
        }

        while (true) {
            if ((value & ~0x7FL) == 0) {
                buffer.write((int)value);
                return;
            } else {
                buffer.write(((int)value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public BinaryWriter varint(long value) {
        writeVarint(value);
        return this;
    }

    public BinaryWriter zigZag(long value) {
        // ZigZag encoding for signed integers
        long zigzag = (value << 1) ^ (value >> 63);
        writeVarint(zigzag);
        return this;
    }

    public BinaryWriter fixed32(int value) {
        // Write in little-endian format
        buffer.write(value & 0xFF);
        buffer.write((value >>> 8) & 0xFF);
        buffer.write((value >>> 16) & 0xFF);
        buffer.write((value >>> 24) & 0xFF);
        return this;
    }

    public BinaryWriter fixed64(long value) {
        // Write in little-endian format
        buffer.write((int)(value & 0xFF));
        buffer.write((int)((value >>> 8) & 0xFF));
        buffer.write((int)((value >>> 16) & 0xFF));
        buffer.write((int)((value >>> 24) & 0xFF));
        buffer.write((int)((value >>> 32) & 0xFF));
        buffer.write((int)((value >>> 40) & 0xFF));
        buffer.write((int)((value >>> 48) & 0xFF));
        buffer.write((int)((value >>> 56) & 0xFF));
        return this;
    }

    public BinaryWriter bytes(byte[] value) {
        writeVarint(value.length);  // Write length as varint
        buffer.write(value, 0, value.length);
        return this;
    }

    public BinaryWriter string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return bytes(bytes);
    }

    public BinaryWriter raw(byte[] value) {
        buffer.write(value, 0, value.length);
        return this;
    }

    public BinaryWriter fork() {
        forks.add(buffer);
        buffer = new ByteArrayOutputStream();
        return this;
    }

    public BinaryWriter join() {
        if (forks.isEmpty()) {
            throw new IllegalStateException("No fork to join");
        }
        byte[] forkedData = buffer.toByteArray();
        buffer = forks.remove(forks.size() - 1);
        writeVarint(forkedData.length);
        raw(forkedData);
        return this;
    }

    public byte[] finish() {
        if (!forks.isEmpty()) {
            throw new IllegalStateException("Unclosed forks remaining");
        }
        return buffer.toByteArray();
    }
}
