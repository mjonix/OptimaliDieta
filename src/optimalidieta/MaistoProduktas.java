package optimalidieta;

public class MaistoProduktas {

    private String pavadinimas;
    private int kilokalorijos;
    private int baltymai;
    private int angliavandeniai;
    private int riebalai;
    private int skaidulines;
    private int kalcis;
    private int cholesterolis;
    private String kategorija;
    private int porcija;
    private int laikas;
    private boolean skystis;

    MaistoProduktas(String pavadinimas, int kilokalorijos, int baltymai, int angliavandeniai, int riebalai, int skaidulines,
            int kalcis, int cholesterolis, int porcija, int laikas, String kategorija, boolean skystis) {
        this.pavadinimas = pavadinimas;
        this.kilokalorijos = kilokalorijos;
        this.baltymai = baltymai;
        this.angliavandeniai = angliavandeniai;
        this.riebalai = riebalai;
        this.skaidulines = skaidulines;
        this.kalcis = kalcis;
        this.cholesterolis = cholesterolis;
        this.porcija = porcija;
        this.laikas = laikas;
        this.pavadinimas = pavadinimas;
        this.kategorija = kategorija;
        this.skystis = skystis;
    }

    public String getPavadinimas() {
        return pavadinimas;
    }

    public String getKategorija() {
        return kategorija;
    }

    public int getKilokalorijos() {
        return kilokalorijos;
    }

    public int getBaltymai() {
        return baltymai;
    }

    public int getAngliavandeniai() {
        return angliavandeniai;
    }

    public int getRiebalai() {
        return riebalai;
    }

    public int getSkaidulines() {
        return skaidulines;
    }

    public int getKalcis() {
        return kalcis;
    }

    public int getCholesterolis() {
        return cholesterolis;
    }

    public int getPorcija() {
        return porcija;
    }

    public int getLaikas() {
        return laikas;
    }

    public boolean getSkystis() {
        return skystis;
    }
}

