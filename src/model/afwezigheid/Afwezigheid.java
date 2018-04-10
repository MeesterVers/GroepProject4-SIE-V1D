package model.afwezigheid;


import model.persoon.Student;

public class Afwezigheid {
    private String soort;
    private String startDatum;
    private String eindDatum;
    private String beschrijving = "";
    private Student student;
    private String vak;

    public Afwezigheid(String soort, String startDatum, String eindDatum, String beschrijving, Student student, String vak) {
        this.vak = vak;
        this.soort = soort;
        this.startDatum = startDatum;
        this.eindDatum = eindDatum;
        this.beschrijving = beschrijving;
        this.student = student;
    }

    public Student getStudent() {
        return this.student;
    }

    public String getSoort() {
        return soort;
    }

    public String getStartDatum() {
        return startDatum;
    }

    public String getEindDatum() {
        return eindDatum;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    @Override
    public String toString() {
        return "Afwezigheid{" +
                "soort='" + soort + '\'' +
                ", startDatum='" + startDatum + '\'' +
                ", eindDatum='" + eindDatum + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", student=" + student +
                ", vak='" + vak + '\'' +
                '}';
    }
}


