
package main;

import javax.swing.SwingUtilities;
import modell.LampakModell;
import nezet.LightOnGUInezet;
import vezerlo.LightOnPrgVezerlo;

public class LightOnPrg {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LampakModell modell = new LampakModell();
            LightOnGUInezet nezet = new LightOnGUInezet();
            // A vezérlő megkeresi a GUI komponenseit a hierarchiából,
            // ezért nem kell megváltoztatni a nezet fájlt.
            new LightOnPrgVezerlo(modell, nezet);
            nezet.setVisible(true);
        });
    }
    
}