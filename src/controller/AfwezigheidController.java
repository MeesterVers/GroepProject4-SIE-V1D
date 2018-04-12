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

public class AfwezigheidController implements Handler {
    private PrIS informatieSysteem;
    private int aantalAfwezigheid;

    public AfwezigheidController(PrIS infoSys) {
        informatieSysteem = infoSys;
    }

    public void handle(Conversation conversation) {
        if (conversation.getRequestedURI().startsWith("/student/afwezigheid")) {
            ophalen(conversation);
        }
    }

    /**
     * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
     * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
     * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
     *
     * @param conversation - alle informatie over het request
     */
    private void ophalen(Conversation conversation) {
        JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
        String lGebruikersnaam = lJsonObjectIn.getString("username");
        Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);

        ArrayList<Afwezigheid> Afwezigheid = informatieSysteem.getPeriodeAfwezigheden();
        JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
        for (Afwezigheid afw : Afwezigheid){
            if (lStudentZelf.equals(afw.getStudent())){
                JsonObjectBuilder lJsonObjectBuilderLes = Json.createObjectBuilder();
                JsonObjectBuilder add = lJsonObjectBuilderLes
                .add("soort", afw.getSoort())
                .add("startDatum", afw.getStartDatum())
                .add("eindDatum", afw.getEindDatum())
                .add("beschrijving", afw.getBeschrijving());
                lJsonArrayBuilder.add(lJsonObjectBuilderLes);
            }
        }

        String lJsonOutStr = lJsonArrayBuilder.build().toString();
        conversation.sendJSONMessage(lJsonOutStr);
    }
}