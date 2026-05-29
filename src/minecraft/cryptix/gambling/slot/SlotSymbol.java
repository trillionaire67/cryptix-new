package cryptix.gambling.slot;

public enum SlotSymbol {
    DIAMOND(0.05f),
    EMERALD(0.1f),
    GOLD(0.2f),
    IRON(0.3f),
    COAL(0.35f);

    private final float weight;

    SlotSymbol(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }
}