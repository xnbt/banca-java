package com.game.protocol.message;

public enum BulletType {
    Normal(0),
    Lock(1),
    Dragon(2),
    DragonOther(3),
    Phoenix(4),
    Drill(5),
    Laser(6),
    Fire(7),
    Free(20),
    FreeDrill(21),
    FreeNet(22),
    Energy(23),
    FreeSpin(24);

    private final int value;

    BulletType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BulletType fromValue(int value) {
        for (BulletType type : values()) {
            if (type.value == value) return type;
        }
        return Normal; // Default value if not found
    }
}
