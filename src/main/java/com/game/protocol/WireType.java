package com.game.protocol;

public enum WireType {
    Varint(0),
    Bit64(1),
    LengthDelimited(2),
    StartGroup(3),
    EndGroup(4),
    Bit32(5);

    private final int value;

    WireType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
