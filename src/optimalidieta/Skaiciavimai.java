package optimalidieta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Skaiciavimai {
    
    private int dietosGeneravimoTrukme=0;
    
    String medziagos(double kmi, int aktyvumas, int kalorijuPoreikis, int amzius, int lytis){
        String baltymai, angliavandeniai, riebalai, kalcis; 
        if(kmi>25.5){
            baltymai = (int)Math.round((kalorijuPoreikis*25)/400)+" - "+(int)Math.round((kalorijuPoreikis*35)/400)+"g"; 
            angliavandeniai = (int)Math.round((kalorijuPoreikis*45)/400)+" - "+(int)Math.round((kalorijuPoreikis*55)/400)+"g";
            riebalai = (int)Math.round((kalorijuPoreikis*15)/900)+" - "+(int)Math.round((kalorijuPoreikis*25)/900)+"g";
        }else{
            if(aktyvumas==1){
                baltymai = (int)Math.round((kalorijuPoreikis*10)/400)+" - "+(int)Math.round((kalorijuPoreikis*20)/400)+"g"; 
                angliavandeniai = (int)Math.round((kalorijuPoreikis*55)/400)+" - "+(int)Math.round((kalorijuPoreikis*75)/400)+"g";
                riebalai = (int)Math.round((kalorijuPoreikis*10)/900)+" - "+(int)Math.round((kalorijuPoreikis*15)/900)+"g";
            }else{
                baltymai = (int)Math.round((kalorijuPoreikis*15)/400)+" - "+(int)Math.round((kalorijuPoreikis*20)/400)+"g"; 
                angliavandeniai = (int)Math.round((kalorijuPoreikis*55)/400)+" - "+(int)Math.round((kalorijuPoreikis*60)/400)+"g";
                riebalai = (int)Math.round((kalorijuPoreikis*15)/900)+" - "+(int)Math.round((kalorijuPoreikis*20)/900)+"g";
            }
        }
        
        if(amzius==0){
            kalcis = "0.24 - 0.26g";
            }else if(amzius>0&&amzius<4){
                kalcis = "0.69 - 0.71g";
            }else if(amzius>3&&amzius<9){
                kalcis = "0.99 - 1.01g";
            }else if(amzius>8&&amzius<14){
                kalcis = "1.29 - 1.31g";
            }else if(amzius>13&&amzius<19){
                kalcis = "1.29 - 1.31g";
            }else if(amzius>18&&amzius<51){
                kalcis = "0.99 - 1.01g";
            }else if(amzius>50&&amzius<71){
                if(lytis==1){
                    kalcis = "0.99 - 1.01g";
                }
                else{
                    kalcis = "1.19 - 1.21g";
                } 
            }else{
                kalcis = "1.19 - 1.21g";
            }
        return " Rekomenduojama suvartoti: "+baltymai+" baltymų, "+angliavandeniai+" angliavandenių, "+riebalai+" riebalų,\n "+
                kalcis+" kalcio ir "+((int)Math.round((kalorijuPoreikis*14)/1000)-5)+" - "
                +((int)Math.round((kalorijuPoreikis*14)/1000)+5)+"g skaidulinių medžiagų.\n\n";
    }
    
    String dietosSudarymas(double kmi, int kalorijuPoreikis, int aktyvumas, int amzius, int lytis, 
            int kartai, int dienos, ArrayList<String>nepageidaujamaKategorija,
            ArrayList<String>nepageidaujamiProduktai, ArrayList<String>ignoruojami,
            boolean mesaPietums)
            throws ClassNotFoundException, SQLException{
        
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");
        
        ArrayList<Integer>kalorijos = new ArrayList<>();
        ArrayList<Integer>baltymai = new ArrayList<>();
        ArrayList<Integer>angliavandeniai = new ArrayList<>();
        ArrayList<Integer>riebalai = new ArrayList<>();
        ArrayList<Integer>skaidulines = new ArrayList<>();
        ArrayList<Integer>kalcis = new ArrayList<>();
        ArrayList<Integer>cholesterolis = new ArrayList<>();
        ArrayList<Integer>porcija = new ArrayList<>();
        ArrayList<String>pavadinimas = new ArrayList<>();
        ArrayList<Integer>laikas = new ArrayList<>();
        ArrayList<String>kategorija = new ArrayList<>();
        ArrayList<Boolean>skystis = new ArrayList<>();
        boolean leidimas;
        while(rs.next()){
            leidimas = true;
            for(String nk : nepageidaujamaKategorija)
                if(rs.getString("kategorija").contains(nk))
                    leidimas = false;
            for(String np : nepageidaujamiProduktai)
                if(rs.getString("pavadinimas").equals(np))
                    leidimas = false;

            if(leidimas){
                pavadinimas.add(rs.getString("pavadinimas"));
                kalorijos.add(rs.getInt("energine_verte"));
                baltymai.add(rs.getInt("baltymai"));
                angliavandeniai.add(rs.getInt("angliavandeniai"));
                riebalai.add(rs.getInt("riebalai"));
                skaidulines.add(rs.getInt("skaidulines_medziagos"));
                kalcis.add(rs.getInt("kalcis"));
                cholesterolis.add(rs.getInt("cholesterolis"));
                porcija.add(rs.getInt("standartine_porcija"));
                kategorija.add(rs.getString("kategorija"));
                skystis.add(rs.getBoolean("skystis"));
                laikas.add(rs.getInt("laikas"));
            }
        }
        conn.close();
        
        int baltymaiMin, baltymaiMax, angliavandeniaiMin, angliavandeniaiMax, riebalaiMin, riebalaiMax,
            kalcisMin, kalcisMax, skaidulinesMin = (((kalorijuPoreikis*14)/1000)*1000000)-5000000, 
            skaidulinesMax = (((kalorijuPoreikis*14)/1000)*1000000)+5000000;
        if(kmi>25.5){
            baltymaiMin=(kalorijuPoreikis*25)/100; baltymaiMax=(kalorijuPoreikis*35)/100;
            angliavandeniaiMin=(kalorijuPoreikis*45)/100; angliavandeniaiMax=(kalorijuPoreikis*55)/100;
            riebalaiMin=(kalorijuPoreikis*15)/100; riebalaiMax=(kalorijuPoreikis*25)/100;
        }else{
            if(aktyvumas==1){
                baltymaiMin=(kalorijuPoreikis*10)/100; baltymaiMax=(kalorijuPoreikis*20)/100;
                angliavandeniaiMin=(kalorijuPoreikis*55)/100; angliavandeniaiMax=(kalorijuPoreikis*75)/100;
                riebalaiMin=(kalorijuPoreikis*10)/100; riebalaiMax=(kalorijuPoreikis*15)/100;
            }else{
                baltymaiMin=(kalorijuPoreikis*15)/100; baltymaiMax=(kalorijuPoreikis*20)/100;
                angliavandeniaiMin=(kalorijuPoreikis*55)/100; angliavandeniaiMax=(kalorijuPoreikis*60)/100;
                riebalaiMin=(kalorijuPoreikis*15)/100; riebalaiMax=(kalorijuPoreikis*20)/100;
            }
        }
            
        if(amzius==0){
            kalcisMin=240000; kalcisMax=260000;
        }else if(amzius>0&&amzius<4){
            kalcisMin=690000; kalcisMax=710000;
        }else if(amzius>3&&amzius<9){
            kalcisMin=990000; kalcisMax=1010000;
        }else if(amzius>8&&amzius<14){
            kalcisMin=1290000; kalcisMax=1310000;
        }else if(amzius>13&&amzius<19){
            kalcisMin=1290000; kalcisMax=1310000;
        }else if(amzius>18&&amzius<51){
            kalcisMin=990000; kalcisMax=1010000;
        }else if(amzius>50&&amzius<71){
            if(lytis==1){
                kalcisMin=990000; kalcisMax=1010000;
            }
            else{
                kalcisMin=1190000; kalcisMax=1210000;
            } 
        }else{
            kalcisMin=1190000; kalcisMax=1210000;
        }
            
        Random r = new Random();
        int bandymas = 0, kartoti = dienos, cholesterolioNorma=300000;
        String rezultatas="";
        
        int pusryciuKal, priespieciuKal=0, pietuKal, pavakariuKal=0, vakarienesKal;
        
        if(kartai==3){
            pusryciuKal = (kalorijuPoreikis*35)/100;
            pietuKal = (kalorijuPoreikis*4)/10;
            vakarienesKal = (kalorijuPoreikis*25)/100;
        }
        else{
            pusryciuKal = (kalorijuPoreikis*3)/10;
            priespieciuKal = kalorijuPoreikis/10;
            pietuKal = (kalorijuPoreikis*3)/10;
            pavakariuKal = kalorijuPoreikis/10;
            vakarienesKal = kalorijuPoreikis/5;
        }
        
        int p0=0, p1=0, p2=0, p3=0, pm=0;
            
        for(int i=0;i<pavadinimas.size();i++){
            if(laikas.get(i)==0&&!skystis.get(i))p0++;
            if(laikas.get(i)==1&&!skystis.get(i))p1++;
            if(laikas.get(i)==2&&!skystis.get(i))p2++;
            if(laikas.get(i)==3&&!skystis.get(i))p3++;
            if(laikas.get(i)==2&&kategorija.get(i).contains("mėsa")&&!skystis.get(i))pm++;
        }
        
        if(p1==0||p2==0||p3==0||(p0==0&&kartai==5)||(pm==0&&mesaPietums))
            return " * Dietos sudaryti neįmanoma. Prašome pasirinkti mažiau produktų, kaip nepageidaujamus, arba"
                    + "\n įtraukite naujų produktų į duomenų bazę.";
        
        final Timer laikmatis = new Timer();
        TimerTask generavimoLaikas = new TimerTask(){
            @Override
            public void run() {
               dietosGeneravimoTrukme++;
            }  
        };
        laikmatis.scheduleAtFixedRate(generavimoLaikas, 1000, 1000);
        while(true){
            if(dietosGeneravimoTrukme>60&&kartoti==dienos&&bandymas>50000){
                laikmatis.cancel();
                return " * Pilnavertės dietos sudaryti nepavyko. Prašome pasirinkti mažiau produktų, kaip"
                        + " nepageidaujamus,\n arba įtraukite naujų produktų į duomenų bazę.";
            }
            bandymas++;
            ArrayList<Integer>pusryciuPr = new ArrayList<>();
            ArrayList<Integer>priespieciuPr = new ArrayList<>();
            ArrayList<Integer>pietuPr = new ArrayList<>();
            ArrayList<Integer>pavakariuPr = new ArrayList<>();
            ArrayList<Integer>vakarienesPr = new ArrayList<>();
            ArrayList<Integer>pusryciuKiekiai = new ArrayList<>();
            ArrayList<Integer>priespieciuKiekiai = new ArrayList<>();
            ArrayList<Integer>pietuKiekiai = new ArrayList<>();
            ArrayList<Integer>pavakariuKiekiai = new ArrayList<>();
            ArrayList<Integer>vakarienesKiekiai = new ArrayList<>();
            ArrayList<String>kategorijos = new ArrayList<>();
            int produktas=0, turimosKalorijos=0;
            
            while(true){
                produktas = r.nextInt(kalorijos.size());
                if(laikas.get(produktas)==1){
                    pusryciuPr.add(produktas);
                    pusryciuKiekiai.add(((pusryciuKal*100)/kalorijos.get(produktas)+5)/10*10);
                    break;
                }
            }

            int papildomas=0;
            int kartojimas=0;
            leidimas = true;
            if(kartai==5){
                while(true){
                    kartojimas++;
                    produktas = r.nextInt(kalorijos.size());
                    if(laikas.get(produktas)==0&&!kategorijos.contains(kategorija.get(produktas))
                            &&(leidimas||!skystis.get(produktas))){

                        if(skystis.get(produktas))leidimas=false;
                        kategorijos.add(kategorija.get(produktas));
                        priespieciuPr.add(produktas);
                        priespieciuKiekiai.add(porcija.get(produktas));
                        turimosKalorijos+=(porcija.get(produktas)*kalorijos.get(produktas))/100;
                    }
                    if(turimosKalorijos+30>priespieciuKal)break;
                    if(kartojimas%10==0&&priespieciuPr.size()>1){
                        produktas = r.nextInt(priespieciuPr.size());
                        if(!skystis.get(priespieciuPr.get(produktas))
                                &&(priespieciuKiekiai.get(produktas)/porcija.get(priespieciuPr.get(produktas))<2)){
                            turimosKalorijos+=(porcija.get(priespieciuPr.get(produktas))*kalorijos.get(priespieciuPr.get(produktas)))/100;
                            priespieciuKiekiai.set(produktas, (porcija.get(priespieciuPr.get(produktas))/2)+priespieciuKiekiai.get(produktas));
                        }else{
                            papildomas++;
                            if(papildomas%10==0){
                                if(!skystis.get(priespieciuPr.get(produktas))){
                                    turimosKalorijos+=((porcija.get(priespieciuPr.get(produktas))/2)*kalorijos.get(priespieciuPr.get(produktas)))/100;
                                    priespieciuKiekiai.set(produktas, (porcija.get(priespieciuPr.get(produktas))/2)+priespieciuKiekiai.get(produktas));
                                }
                            }
                        }
                    }
                }
            }

            kategorijos = new ArrayList<>();
            turimosKalorijos=0;
            kartojimas=0;
            papildomas=0;
            leidimas=true;
            boolean darzove=false;
            while(true){
                kartojimas++;
                produktas = r.nextInt(kalorijos.size());
                if(laikas.get(produktas)==2&&!kategorijos.contains(kategorija.get(produktas))
                        &&(leidimas||!skystis.get(produktas))
                        &&(!darzove||!kategorija.get(produktas).contains("daržovės"))){
                    if(kategorija.get(produktas).contains("daržovės"))darzove=true;
                    if(skystis.get(produktas))leidimas=false;
                    kategorijos.add(kategorija.get(produktas));
                    pietuPr.add(produktas);
                    pietuKiekiai.add(porcija.get(produktas));
                    turimosKalorijos+=(porcija.get(produktas)*kalorijos.get(produktas))/100;
                }
                if(turimosKalorijos+30>pietuKal){
                    if(mesaPietums){
                        leidimas=false;
                        for(int i=0;i<pietuPr.size();i++)
                            if(kategorija.get(pietuPr.get(i)).contains("mėsa"))
                                leidimas=true;
                        if(leidimas)
                            break;
                        else{
                            pietuPr = new ArrayList<>();
                            pietuKiekiai = new ArrayList<>();
                            kategorijos = new ArrayList<>();
                            turimosKalorijos=0;
                            kartojimas=0;
                            papildomas=0;
                            leidimas=true;
                            darzove=false;
                        }
                    }
                    else
                        break;
                }
                if(kartojimas%10==0&&pietuPr.size()>1){
                    produktas = r.nextInt(pietuPr.size());
                    if(!skystis.get(pietuPr.get(produktas))
                            &&(pietuKiekiai.get(produktas)/porcija.get(pietuPr.get(produktas))<2)){
                        turimosKalorijos+=((porcija.get(pietuPr.get(produktas))/2)*kalorijos.get(pietuPr.get(produktas)))/100;
                        pietuKiekiai.set(produktas, (porcija.get(pietuPr.get(produktas))/2)+pietuKiekiai.get(produktas));
                    }
                    else{
                        papildomas++;
                        if(papildomas%10==0){
                            if(!skystis.get(pietuPr.get(produktas))){
                                turimosKalorijos+=(porcija.get(pietuPr.get(produktas))*kalorijos.get(pietuPr.get(produktas)))/100;
                                pietuKiekiai.set(produktas, (porcija.get(pietuPr.get(produktas))/2)+pietuKiekiai.get(produktas));
                            }
                        }
                    }
                }
            }
            
            if(kartai==5){
                leidimas=true;
                kategorijos = new ArrayList<>();
                turimosKalorijos=0;
                kartojimas=0;
                papildomas=0;
                while(true){
                    kartojimas++;
                    produktas = r.nextInt(kalorijos.size());
                    if(laikas.get(produktas)==0&&!kategorijos.contains(kategorija.get(produktas))
                            &&(leidimas||!skystis.get(produktas))){

                        if(skystis.get(produktas))
                            leidimas=false;
                        kategorijos.add(kategorija.get(produktas));
                        pavakariuPr.add(produktas);
                        pavakariuKiekiai.add(porcija.get(produktas));
                        turimosKalorijos+=(porcija.get(produktas)*kalorijos.get(produktas))/100;
                    }
                    if(turimosKalorijos+30>pavakariuKal)break;
                    if(kartojimas%10==0&&pavakariuPr.size()>1){
                        produktas = r.nextInt(pavakariuPr.size());
                        if(!skystis.get(pavakariuPr.get(produktas))
                                &&(pavakariuKiekiai.get(produktas)/porcija.get(pavakariuPr.get(produktas))<2)){
                            turimosKalorijos+=(porcija.get(pavakariuPr.get(produktas))*kalorijos.get(pavakariuPr.get(produktas)))/100;
                            pavakariuKiekiai.set(produktas, (porcija.get(pavakariuPr.get(produktas))/2)+pavakariuKiekiai.get(produktas));
                        }else{
                            papildomas++;
                            if(papildomas%10==0){
                                if(!skystis.get(pavakariuPr.get(produktas))){
                                    turimosKalorijos+=((porcija.get(pavakariuPr.get(produktas))/2)*kalorijos.get(pavakariuPr.get(produktas)))/100;
                                    pavakariuKiekiai.set(produktas, (porcija.get(pavakariuPr.get(produktas))/2)+pavakariuKiekiai.get(produktas));
                                }
                            }
                        }
                    }
                }
            }

            leidimas=true;
            kategorijos = new ArrayList<>();
            turimosKalorijos=0;
            kartojimas=0;
            papildomas=0;
            while(true){
                kartojimas++;
                produktas = r.nextInt(kalorijos.size());
                if(laikas.get(produktas)==3&&!kategorijos.contains(kategorija.get(produktas))
                        &&(leidimas||!skystis.get(produktas))){
                    
                    if(skystis.get(produktas))
                        leidimas=false;
                    kategorijos.add(kategorija.get(produktas));
                    vakarienesPr.add(produktas);
                    vakarienesKiekiai.add(porcija.get(produktas));
                    turimosKalorijos+=(porcija.get(produktas)*kalorijos.get(produktas))/100;
                }
                if(turimosKalorijos+30>vakarienesKal)break;
                if(kartojimas%10==0&&vakarienesPr.size()>1){
                    produktas = r.nextInt(vakarienesPr.size());
                    if(!skystis.get(vakarienesPr.get(produktas))
                            &&(vakarienesKiekiai.get(produktas)/porcija.get(vakarienesPr.get(produktas))<2)){
                        turimosKalorijos+=(porcija.get(vakarienesPr.get(produktas))*kalorijos.get(vakarienesPr.get(produktas)))/100;
                        vakarienesKiekiai.set(produktas, (porcija.get(vakarienesPr.get(produktas))/2)+vakarienesKiekiai.get(produktas));
                    }else{
                        papildomas++;
                        if(papildomas%10==0){
                            if(!skystis.get(vakarienesPr.get(produktas))){
                                turimosKalorijos+=((porcija.get(vakarienesPr.get(produktas))/2)*kalorijos.get(vakarienesPr.get(produktas)))/100;
                                vakarienesKiekiai.set(produktas, (porcija.get(vakarienesPr.get(produktas))/2)+vakarienesKiekiai.get(produktas));
                            }
                        }
                    }
                }
            }

            leidimas = false;
            int baltymuKiekis=0,angliavandeniuKiekis=0,riebaluKiekis=0,skaidulinesMedziagos=0,
                    kalcioKiekis=0, cholesterolioKiekis=0;

            for(int i=0; i<pusryciuPr.size(); i++)
                for(int j=0;j<pusryciuKiekiai.get(i);j+=100)
                    if(pusryciuKiekiai.get(i)-j>99){
                        baltymuKiekis+=baltymai.get(pusryciuPr.get(i));
                        angliavandeniuKiekis+=angliavandeniai.get(pusryciuPr.get(i));
                        riebaluKiekis+=riebalai.get(pusryciuPr.get(i));
                        skaidulinesMedziagos+=skaidulines.get(pusryciuPr.get(i));
                        kalcioKiekis+=kalcis.get(pusryciuPr.get(i));
                        cholesterolioKiekis+=cholesterolis.get(pusryciuPr.get(i));
                    }else{
                        baltymuKiekis+=(baltymai.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                        angliavandeniuKiekis+=(angliavandeniai.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                        riebaluKiekis+=(riebalai.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                        skaidulinesMedziagos+=(skaidulines.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                        kalcioKiekis+=(kalcis.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                        cholesterolioKiekis+=(cholesterolis.get(pusryciuPr.get(i))*(pusryciuKiekiai.get(i)-j))/100;
                    }

            for(int i=0; i<priespieciuPr.size(); i++)
                for(int j=0;j<priespieciuKiekiai.get(i);j+=100)
                    if(priespieciuKiekiai.get(i)-j>99){
                        baltymuKiekis+=baltymai.get(priespieciuPr.get(i));
                        angliavandeniuKiekis+=angliavandeniai.get(priespieciuPr.get(i));
                        riebaluKiekis+=riebalai.get(priespieciuPr.get(i));
                        skaidulinesMedziagos+=skaidulines.get(priespieciuPr.get(i));
                        kalcioKiekis+=kalcis.get(priespieciuPr.get(i));
                        cholesterolioKiekis+=cholesterolis.get(priespieciuPr.get(i));
                    }else{
                        baltymuKiekis+=(baltymai.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                        angliavandeniuKiekis+=(angliavandeniai.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                        riebaluKiekis+=(riebalai.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                        skaidulinesMedziagos+=(skaidulines.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                        kalcioKiekis+=(kalcis.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                        cholesterolioKiekis+=(cholesterolis.get(priespieciuPr.get(i))*(priespieciuKiekiai.get(i)-j))/100;
                    }

            for(int i=0; i<pietuPr.size(); i++)
                for(int j=0;j<pietuKiekiai.get(i);j+=100)
                    if(pietuKiekiai.get(i)-j>99){
                        baltymuKiekis+=baltymai.get(pietuPr.get(i));
                        angliavandeniuKiekis+=angliavandeniai.get(pietuPr.get(i));
                        riebaluKiekis+=riebalai.get(pietuPr.get(i));
                        skaidulinesMedziagos+=skaidulines.get(pietuPr.get(i));
                        kalcioKiekis+=kalcis.get(pietuPr.get(i));
                        cholesterolioKiekis+=cholesterolis.get(pietuPr.get(i));
                    }else{
                        baltymuKiekis+=(baltymai.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                        angliavandeniuKiekis+=(angliavandeniai.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                        riebaluKiekis+=(riebalai.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                        skaidulinesMedziagos+=(skaidulines.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                        kalcioKiekis+=(kalcis.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                        cholesterolioKiekis+=(cholesterolis.get(pietuPr.get(i))*(pietuKiekiai.get(i)-j))/100;
                    }

            for(int i=0; i<pavakariuPr.size(); i++)
                for(int j=0;j<pavakariuKiekiai.get(i);j+=100)
                    if(pavakariuKiekiai.get(i)-j>99){
                        baltymuKiekis+=baltymai.get(pavakariuPr.get(i));
                        angliavandeniuKiekis+=angliavandeniai.get(pavakariuPr.get(i));
                        riebaluKiekis+=riebalai.get(pavakariuPr.get(i));
                        skaidulinesMedziagos+=skaidulines.get(pavakariuPr.get(i));
                        kalcioKiekis+=kalcis.get(pavakariuPr.get(i));
                        cholesterolioKiekis+=cholesterolis.get(pavakariuPr.get(i));
                    }else{
                        baltymuKiekis+=(baltymai.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                        angliavandeniuKiekis+=(angliavandeniai.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                        riebaluKiekis+=(riebalai.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                        skaidulinesMedziagos+=(skaidulines.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                        kalcioKiekis+=(kalcis.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                        cholesterolioKiekis+=(cholesterolis.get(pavakariuPr.get(i))*(pavakariuKiekiai.get(i)-j))/100;
                    }

            for(int i=0; i<vakarienesPr.size(); i++)
                for(int j=0;j<vakarienesKiekiai.get(i);j+=100)
                    if(vakarienesKiekiai.get(i)-j>99){
                        baltymuKiekis+=baltymai.get(vakarienesPr.get(i));
                        angliavandeniuKiekis+=angliavandeniai.get(vakarienesPr.get(i));
                        riebaluKiekis+=riebalai.get(vakarienesPr.get(i));
                        skaidulinesMedziagos+=skaidulines.get(vakarienesPr.get(i));
                        kalcioKiekis+=kalcis.get(vakarienesPr.get(i));
                        cholesterolioKiekis+=cholesterolis.get(vakarienesPr.get(i));
                    }else{
                        baltymuKiekis+=(baltymai.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                        angliavandeniuKiekis+=(angliavandeniai.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                        riebaluKiekis+=(riebalai.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                        skaidulinesMedziagos+=(skaidulines.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                        kalcioKiekis+=(kalcis.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                        cholesterolioKiekis+=(cholesterolis.get(vakarienesPr.get(i))*(vakarienesKiekiai.get(i)-j))/100;
                    }

            double  baltymuKalorijos=(baltymuKiekis*4)/1000000,
                    angliavandeniuKalorijos=(angliavandeniuKiekis/1000000)*4,
                    riebaluKalorijos=(riebaluKiekis/1000000)*9;

            if(((baltymuKalorijos>=baltymaiMin&&baltymuKalorijos<=baltymaiMax)
                    ||ignoruojami.contains("baltymai"))&&
                    ((angliavandeniuKalorijos>=angliavandeniaiMin&&angliavandeniuKalorijos<=angliavandeniaiMax)
                    ||ignoruojami.contains("angliavandeniai"))&&
                    ((riebaluKalorijos>=riebalaiMin&&riebaluKalorijos<=riebalaiMax)
                    ||ignoruojami.contains("riebalai"))&&
                    ((skaidulinesMedziagos>=skaidulinesMin&&skaidulinesMedziagos<=skaidulinesMax)
                    ||ignoruojami.contains("skaidulines"))&&
                    ((kalcioKiekis>=kalcisMin&&kalcioKiekis<=kalcisMax)
                    ||ignoruojami.contains("kalcis"))&&((cholesterolioKiekis<cholesterolioNorma)
                    ||ignoruojami.contains("cholesterolis"))&&!leidimas){
                
                String pusryciai = " Pusryčiams: \n\n", priespieciai = "\n Priešpiečiams: \n\n", pietus = "\n Pietums: \n\n",
                            pavakariai = "\n Pavakariams: \n\n", vakariene = "\n Vakarienei: \n\n";

                if(kartai==3){
                    for(int i=0;i<pusryciuPr.size();i++)
                        if(skystis.get(pusryciuPr.get(i)))
                            pusryciai+=" "+pavadinimas.get(pusryciuPr.get(i))+" "+pusryciuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(pusryciuPr.get(i))*pusryciuKiekiai.get(i))/100+"kcal)\n";
                        else
                            pusryciai+=" "+pavadinimas.get(pusryciuPr.get(i))+" "+pusryciuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(pusryciuPr.get(i))*pusryciuKiekiai.get(i))/100+"kcal)\n";
                    
                    for(int i=0;i<pietuPr.size();i++)
                        if(skystis.get(pietuPr.get(i)))
                            pietus+=" "+pavadinimas.get(pietuPr.get(i))+" "+pietuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(pietuPr.get(i))*pietuKiekiai.get(i))/100+"kcal)\n";
                        else
                            pietus+=" "+pavadinimas.get(pietuPr.get(i))+" "+pietuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(pietuPr.get(i))*pietuKiekiai.get(i))/100+"kcal)\n";
                    
                    for(int i=0;i<vakarienesPr.size();i++)
                        if(skystis.get(vakarienesPr.get(i)))
                            vakariene+=" "+pavadinimas.get(vakarienesPr.get(i))+" "+vakarienesKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(vakarienesPr.get(i))*vakarienesKiekiai.get(i))/100+"kcal)\n";
                        else
                            vakariene+=" "+pavadinimas.get(vakarienesPr.get(i))+" "+vakarienesKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(vakarienesPr.get(i))*vakarienesKiekiai.get(i))/100+"kcal)\n";
                }

                else{
                    for(int i=0;i<pusryciuPr.size();i++)
                        if(skystis.get(pusryciuPr.get(i)))
                            pusryciai+=" "+pavadinimas.get(pusryciuPr.get(i))+" "+pusryciuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(pusryciuPr.get(i))*pusryciuKiekiai.get(i))/100+"kcal)\n";
                        else
                            pusryciai+=" "+pavadinimas.get(pusryciuPr.get(i))+" "+pusryciuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(pusryciuPr.get(i))*pusryciuKiekiai.get(i))/100+"kcal)\n";
                    
                    for(int i=0;i<priespieciuPr.size();i++)
                        if(skystis.get(priespieciuPr.get(i)))
                            priespieciai+=" "+pavadinimas.get(priespieciuPr.get(i))+" "+priespieciuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(priespieciuPr.get(i))*priespieciuKiekiai.get(i))/100+"kcal)\n";
                        else
                            priespieciai+=" "+pavadinimas.get(priespieciuPr.get(i))+" "+priespieciuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(priespieciuPr.get(i))*priespieciuKiekiai.get(i))/100+"kcal)\n";
                    
                    for(int i=0;i<pietuPr.size();i++)
                        if(skystis.get(pietuPr.get(i)))
                            pietus+=" "+pavadinimas.get(pietuPr.get(i))+" "+pietuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(pietuPr.get(i))*pietuKiekiai.get(i))/100+"kcal)\n";
                        else
                            pietus+=" "+pavadinimas.get(pietuPr.get(i))+" "+pietuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(pietuPr.get(i))*pietuKiekiai.get(i))/100+"kcal)\n";
                    
                    for(int i=0;i<pavakariuPr.size();i++)
                        if(skystis.get(pavakariuPr.get(i)))
                                pavakariai+=" "+pavadinimas.get(pavakariuPr.get(i))+" "+pavakariuKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(pavakariuPr.get(i))*pavakariuKiekiai.get(i))/100+"kcal)\n";
                        else
                            pavakariai+=" "+pavadinimas.get(pavakariuPr.get(i))+" "+pavakariuKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(pavakariuPr.get(i))*pavakariuKiekiai.get(i))/100+"kcal)\n";
                        
                    for(int i=0;i<vakarienesPr.size();i++)
                        if(skystis.get(vakarienesPr.get(i)))
                            vakariene+=" "+pavadinimas.get(vakarienesPr.get(i))+" "+vakarienesKiekiai.get(i)+"ml (iš viso energinė vertė: "+(kalorijos.get(vakarienesPr.get(i))*vakarienesKiekiai.get(i))/100+"kcal)\n";
                        else
                            vakariene+=" "+pavadinimas.get(vakarienesPr.get(i))+" "+vakarienesKiekiai.get(i)+"g (iš viso energinė vertė: "+(kalorijos.get(vakarienesPr.get(i))*vakarienesKiekiai.get(i))/100+"kcal)\n";    
                }

                if(dienos==1){
                    laikmatis.cancel();
                    if(kartai==3)
                        return pusryciai+pietus+vakariene;
                    return pusryciai+priespieciai+pietus+pavakariai+vakariene;
                }

                if(kartai==3)
                    rezultatas += "-------------------------------------------------------------------------------------------------------------------------------------------------------\n"
                            +" "+(dienos-kartoti+1)+"-oji diena:\n\n"+pusryciai+pietus+vakariene;
                else 
                    rezultatas += "-------------------------------------------------------------------------------------------------------------------------------------------------------\n"
                            +" "+(dienos-kartoti+1)+"-oji diena:\n\n"+pusryciai+priespieciai+pietus+pavakariai+vakariene;

                kartoti--;
                        
                if(kartoti == 0){
                    laikmatis.cancel();
                    return rezultatas;
                }
            }
               
            if(bandymas>9999&&bandymas%1000==0){
                skaidulinesMin*=0.99;
                skaidulinesMax*=1.01;
                kalcisMin*=0.99;
                kalcisMax*=1.01;
                cholesterolioNorma*=1.01;
                if(bandymas>40000){
                    baltymaiMin*=0.99;
                    baltymaiMax*=1.01;
                    angliavandeniaiMin*=0.99;
                    angliavandeniaiMax*=1.01;
                    riebalaiMin*=0.99;
                    riebalaiMax*=1.01;
                }
            }
        }
    }
}
