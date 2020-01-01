package optimalidieta;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Kalendorius extends JFrame implements MenuListener, ActionListener, KeyListener {

    private int eilute, stulpelis;
    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    private DefaultTableModel modelis;
    private JTable lentele;
    private Calendar kalendorius = new GregorianCalendar();
    private JLabel informacija;
    private JScrollPane slankjuoste;

    private boolean jauItraukta = false, dienosDieta = true;

    Kalendorius(final String itraukiamaDieta) throws ClassNotFoundException, SQLException {
        super("Dietų kalendorius");
        setSize(750, 330);
        setLayout(null);

        menuBar = new JMenuBar();
        menuBar.setLocation(63, 0);
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

        informacija = new JLabel("");
        informacija.setLocation(320, 40);
        informacija.setSize(150, 22);
        add(informacija);

        final JTextArea dietosPlanas = new JTextArea("");
        dietosPlanas.setEditable(false);
        dietosPlanas.setBackground(new Color(166, 223, 32));

        final JScrollPane dietosAtvaizdavimas = new JScrollPane(dietosPlanas);
        dietosAtvaizdavimas.setLocation(55, 30);
        dietosAtvaizdavimas.setSize(635, 490);
        add(dietosAtvaizdavimas);

        final JLabel pasirinkta = new JLabel("Pasirinkite dieną");
        pasirinkta.setLocation(160, 220);
        pasirinkta.setSize(570, 22);
        add(pasirinkta);

        final JLabel klaida = new JLabel("");
        klaida.setLocation(220, 195);
        klaida.setSize(350, 22);
        add(klaida);

        final JButton iKalendoriu = new JButton("Atgal");
        iKalendoriu.setLocation(55, 550);
        iKalendoriu.setSize(100, 25);
        add(iKalendoriu);

        final JButton issaugoti = new JButton("Įtraukti į išsaugotų dietų sąrašą");
        issaugoti.setLocation(470, 520);
        issaugoti.setSize(220, 25);
        add(issaugoti);

        final JButton trinti = new JButton("Atlaisvinti šią dieną");
        trinti.setLocation(535, 217);
        trinti.setSize(150, 25);
        add(trinti);

        final JButton trintiMenesi = new JButton("Atlaisvinti visą mėnesį");
        trintiMenesi.setLocation(55, 193);
        trintiMenesi.setSize(160, 25);
        add(trintiMenesi);

        final JComboBox id = new JComboBox(ids());
        id.setLocation(510, 242);
        id.setSize(100, 20);
        add(id);

        final JComboBox idZiurejimui = new JComboBox(ids());
        idZiurejimui.setLocation(455, 550);
        idZiurejimui.setSize(100, 20);
        add(idZiurejimui);

        final JRadioButton taip = new JRadioButton("Taip");
        taip.setSize(60, 20);
        add(taip);

        final JRadioButton ne = new JRadioButton("Ne");
        ne.setSize(50, 20);
        add(ne);

        final ButtonGroup pasirinkimas = new ButtonGroup();
        pasirinkimas.add(taip);
        pasirinkimas.add(ne);

        final JButton dieta = new JButton("Žiūrėti dietą");
        dieta.setLocation(430, 217);
        dieta.setSize(100, 25);
        add(dieta);

        final JButton itrauktiDieta = new JButton("Priskirti dietą šiai dienai");
        itrauktiDieta.setLocation(430, 220);
        itrauktiDieta.setSize(170, 25);
        add(itrauktiDieta);

        final JButton patvirtinti = new JButton("Patvirtinti");
        patvirtinti.setSize(100, 25);
        add(patvirtinti);

        final JButton patvirtintiMenesi = new JButton("Patvirtinti");
        patvirtintiMenesi.setSize(100, 25);
        add(patvirtintiMenesi);

        final JButton patvirtintiItraukima = new JButton("Patvirtinti");
        patvirtintiItraukima.setLocation(405, 242);
        patvirtintiItraukima.setSize(100, 25);
        add(patvirtintiItraukima);

        final JButton issaugotosDietos = new JButton("Žiūrėti išsaugotas dietas");
        issaugotosDietos.setLocation(130, 242);
        issaugotosDietos.setSize(180, 25);
        add(issaugotosDietos);

        iKalendoriu.setVisible(false);
        trinti.setVisible(false);
        dieta.setVisible(false);
        itrauktiDieta.setVisible(false);
        taip.setVisible(false);
        ne.setVisible(false);
        patvirtinti.setVisible(false);
        patvirtintiMenesi.setVisible(false);
        patvirtintiItraukima.setVisible(false);
        issaugotosDietos.setVisible(false);
        id.setVisible(false);
        idZiurejimui.setVisible(false);
        issaugoti.setVisible(false);

        String[] stulpeliai = {"Se", "Pr", "An", "Tr", "Kt", "Pn", "Št"};
        modelis = new DefaultTableModel(null, stulpeliai) {
            @Override
            public boolean isCellEditable(int eilute, int stulpelis) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        lentele = new JTable(modelis);
        lentele.getTableHeader().setReorderingAllowed(false);
        lentele.setCellSelectionEnabled(true);
        lentele.setRowSelectionAllowed(false);
        lentele.setColumnSelectionAllowed(false);

        if (!itraukiamaDieta.equals("")) {
            pasirinkta.setText("<html><font size='5' color='red'>Parinkite pradinę dieną dietai.</font></html>");
            if (itraukiamaDieta.split("-------------------------------------------------------------------------------------------------------------------------------------------------------").length == 1) {
                pasirinkta.setText("<html><font size='5' color='red'>Parinkite dieną dietai.</font></html>");
            }
        }

        lentele.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                klaida.setText("");
                eilute = lentele.rowAtPoint(me.getPoint());
                stulpelis = lentele.columnAtPoint(me.getPoint());
                String[] dienos = itraukiamaDieta.split("-------------------------------------------------------------------------------------------------------------------------------------------------------");

                try {
                    if (!itraukiamaDieta.equals("") && !jauItraukta
                            && !lentele.getValueAt(eilute, stulpelis).toString().contains("null")) {
                        try {
                            Class.forName("org.sqlite.JDBC");
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            Statement state = conn.createStatement();
                            ResultSet rs = state.executeQuery("select * from KALENDORIUS");
                            boolean leidimas = true;
                            int x = stulpelis, y = eilute;
                            boolean atnaujintas = false;

                            if (dienos.length == 1) {
                                PreparedStatement prep = conn.prepareStatement("insert into KALENDORIUS values(?,?);");
                                prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                        + (kalendorius.get(Calendar.MONTH) + 1)
                                        + "-" + lentele.getValueAt(eilute, stulpelis++));
                                prep.setString(2, itraukiamaDieta);
                                prep.execute();
                                pasirinkta.setText("Dieta sėkmingai priskirta dienai.");
                                jauItraukta = true;
                            } else {
                                while (rs.next()) {
                                    for (int i = 0; i < dienos.length - 1; i++) {
                                        if (stulpelis == 7 && eilute + 1 == lentele.getRowCount()) {
                                            eilute = 0;
                                            stulpelis = 0;
                                            kalendorius.add(Calendar.MONTH, +1);
                                            atnaujintiMenesi();
                                            atnaujintas = true;
                                        }
                                        if (stulpelis == 7) {
                                            eilute++;
                                            stulpelis = 0;
                                        }
                                        if ((lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                                            eilute = 0;
                                            kalendorius.add(Calendar.MONTH, +1);
                                            atnaujintiMenesi();
                                            atnaujintas = true;
                                        }
                                        if (rs.getString("data").equals(kalendorius.get(Calendar.YEAR) + "-"
                                                + (kalendorius.get(Calendar.MONTH) + 1)
                                                + "-" + lentele.getValueAt(eilute, stulpelis++))) {
                                            leidimas = false;
                                        }
                                    }
                                    stulpelis = x;
                                    eilute = y;
                                    if (atnaujintas) {
                                        atnaujintas = false;
                                        kalendorius.add(Calendar.MONTH, -1);
                                        atnaujintiMenesi();
                                    }
                                }

                                if (leidimas) {
                                    if (atnaujintas) {
                                        kalendorius.add(Calendar.MONTH, -1);
                                        atnaujintiMenesi();
                                    }

                                    stulpelis = x;
                                    eilute = y;
                                    for (int i = 0; i < dienos.length - 1; i++) {
                                        if (stulpelis == 7 && eilute + 1 == lentele.getRowCount()) {
                                            eilute = 0;
                                            stulpelis = 0;
                                            kalendorius.add(Calendar.MONTH, +1);
                                            atnaujintiMenesi();
                                            atnaujintas = true;
                                        }
                                        if (stulpelis == 7) {
                                            eilute++;
                                            stulpelis = 0;
                                        }
                                        if ((lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                                            eilute = 0;
                                            kalendorius.add(Calendar.MONTH, +1);
                                            atnaujintiMenesi();
                                        }
                                        PreparedStatement prep = conn.prepareStatement("insert into KALENDORIUS values(?,?);");
                                        prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                                + (kalendorius.get(Calendar.MONTH) + 1)
                                                + "-" + lentele.getValueAt(eilute, stulpelis++));
                                        prep.setString(2, dienos[0] + dienos[i + 1].substring(16));
                                        prep.execute();
                                    }
                                    pasirinkta.setText("Dieta sėkmingai priskirta pasirinktoms dienoms.");
                                    jauItraukta = true;
                                } else {
                                    pasirinkta.setText("Bent viena iš dienų šiam dietos planui jau užimta!");
                                }
                            }
                            conn.close();
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (me.getClickCount() == 1 && !(lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                        try {
                            Class.forName("org.sqlite.JDBC");
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            Statement state = conn.createStatement();
                            ResultSet rs = state.executeQuery("select * from KALENDORIUS");
                            boolean leidimas = false;
                            while (rs.next()) {
                                if (rs.getString("data").equals(kalendorius.get(Calendar.YEAR) + "-"
                                        + (kalendorius.get(Calendar.MONTH) + 1)
                                        + "-" + lentele.getValueAt(eilute, stulpelis))) {
                                    leidimas = true;
                                }
                            }

                            dieta.setVisible(leidimas);
                            trinti.setVisible(leidimas);
                            if (!leidimas) {
                                itrauktiDieta.setVisible(true);
                            } else {
                                itrauktiDieta.setVisible(false);
                            }
                            pasirinkta.setLocation(160, 220);
                            taip.setVisible(false);
                            ne.setVisible(false);
                            patvirtinti.setVisible(false);
                            patvirtintiMenesi.setVisible(false);
                            patvirtintiItraukima.setVisible(false);
                            id.setVisible(false);
                            issaugotosDietos.setVisible(false);
                            pasirinkimas.clearSelection();
                            pasirinkta.setText("Pasirinkta diena: " + informacija.getText().substring(21, informacija.getText()
                                    .length() - 14) + ", " + lentele.getValueAt(eilute, stulpelis) + "d.");
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (NullPointerException ex) {
                }
            }
        });

        slankjuoste = new JScrollPane(lentele);
        slankjuoste.setLocation(0, 70);
        add(slankjuoste);

        final JButton atgal = new JButton("<----");
        atgal.setLocation(135, 50);
        atgal.setSize(70, 25);
        add(atgal);

        final JButton pirmyn = new JButton("---->");
        pirmyn.setLocation(555, 50);
        pirmyn.setSize(70, 25);
        add(pirmyn);

        dietosAtvaizdavimas.setVisible(false);

        atgal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taip.setVisible(false);
                ne.setVisible(false);
                patvirtintiMenesi.setVisible(false);
                patvirtintiItraukima.setVisible(false);
                id.setVisible(false);
                itrauktiDieta.setVisible(false);
                issaugotosDietos.setVisible(false);
                dieta.setVisible(false);
                trinti.setVisible(false);
                pasirinkta.setLocation(160, 220);
                pasirinkta.setText("Pasirinkite dieną");
                klaida.setText("");
                pasirinkimas.clearSelection();
                kalendorius.add(Calendar.MONTH, -1);
                atnaujintiMenesi();
            }
        });

        pirmyn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taip.setVisible(false);
                ne.setVisible(false);
                patvirtintiMenesi.setVisible(false);
                patvirtintiItraukima.setVisible(false);
                id.setVisible(false);
                itrauktiDieta.setVisible(false);
                issaugotosDietos.setVisible(false);
                dieta.setVisible(false);
                trinti.setVisible(false);
                pasirinkta.setLocation(160, 220);
                pasirinkta.setText("Pasirinkite dieną");
                klaida.setText("");
                pasirinkimas.clearSelection();
                kalendorius.add(Calendar.MONTH, +1);
                atnaujintiMenesi();
            }
        });

        issaugoti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();
                    ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");

                    int i = 1;
                    boolean leidimas = true;
                    while (rs.next()) {
                        i++;
                        if (rs.getString("dieta").equals(dietosPlanas.getText().substring(dietosPlanas.getText().indexOf(" Lytis")))) {
                            leidimas = false;
                        }
                    }

                    if (leidimas) {
                        PreparedStatement prep = conn.prepareStatement("insert into ISSAUGOTOS_DIETOS values(?,?);");
                        prep.setInt(1, i);
                        prep.setString(2, dietosPlanas.getText().substring(dietosPlanas.getText().indexOf(" Lytis")));
                        prep.execute();
                        pasirinkta.setText("Dieta išsaugota sėkmingai.");
                        pasirinkta.setLocation(375, 570);
                    } else {
                        pasirinkta.setText("Lygiai tokia pati dieta jau egzistuoja duomenų bazėje.");
                        pasirinkta.setLocation(305, 570);
                    }
                    id.setModel(new DefaultComboBoxModel(ids()));
                    taip.setVisible(false);
                    ne.setVisible(false);
                    patvirtinti.setVisible(false);
                    trinti.setVisible(true);
                    pasirinkimas.clearSelection();
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        dieta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dienosDieta = true;
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();

                    setSize(750, 640);
                    trinti.setLocation(175, 550);
                    slankjuoste.setVisible(false);
                    informacija.setVisible(false);
                    dieta.setVisible(false);
                    atgal.setVisible(false);
                    pirmyn.setVisible(false);
                    iKalendoriu.setVisible(true);
                    issaugoti.setVisible(true);
                    trinti.setVisible(true);
                    dietosAtvaizdavimas.setVisible(true);

                    dietosPlanas.setText(" " + informacija.getText().substring(21, informacija.getText()
                            .length() - 14) + ", " + lentele.getValueAt(eilute, stulpelis) + "d." + "\n\n" + state
                            .executeQuery("select dieta from KALENDORIUS where data ='"
                                    + kalendorius.get(Calendar.YEAR) + "-"
                                    + (kalendorius.get(Calendar.MONTH) + 1)
                                    + "-" + lentele.getValueAt(eilute, stulpelis) + "'").getString("dieta"));
                    dietosPlanas.select(0, 0);
                    conn.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        iKalendoriu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSize(750, 330);
                trinti.setLocation(535, 217);
                iKalendoriu.setVisible(false);
                issaugoti.setVisible(false);
                dietosAtvaizdavimas.setVisible(false);
                taip.setVisible(false);
                ne.setVisible(false);
                patvirtinti.setVisible(false);
                idZiurejimui.setVisible(false);
                idZiurejimui.setSelectedIndex(0);
                slankjuoste.setVisible(true);
                informacija.setVisible(true);
                if (dienosDieta) {
                    pasirinkta.setLocation(160, 220);
                    pasirinkta.setText("Pasirinkta diena: " + informacija.getText().substring(21, informacija.getText()
                            .length() - 14) + ", " + lentele.getValueAt(eilute, stulpelis) + "d.");
                    trinti.setVisible(true);
                    dieta.setVisible(true);
                } else {
                    pasirinkta.setLocation(95, 220);
                    String menesis = (kalendorius.get(Calendar.MONTH) + 1) + "";
                    String diena = lentele.getValueAt(eilute, stulpelis) + "";
                    if (menesis.length() == 1) {
                        menesis = "0" + menesis;
                    }
                    if (diena.length() == 1) {
                        diena = "0" + diena;
                    }
                    pasirinkta.setText("Pirmoji (arba vienintelė) diena dietai: " + kalendorius.get(Calendar.YEAR) + "-"
                            + menesis + "-" + diena + ". Priskiriamos dietos numeris:");
                    issaugotosDietos.setVisible(true);
                    id.setVisible(true);
                    patvirtintiItraukima.setVisible(true);
                }
                atgal.setVisible(true);
                pirmyn.setVisible(true);
                pasirinkimas.clearSelection();
            }
        });

        trinti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trinti.setVisible(false);
                dieta.setVisible(false);
                taip.setVisible(true);
                ne.setVisible(true);
                patvirtinti.setVisible(true);
                pasirinkta.setLocation(205, trinti.getLocation().y - 5);
                taip.setLocation(295, trinti.getLocation().y + 15);
                ne.setLocation(355, trinti.getLocation().y + 15);
                patvirtinti.setLocation(415, trinti.getLocation().y + 15);
                pasirinkta.setText("Ar tikrai norite ištrinti šią dietą iš kalendoriaus?");
            }
        });

        itrauktiDieta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!lentele.getValueAt(eilute, stulpelis).toString().equals("null")) {
                        itrauktiDieta.setVisible(false);
                        if (ids().length > 1) {
                            id.setVisible(true);
                            patvirtintiItraukima.setVisible(true);
                            issaugotosDietos.setVisible(true);
                            pasirinkta.setLocation(95, 220);
                            String menesis = (kalendorius.get(Calendar.MONTH) + 1) + "";
                            String diena = lentele.getValueAt(eilute, stulpelis) + "";
                            if (menesis.length() == 1) {
                                menesis = "0" + menesis;
                            }
                            if (diena.length() == 1) {
                                diena = "0" + diena;
                            }
                            pasirinkta.setText("Pirmoji (arba vienintelė) diena dietai: " + kalendorius.get(Calendar.YEAR) + "-"
                                    + menesis + "-" + diena + ". Priskiriamos dietos numeris:");
                        } else {
                            pasirinkta.setText("Šiuo metu neturite išsaugotų dietų pridėjimui.");
                        }

                    }
                } catch (SQLException | NullPointerException ex) {
                    id.setVisible(false);
                    patvirtintiItraukima.setVisible(false);
                    issaugotosDietos.setVisible(false);
                    pasirinkta.setText("Pasirinkite dieną");
                    pasirinkta.setLocation(160, 220);
                    itrauktiDieta.setVisible(false);
                }
            }
        });

        trintiMenesi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trinti.setVisible(false);
                dieta.setVisible(false);
                patvirtintiItraukima.setVisible(false);
                id.setVisible(false);
                itrauktiDieta.setVisible(false);
                issaugotosDietos.setVisible(false);
                taip.setVisible(true);
                ne.setVisible(true);
                patvirtintiMenesi.setVisible(true);
                pasirinkta.setLocation(205, trinti.getLocation().y - 5);
                taip.setLocation(295, trinti.getLocation().y + 15);
                ne.setLocation(355, trinti.getLocation().y + 15);
                patvirtintiMenesi.setLocation(415, 230);
                pasirinkta.setText("Ar tikrai norite atlaisvinti visas šio mėnesio dienas?");
            }
        });

        patvirtinti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taip.isSelected() || ne.isSelected()) {
                    if (taip.isSelected()) {
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            PreparedStatement prep = conn.prepareStatement("delete from KALENDORIUS where data = ?;");
                            prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                    + (kalendorius.get(Calendar.MONTH) + 1)
                                    + "-" + lentele.getValueAt(eilute, stulpelis));
                            prep.executeUpdate();
                            taip.setVisible(false);
                            ne.setVisible(false);
                            patvirtinti.setVisible(false);
                            dietosAtvaizdavimas.setVisible(false);
                            iKalendoriu.setVisible(false);
                            issaugoti.setVisible(false);
                            trinti.setVisible(false);
                            slankjuoste.setVisible(false);
                            slankjuoste.setVisible(true);
                            pirmyn.setVisible(true);
                            atgal.setVisible(true);
                            informacija.setVisible(true);
                            setSize(750, 330);
                            pasirinkta.setLocation(190, 220);
                            trinti.setLocation(535, 217);
                            pasirinkta.setText("Dieta sėkmingai pašalinta iš kalendoriaus.");
                        } catch (SQLException ex) {
                            Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (ne.isSelected()) {
                        if (trinti.getLocation().y == 217) {
                            pasirinkta.setText("Pasirinkite dieną");
                        } else {
                            pasirinkta.setText("");
                            trinti.setVisible(true);
                        }
                    }
                    taip.setVisible(false);
                    ne.setVisible(false);
                    patvirtinti.setVisible(false);
                    pasirinkimas.clearSelection();
                }
            }
        });

        patvirtintiMenesi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taip.isSelected() || ne.isSelected()) {
                    if (taip.isSelected()) {
                        eilute = 0;
                        stulpelis = 0;
                        while ((lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                            stulpelis++;
                        }
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            PreparedStatement prep = conn.prepareStatement("delete from KALENDORIUS where data = ?;");

                            while (!(lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                                prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                        + (kalendorius.get(Calendar.MONTH) + 1)
                                        + "-" + lentele.getValueAt(eilute, stulpelis));
                                prep.executeUpdate();
                                stulpelis++;
                                if (stulpelis == 7 && eilute + 1 == lentele.getRowCount()) {
                                    break;
                                }
                                if (stulpelis == 7) {
                                    eilute++;
                                    stulpelis = 0;
                                }
                            }

                            dietosAtvaizdavimas.setVisible(false);
                            iKalendoriu.setVisible(false);
                            issaugoti.setVisible(false);
                            trinti.setVisible(false);
                            slankjuoste.setVisible(false);
                            slankjuoste.setVisible(true);
                            pirmyn.setVisible(true);
                            atgal.setVisible(true);
                            informacija.setVisible(true);
                            setSize(750, 330);
                            trinti.setLocation(535, 217);
                            pasirinkta.setText("Šio mėnesio dietos sėkmingai pašalintos iš kalendoriaus.");
                        } catch (SQLException ex) {
                            Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (ne.isSelected()) {
                        if (trinti.getLocation().y == 217) {
                            pasirinkta.setText("Pasirinkite dieną");
                        } else {
                            pasirinkta.setText("");
                            trinti.setVisible(true);
                        }
                    }
                    pasirinkta.setLocation(190, 220);
                    taip.setVisible(false);
                    ne.setVisible(false);
                    patvirtinti.setVisible(false);
                    patvirtintiMenesi.setVisible(false);
                    pasirinkimas.clearSelection();
                }
            }
        });

        issaugotosDietos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dienosDieta = false;
                    setSize(750, 620);
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();
                    ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");
                    if (!rs.next()) {
                        dietosPlanas.setText(" Šiuo metu išsaugotų dietų neturite.");
                    } else {
                        dietosPlanas.setText(" Pasirinkite atvaizduojamos dietos numerį.");
                    }

                    pasirinkta.setText("Pasirinkite atvaizduojamos dietos numerį.");
                    pasirinkta.setLocation(175, 548);
                    idZiurejimui.setVisible(true);

                    dietosAtvaizdavimas.setVisible(true);
                    informacija.setVisible(false);
                    patvirtinti.setVisible(false);
                    trinti.setVisible(false);
                    slankjuoste.setVisible(false);
                    iKalendoriu.setVisible(true);
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        idZiurejimui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer.parseInt((String) idZiurejimui.getSelectedItem());
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();
                    ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS where id = "
                            + Integer.parseInt((String) idZiurejimui.getSelectedItem()) + ";");
                    dietosPlanas.setText(" " + rs.getInt("id") + "-oji išsaugota dieta:\n\n" + rs.getString("dieta") + "\n");
                    dietosPlanas.select(0, 0);
                    pasirinkta.setText("Pasirinkite atvaizduojamos dietos numerį.");
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NumberFormatException ex) {
                    dietosPlanas.setText(" Pasirinkite atvaizduojamos dietos numerį.");
                    pasirinkta.setText("Pasirinkite atvaizduojamos dietos numerį.");
                }
            }
        });

        patvirtintiItraukima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer.parseInt(lentele.getValueAt(eilute, stulpelis).toString());
                    Integer.parseInt((String) id.getSelectedItem());
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();
                    ResultSet rs;

                    boolean leidimas = true;

                    String[] dienos = state.executeQuery("select * from ISSAUGOTOS_DIETOS where id = "
                            + Integer.parseInt((String) id.getSelectedItem()) + ";").getString("dieta")
                            .split("-------------------------------------------------------------------------------------------------------------------------------------------------------");
                    int x = stulpelis, y = eilute;
                    boolean atnaujintas = false;

                    if (dienos.length == 1) {
                        PreparedStatement prep = conn.prepareStatement("insert into KALENDORIUS values(?,?);");
                        prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                + (kalendorius.get(Calendar.MONTH) + 1)
                                + "-" + lentele.getValueAt(eilute, stulpelis++));
                        prep.setString(2, dienos[0]);
                        prep.execute();
                        pasirinkta.setText("Dieta sėkmingai priskirta dienai.");
                    } else {
                        rs = state.executeQuery("select * from KALENDORIUS;");
                        while (rs.next()) {
                            for (int i = 0; i < dienos.length - 1; i++) {
                                if (stulpelis == 7 && eilute + 1 == lentele.getRowCount()) {
                                    eilute = 0;
                                    stulpelis = 0;
                                    kalendorius.add(Calendar.MONTH, +1);
                                    atnaujintiMenesi();
                                    atnaujintas = true;
                                }
                                if (stulpelis == 7) {
                                    eilute++;
                                    stulpelis = 0;
                                }
                                if ((lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                                    eilute = 0;
                                    kalendorius.add(Calendar.MONTH, +1);
                                    atnaujintiMenesi();
                                    atnaujintas = true;
                                }
                                if (rs.getString("data").equals(kalendorius.get(Calendar.YEAR) + "-"
                                        + (kalendorius.get(Calendar.MONTH) + 1)
                                        + "-" + lentele.getValueAt(eilute, stulpelis++))) {
                                    leidimas = false;
                                }
                            }
                            stulpelis = x;
                            eilute = y;
                            if (atnaujintas) {
                                atnaujintas = false;
                                kalendorius.add(Calendar.MONTH, -1);
                                atnaujintiMenesi();
                            }
                        }

                        if (leidimas) {
                            if (atnaujintas) {
                                kalendorius.add(Calendar.MONTH, -1);
                                atnaujintiMenesi();
                            }

                            stulpelis = x;
                            eilute = y;
                            for (int i = 0; i < dienos.length - 1; i++) {
                                if (stulpelis == 7 && eilute + 1 == lentele.getRowCount()) {
                                    eilute = 0;
                                    stulpelis = 0;
                                    kalendorius.add(Calendar.MONTH, +1);
                                    atnaujintiMenesi();
                                    atnaujintas = true;
                                }
                                if (stulpelis == 7) {
                                    eilute++;
                                    stulpelis = 0;
                                }
                                if ((lentele.getValueAt(eilute, stulpelis) + "").equals("null")) {
                                    eilute = 0;
                                    kalendorius.add(Calendar.MONTH, +1);
                                    atnaujintiMenesi();
                                }
                                PreparedStatement prep = conn.prepareStatement("insert into KALENDORIUS values(?,?);");
                                prep.setString(1, kalendorius.get(Calendar.YEAR) + "-"
                                        + (kalendorius.get(Calendar.MONTH) + 1)
                                        + "-" + lentele.getValueAt(eilute, stulpelis++));
                                prep.setString(2, dienos[0] + dienos[i + 1].substring(16));
                                prep.execute();
                            }
                            pasirinkta.setText("Dieta sėkmingai priskirta pasirinktoms dienoms.");
                        } else {
                            pasirinkta.setText("Bent viena iš dienų šiam dietos planui jau užimta!");
                        }
                    }
                    pasirinkta.setLocation(160, 220);
                    patvirtintiItraukima.setVisible(false);
                    issaugotosDietos.setVisible(false);
                    id.setVisible(false);

                    klaida.setText("");
                    slankjuoste.setVisible(false);
                    slankjuoste.setVisible(true);
                    id.setSelectedIndex(0);
                    conn.close();
                } catch (NumberFormatException ex) {
                    klaida.setText("Nepasirinkta dieta.");
                } catch (SQLException | ClassNotFoundException | NullPointerException ex) {
                    id.setVisible(false);
                    patvirtintiItraukima.setVisible(false);
                    issaugotosDietos.setVisible(false);
                    pasirinkta.setText("Pasirinkite dieną");
                    pasirinkta.setLocation(160, 220);
                    itrauktiDieta.setVisible(false);
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
        this.getContentPane().setBackground(new Color(166, 223, 32));
        lentele.setBackground(new Color(255, 251, 111));
        atnaujintiMenesi();
    }

    private String[] ids() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DietaPaciam.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");

        int i = 1;

        while (rs.next()) {
            i++;
        }

        String[] ids = new String[i];
        ids[0] = "";

        rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");
        i = 1;
        while (rs.next()) {
            ids[i] = rs.getString("id");
            i++;
        }

        for (int x = 1; x < ids.length + 1; x++) {
            for (int y = 1; y < ids.length - x; y++) {
                if (Integer.parseInt(ids[y]) > Integer.parseInt(ids[y + 1])) {
                    String laikinas = ids[y];
                    ids[y] = ids[y + 1];
                    ids[y + 1] = laikinas;
                }
            }
        }
        return ids;
    }

    private void atnaujintiMenesi() {

        kalendorius.set(Calendar.DAY_OF_MONTH, 1);

        String en = kalendorius.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        String menesis = "Sausis";
        if (en.equals("February")) {
            menesis = "Vasaris";
        }
        if (en.equals("March")) {
            menesis = "Kovas";
        }
        if (en.equals("April")) {
            menesis = "Balandis";
        }
        if (en.equals("May")) {
            menesis = "Gegužė";
        }
        if (en.equals("June")) {
            menesis = "Birželis";
        }
        if (en.equals("July")) {
            menesis = "Liepa";
        }
        if (en.equals("August")) {
            menesis = "Rugpjūtis";
        }
        if (en.equals("September")) {
            menesis = "Rugsėjis";
        }
        if (en.equals("October")) {
            menesis = "Spalis";
        }
        if (en.equals("November")) {
            menesis = "Lapkritis";
        }
        if (en.equals("December")) {
            menesis = "Gruodis";
        }

        int metai = kalendorius.get(Calendar.YEAR);
        informacija.setText("<html><font size='5'>" + metai + ", " + menesis + "</font></html>");

        int siandien = kalendorius.get(Calendar.DAY_OF_WEEK);
        int dienuSkaicius = kalendorius.getActualMaximum(Calendar.DAY_OF_MONTH);

        modelis.setRowCount(0);
        modelis.setRowCount(6);
        slankjuoste.setSize(750, 127);

        int i = siandien - 1;
        for (int diena = 1; diena <= dienuSkaicius; diena++) {
            modelis.setValueAt(diena, i / 7, i % 7);
            i++;
        }
        lentele.setDefaultRenderer(String.class, new CellRenderer(kalendorius.get(Calendar.YEAR) + "-"
                + (kalendorius.get(Calendar.MONTH) + 1)));
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

class CellRenderer extends DefaultTableCellRenderer {

    String data;

    CellRenderer(String g) {
        data = g;
    }

    public Component getTableCellRendererComponent(JTable lentele, Object verte,
            boolean pasirinktas, boolean fokusavimas, int eilute, int stulpelis) {
        Component c = super.getTableCellRendererComponent(lentele, verte,
                pasirinktas, fokusavimas, eilute, stulpelis);
        try {
            Object valueAt = lentele.getModel().getValueAt(eilute, stulpelis);
            String s = "";
            if (valueAt != null) {
                s = valueAt.toString();
            }

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("select * from KALENDORIUS");
            c.setForeground(Color.BLACK);
            c.setBackground(Color.WHITE);
            while (rs.next()) {
                if (rs.getString("data").equals(data + "-" + s)) {
                    c.setForeground(Color.YELLOW);
                    c.setBackground(Color.GRAY);
                }
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
}
