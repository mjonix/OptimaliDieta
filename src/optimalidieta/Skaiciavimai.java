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

    private int dietosGeneravimoTrukme = 0;

    public String medziagos(double kmi, int aktyvumas, int kalorijuPoreikis, int amzius, int lytis) {
        String baltymai, angliavandeniai, riebalai, kalcis;
        if (kmi > 25.5) {
            baltymai = (int) Math.round((kalorijuPoreikis * 25) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 35) / 400) + "g";
            angliavandeniai = (int) Math.round((kalorijuPoreikis * 45) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 55) / 400) + "g";
            riebalai = (int) Math.round((kalorijuPoreikis * 15) / 900) + " - " + (int) Math.round((kalorijuPoreikis * 25) / 900) + "g";
        } else {
            if (aktyvumas == 1) {
                baltymai = (int) Math.round((kalorijuPoreikis * 10) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 20) / 400) + "g";
                angliavandeniai = (int) Math.round((kalorijuPoreikis * 55) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 75) / 400) + "g";
                riebalai = (int) Math.round((kalorijuPoreikis * 10) / 900) + " - " + (int) Math.round((kalorijuPoreikis * 15) / 900) + "g";
            } else {
                baltymai = (int) Math.round((kalorijuPoreikis * 15) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 20) / 400) + "g";
                angliavandeniai = (int) Math.round((kalorijuPoreikis * 55) / 400) + " - " + (int) Math.round((kalorijuPoreikis * 60) / 400) + "g";
                riebalai = (int) Math.round((kalorijuPoreikis * 15) / 900) + " - " + (int) Math.round((kalorijuPoreikis * 20) / 900) + "g";
            }
        }

        if (amzius == 0) {
            kalcis = "0.24 - 0.26g";
        } else if (amzius > 0 && amzius < 4) {
            kalcis = "0.69 - 0.71g";
        } else if (amzius > 3 && amzius < 9) {
            kalcis = "0.99 - 1.01g";
        } else if (amzius > 8 && amzius < 14) {
            kalcis = "1.29 - 1.31g";
        } else if (amzius > 13 && amzius < 19) {
            kalcis = "1.29 - 1.31g";
        } else if (amzius > 18 && amzius < 51) {
            kalcis = "0.99 - 1.01g";
        } else if (amzius > 50 && amzius < 71) {
            if (lytis == 1) {
                kalcis = "0.99 - 1.01g";
            } else {
                kalcis = "1.19 - 1.21g";
            }
        } else {
            kalcis = "1.19 - 1.21g";
        }
        return " Rekomenduojama suvartoti: " + baltymai + " baltymų, " + angliavandeniai + " angliavandenių, " + riebalai + " riebalų,\n "
                + kalcis + " kalcio ir " + ((int) Math.round((kalorijuPoreikis * 14) / 1000) - 5) + " - "
                + ((int) Math.round((kalorijuPoreikis * 14) / 1000) + 5) + "g skaidulinių medžiagų.";
    }

    public String dietosSudarymas(double kmi, int kalorijuPoreikis, int aktyvumas, int amzius, int lytis,
            int kartai, int dienos, ArrayList<String> nepageidaujamaKategorija,
            ArrayList<String> nepageidaujamiProduktai, ArrayList<String> ignoruojami,
            boolean mesaPietums)
            throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");

        ArrayList<MaistoProduktas> produktai = new ArrayList<>();
        boolean leidimas;
        while (rs.next()) {
            leidimas = true;
            for (String nk : nepageidaujamaKategorija) {
                if (rs.getString("kategorija").contains(nk)) {
                    leidimas = false;
                }
            }
            for (String np : nepageidaujamiProduktai) {
                if (rs.getString("pavadinimas").equals(np)) {
                    leidimas = false;
                }
            }

            if (leidimas) {
                produktai.add(new MaistoProduktas(rs.getString("pavadinimas"), rs.getInt("energine_verte"), rs.getInt("baltymai"),
                        rs.getInt("angliavandeniai"), rs.getInt("riebalai"), rs.getInt("skaidulines_medziagos"), rs.getInt("kalcis"),
                        rs.getInt("cholesterolis"), rs.getInt("standartine_porcija"), rs.getInt("laikas"), rs.getString("kategorija"),
                        rs.getBoolean("skystis")));
            }
        }
        conn.close();

        int baltymaiMin, baltymaiMax, angliavandeniaiMin, angliavandeniaiMax, riebalaiMin, riebalaiMax,
                kalcisMin, kalcisMax, skaidulinesMin = (((kalorijuPoreikis * 14) / 1000) * 1000000) - 5000000,
                skaidulinesMax = (((kalorijuPoreikis * 14) / 1000) * 1000000) + 5000000;
        if (kmi > 25.5) {
            baltymaiMin = (kalorijuPoreikis * 25) / 100;
            baltymaiMax = (kalorijuPoreikis * 35) / 100;
            angliavandeniaiMin = (kalorijuPoreikis * 45) / 100;
            angliavandeniaiMax = (kalorijuPoreikis * 55) / 100;
            riebalaiMin = (kalorijuPoreikis * 15) / 100;
            riebalaiMax = (kalorijuPoreikis * 25) / 100;
        } else {
            if (aktyvumas == 1) {
                baltymaiMin = (kalorijuPoreikis * 10) / 100;
                baltymaiMax = (kalorijuPoreikis * 20) / 100;
                angliavandeniaiMin = (kalorijuPoreikis * 55) / 100;
                angliavandeniaiMax = (kalorijuPoreikis * 75) / 100;
                riebalaiMin = (kalorijuPoreikis * 10) / 100;
                riebalaiMax = (kalorijuPoreikis * 15) / 100;
            } else {
                baltymaiMin = (kalorijuPoreikis * 15) / 100;
                baltymaiMax = (kalorijuPoreikis * 20) / 100;
                angliavandeniaiMin = (kalorijuPoreikis * 55) / 100;
                angliavandeniaiMax = (kalorijuPoreikis * 60) / 100;
                riebalaiMin = (kalorijuPoreikis * 15) / 100;
                riebalaiMax = (kalorijuPoreikis * 20) / 100;
            }
        }

        if (amzius == 0) {
            kalcisMin = 240000;
            kalcisMax = 260000;
        } else if (amzius > 0 && amzius < 4) {
            kalcisMin = 690000;
            kalcisMax = 710000;
        } else if (amzius > 3 && amzius < 9) {
            kalcisMin = 990000;
            kalcisMax = 1010000;
        } else if (amzius > 8 && amzius < 14) {
            kalcisMin = 1290000;
            kalcisMax = 1310000;
        } else if (amzius > 13 && amzius < 19) {
            kalcisMin = 1290000;
            kalcisMax = 1310000;
        } else if (amzius > 18 && amzius < 51) {
            kalcisMin = 990000;
            kalcisMax = 1010000;
        } else if (amzius > 50 && amzius < 71) {
            if (lytis == 1) {
                kalcisMin = 990000;
                kalcisMax = 1010000;
            } else {
                kalcisMin = 1190000;
                kalcisMax = 1210000;
            }
        } else {
            kalcisMin = 1190000;
            kalcisMax = 1210000;
        }

        Random r = new Random();
        int bandymas = 0, kartoti = dienos, cholesterolioNorma = 300000;
        String rezultatas = "";

        int pusryciuKal, priespieciuKal = 0, pietuKal, pavakariuKal = 0, vakarienesKal;

        if (kartai == 3) {
            pusryciuKal = (kalorijuPoreikis * 35) / 100;
            pietuKal = (kalorijuPoreikis * 4) / 10;
            vakarienesKal = (kalorijuPoreikis * 25) / 100;
        } else {
            pusryciuKal = (kalorijuPoreikis * 3) / 10;
            priespieciuKal = kalorijuPoreikis / 10;
            pietuKal = (kalorijuPoreikis * 3) / 10;
            pavakariuKal = kalorijuPoreikis / 10;
            vakarienesKal = kalorijuPoreikis / 5;
        }

        int p0 = 0, p1 = 0, p2 = 0, p3 = 0, pm = 0;
        MaistoProduktas produktas;
        for (int i = 0; i < produktai.size(); i++) {
            produktas = produktai.get(i);
            if (produktas.laikas == 0 && !produktas.skystis) {
                p0++;
            }
            if (produktas.laikas == 1 && !produktas.skystis) {
                p1++;
            }
            if (produktas.laikas == 2 && !produktas.skystis) {
                p2++;
            }
            if (produktas.laikas == 3 && !produktas.skystis) {
                p3++;
            }
            if (produktas.laikas == 2 && produktas.kategorija.contains("mėsa")
                    && !produktas.skystis) {
                pm++;
            }
        }

        if (p1 == 0 || p2 == 0 || p3 == 0 || (p0 == 0 && kartai == 5) || (pm == 0 && mesaPietums)) {
            return " * Dietos sudaryti neįmanoma. Prašome pasirinkti mažiau produktų, kaip nepageidaujamus, arba"
                    + "\n įtraukite naujų produktų į duomenų bazę.";
        }

        final Timer laikmatis = new Timer();
        TimerTask generavimoLaikas = new TimerTask() {
            @Override
            public void run() {
                dietosGeneravimoTrukme++;
            }
        };
        laikmatis.scheduleAtFixedRate(generavimoLaikas, 1000, 1000);
        while (true) {
            if (dietosGeneravimoTrukme > 60 && kartoti == dienos && bandymas > 50000) {
                laikmatis.cancel();
                return " * Pilnavertės dietos sudaryti nepavyko. Prašome pasirinkti mažiau produktų, kaip"
                        + " nepageidaujamus,<br> arba įtraukite naujų produktų į duomenų bazę.";
            }
            bandymas++;
            ArrayList<Integer> pusryciuPr = new ArrayList<>();
            ArrayList<Integer> priespieciuPr = new ArrayList<>();
            ArrayList<Integer> pietuPr = new ArrayList<>();
            ArrayList<Integer> pavakariuPr = new ArrayList<>();
            ArrayList<Integer> vakarienesPr = new ArrayList<>();
            ArrayList<Integer> pusryciuKiekiai = new ArrayList<>();
            ArrayList<Integer> priespieciuKiekiai = new ArrayList<>();
            ArrayList<Integer> pietuKiekiai = new ArrayList<>();
            ArrayList<Integer> pavakariuKiekiai = new ArrayList<>();
            ArrayList<Integer> vakarienesKiekiai = new ArrayList<>();
            ArrayList<String> kategorijos = new ArrayList<>();
            int indeksas = 0, turimosKalorijos = 0;

            while (true) {
                indeksas = r.nextInt(produktai.size());
                produktas = produktai.get(indeksas);
                if (produktas.laikas == 1) {
                    pusryciuPr.add(indeksas);
                    pusryciuKiekiai.add(((pusryciuKal * 100) / produktas.kilokalorijos + 5) / 10 * 10);
                    break;
                }
            }

            int papildomas = 0;
            int kartojimas = 0;
            leidimas = true;
            if (kartai == 5) {
                while (true) {
                    kartojimas++;
                    indeksas = r.nextInt(produktai.size());
                    produktas = produktai.get(indeksas);
                    if (produktas.laikas == 0 && !kategorijos.contains(produktas.kategorija)
                            && (leidimas || !produktas.skystis)) {

                        if (produktas.skystis) {
                            leidimas = false;
                        }
                        kategorijos.add(produktas.kategorija);
                        priespieciuPr.add(indeksas);
                        priespieciuKiekiai.add(produktas.porcija);
                        turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                    }
                    if (turimosKalorijos + 30 > priespieciuKal) {
                        break;
                    }
                    if (kartojimas % 10 == 0 && priespieciuPr.size() > 1) {
                        indeksas = r.nextInt(priespieciuPr.size());
                        produktas = produktai.get(priespieciuPr.get(indeksas));
                        if (!produktas.skystis
                                && (priespieciuKiekiai.get(indeksas) / produktas.porcija < 2)) {
                            turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                            priespieciuKiekiai.set(indeksas, (produktas.porcija / 2) + priespieciuKiekiai.get(indeksas));
                        } else {
                            papildomas++;
                            if (papildomas % 10 == 0) {
                                if (!produktas.skystis) {
                                    turimosKalorijos += ((produktas.porcija / 2) * produktas.kilokalorijos) / 100;
                                    priespieciuKiekiai.set(indeksas, (produktas.porcija / 2) + priespieciuKiekiai.get(indeksas));
                                }
                            }
                        }
                    }
                }
            }

            kategorijos = new ArrayList<>();
            turimosKalorijos = 0;
            kartojimas = 0;
            papildomas = 0;
            leidimas = true;
            boolean darzove = false;
            while (true) {
                kartojimas++;
                indeksas = r.nextInt(produktai.size());
                produktas = produktai.get(indeksas);
                if (produktas.laikas == 2 && !kategorijos.contains(produktas.kategorija)
                        && (leidimas || !produktas.skystis)
                        && (!darzove || !produktas.kategorija.contains("daržovės"))) {
                    if (produktas.kategorija.contains("daržovės")) {
                        darzove = true;
                    }
                    if (produktas.skystis) {
                        leidimas = false;
                    }
                    kategorijos.add(produktas.kategorija);
                    pietuPr.add(indeksas);
                    pietuKiekiai.add(produktas.porcija);
                    turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                }
                if (turimosKalorijos + 30 > pietuKal) {
                    if (mesaPietums) {
                        leidimas = false;
                        for (int i = 0; i < pietuPr.size(); i++) {
                            if (produktai.get(pietuPr.get(i)).kategorija.contains("mėsa")) {
                                leidimas = true;
                            }
                        }
                        if (leidimas) {
                            break;
                        } else {
                            pietuPr = new ArrayList<>();
                            pietuKiekiai = new ArrayList<>();
                            kategorijos = new ArrayList<>();
                            turimosKalorijos = 0;
                            kartojimas = 0;
                            papildomas = 0;
                            leidimas = true;
                            darzove = false;
                        }
                    } else {
                        break;
                    }
                }
                if (kartojimas % 10 == 0 && pietuPr.size() > 1) {
                    indeksas = r.nextInt(pietuPr.size());
                    produktas = produktai.get(pietuPr.get(indeksas));
                    if (!produktas.skystis
                            && (pietuKiekiai.get(indeksas) / produktas.porcija < 2)) {
                        turimosKalorijos += ((produktas.porcija / 2) * produktas.kilokalorijos) / 100;
                        pietuKiekiai.set(indeksas, (produktas.porcija / 2) + pietuKiekiai.get(indeksas));
                    } else {
                        papildomas++;
                        if (papildomas % 10 == 0) {
                            if (!produktas.skystis) {
                                turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                                pietuKiekiai.set(indeksas, (produktas.porcija / 2) + pietuKiekiai.get(indeksas));
                            }
                        }
                    }
                }
            }

            if (kartai == 5) {
                leidimas = true;
                kategorijos = new ArrayList<>();
                turimosKalorijos = 0;
                kartojimas = 0;
                papildomas = 0;
                while (true) {
                    kartojimas++;
                    indeksas = r.nextInt(produktai.size());
                    produktas = produktai.get(indeksas);
                    if (produktas.laikas == 0 && !kategorijos.contains(produktas.kategorija)
                            && (leidimas || !produktas.skystis)) {

                        if (produktas.skystis) {
                            leidimas = false;
                        }
                        kategorijos.add(produktas.kategorija);
                        pavakariuPr.add(indeksas);
                        pavakariuKiekiai.add(produktas.porcija);
                        turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                    }
                    if (turimosKalorijos + 30 > pavakariuKal) {
                        break;
                    }
                    if (kartojimas % 10 == 0 && pavakariuPr.size() > 1) {
                        indeksas = r.nextInt(pavakariuPr.size());
                        produktas = produktai.get(pavakariuPr.get(indeksas));
                        if (!produktas.skystis
                                && (pavakariuKiekiai.get(indeksas) / produktas.porcija < 2)) {
                            turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                            pavakariuKiekiai.set(indeksas, (produktas.porcija / 2) + pavakariuKiekiai.get(indeksas));
                        } else {
                            papildomas++;
                            if (papildomas % 10 == 0) {
                                if (!produktas.skystis) {
                                    turimosKalorijos += ((produktas.porcija / 2) * produktas.kilokalorijos) / 100;
                                    pavakariuKiekiai.set(indeksas, (produktas.porcija / 2) + pavakariuKiekiai.get(indeksas));
                                }
                            }
                        }
                    }
                }
            }

            leidimas = true;
            kategorijos = new ArrayList<>();
            turimosKalorijos = 0;
            kartojimas = 0;
            papildomas = 0;
            while (true) {
                kartojimas++;
                indeksas = r.nextInt(produktai.size());
                produktas = produktai.get(indeksas);
                if (produktas.laikas == 3 && !kategorijos.contains(produktas.kategorija)
                        && (leidimas || !produktas.skystis)) {

                    if (produktas.skystis) {
                        leidimas = false;
                    }
                    kategorijos.add(produktas.kategorija);
                    vakarienesPr.add(indeksas);
                    vakarienesKiekiai.add(produktas.porcija);
                    turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                }
                if (turimosKalorijos + 30 > vakarienesKal) {
                    break;
                }
                if (kartojimas % 10 == 0 && vakarienesPr.size() > 1) {
                    indeksas = r.nextInt(vakarienesPr.size());
                    produktas = produktai.get(vakarienesPr.get(indeksas));
                    if (!produktas.skystis
                            && (vakarienesKiekiai.get(indeksas) / produktas.porcija < 2)) {
                        turimosKalorijos += (produktas.porcija * produktas.kilokalorijos) / 100;
                        vakarienesKiekiai.set(indeksas, (produktas.porcija / 2) + vakarienesKiekiai.get(indeksas));
                    } else {
                        papildomas++;
                        if (papildomas % 10 == 0) {
                            if (!produktas.skystis) {
                                turimosKalorijos += ((produktas.porcija / 2) * produktas.kilokalorijos) / 100;
                                vakarienesKiekiai.set(indeksas, (produktas.porcija / 2) + vakarienesKiekiai.get(indeksas));
                            }
                        }
                    }
                }
            }

            leidimas = false;
            int baltymuKiekis = 0, angliavandeniuKiekis = 0, riebaluKiekis = 0, skaidulinesMedziagos = 0,
                    kalcioKiekis = 0, cholesterolioKiekis = 0;

            for (int i = 0; i < pusryciuPr.size(); i++) {
                for (int j = 0; j < pusryciuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(pusryciuPr.get(i));
                    if (pusryciuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.baltymai;
                        angliavandeniuKiekis += produktas.angliavandeniai;
                        riebaluKiekis += produktas.riebalai;
                        skaidulinesMedziagos += produktas.skaidulines;
                        kalcioKiekis += produktas.kalcis;
                        cholesterolioKiekis += produktas.cholesterolis;
                    } else {
                        baltymuKiekis += (produktas.baltymai * (pusryciuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.angliavandeniai * (pusryciuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.riebalai * (pusryciuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.skaidulines * (pusryciuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.kalcis * (pusryciuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.cholesterolis * (pusryciuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < priespieciuPr.size(); i++) {
                for (int j = 0; j < priespieciuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(priespieciuPr.get(i));
                    if (priespieciuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.baltymai;
                        angliavandeniuKiekis += produktas.angliavandeniai;
                        riebaluKiekis += produktas.riebalai;
                        skaidulinesMedziagos += produktas.skaidulines;
                        kalcioKiekis += produktas.kalcis;
                        cholesterolioKiekis += produktas.cholesterolis;
                    } else {
                        baltymuKiekis += (produktas.baltymai * (priespieciuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.angliavandeniai * (priespieciuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.riebalai * (priespieciuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.skaidulines * (priespieciuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.kalcis * (priespieciuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.cholesterolis * (priespieciuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < pietuPr.size(); i++) {
                for (int j = 0; j < pietuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(pietuPr.get(i));
                    if (pietuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.baltymai;
                        angliavandeniuKiekis += produktas.angliavandeniai;
                        riebaluKiekis += produktas.riebalai;
                        skaidulinesMedziagos += produktas.skaidulines;
                        kalcioKiekis += produktas.kalcis;
                        cholesterolioKiekis += produktas.cholesterolis;
                    } else {
                        baltymuKiekis += (produktas.baltymai * (pietuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.angliavandeniai * (pietuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.riebalai * (pietuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.skaidulines * (pietuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.kalcis * (pietuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.cholesterolis * (pietuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < pavakariuPr.size(); i++) {
                for (int j = 0; j < pavakariuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(pavakariuPr.get(i));
                    if (pavakariuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.baltymai;
                        angliavandeniuKiekis += produktas.angliavandeniai;
                        riebaluKiekis += produktas.riebalai;
                        skaidulinesMedziagos += produktas.skaidulines;
                        kalcioKiekis += produktas.kalcis;
                        cholesterolioKiekis += produktas.cholesterolis;
                    } else {
                        baltymuKiekis += (produktas.baltymai * (pavakariuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.angliavandeniai * (pavakariuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.riebalai * (pavakariuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.skaidulines * (pavakariuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.kalcis * (pavakariuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.cholesterolis * (pavakariuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < vakarienesPr.size(); i++) {
                for (int j = 0; j < vakarienesKiekiai.get(i); j += 100) {
                    produktas = produktai.get(vakarienesPr.get(i));
                    if (vakarienesKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.baltymai;
                        angliavandeniuKiekis += produktas.angliavandeniai;
                        riebaluKiekis += produktas.riebalai;
                        skaidulinesMedziagos += produktas.skaidulines;
                        kalcioKiekis += produktas.kalcis;
                        cholesterolioKiekis += produktas.cholesterolis;
                    } else {
                        baltymuKiekis += (produktas.baltymai * (vakarienesKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.angliavandeniai * (vakarienesKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.riebalai * (vakarienesKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.skaidulines * (vakarienesKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.kalcis * (vakarienesKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.cholesterolis * (vakarienesKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            double baltymuKalorijos = (baltymuKiekis * 4) / 1000000,
                    angliavandeniuKalorijos = (angliavandeniuKiekis / 1000000) * 4,
                    riebaluKalorijos = (riebaluKiekis / 1000000) * 9;

            if (((baltymuKalorijos >= baltymaiMin && baltymuKalorijos <= baltymaiMax)
                    || ignoruojami.contains("baltymai"))
                    && ((angliavandeniuKalorijos >= angliavandeniaiMin && angliavandeniuKalorijos <= angliavandeniaiMax)
                    || ignoruojami.contains("angliavandeniai"))
                    && ((riebaluKalorijos >= riebalaiMin && riebaluKalorijos <= riebalaiMax)
                    || ignoruojami.contains("riebalai"))
                    && ((skaidulinesMedziagos >= skaidulinesMin && skaidulinesMedziagos <= skaidulinesMax)
                    || ignoruojami.contains("skaidulines"))
                    && ((kalcioKiekis >= kalcisMin && kalcioKiekis <= kalcisMax)
                    || ignoruojami.contains("kalcis")) && ((cholesterolioKiekis < cholesterolioNorma)
                    || ignoruojami.contains("cholesterolis")) && !leidimas) {

                String pusryciai = " Pusryčiams: \n\n", priespieciai = "\n Priešpiečiams: \n\n", pietus = "\n Pietums: \n\n",
                        pavakariai = "\n\n Pavakariams: \n\n", vakariene = "\n Vakarienei: \n\n";

                if (kartai == 3) {
                    for (int i = 0; i < pusryciuPr.size(); i++) {
                        produktas = produktai.get(pusryciuPr.get(i));
                        if (produktas.skystis) {
                            pusryciai += " " + produktas.pavadinimas + " " + pusryciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pusryciai += " " + produktas.pavadinimas + " " + pusryciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pietuPr.size(); i++) {
                        produktas = produktai.get(pietuPr.get(i));
                        if (produktas.skystis) {
                            pietus += " " + produktas.pavadinimas + " " + pietuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pietus += " " + produktas.pavadinimas + " " + pietuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < vakarienesPr.size(); i++) {
                        produktas = produktai.get(vakarienesPr.get(i));
                        if (produktas.skystis) {
                            vakariene += " " + produktas.pavadinimas + " " + vakarienesKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            vakariene += " " + produktas.pavadinimas + " " + vakarienesKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }
                } else {
                    for (int i = 0; i < pusryciuPr.size(); i++) {
                        produktas = produktai.get(pusryciuPr.get(i));
                        if (produktas.skystis) {
                            pusryciai += " " + produktas.pavadinimas + " " + pusryciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pusryciai += " " + produktas.pavadinimas + " " + pusryciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < priespieciuPr.size(); i++) {
                        produktas = produktai.get(priespieciuPr.get(i));
                        if (produktas.skystis) {
                            priespieciai += " " + produktas.pavadinimas + " " + priespieciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * priespieciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            priespieciai += " " + produktas.pavadinimas + " " + priespieciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * priespieciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pietuPr.size(); i++) {
                        produktas = produktai.get(pietuPr.get(i));
                        if (produktas.skystis) {
                            pietus += " " + produktas.pavadinimas + " " + pietuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pietus += " " + produktas.pavadinimas + " " + pietuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pavakariuPr.size(); i++) {
                        produktas = produktai.get(pavakariuPr.get(i));
                        if (produktas.skystis) {
                            pavakariai += " " + produktas.pavadinimas + " " + pavakariuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pavakariuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pavakariai += " " + produktas.pavadinimas + " " + pavakariuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * pavakariuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < vakarienesPr.size(); i++) {
                        produktas = produktai.get(vakarienesPr.get(i));
                        if (produktas.skystis) {
                            vakariene += " " + produktas.pavadinimas + " " + vakarienesKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            vakariene += " " + produktas.pavadinimas + " " + vakarienesKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.kilokalorijos * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }
                }

                if (dienos == 1) {
                    laikmatis.cancel();
                    if (kartai == 3) {
                        return pusryciai + pietus + vakariene;
                    }
                    return pusryciai + priespieciai + pietus + pavakariai + vakariene;
                }

                if (kartai == 3) {
                    rezultatas += "-------------------------------------------------------------------------------------------------------------------------------------------------------\n"
                            + " " + (dienos - kartoti + 1) + "-oji diena:\n\n" + pusryciai + pietus + vakariene;
                } else {
                    rezultatas += "-------------------------------------------------------------------------------------------------------------------------------------------------------\n"
                            + " " + (dienos - kartoti + 1) + "-oji diena:\n\n" + pusryciai + priespieciai + pietus + pavakariai + vakariene;
                }

                kartoti--;

                if (kartoti == 0) {
                    laikmatis.cancel();
                    return rezultatas;
                }
            }

            if (bandymas > 9999 && bandymas % 1000 == 0) {
                skaidulinesMin *= 0.99;
                skaidulinesMax *= 1.01;
                kalcisMin *= 0.99;
                kalcisMax *= 1.01;
                cholesterolioNorma *= 1.01;
                if (bandymas > 40000) {
                    baltymaiMin *= 0.99;
                    baltymaiMax *= 1.01;
                    angliavandeniaiMin *= 0.99;
                    angliavandeniaiMax *= 1.01;
                    riebalaiMin *= 0.99;
                    riebalaiMax *= 1.01;
                }
            }
        }
    }
}
