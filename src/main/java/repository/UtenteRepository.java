package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Utente;

import java.io.InputStream;
import java.util.List;

public class UtenteRepository {

    private final ObjectMapper mapper;

    public UtenteRepository() {
        this.mapper = new ObjectMapper();
    }

    public List<Utente> getAll() {

        try {

            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("public.assets/utenti.json");

            if (is == null) {
                throw new RuntimeException("File utenti.json non trovato");
            }

            return mapper.readValue(
                    is,
                    new TypeReference<List<Utente>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante la lettura di utenti.json",
                    e
            );
        }
    }
}