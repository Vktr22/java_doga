package vezerlo;

import modell.LampakModell;
import nezet.LightOnGUInezet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * A vezérlő osztály: összeköti a modellt és a nézetet.
 * Nem hivatkozik konkrét mezőnevekre a _LightOnGUInezet-ben, hanem
 * a komponenseket a GUI hierarchiájából gyűjti össze (biztosítva, hogy
 * a nezet fájlokat ne kelljen módosítani).
 *
 * Itt találhatók a try/catch-ek és a felhasználó értesítései (JOptionPane).
 */
public class LightOnPrgVezerlo {
    private final LampakModell modell;
    private final LightOnGUInezet nezet;

    private final JButton[] gombok = new JButton[9];
    private JLabel lblLepesszam = null;

    // Mentés helye (user.home/.lighton/savedState.java)
    private final Path mentesiUt = Path.of(System.getProperty("user.home"), ".lighton", "savedState.java");

    public LightOnPrgVezerlo(LampakModell modell, LightOnGUInezet nezet) {
        this.modell = modell;
        this.nezet = nezet;

        // Létrehozzuk mentési mappát (try/catch)
        try {
            Files.createDirectories(mentesiUt.getParent());
        } catch (IOException ex) {
            // Ha nem sikerül, jelezzük, de nem állítjuk le a programot.
            JOptionPane.showMessageDialog(nezet,
                    "Figyelem: nem sikerült létrehozni a mentési mappát: " + ex.getMessage(),
                    "Mentési könyvtár hiba", JOptionPane.WARNING_MESSAGE);
        }

        // Komponensek felderítése és események bekötése
        talaldMegAGombokatEsLabelt();
        initGombokEsemények();
        initMenuEsemények();

        // Megjelenítés frissítése a modell alapján
        frissitMegjelenitest();
    }

