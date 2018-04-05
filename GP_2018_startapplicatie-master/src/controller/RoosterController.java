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
import model.klas.Klas;
import model.les.Les;
import model.persoon.Student;
import server.Conversation;
import server.Handler;

public class RoosterController implements Handler {
    private PrIS informatieSysteem;

    /**
     * De StudentController klasse moet alle student-gerelateerde aanvragen
     * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
     * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
     * een nieuwe methode schrijven.
     *
     * @param infoSys - het toegangspunt tot het domeinmodel
     */
    public RoosterController(PrIS infoSys) {
        informatieSysteem = infoSys;
    }

    public void handle(Conversation conversation) {
        if (conversation.getRequestedURI().startsWith("/student/rooster")) {
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
        String  lGroepIdZelf = lStudentZelf.getGroepId();

        Klas lKlas = informatieSysteem.getKlasVanStudent(lStudentZelf);
        String klasCodeStudent = lKlas.getKlasCode();

        ArrayList<Les> rooster = informatieSysteem.getRooster();
        //ArrayList<Les> studentLessen = new ArrayList<Les>();

        JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();

        for (Les les : rooster) {
            if (les.getKlasCode().equals(klasCodeStudent)) {
                JsonObjectBuilder lJsonObjectBuilderLes = Json.createObjectBuilder();
                lJsonObjectBuilderLes
                        .add("datum", les.getDatum())
                        .add("startTijd", les.getStartTijd())
                        .add("eindTijd", les.getEindTijd())
                        .add("vakCode", les.getVakCode())
                        .add("docent", les.getDocent())
                        .add("lokaal", les.getLokaal())
                        .add("klasCode", les.getKlasCode());
                lJsonArrayBuilder.add(lJsonObjectBuilderLes);
            }
        }
        String lJsonOutStr = lJsonArrayBuilder.build().toString();
        conversation.sendJSONMessage(lJsonOutStr);
    }
}
