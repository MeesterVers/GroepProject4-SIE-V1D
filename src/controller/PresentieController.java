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

public class PresentieController implements Handler {
    private PrIS informatieSysteem;
    ArrayList presentie = new ArrayList();

    public PresentieController(PrIS infoSys) {
        informatieSysteem = infoSys;
    }

    public void handle(Conversation conversation) {
        if (conversation.getRequestedURI().startsWith("/docent/presentie")) {
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
        ArrayList<Les> rooster = informatieSysteem.getRooster();
        ArrayList<Klas> klassen = new ArrayList<Klas>();
        String lGebruikersnaam = lJsonObjectIn.getString("username");
        for (Les l : rooster) {
            if (l.getDocent() .equals(lGebruikersnaam)) {
                Klas k = informatieSysteem.getKlasviacode(l.getKlasCode());
                if (!klassen.contains(k)) {
                    klassen.add(k);
                }
            }
        }
        JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();

        for (Klas k : klassen) {
            JsonObjectBuilder lJsonObjectBuilderKlas = Json.createObjectBuilder();
            lJsonObjectBuilderKlas
                .add("klasCode", k.getKlasCode())
                .add("naam", k.getNaam());
            JsonArrayBuilder lJsonArrayBuilder1 = Json.createArrayBuilder();
            for (Student l : k.getStudenten()) {
                JsonObjectBuilder lJsonObjectBuilderLeerling = Json.createObjectBuilder();
                lJsonObjectBuilderLeerling
                        .add("naam", l.getVoornaam())
                        .add("achternaam", l.getVolledigeAchternaam());
                lJsonArrayBuilder1.add(lJsonObjectBuilderLeerling);
            }
            lJsonObjectBuilderKlas.add("leerling", lJsonArrayBuilder1);
            lJsonArrayBuilder.add(lJsonObjectBuilderKlas);
        }
        String lJsonOutStr = lJsonArrayBuilder.build().toString();
        conversation.sendJSONMessage(lJsonOutStr);

    }
}
