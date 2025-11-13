package modell;

import java.util.Random;


public class LampakModell {
    private static final int OLDAL = 3;
    private static final int OSSZES_LAMPA = OLDAL * OLDAL;
    private final LampaModell[] lampak = new LampaModell[OSSZES_LAMPA];
    private int lepesszam = 0;

    public LampakModell() {
        for (int i = 0; i < OSSZES_LAMPA; i++) {
            lampak[i] = new LampaModell();
        }
        ujJatek();
    }

    public int getMeret() {
        return OLDAL;
    }

    public LampaModell getLampa(int sor, int oszlop) {
        ellenorizIndex(sor, oszlop);
        return lampak[indexFromCoords(sor, oszlop)];
    }

    public int getLepesszam() {
        return lepesszam;
    }

    public void noveliLepesszam() {
        lepesszam++;
    }

    public void resetLepesszam() {
        lepesszam = 0;
    }

    
    
    
    public void kattintas(int sor, int oszlop) {
        ellenorizIndex(sor, oszlop);
        kapcsol(sor, oszlop);
        if (sor > 0) kapcsol(sor - 1, oszlop);
        if (sor < OLDAL - 1) kapcsol(sor + 1, oszlop);
        if (oszlop > 0) kapcsol(sor, oszlop - 1);
        if (oszlop < OLDAL - 1) kapcsol(sor, oszlop + 1);
        noveliLepesszam();
    }

    private void kapcsol(int sor, int oszlop) {
        ellenorizIndex(sor, oszlop);
        lampak[indexFromCoords(sor, oszlop)].kapcsol();
    }

    public void ujJatek() {
        Random rnd = new Random();
        boolean vanPiros;
        do {
            vanPiros = false;
            for (int i = 0; i < OSSZES_LAMPA; i++) {
                boolean all = rnd.nextBoolean();
                lampak[i].setAllapot(all);
                if (!all) vanPiros = true;
            }
        } while (!vanPiros);
        resetLepesszam();
    }

    
    public String exportAllapot() {
        String s = "";
        for (int i = 0; i < OSSZES_LAMPA; i++) {
            s = s + (lampak[i].isAllapot() ? '1' : '0');
        }
        s = s + ";" + lepesszam;
        return s;
    }

    public void importAllapot(String adat) {
        if (adat == null) return;
        if (!adat.contains(";")) return;
        String[] parts = adat.split(";", 2);
        String lamp = parts[0];
        int index = 0;
        for (int i = 0; i < OSSZES_LAMPA; i++) {
            if (index < lamp.length()) {
                lampak[i].setAllapot(lamp.charAt(index) == '1');
                index++;
            } else {
                lampak[i].setAllapot(false);
            }
        }
        try {
            lepesszam = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            lepesszam = 0;
        }
    }

    private int indexSorOszlopbol(int sor, int oszlop) {
        return sor * OLDAL + oszlop;
    }

    private void ellenorizIndex(int sor, int oszlop) {
        if (sor < 0 || sor >= OLDAL) {
            throw new IndexOutOfBoundsException("Sor index kívül esik: " + sor);
        }
        if (oszlop < 0 || oszlop >= OLDAL) {
            throw new IndexOutOfBoundsException("Oszlop index kívül esik: " + oszlop);
        }
    }
}