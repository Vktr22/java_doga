
package modell;


public class LampaModell {
    // true = zöld = bekapcsolt; false = piros = kikapcsolt
    private boolean allapot;

    public LampaModell() {
        this.allapot = false;
    }

    public boolean isAllapot() {
        return allapot;
    }

    public void setAllapot(boolean allapot) {
        this.allapot = allapot;
    }

    public void kapcsol() {
        this.allapot = !this.allapot;
    }
}
