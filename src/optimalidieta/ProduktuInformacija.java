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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;

public class ProduktuInformacija extends JFrame implements MenuListener, ActionListener, KeyListener{
    
    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    ProduktuInformacija(final boolean atgal, final ArrayList<String>nepageidaujamiProduktai) throws ClassNotFoundException, SQLException{
        super("Informacija apie maisto produktus");
        setSize(1210, 680);
        setLayout(null);
      
        menuBar = new JMenuBar();
        menuBar.setLocation(293, 0);
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
          
        if(atgal)add(menuBar);
        
        String[] stulpeliai = {"Pavadinimas", "Kilokalorijos (100g)" ,"Baltymai (%)", "Angliavandeniai (%)",
            "Riebalai (%)", "Cholesterolis (%)", "Kalcis (%)", "Skaidulinės medžiagos (%)"};
  
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");

        int i=0;
        boolean leidimas;
        while(rs.next()){
            leidimas = true;
            for (String nepageidaujamiProduktai1 : nepageidaujamiProduktai)
                if (rs.getString("kategorija").contains(nepageidaujamiProduktai1))
                    leidimas = false;
            if(leidimas)i++;
        }
        
        Object[][] turinys = new Object[i][8];
        rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
        i=0;
        
        while(rs.next()){
            leidimas = true;
            for (String nepageidaujamiProduktai1 : nepageidaujamiProduktai)
                if (rs.getString("kategorija").contains(nepageidaujamiProduktai1))
                    leidimas = false;     
            if(leidimas){
                turinys[i][0]=rs.getString("pavadinimas");
                turinys[i][1]=rs.getInt("energine_verte");
                turinys[i][2]=suapvalinti(rs.getInt("baltymai"));
                turinys[i][3]=suapvalinti(rs.getInt("angliavandeniai"));
                turinys[i][4]=suapvalinti(rs.getInt("riebalai"));
                turinys[i][5]=suapvalinti(rs.getInt("cholesterolis"));
                turinys[i][6]=suapvalinti(rs.getInt("kalcis"));
                turinys[i][7]=suapvalinti(rs.getInt("skaidulines_medziagos"));
                i++;
            }
        }

        TableModel modelis = new DefaultTableModel(turinys, stulpeliai) {
            @Override
            public Class getColumnClass(int stulpelis) {
                if (stulpelis >= 0 && stulpelis <= getColumnCount())
                    return getValueAt(0, stulpelis).getClass();
                else
                    return Object.class;
            }
            @Override
            public boolean isCellEditable(int eilute, int stulpelis){
                return false;
            }
        };
          
        final JTable lentele = new JTable(modelis);
        lentele.setAutoCreateRowSorter(true);
        
        JScrollPane scroll = new JScrollPane(lentele);
        scroll.setLocation(0, 70);
        scroll.setSize(1200, 570);
        add(scroll);
        
        final JLabel paaiskinimas = new JLabel("Ieškoti:");
        paaiskinimas.setLocation(10, 48);
        paaiskinimas.setSize(48, 15);
        add(paaiskinimas);
        
        final JLabel informacija = new JLabel("");
        informacija.setLocation(420, 27);
        informacija.setSize(400, 15);
        add(informacija);
        
        final JTextField paieska = new JTextField("");
        paieska.setLocation(60, 44);
        paieska.setToolTipText("Paieškos tekstas");
        paieska.setSize(200, 25);
        add(paieska);
        
        final JButton trinti = new JButton("Trinti iš duomenų bazės");
        trinti.setLocation(435, 50);
        trinti.setSize(170, 25);
        add(trinti);

        final JButton itraukti = new JButton("Įtraukti naują produktą");
        itraukti.setLocation(605, 50);
        itraukti.setSize(170, 25);
        add(itraukti);
        
        final JButton patvirtinti = new JButton("Patvirtinti");
        patvirtinti.setLocation(585, 43);
        patvirtinti.setSize(100, 25);
        add(patvirtinti);
        
        final JRadioButton taip = new JRadioButton("Taip");
        taip.setLocation(460, 44);
        taip.setSize(60, 20);
        add(taip);
        
        final JRadioButton ne = new JRadioButton("Ne");
        ne.setLocation(530, 44);
        ne.setSize(50, 20);
        add(ne);
        
        final ButtonGroup pasirinkimas = new ButtonGroup();
        pasirinkimas.add(taip); pasirinkimas.add(ne);
        
        taip.setVisible(false); ne.setVisible(false); patvirtinti.setVisible(false);
        
        if(!atgal){
            trinti.setVisible(false);
            itraukti.setVisible(false);
        }
        
        trinti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(lentele.getSelectionModel().isSelectionEmpty())
                    informacija.setText("Pasirinkite lentelėje BENT vieną eilutę, kurią norite ištrinti.");
                else{
                    trinti.setVisible(false);
                    itraukti.setVisible(false);
                    taip.setVisible(true);
                    ne.setVisible(true);
                    patvirtinti.setVisible(true);
                    informacija.setText("Ar tikrai norite ištrinti pasirinktą/(-us) produktą/(-us)?");
                }                
            }
        });
        
        itraukti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                new ProduktuItraukimas();
                dispose();
            }
        });
        
        patvirtinti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(taip.isSelected()||ne.isSelected()){
                    if(ne.isSelected())
                        informacija.setText("Produktas/(-ai) nebuvo ištrintas/(-i).");
                    if(taip.isSelected()){
                        if(lentele.getSelectionModel().isSelectionEmpty())
                            informacija.setText("Pasirinkite lentelėje BENT vieną eilutę, kurią norite ištrinti.");
                        else{
                            int[]a = lentele.getSelectedRows();

                            for(int i=0;i<a.length;i++){
                                try {
                                    Class.forName("org.sqlite.JDBC");
                                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");

                                    PreparedStatement prep = conn.prepareStatement("delete from MAISTO_PRODUKTAI where pavadinimas = ?;");
                                    prep.setString(1, lentele.getModel().getValueAt(lentele.convertRowIndexToModel(a[0]),0).toString());
                                    prep.executeUpdate();
                                    ((DefaultTableModel)lentele.getModel())
                                          .removeRow(lentele.convertRowIndexToModel(a[0]));
                                } catch (ClassNotFoundException | SQLException ex) {
                                    Logger.getLogger(ProduktuInformacija.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            informacija.setText("Produktas/(-ai) buvo ištrintas/(-i) sėkmingai.");
                        }
                    }
                    pasirinkimas.clearSelection();
                    taip.setVisible(false);
                    ne.setVisible(false);
                    patvirtinti.setVisible(false);
                    if(atgal)itraukti.setVisible(true);
                    if(atgal)trinti.setVisible(true);
                }
            }
        });
        
        final TableRowSorter<TableModel> rusiavimas = new TableRowSorter<>(modelis);
        rusiavimas.setStringConverter(new TableStringConverter() {
            @Override
            public String toString(TableModel modelis, int eilute, int stulpelis) {
                return modelis.getValueAt(eilute, stulpelis).toString().toLowerCase();
            }
        });
        lentele.setRowSorter(rusiavimas);
        lentele.getRowSorter().addRowSorterListener(new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                lentele.clearSelection();
            }
        });
      
        paieska.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (paieska.getText().length() == 0) {
                  rusiavimas.setRowFilter(null);
                } else {
                  rusiavimas.setRowFilter(RowFilter.regexFilter(paieska.getText().toLowerCase()));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (paieska.getText().length() == 0) {
                  rusiavimas.setRowFilter(null);
                } else {
                  rusiavimas.setRowFilter(RowFilter.regexFilter(paieska.getText().toLowerCase()));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                
            }
        });
   
        if(atgal)setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        else setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if(!atgal)setLocationRelativeTo(null);
        setVisible(true);
        this.getContentPane().setBackground(new Color(166, 223, 32));
        lentele.setBackground(new Color(255, 251, 111));
        
    }
    
    static double suapvalinti(double skaicius) {
        skaicius/=1000000;
        long daugiklis = (long) Math.pow(10, 2);
        skaicius = skaicius * daugiklis;
        return (double) Math.round(skaicius)/daugiklis;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource().equals(iPagrindini))try {
            pagrindinis();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(info))try {
            info();
        }catch (SQLException ex) {
            Logger.getLogger(ProduktuInformacija.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(itraukti))try {
            itraukti();
        }catch (SQLException ex) {
            Logger.getLogger(ProduktuItraukimas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(nauji))try {
            nauji();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(naudoti))try {
            naudoti();
        }catch (SQLException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(skaiciuotiKMI))try {
            kmi();
        }catch (SQLException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(issaugotosDietos))try {
            issaugotos();
        }catch (SQLException ex) {
            Logger.getLogger(DietuInformacija.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(iKalendoriu))try {
            kalendorius();
        }catch (SQLException ex) {
            Logger.getLogger(Kalendorius.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.getSource().equals(svorioIstorija))try {
            svoris();
        }catch (SQLException ex) {
            Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void menuSelected(MenuEvent e) {}

    @Override
    public void menuDeselected(MenuEvent e) {}

    @Override
    public void menuCanceled(MenuEvent e) {}
        
    private void itraukti() throws SQLException{
        new ProduktuItraukimas();
        dispose();
    }
    
    private void info() throws SQLException, ClassNotFoundException{
        new ProduktuInformacija(true, new ArrayList<String>());
        dispose();
    }
    
    private void pagrindinis() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        new OptimaliDieta(0);
        dispose();
    }
    
    private void nauji() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        new OptimaliDieta(1);
        dispose();
    }
    
    private void naudoti() throws SQLException, ClassNotFoundException, InstantiationException, UnsupportedLookAndFeelException, IllegalAccessException{
        new OptimaliDieta(2);
        dispose();
    }
    
    private void kmi() throws SQLException, ClassNotFoundException, InstantiationException, UnsupportedLookAndFeelException, IllegalAccessException{
        new OptimaliDieta(3);
        dispose();
    }
    
    private void issaugotos() throws SQLException{
        new DietuInformacija();
        dispose();
    }
    
    private void kalendorius() throws SQLException, ClassNotFoundException{
        new Kalendorius("");
        dispose();
    }
    
    private void svoris() throws SQLException, ClassNotFoundException{
        new SvorioIstorija();
        dispose();
    }
}