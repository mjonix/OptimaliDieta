package optimalidieta;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class OptimaliDieta extends JFrame implements MenuListener, ActionListener, KeyListener{

    private int amzius, svoris, ugis, lytis, aktyvumas, tikslas, kartai, dienos;
    private String dieta;

    private JMenu pagrindinis, produktai, dietos, kmi, dietuKalendorius, svorioKitimas;
    private JMenuItem iPagrindini, info, itraukti, nauji, naudoti, skaiciuotiKMI,
            issaugotosDietos, iKalendoriu, svorioIstorija;
    private JMenuBar menuBar;
    
    private JTable lentele;

    private Connection conn;
    private Statement state;
    private ResultSet rs;
    
    private JScrollPane duomenys, slankjuostesLangas, slankjuoste;
    private JPanel produktuSarasoLangas, produktuLangas;
    private JLabel produktuAntraste, klausimas, klaida;
    
    private ArrayList<JCheckBox>produktuSarasas;
    private JCheckBox mesa, sokoladas, zuvis, riesutai, pienas, medus, kiausiniai, miltai, kruopos, uogos, grybai,
            baltymai, angliavandeniai, riebalai, kalcis, cholesterolis, skaidulines, butinaiMesa;
    private JButton patvirtintiPietums, isNaujo, issaugoti, itrauktiIKalendoriu;
    private JTextArea dietosPlanas;

    OptimaliDieta(int pasirinkimas) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        super("Optimalios dietos parinkimo sistema");      
        setSize(750, 350);
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
        menuBar.setVisible(false);
        
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        state = conn.createStatement();
        rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
        
        produktuSarasas = new ArrayList<>();
        
        while(rs.next())
            produktuSarasas.add(new JCheckBox(rs.getString("pavadinimas")));
        
        conn.close();
        
        for(int i = 0; i < produktuSarasas.size();i++)
            for (int j = 0; j < produktuSarasas.size()-i-1; j++)  
                if (produktuSarasas.get(j).getText().compareTo(produktuSarasas.get(j+1).getText())>0){
                    JCheckBox laikinas = produktuSarasas.get(j);
                    produktuSarasas.set(j,produktuSarasas.get(j+1));
                    produktuSarasas.set(j+1, laikinas);
                }
        
        produktuLangas = new JPanel();
        produktuSarasoLangas = new JPanel();
        produktuSarasoLangas.setLayout(new BoxLayout(produktuSarasoLangas, BoxLayout.Y_AXIS));
        
        for(int i=0;i<produktuSarasas.size();i++)
            produktuSarasoLangas.add(produktuSarasas.get(i));
        
        produktuSarasoLangas.add(new JLabel("\n\n"));
        produktuSarasoLangas.setBackground(new Color(166, 223, 32));
        
        produktuAntraste = new JLabel("Pažymėkite nepageidaujamus maisto produktus");
        
        produktuLangas.add(produktuAntraste);
        produktuLangas.setLocation(55, 25);
        produktuLangas.setSize(330, 270);
        
        slankjuostesLangas = new JScrollPane(produktuSarasoLangas);
        slankjuostesLangas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        slankjuostesLangas.setPreferredSize(new Dimension (330, 270));
        
        produktuLangas.add(slankjuostesLangas);
        add(produktuLangas);
        
        final JLabel paaiskinimas = new JLabel("Ieškoti pagal pavadinimą:");
        paaiskinimas.setLocation(390, 25);
        paaiskinimas.setSize(180, 15);
        add(paaiskinimas);
        
        final JTextField paieska = new JTextField("");
        paieska.setLocation(385, 40);
        paieska.setToolTipText("Paieškos tekstas");
        paieska.setSize(200, 25);
        add(paieska);
        
        paieska.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                produktuLangas.setVisible(false);
                    
                produktuLangas = new JPanel();
                produktuSarasoLangas = new JPanel();
                produktuSarasoLangas.setLayout(new BoxLayout(produktuSarasoLangas, BoxLayout.Y_AXIS));
                    
                for(int i=0;i<produktuSarasas.size();i++)
                    if(produktuSarasas.get(i).getText().toLowerCase()
                            .contains(paieska.getText().toLowerCase()))
                    produktuSarasoLangas.add(produktuSarasas.get(i));
                    
                produktuSarasoLangas.add(new JLabel("\n\n"));
                produktuSarasoLangas.setBackground(new Color(166, 223, 32));
                    
                produktuAntraste = new JLabel("Pažymėkite nepageidaujamus maisto produktus");
                    
                produktuLangas.add(produktuAntraste);
                produktuLangas.setLocation(55, 25);
                produktuLangas.setSize(330, 270);
                    
                slankjuostesLangas = new JScrollPane(produktuSarasoLangas);
                slankjuostesLangas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                slankjuostesLangas.setPreferredSize(new Dimension (330, 270));
                   
                produktuLangas.add(slankjuostesLangas);
                add(produktuLangas);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                produktuLangas.setVisible(false);
                    
                produktuLangas = new JPanel();
                produktuSarasoLangas = new JPanel();
                produktuSarasoLangas.setLayout(new BoxLayout(produktuSarasoLangas, BoxLayout.Y_AXIS));
                    
                for(int i=0;i<produktuSarasas.size();i++)
                    if(produktuSarasas.get(i).getText().toLowerCase()
                            .contains(paieska.getText().toLowerCase()))
                    produktuSarasoLangas.add(produktuSarasas.get(i));
                    
                produktuSarasoLangas.add(new JLabel("\n\n"));
                produktuSarasoLangas.setBackground(new Color(166, 223, 32));
                    
                produktuAntraste = new JLabel("Pažymėkite nepageidaujamus maisto produktus");
                    
                produktuLangas.add(produktuAntraste);
                produktuLangas.setLocation(55, 25);
                produktuLangas.setSize(330, 270);
                    
                slankjuostesLangas = new JScrollPane(produktuSarasoLangas);
                slankjuostesLangas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                slankjuostesLangas.setPreferredSize(new Dimension (330, 270));
                   
                produktuLangas.add(slankjuostesLangas);
                add(produktuLangas);
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {}
        });
        
        dietosPlanas = new JTextArea("");
        dietosPlanas.setEditable(false);
        
        slankjuoste = new JScrollPane(dietosPlanas);
        slankjuoste.setLocation(58, 30);
        slankjuoste.setSize(635, 500);
        add(slankjuoste);
           
        final JTextArea kmiLaukas = new JTextArea("");
        kmiLaukas.setLocation(105, 24);
        kmiLaukas.setSize(540, 195);
        kmiLaukas.setEditable(false);
        add(kmiLaukas);
        kmiLaukas.setBackground(new Color(166, 223, 32));

        final JRadioButton vyras = new JRadioButton("Vyras");
        vyras.setLocation(295, 78);
        vyras.setSize(90, 20);
        add(vyras);

        final JRadioButton moteris = new JRadioButton("Moteris");
        moteris.setLocation(295, 118);
        moteris.setSize(90, 20);
        add(moteris);
        
        final JRadioButton zemas = new JRadioButton("Žemas");
        zemas.setLocation(320, 60);
        zemas.setSize(90, 20);
        add(zemas);

        final JRadioButton vidutinis = new JRadioButton("Vidutinis");
        vidutinis.setLocation(320, 100);
        vidutinis.setSize(90, 20);
        add(vidutinis);
        
        final JRadioButton aukstas = new JRadioButton("Aukštas");
        aukstas.setLocation(320, 140);
        aukstas.setSize(90, 20);
        add(aukstas);
        
        final JRadioButton svorioMetimas = new JRadioButton("Numesti svorio");
        svorioMetimas.setLocation(310, 60);
        svorioMetimas.setSize(130, 20);
        add(svorioMetimas);
        
        final JRadioButton svorioPalaikymas = new JRadioButton("Palaikyti svorį");
        svorioPalaikymas.setLocation(310, 100);
        svorioPalaikymas.setSize(130, 20);
        add(svorioPalaikymas);
        
        final JRadioButton svorioPriaugimas = new JRadioButton("Priaugti svorio");
        svorioPriaugimas.setLocation(310, 140);
        svorioPriaugimas.setSize(130, 20);
        add(svorioPriaugimas);
        
        final JRadioButton tris = new JRadioButton("3 kartus");
        tris.setLocation(360, 112);
        tris.setSize(90, 20);
        add(tris);

        final JRadioButton penkis = new JRadioButton("5 kartus");
        penkis.setLocation(360, 152);
        penkis.setSize(90, 20);
        add(penkis);
        
        final JRadioButton viena = new JRadioButton("1 dienai");
        viena.setLocation(360, 92);
        viena.setSize(95, 20);
        add(viena);
        
        final JRadioButton trys = new JRadioButton("3 dienom");
        trys.setLocation(360, 132);
        trys.setSize(95, 20);
        add(trys);
        
        final JRadioButton septynios = new JRadioButton("savaitei");
        septynios.setLocation(360, 172);
        septynios.setSize(95, 20);
        add(septynios);
        
        final JRadioButton taip = new JRadioButton("Taip");
        taip.setLocation(430, 45);
        taip.setSize(60, 20);
        add(taip);
        
        final JRadioButton ne = new JRadioButton("Ne");
        ne.setLocation(500, 45);
        ne.setSize(50, 20);
        add(ne);
        
        butinaiMesa = new JCheckBox("Būtinai įtraukti mėsišką produktą pietums.");
        butinaiMesa.setLocation(150, 130);
        butinaiMesa.setSize(320, 20);
        add(butinaiMesa);
      
        mesa = new JCheckBox("Mėsa");
        mesa.setLocation(325, 40);
        mesa.setSize(130, 20);
        add(mesa);

        sokoladas = new JCheckBox("Šokoladas");
        sokoladas.setLocation(325, 60);
        sokoladas.setSize(130, 20);
        add(sokoladas);
        
        zuvis = new JCheckBox("Žuvis");
        zuvis.setLocation(325, 80);
        zuvis.setSize(130, 20);
        add(zuvis);
        
        riesutai = new JCheckBox("Riešutai");
        riesutai.setLocation(325, 100);
        riesutai.setSize(130, 20);
        add(riesutai);
        
        pienas = new JCheckBox("Pieno produktai");
        pienas.setLocation(325, 120);
        pienas.setSize(133, 20);
        add(pienas);
        
        medus = new JCheckBox("Medus");
        medus.setLocation(325, 140);
        medus.setSize(130, 20);
        add(medus);
        
        kiausiniai = new JCheckBox("Kiaušiniai");
        kiausiniai.setLocation(325, 160);
        kiausiniai.setSize(130, 20);
        add(kiausiniai);
        
        miltai = new JCheckBox("Miltų produktai");
        miltai.setLocation(325, 180);
        miltai.setSize(130, 20);
        add(miltai);
        
        kruopos = new JCheckBox("Kruopos");
        kruopos.setLocation(325, 200);
        kruopos.setSize(130, 20);
        add(kruopos);
        
        uogos = new JCheckBox("Uogos");
        uogos.setLocation(325, 220);
        uogos.setSize(130, 20);
        add(uogos);
        
        grybai = new JCheckBox("Grybai");
        grybai.setLocation(325, 240);
        grybai.setSize(130, 20);
        add(grybai);
        
        baltymai = new JCheckBox("Ignoruoti baltymus");
        baltymai.setLocation(375, 80);
        baltymai.setSize(200, 20);
        add(baltymai);
        
        angliavandeniai = new JCheckBox("Ignoruoti angliavandenius");
        angliavandeniai.setLocation(375, 100);
        angliavandeniai.setSize(200, 20);
        add(angliavandeniai);
        
        riebalai = new JCheckBox("Ignoruoti riebalus");
        riebalai.setLocation(375, 120);
        riebalai.setSize(150, 20);
        add(riebalai);
        
        kalcis = new JCheckBox("Ignoruoti kalcį");
        kalcis.setLocation(375, 140);
        kalcis.setSize(150, 20);
        add(kalcis);
        
        skaidulines = new JCheckBox("Ignoruoti skaidulines m.");
        skaidulines.setLocation(375, 160);
        skaidulines.setSize(200, 20);
        add(skaidulines);
        
        cholesterolis = new JCheckBox("Ignoruoti cholesterolį");
        cholesterolis.setLocation(375, 180);
        cholesterolis.setSize(200, 20);
        add(cholesterolis);

        final JTextField amziausLaukas = new JTextField();
        amziausLaukas.setSize(69, 28);
        amziausLaukas.setToolTipText("Jūsų amžius (metai)");
        amziausLaukas.setLocation(325, 95);
        add(amziausLaukas);

        final JTextField ugioLaukas = new JTextField();
        ugioLaukas.setSize(69, 28);
        ugioLaukas.setToolTipText("Jūsų ūgis (cm)");
        ugioLaukas.setLocation(325, 95);
        add(ugioLaukas);

        final JTextField svorioLaukas = new JTextField();
        svorioLaukas.setSize(69, 28);
        svorioLaukas.setToolTipText("Jūsų svoris (kg)");
        svorioLaukas.setLocation(325, 95);
        add(svorioLaukas);
        
        final JButton dietosSudarymas = new JButton("Asmeninis sveikos mitybos planas");
        dietosSudarymas.setLocation(230, 20);
        dietosSudarymas.setSize(275, 25);
        add(dietosSudarymas);
        
        final JButton informacija = new JButton("Maisto produktų duomenų bazė");
        informacija.setLocation(230,60);
        informacija.setSize(275,25);
        add(informacija);
        
        final JButton itraukimas = new JButton("Įtraukti naują produktą į duomenų bazę");
        itraukimas.setLocation(230,100);
        itraukimas.setSize(275,25);
        add(itraukimas);
        
        final JButton istrintiDietas = new JButton("Žiūrėti/trinti išsaugotas dietas");
        istrintiDietas.setLocation(230, 140);
        istrintiDietas.setSize(275, 25);
        add(istrintiDietas);
        
        final JButton kalendorius = new JButton("Dietų kalendorius");
        kalendorius.setLocation(230, 180);
        kalendorius.setSize(275, 25);
        add(kalendorius);
        
        final JButton svorioIstorija = new JButton("Svorio kitimo istorija");
        svorioIstorija.setLocation(230, 220);
        svorioIstorija.setSize(275, 25);
        add(svorioIstorija);
        
        final JButton kunoMasesIndeksas = new JButton("KMI Skaičiavimas");
        kunoMasesIndeksas.setLocation(230, 260);
        kunoMasesIndeksas.setSize(275, 25);
        add(kunoMasesIndeksas);
        
        final JButton naujiDuomenys = new JButton("Įvesti naujus duomenis");
        naujiDuomenys.setLocation(245, 130);
        naujiDuomenys.setSize(260, 25);
        add(naujiDuomenys);
        
        final JButton seniDuomenys = new JButton("Naudoti/trinti ankstesnius duomenis");
        seniDuomenys.setLocation(245, 160);
        seniDuomenys.setSize(260, 25);
        add(seniDuomenys);

        final JButton patvirtintiLyti = new JButton("Patvirtinti");
        patvirtintiLyti.setLocation(405, 97);
        patvirtintiLyti.setSize(100, 25);
        add(patvirtintiLyti);

        final JButton patvirtintiAmziu = new JButton("Patvirtinti");
        patvirtintiAmziu.setLocation(405, 97);
        patvirtintiAmziu.setSize(100, 25);
        add(patvirtintiAmziu);

        final JButton patvirtintiUgi = new JButton("Patvirtinti");
        patvirtintiUgi.setLocation(405, 97);
        patvirtintiUgi.setSize(100, 25);
        add(patvirtintiUgi);

        final JButton patvirtintiSvori = new JButton("Patvirtinti");
        patvirtintiSvori.setLocation(405, 97);
        patvirtintiSvori.setSize(100, 25);
        add(patvirtintiSvori);
        
        final JButton patvirtintiAktyvuma = new JButton("Patvirtinti");
        patvirtintiAktyvuma.setLocation(405, 97);
        patvirtintiAktyvuma.setSize(100, 25);
        add(patvirtintiAktyvuma);
        
        final JButton patvirtintiTiksla = new JButton("Patvirtinti");
        patvirtintiTiksla.setLocation(465, 97);
        patvirtintiTiksla.setSize(100, 25);
        add(patvirtintiTiksla);
        
        final JButton patvirtintiKategorija = new JButton("Patvirtinti");
        patvirtintiKategorija.setLocation(485, 117);
        patvirtintiKategorija.setSize(100, 25);
        add(patvirtintiKategorija);
        
        final JButton patvirtintiKartus = new JButton("Patvirtinti");
        patvirtintiKartus.setLocation(485, 130);
        patvirtintiKartus.setSize(100, 25);
        add(patvirtintiKartus);
        
        final JButton patvirtintiDienas = new JButton("Patvirtinti");
        patvirtintiDienas.setLocation(495, 130);
        patvirtintiDienas.setSize(100, 25);
        add(patvirtintiDienas);
        
        final JButton patvirtintiKriterijus = new JButton("Patvirtinti");
        patvirtintiKriterijus.setLocation(535, 130);
        patvirtintiKriterijus.setSize(100, 25);
        add(patvirtintiKriterijus);
        
        patvirtintiPietums = new JButton("Patvirtinti");
        patvirtintiPietums.setLocation(535, 130);
        patvirtintiPietums.setSize(100, 25);
        add(patvirtintiPietums);
        
        final JButton trinti = new JButton("Trinti įrašą");
        trinti.setLocation(475, 50);
        trinti.setSize(125, 25);
        add(trinti);
        
        final JButton patvirtintiTrynima = new JButton("Patvirtinti");
        patvirtintiTrynima.setLocation(550, 45);
        patvirtintiTrynima.setSize(100, 25);
        add(patvirtintiTrynima);
        
        final JButton patvirtintiDuomenis = new JButton("Sudaryti dietą");
        patvirtintiDuomenis.setLocation(600, 50);
        patvirtintiDuomenis.setSize(125, 25);
        add(patvirtintiDuomenis);
        
        final JButton generuoti1 = new JButton("Sudaryti dietą automatiškai");
        generuoti1.setLocation(460, 137);
        generuoti1.setSize(200, 25);
        add(generuoti1);
        
        final JButton generuoti2 = new JButton("Susidaryti dietą pačiam");
        generuoti2.setLocation(460, 177);
        generuoti2.setSize(200, 25);
        add(generuoti2);     
        
        final JButton apskaiciuotiKMI = new JButton("Apskaičiuoti KMI");
        apskaiciuotiKMI.setLocation(405, 97);
        apskaiciuotiKMI.setSize(140, 25);
        add(apskaiciuotiKMI);
        
        isNaujo = new JButton("Sudaryti dietą iš naujo");
        isNaujo.setLocation(55, 530);
        isNaujo.setSize(170, 25);
        add(isNaujo); 
        
        issaugoti = new JButton("Išsaugoti dietą");
        issaugoti.setLocation(562, 530);
        issaugoti.setSize(130, 25);
        add(issaugoti);
        
        itrauktiIKalendoriu = new JButton("Įtraukti į kalendorių");
        itrauktiIKalendoriu.setLocation(400, 530);
        itrauktiIKalendoriu.setSize(150, 25);
        add(itrauktiIKalendoriu);   
        
        final JButton produktuMygtukas = new JButton("Atsisakyti konkrečių produktų");
        produktuMygtukas.setLocation(250, 260);
        produktuMygtukas.setSize(230, 25);
        add(produktuMygtukas);
        
        final JButton kategorijuMygtukas = new JButton("Rinktis nepageidaujamas kategorijas");
        kategorijuMygtukas.setLocation(120, 300);
        kategorijuMygtukas.setSize(255, 25);
        add(kategorijuMygtukas);
        
        klausimas = new JLabel("Jūsų lytis: ");
        klausimas.setLocation(175, 102);
        klausimas.setSize(410, 15);
        add(klausimas);

        klaida = new JLabel("");
        klaida.setLocation(175, 170);
        klaida.setSize(380, 15);
        add(klaida);
        
        final JLabel sekmingaiIssaugota = new JLabel("Dieta išsaugota sėkmingai!");
        sekmingaiIssaugota.setLocation(425, 560);
        sekmingaiIssaugota.setSize(175, 15);
        add(sekmingaiIssaugota);
        
        final JLabel iveskiteUgi = new JLabel("Jūsų ūgis (cm):");
        iveskiteUgi.setLocation(225, 80);
        iveskiteUgi.setSize(90, 15);
        add(iveskiteUgi);
        
        final JLabel iveskiteSvori = new JLabel("Jūsų svoris (kg):");
        iveskiteSvori.setLocation(225, 120);
        iveskiteSvori.setSize(100, 15);
        add(iveskiteSvori);       

        final ButtonGroup lytiesGrupe = new ButtonGroup();
        lytiesGrupe.add(vyras); lytiesGrupe.add(moteris);
        
        final ButtonGroup aktyvumoGrupe = new ButtonGroup();
        aktyvumoGrupe.add(zemas); aktyvumoGrupe.add(vidutinis); aktyvumoGrupe.add(aukstas);
        
        final ButtonGroup tiksloGrupe = new ButtonGroup();
        tiksloGrupe.add(svorioMetimas); tiksloGrupe.add(svorioPalaikymas); tiksloGrupe.add(svorioPriaugimas);
        
        final ButtonGroup kartuGrupe = new ButtonGroup();
        kartuGrupe.add(tris); kartuGrupe.add(penkis);
        
        final ButtonGroup dienuGrupe = new ButtonGroup();
        dienuGrupe.add(viena); dienuGrupe.add(trys); dienuGrupe.add(septynios);
        
        final ButtonGroup trynimoGrupe = new ButtonGroup();
        trynimoGrupe.add(taip); trynimoGrupe.add(ne);
        
        amziausLaukas.setVisible(false); ugioLaukas.setVisible(false); svorioLaukas.setVisible(false); patvirtintiSvori.setVisible(false);
        patvirtintiLyti.setVisible(false); patvirtintiAmziu.setVisible(false); patvirtintiUgi.setVisible(false); penkis.setVisible(false);
        patvirtintiAktyvuma.setVisible(false); generuoti1.setVisible(false); generuoti2.setVisible(false); tris.setVisible(false);
        patvirtintiTiksla.setVisible(false); vyras.setVisible(false); moteris.setVisible(false); zemas.setVisible(false);
        vidutinis.setVisible(false); aukstas.setVisible(false); svorioMetimas.setVisible(false); svorioPalaikymas.setVisible(false); 
        svorioPriaugimas.setVisible(false);mesa.setVisible(false); sokoladas.setVisible(false); riesutai.setVisible(false);
        zuvis.setVisible(false); pienas.setVisible(false); medus.setVisible(false); kiausiniai.setVisible(false); itrauktiIKalendoriu.setVisible(false);
        miltai.setVisible(false); kruopos.setVisible(false); uogos.setVisible(false); grybai.setVisible(false); slankjuoste.setVisible(false);
        klausimas.setVisible(false); apskaiciuotiKMI.setVisible(false); trinti.setVisible(false); issaugoti.setVisible(false);
        iveskiteUgi.setVisible(false); iveskiteSvori.setVisible(false); kmiLaukas.setVisible(false); patvirtintiTrynima.setVisible(false);
        sekmingaiIssaugota.setVisible(false); patvirtintiDuomenis.setVisible(false); patvirtintiKategorija.setVisible(false);
        naujiDuomenys.setVisible(false); seniDuomenys.setVisible(false); patvirtintiKartus.setVisible(false); patvirtintiDienas.setVisible(false);
        patvirtintiKriterijus.setVisible(false); patvirtintiPietums.setVisible(false); baltymai.setVisible(false); angliavandeniai.setVisible(false);
        riebalai.setVisible(false); kalcis.setVisible(false); skaidulines.setVisible(false); cholesterolis.setVisible(false); viena.setVisible(false);
        trys.setVisible(false); septynios.setVisible(false); taip.setVisible(false); ne.setVisible(false); isNaujo.setVisible(false);
        kategorijuMygtukas.setVisible(false); paieska.setVisible(false); paaiskinimas.setVisible(false); produktuMygtukas.setVisible(false);
        butinaiMesa.setVisible(false); produktuLangas.setVisible(false);
        
        if(pasirinkimas!=0){
            menuBar.setVisible(true);
            dietosSudarymas.setVisible(false);
            informacija.setVisible(false);
            kunoMasesIndeksas.setVisible(false);
            itraukimas.setVisible(false);
            istrintiDietas.setVisible(false);
            kalendorius.setVisible(false);
            svorioIstorija.setVisible(false);
            
            if(pasirinkimas==1){
                klausimas.setVisible(true);
                patvirtintiLyti.setVisible(true);
                vyras.setVisible(true);
                moteris.setVisible(true);
            }

            if(pasirinkimas==2){
                try {
                    setSize(1200, 665);
                    menuBar.setLocation(288, 0);
                    naujiDuomenys.setVisible(false);
                    seniDuomenys.setVisible(false);
                    trinti.setVisible(true);
                    patvirtintiDuomenis.setVisible(true);
                    klausimas.setVisible(true);
                    klausimas.setLocation(420, 27);
                    klausimas.setText("");
                  
                    Class.forName("org.sqlite.JDBC");
                    conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    state = conn.createStatement();
                    rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                    
                    String[] stulpeliai = {"ID", "Amžius" ,"Lytis", "Ūgis",
                        "Svoris", "Aktyvumo lygis", "Tikslas", "Nepageidaujami produktai"};
                    
                    int i=0;
                    while(rs.next())i++;

                    if(i==0)klausimas.setText("Šiuo metu išsaugotų duomenų nėra");
                    
                    Object[][] turinys = new Object[i][8];
                    rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                    i=0;   
            
                    while(rs.next()){
                        String vartotojoLytis;
                        if(rs.getInt("lytis")==1)vartotojoLytis="Vyras";
                        else vartotojoLytis="Moteris";
                            
                        String vartotojoTikslas;
                        if(rs.getInt("tikslas")==1)vartotojoTikslas="Svorio metimas";
                        else if(rs.getInt("tikslas")==2)vartotojoTikslas="Svorio palaikymas";
                        else vartotojoTikslas="Svorio priaugimas";

                        String vartotojoAktyvumas;
                        if(rs.getInt("aktyvumas")==1)vartotojoAktyvumas="Žemas";
                        else if(rs.getInt("aktyvumas")==2)vartotojoAktyvumas="Vidutinis";
                        else vartotojoAktyvumas="Aukštas";
                            
                            String nepageidaujami="";
                            if(rs.getString("nepageidaujami").equals("grybai"))
                                nepageidaujami+="grybai";
                            else{
                                if(!rs.getString("nepageidaujami").equals("")){
                                    String[]produktai = rs.getString("nepageidaujami").split(";");
                                    for(int j=0;j<produktai.length;j++){
                                        if(j==5)nepageidaujami+="\n "+produktai[j]+", ";
                                        else nepageidaujami+=produktai[j]+", ";
                                    }
                                }
                                if(nepageidaujami.equals(""))
                                    nepageidaujami = "Nėra";
                                else nepageidaujami = nepageidaujami.substring(0,nepageidaujami.length()-2);
                            } 
                            
                        turinys[i][0]=rs.getInt("id");
                        turinys[i][1]=rs.getInt("amzius")+"m";
                        turinys[i][2]=vartotojoLytis;
                        turinys[i][3]=rs.getInt("ugis")+"cm";
                        turinys[i][4]=rs.getInt("svoris")+"kg";
                        turinys[i][5]=vartotojoAktyvumas;
                        turinys[i][6]=vartotojoTikslas;
                        turinys[i][7]=nepageidaujami.substring(0, 1).toUpperCase()+nepageidaujami.substring(1);
                        i++;
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
                    
                    lentele = new JTable(modelis);
                    
                    lentele.setBackground(new Color(255, 251, 111));
                    
                    lentele.setAutoCreateRowSorter(true);
                    lentele.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                     
                    duomenys = new JScrollPane(lentele);
                    duomenys.setLocation(0, 70);
                    duomenys.setSize(1200, 570);
                    add(duomenys);
                    
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(pasirinkimas==3){
                ugioLaukas.setVisible(true);
                svorioLaukas.setVisible(true);
                iveskiteUgi.setVisible(true);
                iveskiteSvori.setVisible(true);
                ugioLaukas.setLocation(325, 75);
                svorioLaukas.setLocation(325, 115);
                apskaiciuotiKMI.setVisible(true);
                setSize(750, 270);
            } 
        }
        
        dietosSudarymas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                menuBar.setVisible(true);
                dietosSudarymas.setVisible(false);
                informacija.setVisible(false);
                kunoMasesIndeksas.setVisible(false);
                itraukimas.setVisible(false);
                istrintiDietas.setVisible(false);
                kalendorius.setVisible(false);
                svorioIstorija.setVisible(false);
                naujiDuomenys.setVisible(true);
                seniDuomenys.setVisible(true);
            }
        });
        
        informacija.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    new ProduktuInformacija(true, new ArrayList<String>());
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            }
        });
        
        itraukimas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                new ProduktuItraukimas();
                dispose();
            }
        });
        
        kunoMasesIndeksas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                dietosSudarymas.setVisible(false);
                informacija.setVisible(false);
                kunoMasesIndeksas.setVisible(false);
                itraukimas.setVisible(false);
                istrintiDietas.setVisible(false);
                kalendorius.setVisible(false);
                svorioIstorija.setVisible(false);
                ugioLaukas.setVisible(true);
                svorioLaukas.setVisible(true);
                iveskiteUgi.setVisible(true);
                iveskiteSvori.setVisible(true);
                ugioLaukas.setLocation(325, 75);
                svorioLaukas.setLocation(325, 115);
                apskaiciuotiKMI.setVisible(true);
                setSize(750, 270);
                menuBar.setVisible(true);
            }
        });
        
        itrauktiIKalendoriu.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!dieta.contains("*")){
                    try {
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
                        int i=0;
                        if(mesa.isSelected()){
                            nepageidaujami+="mėsa; ";
                            i++;
                        }
                        if(sokoladas.isSelected()){
                            nepageidaujami+="šokoladas; ";
                            i++;
                        }
                        if(zuvis.isSelected()){
                            nepageidaujami+="žuvis; ";
                            i++;
                        }
                        if(riesutai.isSelected()){
                            nepageidaujami+="riešutai; ";
                            i++;
                        }
                        if(pienas.isSelected()){
                            nepageidaujami+="pieno produktai; ";
                            i++;
                        }
                        if(medus.isSelected()){
                            nepageidaujami+="medus; ";
                            i++;
                        }
                        if(kiausiniai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="kiaušiniai; ";
                            i++;
                        }
                        if(miltai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="miltų produktai; ";
                            i++;
                        }
                        if(kruopos.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="kruopos; ";
                            i++;
                        }
                        if(uogos.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="uogos; ";
                            i++;
                        }
                        if(grybai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="grybai; ";
                            i++;
                        }

                        if(nepageidaujami.equals("Nepageidaujami produktai: "))
                            nepageidaujami = "Nei viena produktų kategorija nepasirinkta kaip nepageidaujama.";

                        nepageidaujami += "\n Dieta sugeneruota automatiškai.\n\n";

                        new Kalendorius(vartotojoLytis+"amžius: "+amzius+"; ūgis: "+ugis+"; svoris: "+svoris+";"+
                            vartotojoTikslas+vartotojoAktyvumas+nepageidaujami+dieta);
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                }
            }
        });
        
        issaugoti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!dieta.contains("*")){
                    issaugoti.setVisible(false);
                    sekmingaiIssaugota.setVisible(true);
                    try {
                        Class.forName("org.sqlite.JDBC");
                        conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        state = conn.createStatement();
                        rs = state.executeQuery("select * from ISSAUGOTOS_DIETOS;");

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
                        int i=0;
                        if(mesa.isSelected()){
                            nepageidaujami+="mėsa; ";
                            i++;
                        }
                        if(sokoladas.isSelected()){
                            nepageidaujami+="šokoladas; ";
                            i++;
                        }
                        if(zuvis.isSelected()){
                            nepageidaujami+="žuvis; ";
                            i++;
                        }
                        if(riesutai.isSelected()){
                            nepageidaujami+="riešutai; ";
                            i++;
                        }
                        if(pienas.isSelected()){
                            nepageidaujami+="pieno produktai; ";
                            i++;
                        }
                        if(medus.isSelected()){
                            nepageidaujami+="medus; ";
                            i++;
                        }
                        if(kiausiniai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="kiaušiniai; ";
                            i++;
                        }
                        if(miltai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="miltų produktai; ";
                            i++;
                        }
                        if(kruopos.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="kruopos; ";
                            i++;
                        }
                        if(uogos.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="uogos; ";
                            i++;
                        }
                        if(grybai.isSelected()){
                            if(i==6)nepageidaujami+="\n ";
                            nepageidaujami+="grybai; ";
                            i++;
                        }

                        if(nepageidaujami.equals("Nepageidaujami produktai: "))
                            nepageidaujami = "Nei viena produktų kategorija nepasirinkta kaip nepageidaujama.";

                        nepageidaujami += "\n Dieta sugeneruota automatiškai.\n\n";

                        String itraukiamaDieta = vartotojoLytis+"amžius: "+amzius+"; ūgis: "+ugis+"; svoris: "+svoris+";"+
                            vartotojoTikslas+vartotojoAktyvumas+nepageidaujami+dieta;
                        
                        i = 1;
                        while(rs.next())i++;

                        PreparedStatement prep = conn.prepareStatement("insert into ISSAUGOTOS_DIETOS values(?,?);");
                        prep.setInt(1, i);
                        prep.setString(2, itraukiamaDieta);
                        prep.execute();
                        conn.close();
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        istrintiDietas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    new DietuInformacija();
                } catch (SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            }
        });
        
        kalendorius.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    new Kalendorius("");
                    dispose();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        svorioIstorija.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    new SvorioIstorija();
                    dispose();
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        apskaiciuotiKMI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    ugis=Integer.parseInt(ugioLaukas.getText());
                    svoris=Integer.parseInt(svorioLaukas.getText());
                    kmiLaukas.setVisible(true);
                    kmiLaukas.setText(" Pagal pateiktą ūgį ir svorį, Jūsų kūno masės indeksas yra: "
                            + ProduktuInformacija.suapvalinti(kmi(svoris,ugis)*1000000)+"\n\n Normalus KMI"
                            + " moterims yra – 18.8 – 23.8; vyrams – 20 – 25. \n\n I laipsnio nutukimas,"
                            + " kai KMI 25,5 – 29,9\n II laipsnio nutukimas, kai KMI 30 – 39,9\n III"
                            + " laipsnio nutukimas, kai KMI daugiau nei 40.\n\n Tiesa, kai kurie žmonės,"
                            + " pavyzdžiui, kultūristai, nėra laikomi nutukusiais, nors ir\n turi aukštą"
                            + " KMI. Žmogus laikomas nutukusiu tik tuo atveju, jei aukštas ne tik"
                            + " KMI,\n bet ir kūno riebalų kiekis.");   
                }catch(NumberFormatException ex){
                    klaida.setText("Prašome įvesti teisingą ūgį ir svorį!");
                }
            }
        });
        
        naujiDuomenys.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                naujiDuomenys.setVisible(false);
                seniDuomenys.setVisible(false);
                klausimas.setVisible(true);
                patvirtintiLyti.setVisible(true);
                vyras.setVisible(true);
                moteris.setVisible(true);
            }
        });
        
        seniDuomenys.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    setSize(1200, 665);
                    menuBar.setLocation(293, 0);
                    naujiDuomenys.setVisible(false);
                    seniDuomenys.setVisible(false);
                    trinti.setVisible(true);
                    patvirtintiDuomenis.setVisible(true);
                    klausimas.setVisible(true);
                    klausimas.setLocation(420, 27);
                    klausimas.setText("");
                  
                    Class.forName("org.sqlite.JDBC");
                    conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    state = conn.createStatement();
                    rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                    
                    String[] stulpeliai = {"ID", "Amžius" ,"Lytis", "Ūgis",
                        "Svoris", "Aktyvumo lygis", "Tikslas", "Nepageidaujami produktai"};
                    
                    int i=0;
                    while(rs.next())i++;
                    
                    if(i==0)klausimas.setText("Šiuo metu išsaugotų duomenų nėra");
                    
                    Object[][] turinys = new Object[i][8];
                    rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                    i=0;   
            
                    while(rs.next()){
                        String vartotojoLytis;
                        if(rs.getInt("lytis")==1)vartotojoLytis="Vyras";
                        else vartotojoLytis="Moteris";
                            
                        String vartotojoTikslas;
                        if(rs.getInt("tikslas")==1)vartotojoTikslas="Svorio metimas";
                        else if(rs.getInt("tikslas")==2)vartotojoTikslas="Svorio palaikymas";
                        else vartotojoTikslas="Svorio priaugimas";

                        String vartotojoAktyvumas;
                        if(rs.getInt("aktyvumas")==1)vartotojoAktyvumas="Žemas";
                        else if(rs.getInt("aktyvumas")==2)vartotojoAktyvumas="Vidutinis";
                        else vartotojoAktyvumas="Aukštas";
                            
                            String nepageidaujami="";
                            if(rs.getString("nepageidaujami").equals("grybai"))
                                nepageidaujami+="grybai";
                            else{
                                if(!rs.getString("nepageidaujami").equals("")){
                                    String[]produktai = rs.getString("nepageidaujami").split(";");
                                    for(int j=0;j<produktai.length;j++){
                                        if(j==5)nepageidaujami+="\n "+produktai[j]+", ";
                                        else nepageidaujami+=produktai[j]+", ";
                                    }
                                }
                                if(nepageidaujami.equals(""))
                                    nepageidaujami = "nėra";
                                else nepageidaujami = nepageidaujami.substring(0,nepageidaujami.length()-2);
                            } 
                        
                        turinys[i][0]=rs.getInt("id");
                        turinys[i][1]=rs.getInt("amzius")+"m";
                        turinys[i][2]=vartotojoLytis;
                        turinys[i][3]=rs.getInt("ugis")+"cm";
                        turinys[i][4]=rs.getInt("svoris")+"kg";
                        turinys[i][5]=vartotojoAktyvumas;
                        turinys[i][6]=vartotojoTikslas;
                        turinys[i][7]=nepageidaujami.substring(0, 1).toUpperCase()+nepageidaujami.substring(1);
                        i++;
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
                    
                    lentele = new JTable(modelis);
                    
                    lentele.setBackground(new Color(255, 251, 111));
                    
                    if(lentele.getRowCount()>0)
                        lentele.setAutoCreateRowSorter(true);
                    lentele.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    
                    duomenys = new JScrollPane(lentele);
                    duomenys.setLocation(0, 70);
                    duomenys.setSize(1200, 570);
                    add(duomenys);
                    
                    conn.close();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        patvirtintiLyti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!vyras.isSelected()&&!moteris.isSelected())klaida.setText("Prašome pasirinkti lytį!");
                else{
                    if(vyras.isSelected())lytis = 1;
                    if(moteris.isSelected())lytis = 2;
                    klaida.setText("");
                    klausimas.setText("Jūsų amžius (metai)?");
                    vyras.setVisible(false);
                    moteris.setVisible(false);
                    patvirtintiLyti.setVisible(false);
                    amziausLaukas.setVisible(true);
                    patvirtintiAmziu.setVisible(true);
            }}
        });
       
        patvirtintiAmziu.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    amzius=Integer.parseInt(amziausLaukas.getText());
                    klaida.setText("");
                    amziausLaukas.setVisible(false);
                    patvirtintiAmziu.setVisible(false);
                    patvirtintiUgi.setVisible(true);
                    ugioLaukas.setVisible(true);
                    klausimas.setText("Jūsų ūgis (cm)?");
                }catch(NumberFormatException ex){
                    klaida.setText("Prašome įvesti teisingą amžių!");
                }
            }
        });
        
        patvirtintiUgi.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    ugis=Integer.parseInt(ugioLaukas.getText());
                    klaida.setText("");
                    ugioLaukas.setVisible(false);
                    patvirtintiUgi.setVisible(false);
                    patvirtintiSvori.setVisible(true);
                    svorioLaukas.setVisible(true);
                    klausimas.setText("Jūsų svoris (kg)?");

                }catch(NumberFormatException ex){
                    klaida.setText("Prašome įvesti teisingą ūgį!");
                }
            }
        });
       
        patvirtintiSvori.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    svoris=Integer.parseInt(svorioLaukas.getText());
                    klaida.setText("");
                    svorioLaukas.setVisible(false);
                    patvirtintiSvori.setVisible(false);
                    patvirtintiAktyvuma.setVisible(true);
                    zemas.setVisible(true);
                    vidutinis.setVisible(true);
                    aukstas.setVisible(true);
                    klausimas.setLocation(80, 102);
                    klausimas.setText("Pasirinkite savo fizinio aktyvumo lygį: ");
                }catch(NumberFormatException ex){
                    klaida.setText("Prašome įvesti teisingą svorį!");
                }
            }
        });
        
        patvirtintiAktyvuma.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!zemas.isSelected()&&!vidutinis.isSelected()&&!aukstas.isSelected())klaida.setText("Prašome pasirinkti aktyvumą!");
                else{
                    if(zemas.isSelected())aktyvumas = 1;
                    if(vidutinis.isSelected())aktyvumas = 2;
                    if(aukstas.isSelected())aktyvumas = 3;
                    klaida.setText("");            
                    klausimas.setText("Pasirinkite norimą pasiekti tikslą.");
                    zemas.setVisible(false);
                    vidutinis.setVisible(false);
                    aukstas.setVisible(false);
                    patvirtintiAktyvuma.setVisible(false);     
                    patvirtintiTiksla.setVisible(true);
                    svorioMetimas.setVisible(true);
                    svorioPalaikymas.setVisible(true);
                    svorioPriaugimas.setVisible(true);
                    }
            }
        });
        
        patvirtintiTiksla.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!svorioMetimas.isSelected()&&!svorioPalaikymas.isSelected()&&!svorioPriaugimas.isSelected())
                    klaida.setText("Prašome pasirinkti tikslą!");
                else{
                    if(svorioMetimas.isSelected())tikslas = 1;
                    if(svorioPalaikymas.isSelected())tikslas = 2;
                    if(svorioPriaugimas.isSelected())tikslas = 3;
                    klaida.setText("");
                    svorioMetimas.setVisible(false);
                    svorioPalaikymas.setVisible(false);
                    svorioPriaugimas.setVisible(false);
                    patvirtintiTiksla.setVisible(false);     
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
                    produktuMygtukas.setVisible(true);
                    generuoti1.setVisible(true);
                    generuoti2.setVisible(true);
                    setSize(750, 380);
                    klausimas.setLocation(5, 132);
                    klausimas.setText("Pažymėkite nepageidaujamas produktų kategorijas.");
                }
            }
        });
        
        produktuMygtukas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
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
                produktuMygtukas.setVisible(false);
                kategorijuMygtukas.setVisible(true);
                produktuLangas.setVisible(true);
                paieska.setVisible(true);
                paaiskinimas.setVisible(true);
                klausimas.setText("");        
            }
        });
        
        kategorijuMygtukas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                kategorijuMygtukas.setVisible(false);
                produktuLangas.setVisible(false);
                paieska.setVisible(false);
                paaiskinimas.setVisible(false);
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
                produktuMygtukas.setVisible(true);
                klausimas.setText("Pažymėkite nepageidaujamas produktų kategorijas.");
            }
        });
        
        generuoti1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                setSize(750, 330);
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
                generuoti1.setVisible(false);
                generuoti2.setVisible(false);
                patvirtintiKategorija.setVisible(false);
                produktuLangas.setVisible(false);
                produktuMygtukas.setVisible(false);
                kategorijuMygtukas.setVisible(false);
                paaiskinimas.setVisible(false);
                paieska.setVisible(false);
                tris.setVisible(true);
                penkis.setVisible(true);
                patvirtintiKartus.setVisible(true);
                klaida.setLocation(175, 195);
                klausimas.setLocation(80, 132);
                klausimas.setText("Kiek kartų per dieną planuojate valgyti?");
            }
        });
        
        generuoti2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                ArrayList<String>nepageidaujamaKategorija = new ArrayList<>();
                if(mesa.isSelected())nepageidaujamaKategorija.add("mėsa");
                if(sokoladas.isSelected())nepageidaujamaKategorija.add("šokoladas");
                if(riesutai.isSelected())nepageidaujamaKategorija.add("riešutai");
                if(zuvis.isSelected())nepageidaujamaKategorija.add("žuvis");
                if(pienas.isSelected())nepageidaujamaKategorija.add("pienas");
                if(medus.isSelected())nepageidaujamaKategorija.add("medus");
                if(kiausiniai.isSelected())nepageidaujamaKategorija.add("kiaušiniai");
                if(miltai.isSelected())nepageidaujamaKategorija.add("miltai");
                if(kruopos.isSelected())nepageidaujamaKategorija.add("kruopos");
                if(uogos.isSelected())nepageidaujamaKategorija.add("uogos");
                if(grybai.isSelected())nepageidaujamaKategorija.add("grybai");
                
                ArrayList<String>nepageidaujamiProduktai = new ArrayList<>();
                
                for(int i=0;i<produktuSarasas.size();i++)
                    if(produktuSarasas.get(i).isSelected())
                        nepageidaujamiProduktai.add(produktuSarasas.get(i).getText());
                
                try {
                    Class.forName("org.sqlite.JDBC");
                    conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                    state = conn.createStatement();
                    rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                    
                    boolean leidimas = true;
                    
                    String nepageidaujami = "";

                    if(mesa.isSelected())nepageidaujami+="mėsa;";
                    if(sokoladas.isSelected())nepageidaujami+="šokoladas;";
                    if(zuvis.isSelected())nepageidaujami+="žuvis;";
                    if(riesutai.isSelected())nepageidaujami+="riešutai;";
                    if(pienas.isSelected())nepageidaujami+="pienas;";
                    if(medus.isSelected())nepageidaujami+="medus;";
                    if(kiausiniai.isSelected())nepageidaujami+="kiaušiniai;";
                    if(miltai.isSelected())nepageidaujami+="miltai;";
                    if(kruopos.isSelected())nepageidaujami+="kruopos;";
                    if(uogos.isSelected())nepageidaujami+="uogos;";
                    if(grybai.isSelected())nepageidaujami+="grybai";
                    
                    while(rs.next())
                        if(rs.getInt("aktyvumas")==aktyvumas&&rs.getInt("amzius")==amzius&&rs.getInt("lytis")==lytis&&rs.getInt("ugis")==ugis
                            &&rs.getInt("svoris")==svoris&&rs.getInt("tikslas")==tikslas&&rs.getString("nepageidaujami").equals(nepageidaujami)) leidimas=false;
                    
                    if(leidimas){
                        rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                        int i = 1;
                        while(rs.next())i++;

                        PreparedStatement prep = conn.prepareStatement("insert into NAUDOTOJO_DUOMENYS values(?,?,?,?,?,?,?,?);");
                        prep.setInt(1, i);
                        prep.setInt(2, aktyvumas);
                        prep.setInt(3, amzius);
                        prep.setInt(4, lytis);
                        prep.setInt(5, svoris);
                        prep.setInt(6, ugis);
                        prep.setInt(7, tikslas);
                        prep.setString(8, nepageidaujami);
                        prep.execute();
                    }
                    conn.close();
                    new DietaPaciam(amzius,lytis,ugis,svoris,tikslas,aktyvumas,nepageidaujamaKategorija,
                            nepageidaujamiProduktai);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            }
        });
        
        patvirtintiKartus.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!tris.isSelected()&&!penkis.isSelected())klaida.setText("Prašome pasirinkti valgių kiekį!");
                else{
                    if(tris.isSelected())kartai = 3;
                    if(penkis.isSelected())kartai = 5;
                    klaida.setText("");
                    klausimas.setText("Kokiam laikotarpiui dietą norite sudaryti?");
                    patvirtintiKartus.setVisible(false);
                    tris.setVisible(false);
                    penkis.setVisible(false);
                    patvirtintiDienas.setVisible(true);
                    viena.setVisible(true);
                    trys.setVisible(true);
                    septynios.setVisible(true);
                }
            }
        });
        
        patvirtintiDienas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!viena.isSelected()&&!trys.isSelected()&&!septynios.isSelected())klaida.setText("Prašome pasirinkti dienų skaičių!");
                    else{
                    if(viena.isSelected())dienos = 1;
                    if(trys.isSelected())dienos = 3;
                    if(septynios.isSelected())dienos = 7;
                    patvirtintiDienas.setVisible(false);
                    viena.setVisible(false);
                    trys.setVisible(false);
                    septynios.setVisible(false);
                    baltymai.setVisible(true);
                    angliavandeniai.setVisible(true); 
                    riebalai.setVisible(true);
                    kalcis.setVisible(true);
                    skaidulines.setVisible(true);
                    cholesterolis.setVisible(true);
                    patvirtintiKriterijus.setVisible(true);
                    klausimas.setText("Pažymėkite kriterijus, kuriuos norite ignoruoti");
                    klaida.setText("");
                }
            }
        });
        
        patvirtintiKriterijus.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                baltymai.setVisible(false);
                angliavandeniai.setVisible(false); 
                riebalai.setVisible(false);
                kalcis.setVisible(false);
                skaidulines.setVisible(false);
                cholesterolis.setVisible(false);
                patvirtintiKriterijus.setVisible(false);
                if(mesa.isSelected())
                    dietosGeneravimas(false);
                else{
                    butinaiMesa.setVisible(true);
                    patvirtintiPietums.setVisible(true);
                    klausimas.setText("");
                }
            }
        });
        
        patvirtintiPietums.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                dietosGeneravimas(butinaiMesa.isSelected());
            }
        });
        
        patvirtintiDuomenis.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(lentele.getSelectionModel().isSelectionEmpty())
                    klausimas.setText("Pasirinkite įrašą, kurio duomenis naudosite dietos sudarymui");
                else{
                    try{
                        Class.forName("org.sqlite.JDBC");
                        conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                        state = conn.createStatement();
                        rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS where id = "+lentele
                                .getModel().getValueAt(lentele.convertRowIndexToModel(lentele.getSelectedRow()),0)+";");
                        amzius = rs.getInt("amzius");
                        lytis = rs.getInt("lytis");
                        ugis = rs.getInt("ugis");
                        svoris = rs.getInt("svoris");
                        aktyvumas = rs.getInt("aktyvumas");
                        tikslas = rs.getInt("tikslas");
                        if(rs.getString("nepageidaujami").contains("mėsa"))mesa.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("šokoladas"))sokoladas.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("žuvis"))zuvis.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("riešutai"))riesutai.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("pienas"))pienas.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("medus"))medus.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("kiaušiniai"))kiausiniai.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("miltai"))miltai.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("kruopos"))kruopos.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("uogos"))uogos.setSelected(true);
                        if(rs.getString("nepageidaujami").contains("grybai"))grybai.setSelected(true);
                        
                        duomenys.setVisible(false);
                        trinti.setVisible(false);
                        patvirtintiDuomenis.setVisible(false);
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
                        produktuMygtukas.setVisible(true);
                        generuoti1.setVisible(true);
                        generuoti2.setVisible(true);
                        klausimas.setLocation(5, 132);
                        klausimas.setText("Pažymėkite nepageidaujamas produktų kategorijas.");
                        setSize(750, 380);
                        menuBar.setLocation(63, 0);
                        conn.close();
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        trinti.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                klausimas.setVisible(true);
                klausimas.setLocation(430, 27);
                klausimas.setText("Ar tikrai norite trinti šį įrašą?");
                if(lentele.getSelectionModel().isSelectionEmpty())
                        klausimas.setText("Pasirinkite įrašą, kurį norite ištrinti");
                else{
                    trinti.setVisible(false);
                    patvirtintiDuomenis.setVisible(false);
                    patvirtintiTrynima.setVisible(true);
                    taip.setVisible(true);
                    ne.setVisible(true); 
                }
            }
        });
        
        patvirtintiTrynima.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(taip.isSelected()||ne.isSelected()){
                    patvirtintiTrynima.setVisible(false);
                    taip.setVisible(false);
                    ne.setVisible(false);
                    trinti.setVisible(true);
                    patvirtintiDuomenis.setVisible(true);
                    
                    if(taip.isSelected()){
                        try{
                            Class.forName("org.sqlite.JDBC");
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
                            Statement state = conn.createStatement();
                            PreparedStatement prep = conn.prepareStatement("delete from NAUDOTOJO_DUOMENYS where id = ?;");
                            prep.setInt(1,lentele.convertRowIndexToModel(lentele.getSelectedRow())+1);
                            prep.executeUpdate();
                            ((DefaultTableModel)lentele.getModel())
                                .removeRow(lentele.convertRowIndexToModel(lentele.getSelectedRow()));
                            ResultSet rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                            int i = 1;
                            while(rs.next()){
                                prep = conn.prepareStatement("update NAUDOTOJO_DUOMENYS set id = ? where id = ?;");
                                prep.setInt(1, i);
                                prep.setInt(2, rs.getInt("id"));
                                prep.executeUpdate();
                                i++;
                            }

                            klausimas.setText("Duomenys ištrinti sėkmingai");
                            rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");

                            String[] stulpeliai = {"ID", "Amžius" ,"Lytis", "Ūgis",
                                "Svoris", "Aktyvumo lygis", "Tikslas", "Nepageidaujami produktai"};

                            i=0;
                            while(rs.next())i++;

                            if(i==0)klausimas.setText("Šiuo metu išsaugotų duomenų nėra");

                            Object[][] turinys = new Object[i][8];
                            rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                            i=0;   

                            while(rs.next()){
                                String vartotojoLytis;
                                if(rs.getInt("lytis")==1)vartotojoLytis="Vyras";
                                else vartotojoLytis="Moteris";

                                String vartotojoTikslas;
                                if(rs.getInt("tikslas")==1)vartotojoTikslas="Svorio metimas";
                                else if(rs.getInt("tikslas")==2)vartotojoTikslas="Svorio palaikymas";
                                else vartotojoTikslas="Svorio priaugimas";

                                String vartotojoAktyvumas;
                                if(rs.getInt("aktyvumas")==1)vartotojoAktyvumas="Žemas";
                                else if(rs.getInt("aktyvumas")==2)vartotojoAktyvumas="Vidutinis";
                                else vartotojoAktyvumas="Aukštas";

                                    String nepageidaujami="";
                                    if(rs.getString("nepageidaujami").equals("grybai"))
                                        nepageidaujami+="grybai";
                                    else{
                                        if(!rs.getString("nepageidaujami").equals("")){
                                            String[]produktai = rs.getString("nepageidaujami").split(";");
                                            for(int j=0;j<produktai.length;j++){
                                                if(j==5)nepageidaujami+="\n "+produktai[j]+", ";
                                                else nepageidaujami+=produktai[j]+", ";
                                            }
                                        }
                                        if(nepageidaujami.equals(""))
                                            nepageidaujami = "nėra";
                                        else nepageidaujami = nepageidaujami.substring(0,nepageidaujami.length()-2);
                                    } 

                                turinys[i][0]=rs.getInt("id");
                                turinys[i][1]=rs.getInt("amzius")+"m";
                                turinys[i][2]=vartotojoLytis;
                                turinys[i][3]=rs.getInt("ugis")+"cm";
                                turinys[i][4]=rs.getInt("svoris")+"kg";
                                turinys[i][5]=vartotojoAktyvumas;
                                turinys[i][6]=vartotojoTikslas;
                                turinys[i][7]=nepageidaujami.substring(0, 1).toUpperCase()+nepageidaujami.substring(1);
                                i++;
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

                            lentele = new JTable(modelis);
                            lentele.setBackground(new Color(255, 251, 111));

                            if(lentele.getRowCount()>0)
                                lentele.setAutoCreateRowSorter(true);
                            lentele.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                            remove(duomenys);
                            duomenys = new JScrollPane(lentele);
                            duomenys.setLocation(0, 70);
                            duomenys.setSize(1200, 570);
                            add(duomenys);
                            conn.close();
                        }catch(ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else
                        klausimas.setText("");
                    trynimoGrupe.clearSelection();
                }
            }
        });
        
        isNaujo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                sekmingaiIssaugota.setVisible(false);
                issaugoti.setVisible(true);
                dietosGeneravimas(butinaiMesa.isSelected());
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        this.getContentPane().setBackground(new Color(166, 223, 32));
        dietosPlanas.setBackground(new Color(166, 223, 32));
    }
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        try{
            UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[1].getClassName());
        }catch(Exception e){}
        new OptimaliDieta(0);
    }
    
    private double kmi(int x, int y){
        return (10000*Double.parseDouble(x+""))/(Double.parseDouble(y+"")*Double.parseDouble(y+""));
    }
    
    private void dietosGeneravimas(boolean pietus){
        
        ArrayList<String>nepageidaujamaKategorija = new ArrayList<>();
        
        if(mesa.isSelected())nepageidaujamaKategorija.add("mėsa");
        if(sokoladas.isSelected())nepageidaujamaKategorija.add("šokoladas");
        if(riesutai.isSelected())nepageidaujamaKategorija.add("riešutai");
        if(zuvis.isSelected())nepageidaujamaKategorija.add("žuvis");
        if(pienas.isSelected())nepageidaujamaKategorija.add("pienas");
        if(medus.isSelected())nepageidaujamaKategorija.add("medus");
        if(kiausiniai.isSelected())nepageidaujamaKategorija.add("kiaušiniai");
        if(miltai.isSelected())nepageidaujamaKategorija.add("miltai");
        if(kruopos.isSelected())nepageidaujamaKategorija.add("kruopos");
        if(uogos.isSelected())nepageidaujamaKategorija.add("uogos");
        if(grybai.isSelected())nepageidaujamaKategorija.add("grybai");

        ArrayList<String>nepageidaujamiProduktai = new ArrayList<>();

        for(int i=0;i<produktuSarasas.size();i++)
            if(produktuSarasas.get(i).isSelected())
                nepageidaujamiProduktai.add(produktuSarasas.get(i).getText());

        ArrayList<String>ignoruojami = new ArrayList<>();
        
        if(baltymai.isSelected())ignoruojami.add("baltymai");
        if(angliavandeniai.isSelected())ignoruojami.add("angliavandeniai");
        if(riebalai.isSelected())ignoruojami.add("riebalai");
        if(kalcis.isSelected())ignoruojami.add("kalcis");
        if(cholesterolis.isSelected())ignoruojami.add("cholesterolis");
        if(skaidulines.isSelected())ignoruojami.add("skaidulines");

        patvirtintiPietums.setVisible(false);
        butinaiMesa.setVisible(false);
        slankjuoste.setVisible(true);
        isNaujo.setVisible(true);
        issaugoti.setVisible(true);
        itrauktiIKalendoriu.setVisible(true);
        klausimas.setText("");
        klaida.setText("");
        setSize(750, 620);
        slankjuoste.setSize(635, 500);
        menuBar.setLocation(63, 0);
        Skaiciavimai s = new Skaiciavimai();
        double aktyvumoKoeficientas=1.5;
        int kalorijuPoreikis=0;
        if(aktyvumas==2)aktyvumoKoeficientas=1.7;
        if(aktyvumas==3)aktyvumoKoeficientas=2;
        if(lytis == 1)kalorijuPoreikis = (int)Math.round(aktyvumoKoeficientas*(65.4 + (13.7 * svoris) + (5 * ugis) - (6.8 * amzius)));
        if(lytis == 2)kalorijuPoreikis = (int)Math.round(aktyvumoKoeficientas*(655 + (9.6 * svoris) + (1.8 * ugis) - (4.7 * amzius)));
        if(tikslas == 1)kalorijuPoreikis-=500;
        if(tikslas == 3)kalorijuPoreikis+=500;

        try {
            dieta = s.dietosSudarymas(kmi(svoris,ugis),kalorijuPoreikis,aktyvumas,amzius,lytis,kartai,dienos,
                    nepageidaujamaKategorija,nepageidaujamiProduktai,ignoruojami,pietus);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }

        dietosPlanas.setText(" Pagal pateiktus duomenis, rekomenduojama paros "
                + "energijos suvartojimo norma yra: "+kalorijuPoreikis+"kcal\n\n"+s.medziagos(kmi(svoris,ugis),aktyvumas,kalorijuPoreikis,amzius,lytis)
                + " Pavyzdinė dieta:\n\n"
                + dieta);

        dietosPlanas.select(0,0);

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
            state = conn.createStatement();
            rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");

            boolean leidimas = true;
            String nepageidaujami = "";

            if(mesa.isSelected())nepageidaujami+="mėsa;";
            if(sokoladas.isSelected())nepageidaujami+="šokoladas;";
            if(zuvis.isSelected())nepageidaujami+="žuvis;";
            if(riesutai.isSelected())nepageidaujami+="riešutai;";
            if(pienas.isSelected())nepageidaujami+="pienas;";
            if(medus.isSelected())nepageidaujami+="medus;";
            if(kiausiniai.isSelected())nepageidaujami+="kiaušiniai;";
            if(miltai.isSelected())nepageidaujami+="miltai;";
            if(kruopos.isSelected())nepageidaujami+="kruopos;";
            if(uogos.isSelected())nepageidaujami+="uogos;";
            if(grybai.isSelected())nepageidaujami+="grybai";

            while(rs.next())
                if(rs.getInt("aktyvumas")==aktyvumas&&rs.getInt("amzius")==amzius&&rs.getInt("lytis")==lytis&&rs.getInt("ugis")==ugis
                        &&rs.getInt("svoris")==svoris&&rs.getInt("tikslas")==tikslas&&rs.getString("nepageidaujami").equals(nepageidaujami))
                    leidimas=false;

            if(leidimas){
                rs = state.executeQuery("select * from NAUDOTOJO_DUOMENYS;");
                int i = 1;
                while(rs.next())i++;

                PreparedStatement prep = conn.prepareStatement("insert into NAUDOTOJO_DUOMENYS values(?,?,?,?,?,?,?,?);");
                prep.setInt(1, i);
                prep.setInt(2, aktyvumas);
                prep.setInt(3, amzius);
                prep.setInt(4, lytis);
                prep.setInt(5, svoris);
                prep.setInt(6, ugis);
                prep.setInt(7, tikslas);
                prep.setString(8, nepageidaujami);
                prep.execute();
            }
            conn.close();
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(OptimaliDieta.class.getName()).log(Level.SEVERE, null, ex);
            }
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