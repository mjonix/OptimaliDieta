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
import javax.swing.JTextArea;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class DietuInformacija extends JFrame implements MenuListener, ActionListener, KeyListener {

    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    DietuInformacija() throws SQLException {
        super("Išsaugotos dietos");
        setSize(750, 630);
        setLayout(null);
        this.getContentPane().setBackground(new Color(166, 223, 32));

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

        final JLabel info = new JLabel("Dietos, kurią norite ištrinti arba peržiūrėti, numeris:");
        info.setLocation(230, 540);
        info.setSize(500, 15);
        add(info);

        final JComboBox id = new JComboBox(ids());
        id.setLocation(500, 560);
        id.setSize(100, 20);
        add(id);

        final JTextArea dietosPlanas = new JTextArea("");
        dietosPlanas.setEditable(false);
        dietosPlanas.setBackground(new Color(166, 223, 32));

        final JScrollPane slankjuoste = new JScrollPane(dietosPlanas);
        slankjuoste.setLocation(55, 30);
        slankjuoste.setSize(635, 500);
        add(slankjuoste);

        final JButton trinti = new JButton("Ištrinti");
        trinti.setLocation(330, 560);
        trinti.setSize(100, 25);
        add(trinti);

        final JButton patvirtinti = new JButton("Patvirtinti");
        patvirtinti.setLocation(355, 560);
        patvirtinti.setSize(100, 25);
        add(patvirtinti);

        final JRadioButton taip = new JRadioButton("Taip");
        taip.setLocation(224, 560);
        taip.setSize(60, 20);
        add(taip);

        final JRadioButton ne = new JRadioButton("Ne");
        ne.setLocation(294, 560);
        ne.setSize(50, 20);
        add(ne);

        final ButtonGroup pasirinkimas = new ButtonGroup();
        pasirinkimas.add(taip);
        pasirinkimas.add(ne);

        patvirtinti.setVisible(false);
        taip.setVisible(false);
        ne.setVisible(false);

        id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer.parseInt((String) id.getSelectedItem());
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    Statement state = conn.createStatement();
                    ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS where id = "
                            + Integer.parseInt((String) id.getSelectedItem()) + ";");
                    dietosPlanas.setText(" " + rs.getInt("id") + "-oji išsaugota dieta:\n\n" + rs.getString("dieta") + "\n");
                    dietosPlanas.select(0, 0);
                    info.setText("Dietos, kurią norite ištrinti arba peržiūrėti, numeris:");
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NumberFormatException ex) {
                    dietosPlanas.setText(" Pasirinkite dietos numerį.");
                }
            }
        });

        trinti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer.parseInt((String) id.getSelectedItem());
                    id.setVisible(false);
                    trinti.setVisible(false);
                    taip.setVisible(true);
                    ne.setVisible(true);
                    patvirtinti.setVisible(true);
                    info.setText("Ar tikrai norite ištrinti " + Integer.parseInt((String) id.getSelectedItem()) + "-ąją dietą?");
                } catch (NumberFormatException ex) {
                    dietosPlanas.setText(" Pasirinkite dietos numerį.");
                }
            }
        });

        patvirtinti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taip.isSelected() || ne.isSelected()) {
                    if (taip.isSelected()) {
                        try {
                            taip.setVisible(false);
                            ne.setVisible(false);
                            patvirtinti.setVisible(false);
                            trinti.setVisible(true);
                            id.setVisible(true);

                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            PreparedStatement prep = conn.prepareStatement("delete from ISSAUGOTOS_DIETOS where id = ?;");
                            prep.setInt(1, Integer.parseInt((String) id.getSelectedItem()));
                            prep.executeUpdate();
                            Statement state = conn.createStatement();
                            ResultSet rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");
                            int i = 1;
                            while (rs.next()) {
                                prep = conn.prepareStatement("update ISSAUGOTOS_DIETOS set id = ? where id = ?;");
                                prep.setInt(1, i);
                                prep.setInt(2, rs.getInt("id"));
                                prep.executeUpdate();
                                i++;
                            }
                            id.setModel(new DefaultComboBoxModel(ids()));
                            dietosPlanas.setText(" Dieta ištrinta sėkmingai.");
                            conn.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(DietuInformacija.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (ne.isSelected()) {
                        taip.setVisible(false);
                        ne.setVisible(false);
                        patvirtinti.setVisible(false);
                        trinti.setVisible(true);
                        id.setVisible(true);
                        id.setSelectedIndex(0);
                        dietosPlanas.setText(" Dieta nebuvo ištrinta.");
                    }
                    info.setText("Dietos, kurią norite ištrinti arba peržiūrėti, numeris:");
                    pasirinkimas.clearSelection();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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