    /**
     * Megkeresi a 9 gombot a GUI-ban (GridLayout-ban) és a lépésszám JLabel-t.
     * Rugalmas keresés: összes JButton összegyűjtése a hierarchiából; a 9 elsőt használjuk.
     */
    /* Csak a talaldMegAGombokatEsLabelt() és segédfüggvényeinek helyettesítő kódja */
private void talaldMegAGombokatEsLabelt() {
    List<JButton> talaltGombok = new ArrayList<>();
    List<JLabel> talaltLabel = new ArrayList<>();
    keresKomponensek(nezet.getContentPane(), talaltGombok, talaltLabel);

    // Fallback reflection (ha kevés gombot találtunk)
    if (talaltGombok.size() < 9) {
        try {
            for (java.lang.reflect.Field f : nezet.getClass().getFields()) {
                if (talaltGombok.size() >= 9) break;
                if (JButton.class.isAssignableFrom(f.getType())) {
                    Object val = f.get(nezet);
                    if (val instanceof JButton) talaltGombok.add((JButton) val);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    // Rendezés: sor-major (először y, majd x)
    // Használjuk a komponens bounds-át. Fontos: hívj nezet.pack()-ot korábban, hogy legyenek érvényes bounds-ok.
    for (int i = 1; i <= 9; i++) {
    try {
        java.lang.reflect.Field f = nezet.getClass().getField("btn" + i);
        Object val = f.get(nezet);
        if (val instanceof JButton) {
            gombok[i-1] = (JButton) val; // 1->index0, 2->index1 ...
        }
    } catch (NoSuchFieldException | IllegalAccessException ignored) {}
}
    // Feltöltjük a gombok tömböt sor-major sorrendben (az első 9 gomb felhasználva)
    for (int i = 0; i < Math.min(9, talaltGombok.size()); i++) {
        gombok[i] = talaltGombok.get(i);
    }

    // Labeleket ugyanúgy gyűjtjük, mint korábban
    for (JLabel l : talaltLabel) {
        if (l.getText() != null && l.getText().toLowerCase().contains("lép")) {
            lblLepesszam = l;
            break;
        }
    }
    if (lblLepesszam == null && !talaltLabel.isEmpty()) {
        lblLepesszam = talaltLabel.get(0);
    }

    // Ha hiányos, jelezzük
    int talalt = 0;
    for (JButton b : gombok) if (b != null) talalt++;
    if (talalt < 9 || lblLepesszam == null) {
        JOptionPane.showMessageDialog(nezet,
                "Figyelem: a GUI komponensek nem teljesen az elvárt helyen találhatók.\n" +
                        "Ellenőrizze a nezet/_LightOnGUInezet.java fájlt. (Talált gombok: " + talalt + ")",
                "GUI kompatibilitás", JOptionPane.WARNING_MESSAGE);
    }
}

    /**
     * Rekurzív komponens-keresés: begyűjti a JButton és JLabel komponenseket.
     */
    private void keresKomponensek(Container kontener, List<JButton> gombLista, List<JLabel> labelLista) {
        for (Component c : kontener.getComponents()) {
            if (c instanceof JButton) {
                gombLista.add((JButton) c);
            } else if (c instanceof JLabel) {
                labelLista.add((JLabel) c);
            } else if (c instanceof Container) {
                keresKomponensek((Container) c, gombLista, labelLista);
            }
        }
    }

    private void initGombokEsemények() {
        for (int i = 0; i < gombok.length; i++) {
            final int index = i;
            final JButton gomb = gombok[i];
            if (gomb == null) continue; // ha nincs, kihagyjuk
            gomb.addActionListener(e -> {
                int sor = index / modell.getMeret();
                int oszlop = index % modell.getMeret();
                try {
                    modell.kattintas(sor, oszlop);
                } catch (AssertionError ae) {
                    // Ha a modell assert-jei megbuktak, informáljuk a fejlesztőt/felhasználót
                    JOptionPane.showMessageDialog(nezet,
                            "Belső hiba: érvénytelen index a modellben (" + sor + "," + oszlop + ")\n" + ae.getMessage(),
                            "Belső hiba", JOptionPane.ERROR_MESSAGE);
                } catch (Throwable t) {
                    JOptionPane.showMessageDialog(nezet,
                            "Hiba történt a kattintás feldolgozásakor: " + t.getMessage(),
                            "Feldolgozási hiba", JOptionPane.ERROR_MESSAGE);
                }
                frissitMegjelenitest();
            });
        }
    }

    /**
     * Menüelemek inicializálása: megkeressük a "Save/Load/New Game/Exit" feliratokat a menüsorban.
     * A menüpontok felirata lehet angol vagy magyar (kis tolerancia).
     */
    private void initMenuEsemények() {
        // megkeressük a menübár menüpontjait és a menüelemeket
        JMenuBar mb = nezet.getJMenuBar();
        if (mb == null) return;
        List<JMenuItem> menuItems = new ArrayList<>();
        for (int i = 0; i < mb.getMenuCount(); i++) {
            JMenu m = mb.getMenu(i);
            if (m == null) continue;
            for (Component c : m.getMenuComponents()) {
                if (c instanceof JMenuItem) {
                    menuItems.add((JMenuItem) c);
                }
            }
        }

        for (JMenuItem mi : menuItems) {
            String text = mi.getText() != null ? mi.getText().toLowerCase() : "";
            if (text.contains("save") || text.contains("ment") || text.contains("save")) {
                mi.addActionListener(e -> ment());
            } else if (text.contains("load") || text.contains("betölt") || text.contains("betolt")) {
                mi.addActionListener(e -> betolt());
            } else if (text.contains("new") || text.contains("új") || text.contains("uj")) {
                mi.addActionListener(e -> {
                    modell.ujJatek();
                    frissitMegjelenitest();
                });
            } else if (text.contains("exit") || text.contains("kilep") || text.contains("kilép")) {
                mi.addActionListener(e -> nezet.dispose());
            }
        }
    }

    /**
     * A képernyő frissítése a modell aktuális állapota szerint.
     */
    private void frissitMegjelenitest() {
        for (int i = 0; i < gombok.length; i++) {
            JButton g = gombok[i];
            if (g == null) continue;
            int sor = i / modell.getMeret();
            int oszlop = i % modell.getMeret();
            boolean allapot = modell.getLampa(sor, oszlop).isAllapot();
            beallitGombSzinet(g, allapot);
        }
        if (lblLepesszam != null) {
            lblLepesszam.setText("Lépésszám: " + modell.getLepesszam());
        }
    }

    /**
     * A gomb megfestése: zöld = on, piros = off (megadott RGB értékek).
     */
    private void beallitGombSzinet(JButton gomb, boolean allapot) {
        if (allapot) {
            gomb.setBackground(new Color(51, 204, 0));
        } else {
            gomb.setBackground(new Color(102, 0, 51));
        }
    }

    /**
     * Mentés fájlba: .java kiterjesztésű (egyszerű szöveg), try/catch és felhasználói értesítés.
     */
    public void ment() {
        String export = modell.exportAllapot();
        try (BufferedWriter bw = Files.newBufferedWriter(mentesiUt)) {
            bw.write("// LightOn mentés - ne szerkessze kézzel, a program olvassa\n");
            bw.write("/* STATE " + export + " */\n");
            bw.flush();
            JOptionPane.showMessageDialog(nezet,
                    "Mentés sikeres: " + mentesiUt.toString(),
                    "Mentés", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(nezet,
                    "Mentés sikertelen: " + ex.getMessage(),
                    "Mentés hiba", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(nezet,
                    "Ismeretlen hiba mentés közben: " + t.getMessage(),
                    "Mentés hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Betöltés: ha nincs mentés, figyelmeztetünk; ha van, megkíséreljük az importálást.
     */
    public void betolt() {
        if (!Files.exists(mentesiUt)) {
            JOptionPane.showMessageDialog(nezet,
                    "Nincs korábbi mentés.",
                    "Betöltés", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            String tartalom = Files.readString(mentesiUt);
            int idx = tartalom.indexOf("/* STATE ");
            if (idx >= 0) {
                int end = tartalom.indexOf("*/", idx);
                if (end > idx) {
                    String inside = tartalom.substring(idx + "/* STATE ".length(), end).trim();
                    modell.importAllapot(inside);
                    frissitMegjelenitest();
                    JOptionPane.showMessageDialog(nezet,
                            "Betöltés sikeres.",
                            "Betöltés", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(nezet,
                    "Érvénytelen mentés fájl.",
                    "Betöltés hiba", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(nezet,
                    "Betöltés sikertelen: " + ex.getMessage(),
                    "Betöltés hiba", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(nezet,
                    "Ismeretlen hiba betöltés közben: " + t.getMessage(),
                    "Betöltés hiba", JOptionPane.ERROR_MESSAGE);
        }
    }
}