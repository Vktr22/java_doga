package teszt;

import nezet.LightOnGUInezet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class GUInezetTeszt {
    public static void main(String[] args) throws Exception {
        
        boolean assertEngedelyezve = false;
        assert assertEngedelyezve = true;
        if (!assertEngedelyezve) {
            System.err.println("Figyelem: assert-ek nincsenek engedélyezve. Futtassa a JVM-et -ea kapcsolóval.");
        }

        final AtomicReference<List<JButton>> gombokRef = new AtomicReference<>();

        
        SwingUtilities.invokeAndWait(() -> {
            LightOnGUInezet nezet = new LightOnGUInezet();
            nezet.pack(); 

            List<JButton> gombok = new ArrayList<>();
            keresKomponensek(nezet.getContentPane(), gombok);

            gombokRef.set(gombok);
            nezet.dispose();
        });

        List<JButton> gombok = gombokRef.get();
        if (gombok == null) gombok = new ArrayList<>();

        
        assert gombok.size() >= 9 : "Nem található legalább 9 JButton a GUI-ban. Talált: " + gombok.size();

        System.out.println("GUInezetTeszt: OK — legalább 9 gomb található (talált: " + gombok.size() + ")");
    }

    private static void keresKomponensek(Container kontener, List<JButton> gombLista) {
        for (Component c : kontener.getComponents()) {
            if (c instanceof JButton) gombLista.add((JButton) c);
            else if (c instanceof Container) keresKomponensek((Container) c, gombLista);
        }
    }
}