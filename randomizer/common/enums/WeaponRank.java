package randomizer.common.enums;

public enum WeaponRank {
    None (0),
    E (0x1),
    D (0x15),
    C (0x33),
    B (0x60),
    A (0xA1),
    S (0xFB);

    private int value;

    WeaponRank(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
