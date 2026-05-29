package cryptix.gambling.slot;

public class SlotMachine {

    private final SlotReel[] reels = {
        new SlotReel(),
        new SlotReel(),
        new SlotReel()
    };

    private boolean spinning;

    public void spin() {
        if (spinning) return;

        spinning = true;

        for (SlotReel reel : reels) {
            reel.startSpin();
        }
    }

    public void update() {
        spinning = false;

        for (SlotReel reel : reels) {
            reel.update();
            if (reel.isSpinning()) spinning = true;
        }
    }

    public boolean isFinished() {
        return !spinning;
    }

    public SlotReel[] getReels() {
        return reels;
    }

    public SlotSymbol[] getResults() {
        SlotSymbol[] out = new SlotSymbol[reels.length];

        for (int i = 0; i < reels.length; i++) {
            SlotReel r = reels[i];

            int center = (int)(r.getPosition() / 32);
            out[i] = r.getSymbolAt(center);
        }

        return out;
    }

    public int getPayout() {
        SlotSymbol[] r = getResults();

        if (r[0] == r[1] && r[1] == r[2]) {
            switch (r[0]) {
                case DIAMOND: return 100;
                case EMERALD: return 50;
                case GOLD: return 20;
                case IRON: return 10;
                default: return 5;
            }
        }

        return 0;
    }
}