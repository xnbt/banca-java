package com.game.protocol.message;

import com.game.protocol.*;
import java.util.Arrays;

public class Point extends Message {
    private double x;
    private double y;

    public static final MessageType TYPE = new MessageType("protocol.Point",
            Arrays.asList(
                    new FieldDescriptor(1, "x", FieldType.INT32, false, false, false, null),
                    new FieldDescriptor(2, "y", FieldType.INT32, false, false, false, null)
            ),
            BinaryRuntime.getInstance()
    );

    public byte[] toBinary() {
        BinaryWriter writer = new BinaryWriter();

        // Field 1: x
        writer.writeTag(1, 5);  // field_number = 1, wire_type = 5 (32-bit)
        writer.writeDouble(x);

        // Field 2: y
        writer.writeTag(2, 5);  // field_number = 2, wire_type = 5 (32-bit)
        writer.writeDouble(y);

        // Field 3: z
        writer.writeTag(3, 5);  // field_number = 3, wire_type = 5 (32-bit)

        return writer.toByteArray();
    }

    @Override
    public MessageType getMessageType() {  // Changed from getType() to getMessageType()
        return TYPE;
    }

    @Override
    public Object getField(String name) {
        switch (name) {
            case "x": return x;
            case "y": return y;
            default: return null;
        }
    }

    @Override
    public void setField(String name, Object value) {
        switch (name) {
            case "x": x = (Integer) value; break;
            case "y": y = (Integer) value; break;
        }
    }

    @Override
    public OneofCase getOneofCase(OneofDescriptor oneof) {
        return null; // No oneofs in this message
    }

    // Convenience getters/setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}
