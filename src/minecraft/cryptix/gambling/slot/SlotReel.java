package cryptix.gambling.slot;

public class SlotReel {

    private final SlotSymbol[] strip = new SlotSymbol[50];

    private float position;
    private float speed;
    private boolean spinning;

    private int resultIndex;

    public SlotReel() {
        for (int i = 0; i < strip.length; i++) {
            strip[i] = SlotRandom.getRandomSymbol();
        }
    }

    public void startSpin() {
        spinning = true;

        // refresh strip each spin so it feels alive
        for (int i = 0; i < strip.length; i++) {
            strip[i] = SlotRandom.getRandomSymbol();
        }

        speed = 30f + (float)Math.random() * 10f;

        resultIndex = (int)(Math.random() * strip.length);
    }

    public void update() {
        if (!spinning) return;

        position += speed;

        // stronger deceleration = visible spin
        speed *= 0.92f;

        if (speed < 0.6f) {
            spinning = false;

            // snap cleanly
            position = resultIndex * 32;
        }
    }

    public boolean isSpinning() {
        return spinning;
    }

    public float getPosition() {
        return position;
    }

    public SlotSymbol getSymbolAt(int index) {
        int len = strip.length;
        int wrapped = ((index % len) + len) % len;
        return strip[wrapped];
    }

    public int getBaseIndex() {
        return (int)(position / 32);
    }
}