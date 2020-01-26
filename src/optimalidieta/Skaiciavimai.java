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

    private int dietosGeneravimoTrukme = 0, baltymaiMin, baltymaiMax, angliavandeniaiMin, angliavandeniaiMax, riebalaiMin,
            riebalaiMax, kalcisMin, kalcisMax, pusryciuKal, priespieciuKal = 0, pietuKal, pavakariuKal = 0, vakarienesKal,
            skaidulinesMin, skaidulinesMax;

    public String pateiktiMedziaguNormas(double kmi, int aktyvumas, int kalorijuPoreikis, int amzius, int lytis) {
        apskaiciuotiNormas(kmi, kalorijuPoreikis, aktyvumas, amzius, lytis, 1);
        return " Rekomenduojama suvartoti: " + baltymaiMin/4 + " - " + baltymaiMax/4 + "g baltymų, " + angliavandeniaiMin/4 + " - " + angliavandeniaiMax/4
                + "g angliavandenių, " + riebalaiMin/9 + " - " + riebalaiMax/9 + "g riebalų,\n " + ((double) kalcisMin / 1000000) + " - "
                + ((double) kalcisMax / 1000000) + "g kalcio ir " + ((int) Math.round((kalorijuPoreikis * 14) / 1000) - 5) + " - "
                + ((int) Math.round((kalorijuPoreikis * 14) / 1000)
                + 5) + "g skaidulinių medžiagų.\n\n";
    }

    private void apskaiciuotiNormas(double kmi, int kalorijuPoreikis, int aktyvumas, int amzius, int lytis, int kartai) {
        skaidulinesMin = (((kalorijuPoreikis * 14) / 1000) * 1000000) - 5000000;
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
    }

    private ArrayList<MaistoProduktas> gautiProduktus(ArrayList<String> nepageidaujamaKategorija,
            ArrayList<String> nepageidaujamiProduktai) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DUOMENU_BAZE.db");
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from MAISTO_PRODUKTAI;");

        ArrayList<MaistoProduktas> produktai = new ArrayList();
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
        return produktai;
    }

    private boolean imanomaSudarytiDieta(ArrayList<MaistoProduktas> produktai, int kartai, boolean mesaPietums) {
        int p0 = 0, p1 = 0, p2 = 0, p3 = 0, pm = 0;
        MaistoProduktas produktas;
        for (int i = 0; i < produktai.size(); i++) {
            produktas = produktai.get(i);
            if (produktas.getLaikas() == 0 && !produktas.getSkystis()) {
                p0++;
            }
            if (produktas.getLaikas() == 1 && !produktas.getSkystis()) {
                p1++;
            }
            if (produktas.getLaikas() == 2 && !produktas.getSkystis()) {
                p2++;
            }
            if (produktas.getLaikas() == 3 && !produktas.getSkystis()) {
                p3++;
            }
            if (produktas.getLaikas() == 2 && produktas.getKategorija().contains("mėsa")
                    && !produktas.getSkystis()) {
                pm++;
            }
        }

        if (p1 == 0 || p2 == 0 || p3 == 0 || (p0 == 0 && kartai == 5) || (pm == 0 && mesaPietums)) {
            return false;
        }
        return true;
    }

    public String parinktiDieta(double kmi, int kalorijuPoreikis, int aktyvumas, int amzius, int lytis,
            int kartai, int dienos, ArrayList<String> nepageidaujamaKategorija,
            ArrayList<String> nepageidaujamiProduktai, ArrayList<String> ignoruojami,
            boolean mesaPietums) throws ClassNotFoundException, SQLException {

        ArrayList<MaistoProduktas> produktai = gautiProduktus(nepageidaujamaKategorija, nepageidaujamiProduktai);

        if (!imanomaSudarytiDieta(produktai, kartai, mesaPietums)) {
            return " * Dietos sudaryti neįmanoma. Prašome pasirinkti mažiau produktų, kaip nepageidaujamus, arba"
                    + "\n įtraukite naujų produktų į duomenų bazę.";
        }
        apskaiciuotiNormas(kmi, kalorijuPoreikis, aktyvumas, amzius, lytis, kartai);
        Random r = new Random();
        int bandymas = 0, kartoti = dienos, cholesterolioNorma = 300000;
        String rezultatas = "";

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
                        + " nepageidaujamus,\n arba įtraukite naujų produktų į duomenų bazę.";
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

            MaistoProduktas produktas;
            while (true) {
                indeksas = r.nextInt(produktai.size());
                produktas = produktai.get(indeksas);
                if (produktas.getLaikas() == 1) {
                    pusryciuPr.add(indeksas);
                    pusryciuKiekiai.add(((pusryciuKal * 100) / produktas.getKilokalorijos() + 5) / 10 * 10);
                    break;
                }
            }

            int papildomas = 0;
            int kartojimas = 0;
            boolean leidimas = true;
            if (kartai == 5) {
                while (true) {
                    kartojimas++;
                    indeksas = r.nextInt(produktai.size());
                    produktas = produktai.get(indeksas);
                    if (produktas.getLaikas() == 0 && !kategorijos.contains(produktas.getKategorija())
                            && (leidimas || !produktas.getSkystis())) {

                        if (produktas.getSkystis()) {
                            leidimas = false;
                        }
                        kategorijos.add(produktas.getKategorija());
                        priespieciuPr.add(indeksas);
                        priespieciuKiekiai.add(produktas.getPorcija());
                        turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                    }
                    if (turimosKalorijos + 30 > priespieciuKal) {
                        break;
                    }
                    if (kartojimas % 10 == 0 && priespieciuPr.size() > 1) {
                        indeksas = r.nextInt(priespieciuPr.size());
                        produktas = produktai.get(priespieciuPr.get(indeksas));
                        if (!produktas.getSkystis()
                                && (priespieciuKiekiai.get(indeksas) / produktas.getPorcija() < 2)) {
                            turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                            priespieciuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + priespieciuKiekiai.get(indeksas));
                        } else {
                            papildomas++;
                            if (papildomas % 10 == 0) {
                                if (!produktas.getSkystis()) {
                                    turimosKalorijos += ((produktas.getPorcija() / 2) * produktas.getKilokalorijos()) / 100;
                                    priespieciuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + priespieciuKiekiai.get(indeksas));
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
                if (produktas.getLaikas() == 2 && !kategorijos.contains(produktas.getKategorija())
                        && (leidimas || !produktas.getSkystis())
                        && (!darzove || !produktas.getKategorija().contains("daržovės"))) {
                    if (produktas.getKategorija().contains("daržovės")) {
                        darzove = true;
                    }
                    if (produktas.getSkystis()) {
                        leidimas = false;
                    }
                    kategorijos.add(produktas.getKategorija());
                    pietuPr.add(indeksas);
                    pietuKiekiai.add(produktas.getPorcija());
                    turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                }
                if (turimosKalorijos + 30 > pietuKal) {
                    if (mesaPietums) {
                        leidimas = false;
                        for (int i = 0; i < pietuPr.size(); i++) {
                            if (produktai.get(pietuPr.get(i)).getKategorija().contains("mėsa")) {
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
                    if (!produktas.getSkystis()
                            && (pietuKiekiai.get(indeksas) / produktas.getPorcija() < 2)) {
                        turimosKalorijos += ((produktas.getPorcija() / 2) * produktas.getKilokalorijos()) / 100;
                        pietuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + pietuKiekiai.get(indeksas));
                    } else {
                        papildomas++;
                        if (papildomas % 10 == 0) {
                            if (!produktas.getSkystis()) {
                                turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                                pietuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + pietuKiekiai.get(indeksas));
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
                    if (produktas.getLaikas() == 0 && !kategorijos.contains(produktas.getKategorija())
                            && (leidimas || !produktas.getSkystis())) {

                        if (produktas.getSkystis()) {
                            leidimas = false;
                        }
                        kategorijos.add(produktas.getKategorija());
                        pavakariuPr.add(indeksas);
                        pavakariuKiekiai.add(produktas.getPorcija());
                        turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                    }
                    if (turimosKalorijos + 30 > pavakariuKal) {
                        break;
                    }
                    if (kartojimas % 10 == 0 && pavakariuPr.size() > 1) {
                        indeksas = r.nextInt(pavakariuPr.size());
                        produktas = produktai.get(pavakariuPr.get(indeksas));
                        if (!produktas.getSkystis()
                                && (pavakariuKiekiai.get(indeksas) / produktas.getPorcija() < 2)) {
                            turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                            pavakariuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + pavakariuKiekiai.get(indeksas));
                        } else {
                            papildomas++;
                            if (papildomas % 10 == 0) {
                                if (!produktas.getSkystis()) {
                                    turimosKalorijos += ((produktas.getPorcija() / 2) * produktas.getKilokalorijos()) / 100;
                                    pavakariuKiekiai.set(indeksas, (produktas.getPorcija() / 2) + pavakariuKiekiai.get(indeksas));
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
                if (produktas.getLaikas() == 3 && !kategorijos.contains(produktas.getKategorija())
                        && (leidimas || !produktas.getSkystis())) {

                    if (produktas.getSkystis()) {
                        leidimas = false;
                    }
                    kategorijos.add(produktas.getKategorija());
                    vakarienesPr.add(indeksas);
                    vakarienesKiekiai.add(produktas.getPorcija());
                    turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                }
                if (turimosKalorijos + 30 > vakarienesKal) {
                    break;
                }
                if (kartojimas % 10 == 0 && vakarienesPr.size() > 1) {
                    indeksas = r.nextInt(vakarienesPr.size());
                    produktas = produktai.get(vakarienesPr.get(indeksas));
                    if (!produktas.getSkystis()
                            && (vakarienesKiekiai.get(indeksas) / produktas.getPorcija() < 2)) {
                        turimosKalorijos += (produktas.getPorcija() * produktas.getKilokalorijos()) / 100;
                        vakarienesKiekiai.set(indeksas, (produktas.getPorcija() / 2) + vakarienesKiekiai.get(indeksas));
                    } else {
                        papildomas++;
                        if (papildomas % 10 == 0) {
                            if (!produktas.getSkystis()) {
                                turimosKalorijos += ((produktas.getPorcija() / 2) * produktas.getKilokalorijos()) / 100;
                                vakarienesKiekiai.set(indeksas, (produktas.getPorcija() / 2) + vakarienesKiekiai.get(indeksas));
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
                        baltymuKiekis += produktas.getBaltymai();
                        angliavandeniuKiekis += produktas.getAngliavandeniai();
                        riebaluKiekis += produktas.getRiebalai();
                        skaidulinesMedziagos += produktas.getSkaidulines();
                        kalcioKiekis += produktas.getKalcis();
                        cholesterolioKiekis += produktas.getCholesterolis();
                    } else {
                        baltymuKiekis += (produktas.getBaltymai() * (pusryciuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.getAngliavandeniai() * (pusryciuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.getRiebalai() * (pusryciuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.getSkaidulines() * (pusryciuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.getKalcis() * (pusryciuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.getCholesterolis() * (pusryciuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < priespieciuPr.size(); i++) {
                for (int j = 0; j < priespieciuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(priespieciuPr.get(i));
                    if (priespieciuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.getBaltymai();
                        angliavandeniuKiekis += produktas.getAngliavandeniai();
                        riebaluKiekis += produktas.getRiebalai();
                        skaidulinesMedziagos += produktas.getSkaidulines();
                        kalcioKiekis += produktas.getKalcis();
                        cholesterolioKiekis += produktas.getCholesterolis();
                    } else {
                        baltymuKiekis += (produktas.getBaltymai() * (priespieciuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.getAngliavandeniai() * (priespieciuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.getRiebalai() * (priespieciuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.getSkaidulines() * (priespieciuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.getKalcis() * (priespieciuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.getCholesterolis() * (priespieciuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < pietuPr.size(); i++) {
                for (int j = 0; j < pietuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(pietuPr.get(i));
                    if (pietuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.getBaltymai();
                        angliavandeniuKiekis += produktas.getAngliavandeniai();
                        riebaluKiekis += produktas.getRiebalai();
                        skaidulinesMedziagos += produktas.getSkaidulines();
                        kalcioKiekis += produktas.getKalcis();
                        cholesterolioKiekis += produktas.getCholesterolis();
                    } else {
                        baltymuKiekis += (produktas.getBaltymai() * (pietuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.getAngliavandeniai() * (pietuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.getRiebalai() * (pietuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.getSkaidulines() * (pietuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.getKalcis() * (pietuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.getCholesterolis() * (pietuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < pavakariuPr.size(); i++) {
                for (int j = 0; j < pavakariuKiekiai.get(i); j += 100) {
                    produktas = produktai.get(pavakariuPr.get(i));
                    if (pavakariuKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.getBaltymai();
                        angliavandeniuKiekis += produktas.getAngliavandeniai();
                        riebaluKiekis += produktas.getRiebalai();
                        skaidulinesMedziagos += produktas.getSkaidulines();
                        kalcioKiekis += produktas.getKalcis();
                        cholesterolioKiekis += produktas.getCholesterolis();
                    } else {
                        baltymuKiekis += (produktas.getBaltymai() * (pavakariuKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.getAngliavandeniai() * (pavakariuKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.getRiebalai() * (pavakariuKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.getSkaidulines() * (pavakariuKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.getKalcis() * (pavakariuKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.getCholesterolis() * (pavakariuKiekiai.get(i) - j)) / 100;
                    }
                }
            }

            for (int i = 0; i < vakarienesPr.size(); i++) {
                for (int j = 0; j < vakarienesKiekiai.get(i); j += 100) {
                    produktas = produktai.get(vakarienesPr.get(i));
                    if (vakarienesKiekiai.get(i) - j > 99) {
                        baltymuKiekis += produktas.getBaltymai();
                        angliavandeniuKiekis += produktas.getAngliavandeniai();
                        riebaluKiekis += produktas.getRiebalai();
                        skaidulinesMedziagos += produktas.getSkaidulines();
                        kalcioKiekis += produktas.getKalcis();
                        cholesterolioKiekis += produktas.getCholesterolis();
                    } else {
                        baltymuKiekis += (produktas.getBaltymai() * (vakarienesKiekiai.get(i) - j)) / 100;
                        angliavandeniuKiekis += (produktas.getAngliavandeniai() * (vakarienesKiekiai.get(i) - j)) / 100;
                        riebaluKiekis += (produktas.getRiebalai() * (vakarienesKiekiai.get(i) - j)) / 100;
                        skaidulinesMedziagos += (produktas.getSkaidulines() * (vakarienesKiekiai.get(i) - j)) / 100;
                        kalcioKiekis += (produktas.getKalcis() * (vakarienesKiekiai.get(i) - j)) / 100;
                        cholesterolioKiekis += (produktas.getCholesterolis() * (vakarienesKiekiai.get(i) - j)) / 100;
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
                        pavakariai = "\n Pavakariams: \n\n", vakariene = "\n Vakarienei: \n\n";

                if (kartai == 3) {
                    for (int i = 0; i < pusryciuPr.size(); i++) {
                        produktas = produktai.get(pusryciuPr.get(i));
                        if (produktas.getSkystis()) {
                            pusryciai += " " + produktas.getPavadinimas() + " " + pusryciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pusryciai += " " + produktas.getPavadinimas() + " " + pusryciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pietuPr.size(); i++) {
                        produktas = produktai.get(pietuPr.get(i));
                        if (produktas.getSkystis()) {
                            pietus += " " + produktas.getPavadinimas() + " " + pietuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pietus += " " + produktas.getPavadinimas() + " " + pietuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < vakarienesPr.size(); i++) {
                        produktas = produktai.get(vakarienesPr.get(i));
                        if (produktas.getSkystis()) {
                            vakariene += " " + produktas.getPavadinimas() + " " + vakarienesKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            vakariene += " " + produktas.getPavadinimas() + " " + vakarienesKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }
                } else {
                    for (int i = 0; i < pusryciuPr.size(); i++) {
                        produktas = produktai.get(pusryciuPr.get(i));
                        if (produktas.getSkystis()) {
                            pusryciai += " " + produktas.getPavadinimas() + " " + pusryciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pusryciai += " " + produktas.getPavadinimas() + " " + pusryciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pusryciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < priespieciuPr.size(); i++) {
                        produktas = produktai.get(priespieciuPr.get(i));
                        if (produktas.getSkystis()) {
                            priespieciai += " " + produktas.getPavadinimas() + " " + priespieciuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * priespieciuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            priespieciai += " " + produktas.getPavadinimas() + " " + priespieciuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * priespieciuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pietuPr.size(); i++) {
                        produktas = produktai.get(pietuPr.get(i));
                        if (produktas.getSkystis()) {
                            pietus += " " + produktas.getPavadinimas() + " " + pietuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pietus += " " + produktas.getPavadinimas() + " " + pietuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pietuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < pavakariuPr.size(); i++) {
                        produktas = produktai.get(pavakariuPr.get(i));
                        if (produktas.getSkystis()) {
                            pavakariai += " " + produktas.getPavadinimas() + " " + pavakariuKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pavakariuKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            pavakariai += " " + produktas.getPavadinimas() + " " + pavakariuKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * pavakariuKiekiai.get(i)) / 100 + "kcal)\n";
                        }
                    }

                    for (int i = 0; i < vakarienesPr.size(); i++) {
                        produktas = produktai.get(vakarienesPr.get(i));
                        if (produktas.getSkystis()) {
                            vakariene += " " + produktas.getPavadinimas() + " " + vakarienesKiekiai.get(i) + "ml (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
                        } else {
                            vakariene += " " + produktas.getPavadinimas() + " " + vakarienesKiekiai.get(i) + "g (iš viso energinė vertė: "
                                    + (produktas.getKilokalorijos() * vakarienesKiekiai.get(i)) / 100 + "kcal)\n";
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