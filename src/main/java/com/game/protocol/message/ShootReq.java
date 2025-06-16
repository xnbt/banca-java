package com.game.protocol.message;

import com.game.protocol.*;
import java.util.Arrays;

public class ShootReq extends Message {
    private int bet;
    private Point point;
    private BulletType type;
    private int fishId;
    private int bulletId;

    public static final MessageType TYPE = new MessageType("protocol.ShootReq",
            Arrays.asList(
                    new FieldDescriptor(1, "bet", FieldType.INT64, false, false, false, null),
                    new FieldDescriptor(2, "point", FieldType.MESSAGE, false, false, false, null),
                    new FieldDescriptor(3, "type", FieldType.ENUM, false, false, false, null),
                    new FieldDescriptor(4, "fishId", FieldType.UINT32, false, false, false, null),
                    new FieldDescriptor(5, "bulletId", FieldType.UINT32, false, false, false, null)
            ),
            BinaryRuntime.getInstance()
    );


    public byte[] toBinary() {
        BinaryWriter writer = new BinaryWriter();

        // Field 1: bet (100)
        writer.writeTag(1, 0);  // field_number = 1, wire_type = 0 (varint)
        writer.writeInt32(bet);

        // Field 2: point
        writer.writeTag(2, 2);  // field_number = 2, wire_type = 2 (length-delimited)
        byte[] pointBytes = point.toBinary();
        writer.writeInt32(pointBytes.length);
        writer.write(pointBytes);

        // Field 3: type
        writer.writeTag(3, 0);  // field_number = 3, wire_type = 0 (varint)
        writer.writeInt32(type.getValue());

        // Field 4: fishId
        writer.writeTag(4, 0);  // field_number = 4, wire_type = 0 (varint)
        writer.writeInt32(fishId);

        // Field 5: bulletId
        writer.writeTag(5, 0);  // field_number = 5, wire_type = 0 (varint)
        writer.writeInt32(bulletId);

        return writer.toByteArray();
    }

    @Override
    public MessageType getMessageType() {  // Changed from getType() to getMessageType()
        return TYPE;
    }

    @Override
    public Object getField(String name) {
        switch (name) {
            case "bet": return bet;
            case "point": return point;
            case "type": return type;
            case "fishId": return fishId;
            case "bulletId": return bulletId;
            default: return null;
        }
    }

    @Override
    public void setField(String name, Object value) {
        switch (name) {
            case "bet": bet = ((Number) value).intValue(); break;
            case "point": point = (Point) value; break;
            case "type": type = (BulletType) value; break;
            case "fishId": fishId = ((Number) value).intValue(); break;
            case "bulletId": bulletId = ((Number) value).intValue(); break;
        }
    }

    @Override
    public OneofCase getOneofCase(OneofDescriptor oneof) {
        return null; // No oneofs in this message
    }

    // Convenience getters/setters
    public long getBet() { return bet; }
    public void setBet(int bet) { this.bet = bet; }

    public Point getPoint() { return point; }
    public void setPoint(Point point) { this.point = point; }

    public BulletType getType() { return type; }

    public void setType(BulletType type) { this.type = type; }

    public long getFishId() { return fishId; }
    public void setFishId(int fishId) { this.fishId = fishId; }

    public long getBulletId() { return bulletId; }
    public void setBulletId(int bulletId) { this.bulletId = bulletId; }
}
