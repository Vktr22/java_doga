package teszt;

import modell.LampakModell;


public class LampaKattintTeszt {
    public static void main(String[] args) {
        
        boolean assertEngedelyezve = false;
        assert assertEngedelyezve = true;
        if (!assertEngedelyezve) {
            System.err.println("Figyelem: assert-ek nincsenek engedélyezve. Futtassa a JVM-et -ea kapcsolóval.");
        }

        LampakModell m = new LampakModell();

       
        for (int i = 0; i < m.getMeret(); i++) {
            for (int j = 0; j < m.getMeret(); j++) {
                m.getLampa(i, j).setAllapot(false);
            }
        }
        
        try {
            m.resetLepesszam();
        } catch (Throwable ignored) {}

        
        m.kattintas(1, 1);

        
        assert m.getLampa(1,1).isAllapot() : "Középső lámpa nem kapcsolt be.";
        assert m.getLampa(0,1).isAllapot() : "Fent lévő lámpa nem kapcsolt be.";
        assert m.getLampa(2,1).isAllapot() : "Lent lévő lámpa nem kapcsolt be.";
        assert m.getLampa(1,0).isAllapot() : "Bal oldali lámpa nem kapcsolt be.";
        assert m.getLampa(1,2).isAllapot() : "Jobb oldali lámpa nem kapcsolt be.";

        assert !m.getLampa(0,0).isAllapot() : "Bal felső saroknak kikapcsoltnak kell maradnia.";
        assert !m.getLampa(0,2).isAllapot() : "Jobb felső saroknak kikapcsoltnak kell maradnia.";
        assert !m.getLampa(2,0).isAllapot() : "Bal alsó saroknak kikapcsoltnak kell maradnia.";
        assert !m.getLampa(2,2).isAllapot() : "Jobb alsó saroknak kikapcsoltnak kell maradnia.";

        assert m.getLepesszam() == 1 : "Lépésszámot 1-nek vártuk, kaptuk: " + m.getLepesszam();

        System.out.println("LampaKattintTeszt: OK");
    }
}