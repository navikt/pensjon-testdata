package no.nav.pensjon.testdata.consumer.grunnbelop.support;

public class VeietSatsResultat {
    public int ar;
    public double verdi;

    public VeietSatsResultat() {
    }

    public VeietSatsResultat(int ar, double verdi) {
        this.ar = ar;
        this.verdi = verdi;
    }

    public int getAr() {
        return this.ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public long getVerdi() {
        return Math.round(verdi);
    }

    public void setVerdi(double verdi) {
        this.verdi = verdi;
    }

}
