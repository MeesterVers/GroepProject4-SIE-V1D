package controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.afwezigheid.Afwezigheid;
import model.klas.Klas;
import model.les.Les;
import model.persoon.Student;
import model.presentie.Presentie;
import server.Conversation;
import server.Handler;

public class PresentieController implements Handler {
    private PrIS informatieSysteem;
    ArrayList presentie = new ArrayList();

    public PresentieController(PrIS infoSys) {
        informatieSysteem = infoSys;
    }

    public void handle(Conversation conversation) {
        if (conversation.getRequestedURI().startsWith("/docent/presentie/ophalen")) {
            try {
                ophalen(conversation);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
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
    private void ophalen(Conversation conversation) throws ParseException {
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
                        .add("datum", l.getDatum())
                        .add("klasCode", l.getKlasCode())
                        .add("startTijd", l.getStartTijd())
                        .add("eindTijd", l.getEindTijd())
                        .add("vakCode", l.getVakCode());
                JsonArrayBuilder lJsonArrayBuilder1 = Json.createArrayBuilder();
                for (Student std : klasobj.getStudenten()) {
                    JsonObjectBuilder lJsonObjectBuilderStudent = Json.createObjectBuilder();
                    lJsonObjectBuilderStudent
                            .add("id", std.getStudentNummer())
                            .add("naam", std.getVoornaam())
                            .add("achternaam", std.getVolledigeAchternaam())
                            .add("soort", "aanwezig");
                    for (Afwezigheid afw : l.getAfwezigheden()) {
                        if (afw.getStudent().getGebruikersnaam().equals(std.getGebruikersnaam())) {
                            //System.out.println(afw.getSoort());
                            lJsonObjectBuilderStudent
                            .add("soort", afw.getSoort())
                            .add("startDatum", afw.getStartDatum())
                            .add("eindDatum", afw.getEindDatum())
                            .add("beschrijving", afw.getBeschrijving());
                        }
                    }

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date les_datum = format.parse(datum);
                    //System.out.println(les_datum);

                    for (Afwezigheid afw : informatieSysteem.getPeriodeAfwezigheden()) {
                        if (afw.getStudent().getGebruikersnaam().equals(std.getGebruikersnaam())) {
                            Date afw_start_datum = format.parse(afw.getStartDatum());
                            Date afw_eind_datum = format.parse(afw.getEindDatum());

                            //System.out.println(les_datum + " " + afw_start_datum + " " + afw_eind_datum);
                            if (afw_start_datum.compareTo(les_datum) * les_datum.compareTo(afw_eind_datum) >= 0 || afw.getBeschrijving().equals("ziekte")) {
                                //System.out.println("true");
                                lJsonObjectBuilderStudent
                                    .add("soort", afw.getSoort())
                                    .add("startDatum", afw.getStartDatum())
                                    .add("eindDatum", afw.getEindDatum())
                                    .add("beschrijving", afw.getBeschrijving());
                                }
                            } else {
                            //System.out.println("false");
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


    /**
     * Deze methode haalt eerst de opgestuurde JSON-data op. Op basis van deze gegevens
     * het domeinmodel gewijzigd. Een eventuele errorcode wordt tenslotte
     * weer (als JSON) teruggestuurd naar de Polymer-GUI!
     *
     * @param conversation - alle informatie over het request
     */
    private void opslaan(Conversation conversation) {
        JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
        String vakCode= lJsonObjectIn.getString("vakCode");
        String klasCode = lJsonObjectIn.getString("klasCode");
        String datum = lJsonObjectIn.getString("datum");

        Les rooster_les = null;
        for (Les l : informatieSysteem.getRooster()) {
            if (l.getVakCode().equals(vakCode) && l.getKlasCode().equals(klasCode) && l.getDatum().equals(datum)) {
                rooster_les = l;
            }
        }

        //Het lJsonObjectIn bevat niet enkel strings maar ook een heel (Json) array van Json-objecten.
        // in dat Json-object zijn telkens het studentnummer en een indicatie of de student
        // tot het zelfde team hoort opgenomen.

        //Het Json-array heeft als naam: "groupMembers"
        JsonArray leerlingen = lJsonObjectIn.getJsonArray("leerlingen");

        rooster_les.leegPresenties();
        if (leerlingen != null) {
            // bepaal op basis van de huidige tijd een unieke string
            for (int i=0;i<leerlingen.size();i++){
                JsonObject leerling = leerlingen.getJsonObject(i);
                String status = leerling.getString("status");
                String id = leerling.getString("id");
                Student std = informatieSysteem.getStudent(Integer.parseInt(id));
                rooster_les.voegPresentieToe(new Presentie(std, status));
            }
        }

//        for (Les l : informatieSysteem.getRooster()) {
//            for (Presentie p : l.getPresenties()) {
//                System.out.println(p);
//            }
//        }
//        System.out.println("\n");

        JsonObjectBuilder lJob = Json.createObjectBuilder();
        lJob.add("errorcode", 0);
        //nothing to return use only errorcode to signal: ready!
        String lJsonOutStr = lJob.build().toString();
        conversation.sendJSONMessage(lJsonOutStr);					// terug naar de Polymer-GUI!
    }
}
