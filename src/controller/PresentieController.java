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
        String les = lJsonObjectIn.getString("les");
        String docent = lJsonObjectIn.getString("username");
        String[] parts = les.split("\\.");
        String vakCode = parts[0];
        String datum = parts[1];
        String startTijd = parts[2];
        JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();

        for (Les l : rooster) {
            if (l.getDatum().equals(datum) && l.getVakCode().equals(vakCode) && l.getStartTijd().equals(startTijd) && l.getDocent().equals(docent)) {
                String klascode = l.getKlasCode();
                Klas klasobj = informatieSysteem.getKlasviacode(klascode);
                JsonObjectBuilder lJsonObjectBuilderLes = Json.createObjectBuilder();
                lJsonObjectBuilderLes
                        .add("klasCode", l.getKlasCode())
                        .add("startTijd", l.getStartTijd())
                        .add("eindTijd", l.getEindTijd())
                        .add("vakCode", l.getVakCode());
                JsonArrayBuilder lJsonArrayBuilder1 = Json.createArrayBuilder();
                for (Student std : klasobj.getStudenten()) {
                    JsonObjectBuilder lJsonObjectBuilderStudent = Json.createObjectBuilder();
                    lJsonObjectBuilderStudent
                            .add("naam", std.getVoornaam())
                            .add("achternaam", std.getVolledigeAchternaam())
                            .add("soort", "aanwezig");
                    for (Afwezigheid afw : l.getAfwezigheden()) {
                        if (afw.getStudent().getGebruikersnaam().equals(std.getGebruikersnaam())) {
                            lJsonObjectBuilderStudent
                            .add("soort", afw.getSoort())
                            .add("startDatum", afw.getStartDatum())
                            .add("eindDatum", afw.getEindDatum())
                            .add("beschrijving", afw.getBeschrijving());
                        }
                    }

                    for (Afwezigheid afw : informatieSysteem.getPeriodeAfwezigheden()) {
                        if (afw.getStudent().getGebruikersnaam().equals(std.getGebruikersnaam())) {
                            lJsonObjectBuilderStudent
                                    .add("soort", afw.getSoort())
                                    .add("startDatum", afw.getStartDatum())
                                    .add("eindDatum", afw.getEindDatum())
                                    .add("beschrijving", afw.getBeschrijving());
                        }
                    }
                    lJsonArrayBuilder1.add(lJsonObjectBuilderStudent);
                }
                lJsonObjectBuilderLes.add("leerlingen", lJsonArrayBuilder1);
                lJsonArrayBuilder.add(lJsonObjectBuilderLes);
            }
        }

        String lJsonOutStr = lJsonArrayBuilder.build().toString();
        conversation.sendJSONMessage(lJsonOutStr);

    }
}
