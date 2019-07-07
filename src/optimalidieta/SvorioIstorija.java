package optimalidieta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.jdbc.JDBCCategoryDataset;

public class SvorioIstorija extends JFrame implements MenuListener, ActionListener, KeyListener{
    
    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    private Connection conn;
    private Statement state;
    private ResultSet rs;

    private ArrayList<JCheckBox>datuSarasas;
    private JScrollPane slankjuostesLangas;
    private JPanel datuSarasoLangas, datuLangas;
    private JLabel datuAntraste;

    private Calendar kalendorius = new GregorianCalendar();

    SvorioIstorija() throws SQLException, ClassNotFoundException{
        super("Svorio kitimo per pastaruosius metus istorija");
        setSize(750, 380);
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
        
        trintiPasenusiusIrasus();
        pridetiIrasus();
        
        final JComboBox pasirinkimas = new JComboBox(dienos());
        pasirinkimas.setLocation(175, 100);
        pasirinkimas.setSize(150, 20);
        add(pasirinkimas);
        
        final JLabel info = new JLabel("");
        info.setLocation(125, 175);
        info.setSize(630, 15);
        add(info);
        
        final JLabel trynimoInfo = new JLabel("");
        trynimoInfo.setLocation(410, 325);
        trynimoInfo.setSize(630, 15);
        add(trynimoInfo);
        
        final JLabel sukurkiteIrasa = new JLabel("<html><h2>Įtraukite naują įrašą:</h2></html>");
        sukurkiteIrasa.setLocation(125, 60);
        sukurkiteIrasa.setSize(200, 25);
        add(sukurkiteIrasa);
        
        final JLabel iveskiteData = new JLabel("Data:");
        iveskiteData.setLocation(125, 102);
        iveskiteData.setSize(40, 15);
        add(iveskiteData);
        
        final JLabel iveskiteSvori = new JLabel("Svoris:");
        iveskiteSvori.setLocation(125, 142);
        iveskiteSvori.setSize(45, 15);
        add(iveskiteSvori);
        
        final JLabel kg = new JLabel("kg");
        kg.setLocation(217, 142);
        kg.setSize(20, 15);
        add(kg);
        
        final JTextField svoris = new JTextField("");
        svoris.setLocation(177, 140);
        svoris.setToolTipText("Jūsų svoris pasirinktą dieną (kilogramais)");
        svoris.setSize(39, 25);
        add(svoris);
        
        final JButton itraukti = new JButton("Įtraukti");
        itraukti.setLocation(240, 138);
        itraukti.setSize(100, 25);
        add(itraukti); 
        
        final JButton brezti = new JButton("Nubrėžti grafiką");
        brezti.setLocation(225, 230);
        brezti.setSize(140, 25);
        add(brezti); 
        
        final JButton trinti = new JButton("Trinti pasirinktus įrašus");
        trinti.setLocation(425, 295);
        trinti.setSize(190, 25);
        add(trinti); 
        
        pasirinkimas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                info.setText("");
            }
        });
        
        itraukti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    if(Integer.parseInt(svoris.getText())<=0)
                        info.setText("Įveskite svorį kaip natūralųjį skaičių!");
                    else{
                        Class.forName("org.sqlite.JDBC");
                        conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        state = conn.createStatement();
                        rs = state.executeQuery("select * from svorio_istorija");
                        boolean leidimas=true;
                        while(rs.next())
                            if(rs.getString("data").equals(pasirinkimas.getSelectedItem().toString()))
                                leidimas=false;
                        if(leidimas){
                            PreparedStatement prep = conn.prepareStatement("insert into SVORIO_ISTORIJA values(?,?);");
                            prep.setString(1, pasirinkimas.getSelectedItem().toString());
                            prep.setInt(2, Integer.parseInt(svoris.getText()));
                            prep.execute();
                            info.setText("Įrašas sėkmingai įtrauktas į duomenų bazę.");
                            svoris.setText("");
                            pridetiIrasus();
                        }
                        else
                            info.setText("Ši diena jau užimta!");
                        conn.close();
                    }
                }catch(NumberFormatException ex) {
                    info.setText("Įveskite svorį kaip natūralųjį skaičių!");
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
                }
                trynimoInfo.setText("");
            }
        });
        
        brezti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    Class.forName("org.sqlite.JDBC");
                    conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    JDBCCategoryDataset dataset = new JDBCCategoryDataset(conn,"select data, svoris from SVORIO_ISTORIJA ORDER BY data;");
                    JFreeChart grafikas = ChartFactory.createLineChart("Svorio kitimo grafikas", "data", "svoris (kg)", dataset, 
                            PlotOrientation.VERTICAL, false, true, true);
                    
                    grafikas.setBackgroundPaint(new Color(166, 223, 32));
                    CategoryPlot cplot = (CategoryPlot)grafikas.getPlot();
                    cplot.setBackgroundPaint(new Color(166, 223, 32));

                    ChartFrame frame = new ChartFrame("Grafikas",grafikas);
                    frame.setVisible(true);
                    frame.setSize(1200,400);
                    frame.setLocationRelativeTo(null);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        trinti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                for(int i=0;i<datuSarasas.size();i++)
                    if(datuSarasas.get(i).isSelected()){
                        try {
                            datuSarasas.get(i).setVisible(false);
                            conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            PreparedStatement prep = conn.prepareStatement("delete from SVORIO_ISTORIJA where data = ?;");
                            prep.setString(1, datuSarasas.get(i).getText().substring(0,10));
                            prep.executeUpdate();
                            conn.close();
                            trynimoInfo.setText("Įrašas/(-ai) ištrintas/(-i) sėkmingai.");
                            info.setText("");
                        } catch (SQLException ex) {
                            Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }
        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        this.getContentPane().setBackground(new Color(166, 223, 32));
    }
    
    private void trintiPasenusiusIrasus() throws ClassNotFoundException{
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
            state = conn.createStatement();
            rs = state.executeQuery("select * from SVORIO_ISTORIJA;");
            String[]dienos=dienos();
            boolean leidimas;
            while(rs.next()){
                leidimas=false;
                for(int i=0;i<dienos.length;i++)
                    if(rs.getString("data").equals(dienos[i]))
                        leidimas=true;
                if(!leidimas){
                   PreparedStatement prep = conn.prepareStatement("delete from SVORIO_ISTORIJA where data = ?;");
                   prep.setString(1, rs.getString("data"));
                   prep.executeUpdate();
                }
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void pridetiIrasus() throws ClassNotFoundException{
        try {
            if(datuLangas!=null){
                datuLangas.setVisible(false);
                remove(datuLangas);
            }
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
            state = conn.createStatement();
            rs = state.executeQuery("select * from SVORIO_ISTORIJA;");
            
            datuSarasas = new ArrayList<>();
            
            while(rs.next())
                datuSarasas.add(new JCheckBox(rs.getString("data")+": "+rs.getString("svoris")+"kg"));
            
            conn.close();
            
            for(int i = 0; i < datuSarasas.size();i++)
                for (int j = 0; j < datuSarasas.size()-i-1; j++)
                    if (datuSarasas.get(j).getText().compareTo(datuSarasas.get(j+1).getText())>0){
                        JCheckBox laikinas = datuSarasas.get(j);
                        datuSarasas.set(j,datuSarasas.get(j+1));
                        datuSarasas.set(j+1, laikinas);
                    }
            
            datuLangas = new JPanel();
            datuSarasoLangas = new JPanel();
            datuSarasoLangas.setLayout(new BoxLayout(datuSarasoLangas, BoxLayout.Y_AXIS));
            
            for(int i=0;i<datuSarasas.size();i++)
                datuSarasoLangas.add(datuSarasas.get(i));
            
            datuSarasoLangas.add(new JLabel("\n\n"));
            datuSarasoLangas.setBackground(new Color(166, 223, 32));
            
            datuAntraste = new JLabel("Išsaugoti svorio įrašai");
            
            datuLangas.add(datuAntraste);
            datuLangas.setLocation(430, 25);
            datuLangas.setSize(200, 270);
            
            slankjuostesLangas = new JScrollPane(datuSarasoLangas);
            slankjuostesLangas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            slankjuostesLangas.setPreferredSize(new Dimension (200, 250));
            
            datuLangas.add(slankjuostesLangas);
            add(datuLangas);
        } catch (SQLException ex) {
            Logger.getLogger(SvorioIstorija.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String[]dienos(){
        String[]dienos = new String[365];
        for(int i=365;i>0;i--){
            String menesis = (kalendorius.get(Calendar.MONTH)+1)+"";
            String diena = kalendorius.get(Calendar.DATE)+"";
            if(menesis.length()==1)
                menesis="0"+menesis;
            if(diena.length()==1)
               diena="0"+diena;
            dienos[365-i]=kalendorius.get(Calendar.YEAR)+"-"+menesis+"-"+diena;
            kalendorius.add(Calendar.DATE, -1);
        }
        for(int i=365;i>0;i--)
            kalendorius.add(Calendar.DATE, +1);
        return dienos;
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