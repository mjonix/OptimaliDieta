package optimalidieta;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class ProduktuItraukimas extends JFrame implements MenuListener, ActionListener, KeyListener {

    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    ProduktuItraukimas() {
        super("Įveskite kilokalorijų ir maistinių medžiagų kiekius 100g produkto");
        setSize(1050, 240);
        setLayout(null);
        this.getContentPane().setBackground(new Color(166, 223, 32));

        menuBar = new JMenuBar();
        menuBar.setLocation(213, 0);
        menuBar.setSize(614, 20);

        pagrindinis = new JMenu("Pagrindinis langas");
        produktai = new JMenu("Produktai");
        dietos = new JMenu("Dietos sudarymas");
        kmi = new JMenu("KMI skaičiavimas");
        dietuKalendorius = new JMenu("Dietų kalendorius");
        svorioKitimas = new JMenu("Svorio kitimo istorija");

        iPagrindini = new JMenuItem("Eiti į pagrindinį langą");
        info = new JMenuItem("Peržiūrėti/trinti duomenų bazėje esančius produktus");
        itraukti = new JMenuItem("Įtraukti naują produktą į duomenų bazę");
        nauji = new JMenuItem("Įvedant naujus vartotojo duomenis");
        naudoti = new JMenuItem("Naudoti/trinti anksčiau įvestus duomenis");
        skaiciuotiKMI = new JMenuItem("Skaičiuoti kūno masės indeksą");
        issaugotosDietos = new JMenuItem("Žiūrėti/trinti išsaugotas dietas");
        iKalendoriu = new JMenuItem("Žiūrėti kalendorių");
        svorioIstorija = new JMenuItem("Žiūrėti svorio kitimo istoriją");

        iPagrindini.addActionListener(this);
        info.addActionListener(this);
        itraukti.addActionListener(this);
        nauji.addActionListener(this);
        naudoti.addActionListener(this);
        skaiciuotiKMI.addActionListener(this);
        issaugotosDietos.addActionListener(this);
        iKalendoriu.addActionListener(this);
        svorioIstorija.addActionListener(this);

        pagrindinis.add(iPagrindini);
        produktai.add(info);
        produktai.add(itraukti);
        dietos.add(nauji);
        dietos.add(naudoti);
        dietos.add(issaugotosDietos);
        kmi.add(skaiciuotiKMI);
        dietuKalendorius.add(iKalendoriu);
        svorioKitimas.add(svorioIstorija);

        menuBar.add(pagrindinis);
        menuBar.add(dietos);
        menuBar.add(dietuKalendorius);
        menuBar.add(produktai);
        menuBar.add(svorioKitimas);
        menuBar.add(kmi);

        add(menuBar);

        final JLabel info = new JLabel("");
        info.setLocation(600, 164);
        info.setSize(750, 15);
        add(info);

        final JLabel iveskitePavadinima = new JLabel("Produkto pavadinimas:");
        iveskitePavadinima.setLocation(20, 33);
        iveskitePavadinima.setSize(145, 15);
        add(iveskitePavadinima);

        final JTextField pavadinimas = new JTextField("");
        pavadinimas.setLocation(17, 53);
        pavadinimas.setToolTipText("Produkto pavadinimas");
        pavadinimas.setSize(152, 25);
        add(pavadinimas);

        final JLabel iveskiteKalorijas = new JLabel("Kilokalorijos (kcal):");
        iveskiteKalorijas.setLocation(198, 33);
        iveskiteKalorijas.setSize(130, 15);
        add(iveskiteKalorijas);

        final JTextField kalorijos = new JTextField("");
        kalorijos.setLocation(195, 53);
        kalorijos.setToolTipText("Kilokalorijų skaičius (100g)");
        kalorijos.setSize(130, 25);
        add(kalorijos);

        final JLabel iveskiteBaltymus = new JLabel("Baltymai (g):");
        iveskiteBaltymus.setLocation(361, 33);
        iveskiteBaltymus.setSize(80, 15);
        add(iveskiteBaltymus);

        final JTextField baltymai = new JTextField("");
        baltymai.setLocation(358, 53);
        baltymai.setToolTipText("Baltymų kiekis (100g)");
        baltymai.setSize(80, 25);
        add(baltymai);

        final JLabel iveskiteAngliavandenius = new JLabel("Angliavandeniai (g):");
        iveskiteAngliavandenius.setLocation(474, 33);
        iveskiteAngliavandenius.setSize(128, 15);
        add(iveskiteAngliavandenius);

        final JTextField angliavandeniai = new JTextField("");
        angliavandeniai.setLocation(471, 53);
        angliavandeniai.setToolTipText("Angliavandenių kiekis (100g)");
        angliavandeniai.setSize(128, 25);
        add(angliavandeniai);

        final JLabel iveskiteRiebalus = new JLabel("Riebalai (g):");
        iveskiteRiebalus.setLocation(635, 33);
        iveskiteRiebalus.setSize(77, 15);
        add(iveskiteRiebalus);

        final JTextField riebalai = new JTextField("");
        riebalai.setLocation(632, 53);
        riebalai.setToolTipText("Riebalų kiekis (100g)");
        riebalai.setSize(77, 25);
        add(riebalai);

        final JLabel iveskiteKalci = new JLabel("Kalcis (g):");
        iveskiteKalci.setLocation(750, 33);
        iveskiteKalci.setSize(64, 15);
        add(iveskiteKalci);

        final JTextField kalcis = new JTextField("");
        kalcis.setLocation(747, 53);
        kalcis.setToolTipText("Kalcio kiekis (100g)");
        kalcis.setSize(64, 25);
        add(kalcis);

        final JLabel iveskiteSkaidulines = new JLabel("Skaidulinės medžiagos (g):");
        iveskiteSkaidulines.setLocation(842, 33);
        iveskiteSkaidulines.setSize(172, 15);
        add(iveskiteSkaidulines);

        final JTextField skaidulines = new JTextField("");
        skaidulines.setLocation(840, 53);
        skaidulines.setToolTipText("Skaidulinių medžiagų kiekis (100g)");
        skaidulines.setSize(172, 25);
        add(skaidulines);

        final JLabel iveskiteCholesteroli = new JLabel("Cholesterolio kiekis (g):");
        iveskiteCholesteroli.setLocation(20, 100);
        iveskiteCholesteroli.setSize(154, 15);
        add(iveskiteCholesteroli);

        final JTextField cholesterolis = new JTextField("");
        cholesterolis.setLocation(17, 120);
        cholesterolis.setToolTipText("Cholesterolio kiekis (100g)");
        cholesterolis.setSize(154, 25);
        add(cholesterolis);

        final JLabel iveskitePorcija = new JLabel("Stand. porcija (g arba ml):");
        iveskitePorcija.setLocation(199, 100);
        iveskitePorcija.setSize(165, 15);
        add(iveskitePorcija);

        final JTextField porcija = new JTextField("");
        porcija.setLocation(196, 120);
        porcija.setToolTipText("Standartinė produkto porcija gramais arba mililitrais");
        porcija.setSize(165, 25);
        add(porcija);

        final JLabel nurodykiteArSkystas = new JLabel("Ar produktas yra skystas?");
        nurodykiteArSkystas.setLocation(389, 100);
        nurodykiteArSkystas.setSize(165, 15);
        add(nurodykiteArSkystas);

        final JRadioButton taip = new JRadioButton("Taip");
        taip.setLocation(399, 120);
        taip.setSize(60, 20);
        add(taip);
        taip.setBackground(new Color(166, 223, 32));

        final JRadioButton ne = new JRadioButton("Ne");
        ne.setLocation(494, 120);
        ne.setSize(50, 20);
        add(ne);

        final JRadioButton pusryciai = new JRadioButton("Pusryčiams");
        pusryciai.setLocation(70, 90);
        pusryciai.setSize(130, 20);
        add(pusryciai);

        final JRadioButton pietus = new JRadioButton("Pietums");
        pietus.setLocation(260, 90);
        pietus.setSize(130, 20);
        add(pietus);

        final JRadioButton vakariene = new JRadioButton("Vakarienei");
        vakariene.setLocation(450, 90);
        vakariene.setSize(130, 20);
        add(vakariene);

        final JRadioButton uzkandziai = new JRadioButton("Užkandžiams (priešpiečiams ir pavakariams)");
        uzkandziai.setLocation(640, 90);
        uzkandziai.setSize(320, 20);
        add(uzkandziai);

        final ButtonGroup skystis = new ButtonGroup();
        skystis.add(taip);
        skystis.add(ne);

        final ButtonGroup valgiai = new ButtonGroup();
        valgiai.add(pusryciai);
        valgiai.add(pietus);
        valgiai.add(vakariene);
        valgiai.add(uzkandziai);

        final JLabel nurodykiteKategorija = new JLabel("Paspauskite kategorijai pasirinkti");
        nurodykiteKategorija.setLocation(579, 100);
        nurodykiteKategorija.setSize(210, 15);
        add(nurodykiteKategorija);

        final JButton kategorija = new JButton("Produkto kategorija");
        kategorija.setLocation(609, 120);
        kategorija.setSize(150, 25);
        add(kategorija);

        final JLabel nurodykiteMeta = new JLabel("Paspauskite valgio metui pasirinkti");
        nurodykiteMeta.setLocation(817, 100);
        nurodykiteMeta.setSize(230, 15);
        add(nurodykiteMeta);

        final JButton metas = new JButton("Tinkamas metas valgyti");
        metas.setLocation(842, 120);
        metas.setSize(175, 25);
        add(metas);

        final JCheckBox mesa = new JCheckBox("Mėsa");
        mesa.setLocation(70, 50);
        mesa.setSize(130, 20);
        add(mesa);

        final JCheckBox sokoladas = new JCheckBox("Šokoladas");
        sokoladas.setLocation(305, 50);
        sokoladas.setSize(130, 20);
        add(sokoladas);

        final JCheckBox zuvis = new JCheckBox("Žuvis");
        zuvis.setLocation(540, 50);
        zuvis.setSize(130, 20);
        add(zuvis);

        final JCheckBox riesutai = new JCheckBox("Riešutai");
        riesutai.setLocation(775, 50);
        riesutai.setSize(130, 20);
        add(riesutai);

        final JCheckBox pienas = new JCheckBox("Pieno produktai");
        pienas.setLocation(70, 90);
        pienas.setSize(133, 20);
        add(pienas);

        final JCheckBox medus = new JCheckBox("Medus");
        medus.setLocation(305, 90);
        medus.setSize(130, 20);
        add(medus);

        final JCheckBox kiausiniai = new JCheckBox("Kiaušiniai");
        kiausiniai.setLocation(540, 90);
        kiausiniai.setSize(130, 20);
        add(kiausiniai);

        final JCheckBox miltai = new JCheckBox("Miltų produktai");
        miltai.setLocation(775, 90);
        miltai.setSize(130, 20);
        add(miltai);

        final JCheckBox kruopos = new JCheckBox("Kruopos");
        kruopos.setLocation(70, 130);
        kruopos.setSize(130, 20);
        add(kruopos);

        final JCheckBox uogos = new JCheckBox("Uogos");
        uogos.setLocation(305, 130);
        uogos.setSize(130, 20);
        add(uogos);

        final JCheckBox grybai = new JCheckBox("Grybai");
        grybai.setLocation(540, 130);
        grybai.setSize(130, 20);
        add(grybai);

        final JCheckBox darzoves = new JCheckBox("Daržovės");
        darzoves.setLocation(70, 170);
        darzoves.setSize(130, 20);
        add(darzoves);

        final JCheckBox vaisiai = new JCheckBox("Vaisiai");
        vaisiai.setLocation(305, 170);
        vaisiai.setSize(130, 20);
        add(vaisiai);

        final JButton patvirtintiKategorija = new JButton("Patvirtinti");
        patvirtintiKategorija.setLocation(775, 155);
        patvirtintiKategorija.setSize(90, 25);
        add(patvirtintiKategorija);

        final JButton patvirtintiLaika = new JButton("Patvirtinti");
        patvirtintiLaika.setLocation(455, 135);
        patvirtintiLaika.setSize(90, 25);
        add(patvirtintiLaika);

        final JButton patvirtinti = new JButton("Įtraukti į duomenų bazę");
        patvirtinti.setLocation(412, 160);
        patvirtinti.setSize(176, 25);
        add(patvirtinti);

        mesa.setVisible(false);
        sokoladas.setVisible(false);
        riesutai.setVisible(false);
        zuvis.setVisible(false);
        pienas.setVisible(false);
        medus.setVisible(false);
        kiausiniai.setVisible(false);
        miltai.setVisible(false);
        kruopos.setVisible(false);
        uogos.setVisible(false);
        grybai.setVisible(false);
        darzoves.setVisible(false);
        vaisiai.setVisible(false);
        patvirtintiKategorija.setVisible(false);
        pusryciai.setVisible(false);
        uzkandziai.setVisible(false);
        pietus.setVisible(false);
        vakariene.setVisible(false);
        patvirtintiLaika.setVisible(false);

        kategorija.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mesa.setVisible(true);
                sokoladas.setVisible(true);
                riesutai.setVisible(true);
                zuvis.setVisible(true);
                pienas.setVisible(true);
                medus.setVisible(true);
                kiausiniai.setVisible(true);
                miltai.setVisible(true);
                kruopos.setVisible(true);
                uogos.setVisible(true);
                grybai.setVisible(true);
                darzoves.setVisible(true);
                vaisiai.setVisible(true);
                patvirtintiKategorija.setVisible(true);
                nurodykiteMeta.setVisible(false);
                iveskitePavadinima.setVisible(false);
                iveskiteKalorijas.setVisible(false);
                pavadinimas.setVisible(false);
                kalorijos.setVisible(false);
                iveskiteBaltymus.setVisible(false);
                baltymai.setVisible(false);
                iveskiteAngliavandenius.setVisible(false);
                angliavandeniai.setVisible(false);
                iveskiteRiebalus.setVisible(false);
                riebalai.setVisible(false);
                iveskiteKalci.setVisible(false);
                kalcis.setVisible(false);
                metas.setVisible(false);
                iveskiteSkaidulines.setVisible(false);
                skaidulines.setVisible(false);
                iveskiteCholesteroli.setVisible(false);
                cholesterolis.setVisible(false);
                iveskitePorcija.setVisible(false);
                porcija.setVisible(false);
                nurodykiteArSkystas.setVisible(false);
                taip.setVisible(false);
                ne.setVisible(false);
                nurodykiteKategorija.setVisible(false);
                patvirtinti.setVisible(false);
                kategorija.setVisible(false);
                info.setText("Prašome pasirinkti BENT vieną kategoriją");
                info.setLocation(600, 184);
            }
        });

        patvirtintiKategorija.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mesa.setVisible(false);
                sokoladas.setVisible(false);
                riesutai.setVisible(false);
                zuvis.setVisible(false);
                pienas.setVisible(false);
                medus.setVisible(false);
                kiausiniai.setVisible(false);
                miltai.setVisible(false);
                kruopos.setVisible(false);
                uogos.setVisible(false);
                grybai.setVisible(false);
                darzoves.setVisible(false);
                vaisiai.setVisible(false);
                patvirtintiKategorija.setVisible(false);
                nurodykiteKategorija.setVisible(true);
                iveskitePavadinima.setVisible(true);
                iveskiteKalorijas.setVisible(true);
                patvirtinti.setVisible(true);
                pavadinimas.setVisible(true);
                kalorijos.setVisible(true);
                iveskiteBaltymus.setVisible(true);
                baltymai.setVisible(true);
                iveskiteAngliavandenius.setVisible(true);
                angliavandeniai.setVisible(true);
                iveskiteRiebalus.setVisible(true);
                riebalai.setVisible(true);
                iveskiteKalci.setVisible(true);
                kalcis.setVisible(true);
                skaidulines.setVisible(true);
                iveskiteSkaidulines.setVisible(true);
                iveskiteCholesteroli.setVisible(true);
                cholesterolis.setVisible(true);
                iveskitePorcija.setVisible(true);
                porcija.setVisible(true);
                kategorija.setVisible(true);
                metas.setVisible(true);
                nurodykiteArSkystas.setVisible(true);
                taip.setVisible(true);
                ne.setVisible(true);
                nurodykiteMeta.setVisible(true);
                info.setText("");
                info.setLocation(600, 164);
            }
        });

        metas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pusryciai.setVisible(true);
                uzkandziai.setVisible(true);
                pietus.setVisible(true);
                vakariene.setVisible(true);
                patvirtintiLaika.setVisible(true);
                metas.setVisible(false);
                nurodykiteMeta.setVisible(false);
                iveskitePavadinima.setVisible(false);
                iveskiteKalorijas.setVisible(false);
                pavadinimas.setVisible(false);
                kalorijos.setVisible(false);
                iveskiteBaltymus.setVisible(false);
                baltymai.setVisible(false);
                iveskiteAngliavandenius.setVisible(false);
                angliavandeniai.setVisible(false);
                iveskiteRiebalus.setVisible(false);
                riebalai.setVisible(false);
                iveskiteKalci.setVisible(false);
                kalcis.setVisible(false);
                iveskiteSkaidulines.setVisible(false);
                skaidulines.setVisible(false);
                iveskiteCholesteroli.setVisible(false);
                cholesterolis.setVisible(false);
                iveskitePorcija.setVisible(false);
                porcija.setVisible(false);
                nurodykiteArSkystas.setVisible(false);
                taip.setVisible(false);
                ne.setVisible(false);
                nurodykiteKategorija.setVisible(false);
                kategorija.setVisible(false);
                patvirtinti.setVisible(false);
                info.setLocation(150, 44);
                info.setText("Prašome pasirinkti valgį, kurio metu šis produktas yra tinkamiausias (bus atsižvelgiama sudarant dietą automatiškai).");
            }
        });

        patvirtintiLaika.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pusryciai.setVisible(false);
                uzkandziai.setVisible(false);
                patvirtintiLaika.setVisible(false);
                pietus.setVisible(false);
                vakariene.setVisible(false);
                metas.setVisible(true);
                iveskitePavadinima.setVisible(true);
                iveskiteKalorijas.setVisible(true);
                patvirtinti.setVisible(true);
                pavadinimas.setVisible(true);
                kalorijos.setVisible(true);
                iveskiteBaltymus.setVisible(true);
                baltymai.setVisible(true);
                iveskiteAngliavandenius.setVisible(true);
                angliavandeniai.setVisible(true);
                iveskiteRiebalus.setVisible(true);
                riebalai.setVisible(true);
                iveskiteKalci.setVisible(true);
                kalcis.setVisible(true);
                skaidulines.setVisible(true);
                iveskiteSkaidulines.setVisible(true);
                iveskiteCholesteroli.setVisible(true);
                cholesterolis.setVisible(true);
                iveskitePorcija.setVisible(true);
                porcija.setVisible(true);
                kategorija.setVisible(true);
                nurodykiteMeta.setVisible(true);
                nurodykiteArSkystas.setVisible(true);
                taip.setVisible(true);
                ne.setVisible(true);
                nurodykiteKategorija.setVisible(true);
                info.setLocation(600, 164);
                info.setText("");
            }
        });

        patvirtinti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    try {
                        if (pavadinimas.getText().equals("")) {
                            info.setText("Nenurodytas produkto pavadinimas!");
                            break;
                        }
                        if (!pavadinimas.getText().matches("[a-zA-Z0-9ąčęėįšųūžĄČĘĖĮŠŲŪŽ ]*")
                                || pavadinimas.getText().split(" ").length == 0) {
                            info.setText("Prašome pavadinime naudoti tik raides ir skaičius.");
                            break;
                        }
                        if (pavadinimas.getText().length() > 55) {
                            info.setText("Per ilgas pavadinimas!");
                            break;
                        }
                        Class.forName("org.sqlite.JDBC");
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        Statement state = conn.createStatement();
                        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
                        boolean leidimas = true;
                        while (rs.next()) {
                            if (rs.getString("pavadinimas").toLowerCase().equals(pavadinimas.getText().toLowerCase())) {
                                leidimas = false;
                            }
                        }
                        if (!leidimas) {
                            info.setText("Produktas tokiu pavadinimu jau egzistuoja!");
                            conn.close();
                            break;
                        }
                        try {
                            if (Integer.parseInt(kalorijos.getText()) <= 0) {
                                info.setText("Įveskite kilokalorijų kiekį kaip natūralųjį skaičių!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Įveskite kilokalorijų kiekį kaip natūralųjį skaičių!");
                            conn.close();
                            break;
                        }

                        baltymai.setText(baltymai.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(baltymai.getText()) > 100 || Double.parseDouble(baltymai.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą baltymų dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą baltymų dalį produkte!");
                            conn.close();
                            break;
                        }

                        angliavandeniai.setText(angliavandeniai.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(angliavandeniai.getText()) > 100 || Double.parseDouble(baltymai.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą angliavandenių dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą angliavandenių dalį produkte!");
                            conn.close();
                            break;
                        }

                        riebalai.setText(riebalai.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(riebalai.getText()) > 100 || Double.parseDouble(riebalai.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą riebalų dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą riebalų dalį produkte!");
                            conn.close();
                            break;
                        }

                        kalcis.setText(kalcis.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(kalcis.getText()) > 100 || Double.parseDouble(kalcis.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą kalcio dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą kalcio dalį produkte!");
                            conn.close();
                            break;
                        }

                        skaidulines.setText(skaidulines.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(skaidulines.getText()) > 100 || Double.parseDouble(skaidulines.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą skaidulinių medžiagų dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą skaidulinių medžiagų dalį produkte!");
                            conn.close();
                            break;
                        }

                        cholesterolis.setText(cholesterolis.getText().replace(",", "."));

                        try {
                            if (Double.parseDouble(cholesterolis.getText()) > 100 || Double.parseDouble(cholesterolis.getText()) < 0) {
                                info.setText("Prašome nurodyti teisingą cholesterolio dalį produkte!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Prašome nurodyti teisingą cholesterolio dalį produkte!");
                            conn.close();
                            break;
                        }
                        try {
                            if (Integer.parseInt(porcija.getText()) <= 0) {
                                info.setText("Nurodykite standartinę produkto porciją kaip natūralųjį skaičių!");
                                conn.close();
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            info.setText("Nurodykite standartinę produkto porciją kaip natūralųjį skaičių!");
                            conn.close();
                            break;
                        }
                        if (!taip.isSelected() && !ne.isSelected()) {
                            info.setText("Nurodykite, ar produktas yra skystas!");
                            conn.close();
                            break;
                        }
                        if (!mesa.isSelected() && !sokoladas.isSelected() && !zuvis.isSelected() && !riesutai.isSelected()
                                && !pienas.isSelected() && !medus.isSelected() && !kiausiniai.isSelected() && !miltai.isSelected()
                                && !kruopos.isSelected() && !uogos.isSelected() && !grybai.isSelected() && !darzoves.isSelected()
                                && !vaisiai.isSelected()) {
                            info.setText("Priskirkite produktą bent vienai kategorijai!");
                            conn.close();
                            break;
                        }

                        String produktoKategorija = "";

                        if (kruopos.isSelected()) {
                            produktoKategorija += "kruopos";
                        }

                        if (sokoladas.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "šokoladas";
                            } else {
                                produktoKategorija += "+šokoladas";
                            }
                        }

                        if (zuvis.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "žuvis";
                            } else {
                                produktoKategorija += "+žuvis";
                            }
                        }

                        if (riesutai.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "riešutai";
                            } else {
                                produktoKategorija += "+riešutai";
                            }
                        }

                        if (darzoves.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "daržovės";
                            } else {
                                produktoKategorija += "+daržovės";
                            }
                        }

                        if (pienas.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "pienas";
                            } else {
                                produktoKategorija += "+pienas";
                            }
                        }

                        if (medus.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "medus";
                            } else {
                                produktoKategorija += "+medus";
                            }
                        }

                        if (kiausiniai.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "kiaušiniai";
                            } else {
                                produktoKategorija += "+kiaušiniai";
                            }
                        }

                        if (mesa.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "mėsa";
                            } else {
                                produktoKategorija += "+mėsa";
                            }
                        }

                        if (miltai.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "miltai";
                            } else {
                                produktoKategorija += "+miltai";
                            }
                        }

                        if (uogos.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "uogos";
                            } else {
                                produktoKategorija += "+uogos";
                            }
                        }

                        if (grybai.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "grybai";
                            } else {
                                produktoKategorija += "+grybai";
                            }
                        }

                        if (vaisiai.isSelected()) {
                            if (produktoKategorija.equals("")) {
                                produktoKategorija += "vaisiai";
                            } else {
                                produktoKategorija += "+vaisiai";
                            }
                        }

                        if (!pusryciai.isSelected() && !pietus.isSelected() && !vakariene.isSelected()
                                && !uzkandziai.isSelected()) {
                            info.setText("Priskirkite produktą vienam iš valgių!");
                            conn.close();
                            break;
                        }

                        int valgis = 0;
                        if (pusryciai.isSelected()) {
                            valgis = 1;
                        }
                        if (pietus.isSelected()) {
                            valgis = 2;
                        }
                        if (vakariene.isSelected()) {
                            valgis = 3;
                        }

                        pavadinimas.setText(pavadinimas.getText().replace("\n", " "));
                        PreparedStatement prep = conn.prepareStatement("insert into MAISTO_PRODUKTAI values(?,?,?,?,?,?,?,?,?,?,?,?);");
                        prep.setString(1, pavadinimas.getText());
                        prep.setInt(2, Integer.parseInt(kalorijos.getText()));
                        prep.setDouble(3, Double.parseDouble(baltymai.getText()) * 1000000);
                        prep.setDouble(4, Double.parseDouble(angliavandeniai.getText()) * 1000000);
                        prep.setDouble(5, Double.parseDouble(riebalai.getText()) * 1000000);
                        prep.setDouble(6, Double.parseDouble(kalcis.getText()) * 1000000);
                        prep.setDouble(7, Double.parseDouble(skaidulines.getText()) * 1000000);
                        prep.setDouble(8, Double.parseDouble(cholesterolis.getText()) * 1000000);
                        prep.setString(9, produktoKategorija);
                        prep.setInt(10, Integer.parseInt(porcija.getText()));
                        if (taip.isSelected()) {
                            prep.setBoolean(11, true);
                        } else {
                            prep.setBoolean(11, false);
                        }
                        prep.setInt(12, valgis);
                        prep.execute();
                        conn.close();
                        info.setText("Produktas sėkmingai įtrauktas į duomenų bazę!");
                        pavadinimas.setText("");
                        kalorijos.setText("");
                        baltymai.setText("");
                        angliavandeniai.setText("");
                        riebalai.setText("");
                        kalcis.setText("");
                        skaidulines.setText("");
                        cholesterolis.setText("");
                        porcija.setText("");

                        mesa.setSelected(false);
                        sokoladas.setSelected(false);
                        zuvis.setSelected(false);
                        riesutai.setSelected(false);
                        pienas.setSelected(false);
                        medus.setSelected(false);
                        kiausiniai.setSelected(false);
                        miltai.setSelected(false);
                        kruopos.setSelected(false);
                        uogos.setSelected(false);
                        grybai.setSelected(false);
                        darzoves.setSelected(false);
                        vaisiai.setSelected(false);
                        pusryciai.setSelected(false);
                        uzkandziai.setSelected(false);
                        pietus.setSelected(false);
                        vakariene.setSelected(false);
                        skystis.clearSelection();
                        valgiai.clearSelection();
                        break;
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(ProduktuItraukimas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(iPagrindini)) {
            try {
                pagrindinis();
            } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(info)) {
            try {
                info();
            } catch (SQLException ex) {
                Logger.getLogger(ProduktuInformacija.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(itraukti)) {
            try {
                itraukti();
            } catch (SQLException ex) {
                Logger.getLogger(ProduktuItraukimas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(nauji)) {
            try {
                nauji();
            } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(naudoti)) {
            try {
                naudoti();
            } catch (SQLException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(skaiciuotiKMI)) {
            try {
                kmi();
            } catch (SQLException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(issaugotosDietos)) {
            try {
                issaugotos();
            } catch (SQLException ex) {
                Logger.getLogger(DietuInformacija.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(iKalendoriu)) {
            try {
                kalendorius();
            } catch (SQLException ex) {
                Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(svorioIstorija)) {
            try {
                svoris();
            } catch (SQLException ex) {
                Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void menuSelected(MenuEvent e) {
    }

    @Override
    public void menuDeselected(MenuEvent e) {
    }

    @Override
    public void menuCanceled(MenuEvent e) {
    }

    private void itraukti() throws SQLException {
        new ProduktuItraukimas();
        dispose();
    }

    private void info() throws SQLException, ClassNotFoundException {
        new ProduktuInformacija(true, new ArrayList<String>());
        dispose();
    }

    private void pagrindinis() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        new OptimaliDieta(0);
        dispose();
    }

    private void nauji() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        new OptimaliDieta(1);
        dispose();
    }

    private void naudoti() throws SQLException, ClassNotFoundException, InstantiationException, UnsupportedLookAndFeelException, IllegalAccessException {
        new OptimaliDieta(2);
        dispose();
    }

    private void kmi() throws SQLException, ClassNotFoundException, InstantiationException, UnsupportedLookAndFeelException, IllegalAccessException {
        new OptimaliDieta(3);
        dispose();
    }

    private void issaugotos() throws SQLException {
        new DietuInformacija();
        dispose();
    }

    private void kalendorius() throws SQLException, ClassNotFoundException {
        new Kalendorius("");
        dispose();
    }

    private void svoris() throws SQLException, ClassNotFoundException {
        new SvorioIstorija();
        dispose();
    }
}