package converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.Proiezione;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProiezioneConverter {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper mapper = new ObjectMapper();

    public Proiezione fromJson(JsonNode json) {
        return new Proiezione(
                json.get("id").asInt(),
                json.get("id_film").asInt(),
                LocalDateTime.parse(json.get("data_ora_proiezione").asText(), FORMATO_DATA),
                json.get("eta_minima").asInt(),
                json.get("prezzo_biglietto").asDouble()
        );
    }

    public List<Proiezione> fromJsonArray(JsonNode jsonArray) {
        List<Proiezione> proiezioni = new ArrayList<>();
        for (JsonNode json : jsonArray) {
            proiezioni.add(fromJson(json));
        }
        return proiezioni;
    }

    public ObjectNode toJson(Proiezione proiezione) {
        ObjectNode json = mapper.createObjectNode();
        json.put("id", proiezione.getId());
        json.put("id_film", proiezione.getIdFilm());
        json.put("data_ora_proiezione", proiezione.getDataOra().format(FORMATO_DATA));
        json.put("eta_minima", proiezione.getEtaMinima());
        json.put("prezzo_biglietto", proiezione.getPrezzoBiglietto());
        return json;
    }
}
