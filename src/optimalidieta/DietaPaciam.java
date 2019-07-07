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
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class DietaPaciam extends JFrame implements MenuListener, ActionListener, KeyListener{
    
    private int kalorijuPoreikis = 0;
    private double baltymaiMin, baltymaiMax, angliavandeniaiMin, angliavandeniaiMax,
        riebalaiMin, riebalaiMax, skaidulinesMin, skaidulinesMax, kalcisMin, kalcisMax;
    
    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;

    private TableModel modelis;
    private JTable lentele;
    private JScrollPane scroll;
    
    private JComboBox pasirinkimas;

    private ArrayList<String>pasirinkti = new ArrayList<>();

    DietaPaciam(final int amzius, final int lytis, final int ugis, final int svoris, final int tikslas, final int aktyvumas,
            final ArrayList<String>nepageidaujamaKategorija, final ArrayList<String>nepageidaujamiProduktai) throws SQLException{
        
        super("Savarankiškas dietos generavimas");
        setSize(1210, 665);
        setLayout(null);
        this.getContentPane().setBackground(new Color(166, 223, 32));
        
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
        
        add(menuBar);
        
        double aktyvumoKoeficientas=1.5;
        
        if(aktyvumas == 2)aktyvumoKoeficientas=1.7;
        if(aktyvumas == 3)aktyvumoKoeficientas=2;
        if(lytis == 1)kalorijuPoreikis = (int)Math.round(aktyvumoKoeficientas*(65.4 + (13.7 * svoris) + (5 * ugis) - (6.8 * amzius)));
        if(lytis == 2)kalorijuPoreikis = (int)Math.round(aktyvumoKoeficientas*(655 + (9.6 * svoris) + (1.8 * ugis) - (4.7 * amzius)));
        if(tikslas == 1)kalorijuPoreikis-=500;
        if(tikslas == 3)kalorijuPoreikis+=500;
        
        skaidulinesMin = ((kalorijuPoreikis*14)/1000)-5;
        skaidulinesMax = ((kalorijuPoreikis*14)/1000)+5;
        
        if((10000*Double.parseDouble(svoris+""))/(Double.parseDouble(ugis+"")*Double.parseDouble(ugis+""))>25.5){
            baltymaiMin=(kalorijuPoreikis*25)/400; baltymaiMax=(kalorijuPoreikis*35)/400;
            angliavandeniaiMin=(kalorijuPoreikis*45)/400; angliavandeniaiMax=(kalorijuPoreikis*55)/400;
            riebalaiMin=(kalorijuPoreikis*15)/900; riebalaiMax=(kalorijuPoreikis*25)/900;
        }else{
            if(aktyvumas==1){
                baltymaiMin=(kalorijuPoreikis*10)/400; baltymaiMax=(kalorijuPoreikis*20)/400;
                angliavandeniaiMin=(kalorijuPoreikis*55)/400; angliavandeniaiMax=(kalorijuPoreikis*75)/400;
                riebalaiMin=(kalorijuPoreikis*10)/900; riebalaiMax=(kalorijuPoreikis*15)/900;
            }else{
                baltymaiMin=(kalorijuPoreikis*15)/400; baltymaiMax=(kalorijuPoreikis*20)/400;
                angliavandeniaiMin=(kalorijuPoreikis*55)/400; angliavandeniaiMax=(kalorijuPoreikis*60)/400;
                riebalaiMin=(kalorijuPoreikis*15)/900; riebalaiMax=(kalorijuPoreikis*20)/900;
            }
        }
            
        if(amzius==0){
            kalcisMin=0.24; kalcisMax=0.26;
        }else if(amzius>0&&amzius<4){
            kalcisMin=0.69; kalcisMax=0.71;
        }else if(amzius>3&&amzius<9){
            kalcisMin=0.99; kalcisMax=1.01;
        }else if(amzius>8&&amzius<14){
            kalcisMin=1.29; kalcisMax=1.31;
        }else if(amzius>13&&amzius<19){
            kalcisMin=1.29; kalcisMax=1.31;
        }else if(amzius>18&&amzius<51){
            kalcisMin=0.99; kalcisMax=1.01;
        }else if(amzius>50&&amzius<71){
            if(lytis==1){
                kalcisMin=0.99; kalcisMax=1.01;
            }
            else{
                kalcisMin=1.19; kalcisMax=1.21;
            } 
        }else{
            kalcisMin=1.19; kalcisMax=1.21;
        }
        
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException ex) {
            Logger.getLogger(DietaPaciam.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
        
        boolean leidimas;
        int i = 1;
        while(rs.next()){
            leidimas = true;
            for (String nk : nepageidaujamaKategorija)
                if (rs.getString("kategorija").contains(nk))
                    leidimas = false;
            for (String np:nepageidaujamiProduktai)
                if(rs.getString("pavadinimas").equals(np))
                    leidimas=false;
            if(leidimas)i++;
        }
        
        final String[]produktai = new String[i];
        produktai[0] = "";
        
        rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
        i = 1;
        while(rs.next()){
            leidimas = true;
            for (String nk : nepageidaujamaKategorija)
                if (rs.getString("kategorija").contains(nk))
                    leidimas = false;
            for (String np : nepageidaujamiProduktai)
                if (rs.getString("pavadinimas").equals(np))
                    leidimas = false;
            if(leidimas){
                produktai[i]=rs.getString("pavadinimas");
                i++;
            }
        }
        
        for(int x = 1; x < produktai.length+1;x++)
            for (int y = 1; y < produktai.length-x; y++)  
                if (produktai[y].compareTo(produktai[y+1])>0){
                    String laikinas = produktai[y];
                    produktai[y] = produktai[y+1];
                    produktai[y+1] = laikinas;
                }
        conn.close();
        
        pasirinkimas = new JComboBox(produktai);
        pasirinkimas.setLocation(296, 122);
        pasirinkimas.setSize(245, 25);
        add(pasirinkimas);
        pasirinkimas.setEditable(true);
        
        AutoCompleteDecorator.decorate(pasirinkimas);
        
        Object[][] turinys = new Object[0][9];
        
        String[] stulpeliai = {"Pavadinimas", "Kiekis (g arba ml)", "Energinė vertė (kcal)" ,"Baltymai (g)", "Angliavandeniai (g)",
            "Riebalai (g)", "Cholesterolis (g)", "Kalcis (g)", "Skaidulinės medžiagos (g)"};
        
        
        modelis = new DefaultTableModel(turinys, stulpeliai) {
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
          
        lentele = new JTable(modelis);
        lentele.getTableHeader().setReorderingAllowed(false);
        
        scroll = new JScrollPane(lentele);
        scroll.setLocation(0, 210);
        scroll.setSize(1200, 200);
        add(scroll);
        
        final JLabel info = new JLabel("");
        info.setLocation(320, 173);
        info.setSize(630, 15);
        add(info);
        
        final JLabel istrynimoInfo = new JLabel("");
        istrynimoInfo.setLocation(30, 590);
        istrynimoInfo.setSize(555, 15);
        add(istrynimoInfo);
        
        final JLabel itraukiteProdukta = new JLabel("Įveskite naujo produkto pavadinimą:");
        itraukiteProdukta.setLocation(50, 122);
        itraukiteProdukta.setSize(230, 20);
        add(itraukiteProdukta);
        
        final JLabel kalorijos = new JLabel("");
        kalorijos.setLocation(20, 420);
        kalorijos.setSize(700, 15);
        add(kalorijos);
        
        final JLabel baltymai = new JLabel("");
        baltymai.setLocation(20, 445);
        baltymai.setSize(700, 15);
        add(baltymai);
        
        final JLabel angliavandeniai = new JLabel("");
        angliavandeniai.setLocation(20, 470);
        angliavandeniai.setSize(700, 15);
        add(angliavandeniai);
        
        final JLabel riebalai = new JLabel("");
        riebalai.setLocation(20, 495);
        riebalai.setSize(700, 15);
        add(riebalai);
        
        final JLabel kalcis = new JLabel("");
        kalcis.setLocation(20, 520);
        kalcis.setSize(700, 15);
        add(kalcis);
        
        final JLabel skaidulines = new JLabel("");
        skaidulines.setLocation(20, 545);
        skaidulines.setSize(700, 15);
        add(skaidulines);
        
        final JLabel cholesterolis = new JLabel("");
        cholesterolis.setLocation(20, 570);
        cholesterolis.setSize(700, 15);
        add(cholesterolis);
        
        final JLabel itraukiteKieki = new JLabel("Produkto kiekis:");
        itraukiteKieki.setLocation(177, 147);
        itraukiteKieki.setSize(105, 20);
        add(itraukiteKieki);
        
        final JLabel sudarykiteDieta = new JLabel("Sudaroma dieta:");
        sudarykiteDieta.setLocation(20, 190);
        sudarykiteDieta.setSize(105, 20);
        add(sudarykiteDieta);
        
        final JLabel gml = new JLabel("g arba ml");
        gml.setLocation(335, 147);
        gml.setSize(60, 20);
        add(gml);
        
        final JTextArea rekomendacija = new JTextArea("");
        rekomendacija.setEditable(false);
        rekomendacija.setLocation(295, 21);
        rekomendacija.setSize(620, 75);
        add(rekomendacija);
        
        final JTextField kiekis = new JTextField("");
        kiekis.setLocation(296, 147);
        kiekis.setToolTipText("Produkto kiekis gramais arba mililitrais");
        kiekis.setSize(39, 25);
        add(kiekis);
        
        final JButton sarasas = new JButton("Produktų informacija");
        sarasas.setLocation(550, 120);
        sarasas.setSize(160, 25);
        add(sarasas);
        
        final JButton itraukti = new JButton("Įtraukti į dietą");
        itraukti.setLocation(424, 147);
        itraukti.setSize(120, 25);
        add(itraukti);
        
        final JButton istrintiProdukta = new JButton("Ištrinti produktą/(-us) iš dietos");
        istrintiProdukta.setLocation(431, 190);
        istrintiProdukta.setSize(223, 25);
        add(istrintiProdukta);
        
        final JButton issaugoti = new JButton("Išsaugoti dietą");
        issaugoti.setLocation(885, 190);
        issaugoti.setSize(115, 25);
        add(issaugoti);
        
        final JButton iKalendoriu = new JButton("Įtraukti į kalendorių");
        iKalendoriu.setLocation(1000, 190);
        iKalendoriu.setSize(150, 25);
        add(iKalendoriu);
        
        final JButton patvirtinti = new JButton("Patvirtinti");
        patvirtinti.setLocation(520, 187);
        patvirtinti.setSize(100, 25);
        add(patvirtinti);
        
        final JButton isjungti = new JButton("Išjungti pavadinimo spėjimą");
        isjungti.setLocation(294, 98);
        isjungti.setSize(210, 25);
        add(isjungti);
        
        final JButton ijungti = new JButton("Įjungti pavadinimo spėjimą");
        ijungti.setLocation(294, 98);
        ijungti.setSize(210, 25);
        add(ijungti);ijungti.setVisible(false);
        
        final JRadioButton taip = new JRadioButton("Taip");
        taip.setLocation(400, 187);
        taip.setSize(60, 20);
        add(taip);
        
        final JRadioButton ne = new JRadioButton("Ne");
        ne.setLocation(470, 187);
        ne.setSize(50, 20);
        add(ne);
        
        final ButtonGroup patvirtinimas = new ButtonGroup();
        patvirtinimas.add(taip); patvirtinimas.add(ne);
        
        patvirtinti.setVisible(false); ne.setVisible(false); taip.setVisible(false);
        
        Skaiciavimai s = new Skaiciavimai();
        
        rekomendacija.setText(" Pagal pateiktus duomenis, rekomenduojama paros "
                    + "energijos suvartojimo norma yra: "+kalorijuPoreikis+"kcal\n\n"+s.medziagos((10000*Double.parseDouble(svoris+""))/(Double.parseDouble(ugis+"")
                            *Double.parseDouble(ugis+"")),aktyvumas,kalorijuPoreikis,amzius,lytis));
        
        rekomendacija.setBackground(new Color(166, 223, 32));
        
        sarasas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    new ProduktuInformacija(false, nepageidaujamaKategorija);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(DietaPaciam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        itraukti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    if(Integer.parseInt(kiekis.getText())<=0)
                        info.setText("Įveskite produkto kiekį kaip natūralųjį skaičių!");
                    else{
                        String[] stulpeliai = {"Pavadinimas", "Kiekis (g arba ml)", "Energinė vertė (kcal)", "Baltymai (g)", "Angliavandeniai (g)",
                           "Riebalai (g)", "Cholesterolis (g)", "Kalcis (g)", "Skaidulinės medžiagos (g)"};

                        Class.forName("org.sqlite.JDBC");
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        Statement state = conn.createStatement();
                        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
                            
                        if(!state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                                +((String)pasirinkimas.getSelectedItem())+"';").next())
                            info.setText("Duomenų bazėje nėra produkto tokiu pavadinimu (atkreipkite dėmesį į didžiąsias/mažąsias raides).");
                        else{
                            Object[][] turinys;
                            int pasikartojimas=-1;
                            if(pasirinkti.contains(state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                                   +((String)pasirinkimas.getSelectedItem())+"';").getString("pavadinimas"))){
                                turinys = new Object[pasirinkti.size()][9];
                                pasikartojimas=pasirinkti.indexOf(state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                                    +((String)pasirinkimas.getSelectedItem())+"';").getString("pavadinimas"));
                            }
                            else{
                                rs = state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                                        +((String)pasirinkimas.getSelectedItem())+"';");
                                pasirinkti.add(rs.getString("pavadinimas"));
                                turinys = new Object[pasirinkti.size()][9];
                            }
                                                
                            for(int i=0;i<pasirinkti.size();i++){
                                rs = state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                                        +pasirinkti.get(i)+"';");
                                turinys[i][0]=rs.getString("pavadinimas");
                                if(i==pasikartojimas)
                                    turinys[i][1]=Integer.parseInt(lentele.getValueAt(i, 1).toString())+Integer.parseInt(kiekis.getText());
                                else{
                                    if(lentele.getRowCount()>i)
                                        turinys[i][1]=lentele.getValueAt(i, 1);
                                    else
                                        turinys[i][1]=kiekis.getText();
                                }
                                turinys[i][2]=rs.getInt("energine_verte")*Integer.parseInt(turinys[i][1].toString())/100;
                                turinys[i][3]=ProduktuInformacija.suapvalinti((rs.getInt("baltymai")/100)*Integer.parseInt(turinys[i][1].toString()));
                                turinys[i][4]=ProduktuInformacija.suapvalinti((rs.getInt("angliavandeniai")/100)*Integer.parseInt(turinys[i][1].toString()));
                                turinys[i][5]=ProduktuInformacija.suapvalinti((rs.getInt("riebalai")/100)*Integer.parseInt(turinys[i][1].toString()));
                                turinys[i][6]=ProduktuInformacija.suapvalinti((rs.getInt("cholesterolis")/100)*Integer.parseInt(turinys[i][1].toString()));
                                turinys[i][7]=ProduktuInformacija.suapvalinti((rs.getInt("kalcis")/100)*Integer.parseInt(turinys[i][1].toString()));
                                turinys[i][8]=ProduktuInformacija.suapvalinti((rs.getInt("skaidulines_medziagos")/100)*Integer.parseInt(turinys[i][1].toString()));
                            }
                            conn.close();
                               
                            modelis = new DefaultTableModel(turinys, stulpeliai) {
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

                            lentele = new JTable(modelis);
                                
                            lentele.setAutoCreateRowSorter(true);
                            lentele.setBackground(new Color(255, 251, 111));
                            lentele.getTableHeader().setReorderingAllowed(false);
                                
                            remove(scroll);
                            scroll = new JScrollPane(lentele);
                            scroll.setLocation(0, 210);
                            scroll.setSize(1200, 200);
                            add(scroll);
                                
                            int kalorijosDietoje = 0;
                            double  baltymaiDietoje = 0,  angliavandeniaiDietoje = 0, riebalaiDietoje = 0, 
                                    kalcisDietoje = 0, skaidulinesDietoje = 0, cholesterolisDietoje = 0;
                                
                            for(int i=0;i<lentele.getRowCount();i++){
                                kalorijosDietoje+=Integer.parseInt(lentele.getValueAt(i,2).toString());
                                baltymaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,3).toString())*1000000);
                                angliavandeniaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,4).toString())*1000000);
                                riebalaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,5).toString())*1000000);
                                cholesterolisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,6).toString())*1000000);
                                kalcisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,7).toString())*1000000);
                                skaidulinesDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,8).toString())*1000000);
                            }
                   
                            if(kalorijosDietoje<kalorijuPoreikis)
                                kalorijos.setText("<html><font color='blue'>Iki rekomenduojamos kilokalorijų"
                                    + " normos trūksta: "+(kalorijuPoreikis-kalorijosDietoje)
                                        +"kcal</font></html>");
                                
                            else if(kalorijosDietoje>kalorijuPoreikis)
                                kalorijos.setText("<html><font color='red'>Rekomenduojama kilokalorijų norma"
                                    + " viršyta: "+(kalorijosDietoje-kalorijuPoreikis)
                                        +"kcal</font></html>");
                                
                            else 
                                kalorijos.setText("Kilokalorijų norma patenkinta idealiai.");
                                
                            if(baltymaiDietoje<baltymaiMin)
                                baltymai.setText("<html><font color='blue'>Iki rekomenduojamos baltymų"
                                    + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiMin-baltymaiDietoje))
                                        +"g</font></html>");
                                
                            else if(baltymaiDietoje>baltymaiMax)
                                baltymai.setText("<html><font color='red'>Rekomenduojama baltymų norma"
                                    + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiDietoje-baltymaiMax))
                                        +"g</font></html>");
                                
                            else
                                baltymai.setText("Baltymų norma patenkinta idealiai.");
                                
                            if(angliavandeniaiDietoje<angliavandeniaiMin)
                                angliavandeniai.setText("<html><font color='blue'>Iki rekomenduojamos angliavandenių"
                                    + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiMin-angliavandeniaiDietoje))
                                        +"g</font></html>");
                                
                            else if(angliavandeniaiDietoje>angliavandeniaiMax)
                                angliavandeniai.setText("<html><font color='red'>Rekomenduojama angliavandenių norma"
                                    + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiDietoje-angliavandeniaiMax))
                                        +"g</font></html>");
                                
                            else
                                angliavandeniai.setText("Angliavandenių norma patenkinta idealiai.");
                                
                            if(riebalaiDietoje<riebalaiMin)
                                riebalai.setText("<html><font color='blue'>Iki rekomenduojamos riebalų"
                                    + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiMin-riebalaiDietoje))
                                        +"g</font></html>");
                                
                            else if(riebalaiDietoje>riebalaiMax)
                                riebalai.setText("<html><font color='red'>Rekomenduojama riebalų norma"
                                    + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiDietoje-riebalaiMax))
                                        +"g</font></html>");
                                
                            else
                                riebalai.setText("Riebalų norma patenkinta idealiai.");
                                
                            if(kalcisDietoje<kalcisMin)
                                kalcis.setText("<html><font color='blue'>Iki rekomenduojamos kalcio"
                                    + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisMin-kalcisDietoje))
                                        +"g</font></html>");
                                
                            else if(kalcisDietoje>kalcisMax)
                                kalcis.setText("<html><font color='red'>Rekomenduojama kalcio norma"
                                    + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisDietoje-kalcisMax))
                                        +"g</font></html>");
                                
                            else
                                kalcis.setText("Kalcio norma patenkinta idealiai.");
                                
                            if(skaidulinesDietoje<skaidulinesMin)
                                skaidulines.setText("<html><font color='blue'>Iki rekomenduojamos skaidulinių"
                                    + " medžiagų normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesMin-skaidulinesDietoje))
                                        +"g</font></html>");
                                
                            else if(skaidulinesDietoje>skaidulinesMax)
                                skaidulines.setText("<html><font color='red'>Rekomenduojama skaidulinių medžiagų"
                                    + " norma viršyta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesDietoje-skaidulinesMax))
                                        +"g</font></html>");
                                
                            else
                                skaidulines.setText("Skaidulinių medžiagų norma patenkinta idealiai.");
                                
                            if(cholesterolisDietoje<0.3)
                                cholesterolis.setText("Cholesterolio norma neviršyta"
                                    + " (iki pertekliaus trūksta: "+ProduktuInformacija.suapvalinti(1000000*(0.3-cholesterolisDietoje))+"g)");
                                
                            else if(cholesterolisDietoje>0.3)
                                cholesterolis.setText("<html><font color='red'>Rekomenduojama cholesterolio norma"
                                    + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(cholesterolisDietoje-0.3))
                                        +"g</font></html>");
                                
                            else
                                cholesterolis.setText("Cholesterolio norma pasiekta, bet neviršyta.");
                            
                            info.setText("");
                            kiekis.setText("");
                            pasirinkimas.setSelectedIndex(0);
                            }
                        }
                    }catch(NumberFormatException ex) {
                        info.setText("Įveskite produkto kiekį kaip natūralųjį skaičių!");
                    }
                    catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(DietaPaciam.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        });
        
        istrintiProdukta.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e){
                if(lentele.getSelectionModel().isSelectionEmpty())
                    info.setText("Pasirinkite lentelėje BENT vieną eilutę, kurią norite ištrinti.");
                else{
                    istrintiProdukta.setVisible(false);
                    patvirtinti.setVisible(true);
                    taip.setVisible(true);
                    ne.setVisible(true);
                    info.setText("Ar tikrai norite ištrinti pasirinktą/(-us) produktą/(-us)?");
                }
            }
        }); 
        
        patvirtinti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(ne.isSelected()||taip.isSelected()){
                    if(taip.isSelected()){
                        int[]a = lentele.getSelectedRows();
                        for(int i=0;i<a.length;i++){
                            pasirinkti.remove(lentele.convertRowIndexToModel(a[0]));
                            ((DefaultTableModel)lentele.getModel())
                                .removeRow(lentele.convertRowIndexToModel(a[0]));     
                        }

                        if(lentele.getRowCount()==0)
                            lentele.setRowSorter(null);

                        int kalorijosDietoje = 0;
                        double  baltymaiDietoje = 0,  angliavandeniaiDietoje = 0, riebalaiDietoje = 0, 
                                kalcisDietoje = 0, skaidulinesDietoje = 0, cholesterolisDietoje = 0;

                        for(int i=0;i<lentele.getRowCount();i++){
                            kalorijosDietoje+=Integer.parseInt(lentele.getValueAt(i,2).toString());
                            baltymaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,3).toString())*1000000);
                            angliavandeniaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,4).toString())*1000000);
                            riebalaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,5).toString())*1000000);
                            cholesterolisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,6).toString())*1000000);
                            kalcisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,7).toString())*1000000);
                            skaidulinesDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,8).toString())*1000000);
                        }

                        if(kalorijosDietoje<kalorijuPoreikis)
                            kalorijos.setText("<html><font color='blue'>Iki rekomenduojamos kilokalorijų"
                                + " normos trūksta: "+(kalorijuPoreikis-kalorijosDietoje)
                                    +"kcal</font></html>");

                        else if(kalorijosDietoje>kalorijuPoreikis)
                            kalorijos.setText("<html><font color='red'>Rekomenduojama kilokalorijų norma"
                                + " viršyta: "+(kalorijosDietoje-kalorijuPoreikis)
                                    +"kcal</font></html>");

                        else 
                            kalorijos.setText("Kilokalorijų norma patenkinta idealiai.");

                        if(baltymaiDietoje<baltymaiMin)
                            baltymai.setText("<html><font color='blue'>Iki rekomenduojamos baltymų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiMin-baltymaiDietoje))
                                    +"g</font></html>");

                        else if(baltymaiDietoje>baltymaiMax)
                            baltymai.setText("<html><font color='red'>Rekomenduojama baltymų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiDietoje-baltymaiMax))
                                    +"g</font></html>");

                        else
                            baltymai.setText("Baltymų norma patenkinta idealiai.");

                        if(angliavandeniaiDietoje<angliavandeniaiMin)
                            angliavandeniai.setText("<html><font color='blue'>Iki rekomenduojamos angliavandenių"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiMin-angliavandeniaiDietoje))
                                    +"g</font></html>");

                        else if(angliavandeniaiDietoje>angliavandeniaiMax)
                            angliavandeniai.setText("<html><font color='red'>Rekomenduojama angliavandenių norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiDietoje-angliavandeniaiMax))
                                    +"g</font></html>");

                        else
                            angliavandeniai.setText("Angliavandenių norma patenkinta idealiai.");

                        if(riebalaiDietoje<riebalaiMin)
                            riebalai.setText("<html><font color='blue'>Iki rekomenduojamos riebalų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiMin-riebalaiDietoje))
                                    +"g</font></html>");

                        else if(riebalaiDietoje>riebalaiMax)
                            riebalai.setText("<html><font color='red'>Rekomenduojama riebalų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiDietoje-riebalaiMax))
                                    +"g</font></html>");

                        else
                            riebalai.setText("Riebalų norma patenkinta idealiai.");

                        if(kalcisDietoje<kalcisMin)
                            kalcis.setText("<html><font color='blue'>Iki rekomenduojamos kalcio"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisMin-kalcisDietoje))
                                    +"g</font></html>");

                        else if(kalcisDietoje>kalcisMax)
                            kalcis.setText("<html><font color='red'>Rekomenduojama kalcio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisDietoje-kalcisMax))
                                    +"g</font></html>");

                        else
                            kalcis.setText("Kalcio norma patenkinta idealiai.");

                        if(skaidulinesDietoje<skaidulinesMin)
                            skaidulines.setText("<html><font color='blue'>Iki rekomenduojamos skaidulinių"
                                + " medžiagų normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesMin-skaidulinesDietoje))
                                    +"g</font></html>");

                        else if(skaidulinesDietoje>skaidulinesMax)
                            skaidulines.setText("<html><font color='red'>Rekomenduojama skaidulinių medžiagų"
                                + " norma viršyta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesDietoje-skaidulinesMax))
                                    +"g</font></html>");

                        else
                            skaidulines.setText("Skaidulinių medžiagų norma patenkinta idealiai.");

                        if(cholesterolisDietoje<0.3)
                            cholesterolis.setText("Cholesterolio norma neviršyta"
                                + " (iki pertekliaus trūksta: "+ProduktuInformacija.suapvalinti(1000000*(0.3-cholesterolisDietoje))+"g)");

                        else if(cholesterolisDietoje>0.3)
                            cholesterolis.setText("<html><font color='red'>Rekomenduojama cholesterolio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(cholesterolisDietoje-0.3))
                                    +"g</font></html>");

                        else
                            cholesterolis.setText("Cholesterolio norma pasiekta, bet neviršyta.");

                        info.setText("Produktas/(-ai) buvo ištrintas/(-i) sėkmingai");
                    }
                    else
                        info.setText("Produktas/(-ai) nebuvo ištrintas/(-i).");
                }
                taip.setVisible(false);
                ne.setVisible(false);
                patvirtinti.setVisible(false);
                istrintiProdukta.setVisible(true);
                patvirtinimas.clearSelection();
            }
        });
        
        issaugoti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(lentele.getRowCount()>0)
                    try {
                        Class.forName("org.sqlite.JDBC");
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        Statement state = conn.createStatement();
                        ResultSet rs;

                        String rezultatas = rekomendacija.getText()+" Sudaryta dieta:\n\n";

                        for(int i=0;i<lentele.getRowCount();i++){
                            rs = state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                            +lentele.getValueAt(i, 0).toString()+"';");
                            if(rs.getBoolean("skystis"))
                                rezultatas+=" "+lentele.getValueAt(i, 0).toString()+" "+lentele.getValueAt(i, 1).toString()+"ml "
                                    + "(iš viso energinė vertė: "+lentele.getValueAt(i, 2).toString()+"kcal)\n";
                            else
                                rezultatas+=" "+lentele.getValueAt(i, 0).toString()+" "+lentele.getValueAt(i, 1).toString()+"g "
                                    + "(iš viso energinė vertė: "+lentele.getValueAt(i, 2).toString()+"kcal)\n";
                        }
                        rezultatas+="\n";
                        int kalorijosDietoje = 0;
                        double  baltymaiDietoje = 0,  angliavandeniaiDietoje = 0, riebalaiDietoje = 0, 
                                kalcisDietoje = 0, skaidulinesDietoje = 0, cholesterolisDietoje = 0;

                        for(int i=0;i<lentele.getRowCount();i++){
                            kalorijosDietoje+=Integer.parseInt(lentele.getValueAt(i,2).toString());
                            baltymaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,3).toString())*1000000);
                            angliavandeniaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,4).toString())*1000000);
                            riebalaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,5).toString())*1000000);
                            cholesterolisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,6).toString())*1000000);
                            kalcisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,7).toString())*1000000);
                            skaidulinesDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,8).toString())*1000000);
                        }

                        if(kalorijosDietoje<kalorijuPoreikis)
                            rezultatas+=" Iki rekomenduojamos kilokalorijų"+ " normos trūksta: "
                                    +(kalorijuPoreikis-kalorijosDietoje)+"kcal\n";

                        else if(kalorijosDietoje>kalorijuPoreikis)
                            rezultatas+=" Rekomenduojama kilokalorijų norma"
                                + " viršyta: "+(kalorijosDietoje-kalorijuPoreikis)+"kcal\n";

                        else 
                            rezultatas+=" Kilokalorijų norma patenkinta idealiai.\n";

                        if(baltymaiDietoje<baltymaiMin)
                            rezultatas+=" Iki rekomenduojamos baltymų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiMin-baltymaiDietoje))
                                    +"g\n";

                        else if(baltymaiDietoje>baltymaiMax)
                            rezultatas+=" Rekomenduojama baltymų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiDietoje-baltymaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Baltymų norma patenkinta idealiai.\n";

                        if(angliavandeniaiDietoje<angliavandeniaiMin)
                            rezultatas+=" Iki rekomenduojamos angliavandenių"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiMin-angliavandeniaiDietoje))
                                    +"g\n";

                        else if(angliavandeniaiDietoje>angliavandeniaiMax)
                            rezultatas+=" Rekomenduojama angliavandenių norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiDietoje-angliavandeniaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Angliavandenių norma patenkinta idealiai.\n";

                        if(riebalaiDietoje<riebalaiMin)
                            rezultatas+=" Iki rekomenduojamos riebalų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiMin-riebalaiDietoje))
                                    +"g\n";

                        else if(riebalaiDietoje>riebalaiMax)
                            rezultatas+=" Rekomenduojama riebalų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiDietoje-riebalaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Riebalų norma patenkinta idealiai.\n";

                        if(kalcisDietoje<kalcisMin)
                            rezultatas+=" Iki rekomenduojamos kalcio"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisMin-kalcisDietoje))
                                    +"g\n";

                        else if(kalcisDietoje>kalcisMax)
                            rezultatas+=" Rekomenduojama kalcio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisDietoje-kalcisMax))
                                    +"g\n";

                        else
                            rezultatas+=" Kalcio norma patenkinta idealiai.\n";

                        if(skaidulinesDietoje<skaidulinesMin)
                            rezultatas+=" Iki rekomenduojamos skaidulinių"
                                + " medžiagų normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesMin-skaidulinesDietoje))
                                    +"g\n";

                        else if(skaidulinesDietoje>skaidulinesMax)
                            rezultatas+=" Rekomenduojama skaidulinių medžiagų"
                                + " norma viršyta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesDietoje-skaidulinesMax))
                                    +"g\n";

                        else
                            rezultatas+=" Skaidulinių medžiagų norma patenkinta idealiai.\n";

                        if(cholesterolisDietoje<0.3)
                            rezultatas+=" Cholesterolio norma neviršyta"
                                + " (iki pertekliaus trūksta: "+ProduktuInformacija.suapvalinti(1000000*(0.3-cholesterolisDietoje))+"g)";

                        else if(cholesterolisDietoje>0.3)
                            rezultatas+=" Rekomenduojama cholesterolio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(cholesterolisDietoje-0.3))
                                    +"g";

                        else
                            rezultatas+=" Cholesterolio norma pasiekta, bet neviršyta.";

                        String vartotojoLytis = " Lytis: ";
                        if(lytis == 1)vartotojoLytis += "vyras; ";
                        else vartotojoLytis += "moteris; ";

                        String vartotojoTikslas = " tikslas: ";
                        if(tikslas == 1)vartotojoTikslas += "numesti svorio; ";
                        else if(tikslas == 2) vartotojoTikslas += "palaikyti svorį; ";
                        else vartotojoTikslas += "priaugti svorio; ";

                        String vartotojoAktyvumas = " aktyvumas: ";
                        if(aktyvumas == 1)vartotojoAktyvumas += "žemas; ";
                        else if(aktyvumas == 2) vartotojoAktyvumas += "vidutinis; ";
                        else vartotojoAktyvumas += "aukštas; ";
                        vartotojoAktyvumas+="\n ";

                        String nepageidaujami = "Nepageidaujami produktai: ";
                        for(int i=0;i<nepageidaujamiProduktai.size();i++){
                            if(i==5)nepageidaujami+="\n ";
                            if(nepageidaujamiProduktai.get(i).equals("pienas"))
                                nepageidaujami+="pieno produktai; ";
                            else if(nepageidaujamiProduktai.get(i).equals("miltai"))
                                nepageidaujami+="miltų produktai; ";
                            else
                                nepageidaujami+=nepageidaujamiProduktai.get(i)+"; ";
                        }

                        if(nepageidaujami.equals("Nepageidaujami produktai: "))
                            nepageidaujami = "Nei viena produktų kategorija nepasirinkta kaip nepageidaujama.";

                        nepageidaujami += "\n Dieta sugeneruota savarankiškai.\n\n";

                        String itraukiamaDieta = vartotojoLytis+"amžius: "+amzius+"; ūgis: "+ugis+"; svoris: "+svoris+";"+
                            vartotojoTikslas+vartotojoAktyvumas+nepageidaujami+rezultatas;

                        rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");
                        int i = 1;
                        while(rs.next())i++;

                        PreparedStatement prep = conn.prepareStatement("insert into ISSAUGOTOS_DIETOS values(?,?);");
                        prep.setInt(1, i);
                        prep.setString(2, itraukiamaDieta);
                        prep.execute();
                        conn.close();
                        info.setText("Dieta išsaugota sėkmingai.");
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }); 
        
        iKalendoriu.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(lentele.getRowCount()>0){
                    try {
                        Class.forName("org.sqlite.JDBC");
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        Statement state = conn.createStatement();
                        ResultSet rs;

                        String rezultatas = rekomendacija.getText()+" Sudaryta dieta:\n\n";

                        for(int i=0;i<lentele.getRowCount();i++){
                            rs = state.executeQuery("select * from MAISTO_PRODUKTAI where pavadinimas = '"
                            +lentele.getValueAt(i, 0).toString()+"';");
                            if(rs.getBoolean("skystis"))
                                rezultatas+=" "+lentele.getValueAt(i, 0).toString()+" "+lentele.getValueAt(i, 1).toString()+"ml "
                                    + "(iš viso energinė vertė: "+lentele.getValueAt(i, 2).toString()+"kcal)\n";
                            else
                                rezultatas+=" "+lentele.getValueAt(i, 0).toString()+" "+lentele.getValueAt(i, 1).toString()+"g "
                                    + "(iš viso energinė vertė: "+lentele.getValueAt(i, 2).toString()+"kcal)\n";
                        }
                        rezultatas+="\n";
                        int kalorijosDietoje = 0;
                        double  baltymaiDietoje = 0,  angliavandeniaiDietoje = 0, riebalaiDietoje = 0, 
                                kalcisDietoje = 0, skaidulinesDietoje = 0, cholesterolisDietoje = 0;

                        for(int i=0;i<lentele.getRowCount();i++){
                            kalorijosDietoje+=Integer.parseInt(lentele.getValueAt(i,2).toString());
                            baltymaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,3).toString())*1000000);
                            angliavandeniaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,4).toString())*1000000);
                            riebalaiDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,5).toString())*1000000);
                            cholesterolisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,6).toString())*1000000);
                            kalcisDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,7).toString())*1000000);
                            skaidulinesDietoje+=ProduktuInformacija.suapvalinti(Double.parseDouble(lentele.getValueAt(i,8).toString())*1000000);
                        }

                        if(kalorijosDietoje<kalorijuPoreikis)
                            rezultatas+=" Iki rekomenduojamos kilokalorijų"+ " normos trūksta: "
                                    +(kalorijuPoreikis-kalorijosDietoje)+"kcal\n";

                        else if(kalorijosDietoje>kalorijuPoreikis)
                            rezultatas+=" Rekomenduojama kilokalorijų norma"
                                + " viršyta: "+(kalorijosDietoje-kalorijuPoreikis)+"kcal\n";

                        else 
                            rezultatas+=" Kilokalorijų norma patenkinta idealiai.\n";

                        if(baltymaiDietoje<baltymaiMin)
                            rezultatas+=" Iki rekomenduojamos baltymų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiMin-baltymaiDietoje))
                                    +"g\n";

                        else if(baltymaiDietoje>baltymaiMax)
                            rezultatas+=" Rekomenduojama baltymų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(baltymaiDietoje-baltymaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Baltymų norma patenkinta idealiai.\n";

                        if(angliavandeniaiDietoje<angliavandeniaiMin)
                            rezultatas+=" Iki rekomenduojamos angliavandenių"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiMin-angliavandeniaiDietoje))
                                    +"g\n";

                        else if(angliavandeniaiDietoje>angliavandeniaiMax)
                            rezultatas+=" Rekomenduojama angliavandenių norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(angliavandeniaiDietoje-angliavandeniaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Angliavandenių norma patenkinta idealiai.\n";

                        if(riebalaiDietoje<riebalaiMin)
                            rezultatas+=" Iki rekomenduojamos riebalų"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiMin-riebalaiDietoje))
                                    +"g\n";

                        else if(riebalaiDietoje>riebalaiMax)
                            rezultatas+=" Rekomenduojama riebalų norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(riebalaiDietoje-riebalaiMax))
                                    +"g\n";

                        else
                            rezultatas+=" Riebalų norma patenkinta idealiai.\n";

                        if(kalcisDietoje<kalcisMin)
                            rezultatas+=" Iki rekomenduojamos kalcio"
                                + " normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisMin-kalcisDietoje))
                                    +"g\n";

                        else if(kalcisDietoje>kalcisMax)
                            rezultatas+=" Rekomenduojama kalcio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(kalcisDietoje-kalcisMax))
                                    +"g\n";

                        else
                            rezultatas+=" Kalcio norma patenkinta idealiai.\n";

                        if(skaidulinesDietoje<skaidulinesMin)
                            rezultatas+=" Iki rekomenduojamos skaidulinių"
                                + " medžiagų normos trūksta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesMin-skaidulinesDietoje))
                                    +"g\n";

                        else if(skaidulinesDietoje>skaidulinesMax)
                            rezultatas+=" Rekomenduojama skaidulinių medžiagų"
                                + " norma viršyta: "+ProduktuInformacija.suapvalinti(1000000*(skaidulinesDietoje-skaidulinesMax))
                                    +"g\n";

                        else
                            rezultatas+=" Skaidulinių medžiagų norma patenkinta idealiai.\n";

                        if(cholesterolisDietoje<0.3)
                            rezultatas+=" Cholesterolio norma neviršyta"
                                + " (iki pertekliaus trūksta: "+ProduktuInformacija.suapvalinti(1000000*(0.3-cholesterolisDietoje))+"g)";

                        else if(cholesterolisDietoje>0.3)
                            rezultatas+=" Rekomenduojama cholesterolio norma"
                                + " viršyta: "+ProduktuInformacija.suapvalinti(1000000*(cholesterolisDietoje-0.3))
                                    +"g";

                        else
                            rezultatas+=" Cholesterolio norma pasiekta, bet neviršyta.";

                        String vartotojoLytis = " Lytis: ";
                        if(lytis == 1)vartotojoLytis += "vyras; ";
                        else vartotojoLytis += "moteris; ";

                        String vartotojoTikslas = " tikslas: ";
                        if(tikslas == 1)vartotojoTikslas += "numesti svorio; ";
                        else if(tikslas == 2) vartotojoTikslas += "palaikyti svorį; ";
                        else vartotojoTikslas += "priaugti svorio; ";

                        String vartotojoAktyvumas = " aktyvumas: ";
                        if(aktyvumas == 1)vartotojoAktyvumas += "žemas; ";
                        else if(aktyvumas == 2) vartotojoAktyvumas += "vidutinis; ";
                        else vartotojoAktyvumas += "aukštas; ";
                        vartotojoAktyvumas+="\n ";

                        String nepageidaujami = "Nepageidaujami produktai: ";

                        for(int i=0;i<nepageidaujamiProduktai.size();i++){
                            if(i==6)nepageidaujami+="\n ";
                            if(nepageidaujamiProduktai.get(i).equals("pienas"))
                                nepageidaujami+="pieno produktai; ";
                            else if(nepageidaujamiProduktai.get(i).equals("miltai"))
                                nepageidaujami+="miltų produktai; ";
                            else
                                nepageidaujami+=nepageidaujamiProduktai.get(i)+"; ";
                        }

                        if(nepageidaujami.equals("Nepageidaujami produktai: "))
                            nepageidaujami = "Nei viena produktų kategorija nepasirinkta kaip nepageidaujama.";

                        nepageidaujami += "\n Dieta sugeneruota savarankiškai.\n\n";
                        
                        conn.close();
                        new Kalendorius(vartotojoLytis+"amžius: "+amzius+"; ūgis: "+ugis+"; svoris: "+svoris+";"+
                            vartotojoTikslas+vartotojoAktyvumas+nepageidaujami+rezultatas);
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                dispose();
                }
            }
        });
        
        isjungti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                isjungti.setVisible(false);
                ijungti.setVisible(true);
                pasirinkimas.setVisible(false);
                pasirinkimas = new JComboBox(produktai);
                pasirinkimas.setLocation(296, 122);
                pasirinkimas.setSize(245, 25);
                add(pasirinkimas);
                pasirinkimas.setEditable(true);
            }
        }); 
        
        ijungti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                ijungti.setVisible(false);
                isjungti.setVisible(true);
                AutoCompleteDecorator.decorate(pasirinkimas);
            }
        }); 
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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