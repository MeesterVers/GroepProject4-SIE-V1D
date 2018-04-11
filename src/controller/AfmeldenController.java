package controller;

import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;

import model.PrIS;
import model.afwezigheid.Afwezigheid;
import model.klas.Klas;
import model.les.Les;
import model.persoon.Student;
import server.Conversation;
import server.Handler;

public class AfmeldenController implements Handler {
    private PrIS informatieSysteem;

    public AfmeldenController(PrIS infoSys) {
        informatieSysteem = infoSys;
    }

    public void handle(Conversation conversation) {
        if (conversation.getRequestedURI().startsWith("/student/afmelden")) {
            opslaan(conversation);
        }
    }

    /**
     * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
     * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
     * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
     *
     * @param conversation - alle informatie over het request
     */
    private void opslaan(Conversation conversation) {
        JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
        String lGebruikersnaam = lJsonObjectIn.getString("username");
        Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);

        String soort = lJsonObjectIn.getString("soort");
        String afwezigheidLes = lJsonObjectIn.getString("les");

        String beschrijving = "";
        String startDatum;
        String eindDatum;
        String vakCode = "";
        String datum = "";

        if (soort.equals("Afwezig")) {
            beschrijving = lJsonObjectIn.getString("beschrijving");
            startDatum = lJsonObjectIn.getString("startDatum");
            eindDatum = lJsonObjectIn.getString("eindDatum");
            informatieSysteem.voegPeriodeAfwezigheidToe(new Afwezigheid(soort, startDatum, eindDatum, beschrijving, lStudentZelf, ""));

        }

        if(soort.equals("Ziek") && afwezigheidLes.equals("allemaal")){
            startDatum = lJsonObjectIn.getString("startDatum");
            eindDatum = lJsonObjectIn.getString("eindDatum");
            informatieSysteem.voegPeriodeAfwezigheidToe(new Afwezigheid(soort, startDatum, eindDatum, beschrijving, lStudentZelf, ""));
            
        }

        if(soort.equals("Te laat")){
            beschrijving = lJsonObjectIn.getString("beschrijving");
            String[] parts = afwezigheidLes.split("\\.");
            vakCode = parts[0];
            datum = parts[1];
            for (Les les : informatieSysteem.getRooster()) {
                if (les.getDatum().equals(datum) && les.getVakCode().equals(vakCode)) {
                    les.voegAfwezigheidToe(new Afwezigheid(soort, datum, datum, beschrijving, lStudentZelf, vakCode));
                }
            }
        }
    }
}
