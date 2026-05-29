package cryptix.gambling.slot;

import java.util.Random;

public class SlotRandom {

    private static final Random RANDOM = new Random();

    public static SlotSymbol getRandomSymbol() {
        float total = 0;
        for (SlotSymbol s : SlotSymbol.values()) {
            total += s.getWeight();
        }

        float r = RANDOM.nextFloat() * total;

        float cumulative = 0;
        for (SlotSymbol s : SlotSymbol.values()) {
            cumulative += s.getWeight();
            if (r <= cumulative) {
                return s;
            }
        }

        return SlotSymbol.COAL;
    }
}