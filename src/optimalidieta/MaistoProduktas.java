package optimalidieta;

public class MaistoProduktas {

    String pavadinimas;
    String kategorija;
    int kilokalorijos;
    int baltymai;
    int angliavandeniai;
    int riebalai;
    int skaidulines;
    int kalcis;
    int cholesterolis;
    int porcija;
    int laikas;
    boolean skystis;

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
}
