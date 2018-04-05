package model.les;

import java.util.Date;

public class Les {
    private String datum;
    private String startTijd;
    private String eindTijd;
    private String vakCode;
    private String docent;
    private String lokaal;
    private String klasCode;

    public Les(String datum, String startTijd, String eindTijd, String vakCode, String docent, String lokaal, String klasCode) {
        this.datum = datum;
        this.startTijd = startTijd;
        this.eindTijd = eindTijd;
        this.vakCode = vakCode;
        this.docent = docent;
        this.lokaal = lokaal;
        this.klasCode = klasCode;
    }

    public String getKlasCode() {
        return klasCode;
    }

    public String getDatum() {
        return datum;
    }

    public String getStartTijd() {
        return startTijd;
    }

    public String getEindTijd() {
        return eindTijd;
    }

    public String getVakCode() {
        return vakCode;
    }

    public String getDocent() {
        return docent;
    }

    public String getLokaal() {
        return lokaal;
    }

    public String toString() {
        return "Les{" +
                "datum='" + datum + '\'' +
                ", startTijd='" + startTijd + '\'' +
                ", eindTijd='" + eindTijd + '\'' +
                ", vakCode='" + vakCode + '\'' +
                ", docent='" + docent + '\'' +
                ", lokaal='" + lokaal + '\'' +
                ", klasCode='" + klasCode + '\'' +
                '}';
    }
}
