package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Prenotazione;
import model.Utente;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class UtenteRepository {
    //ATTRIBUTI
    private LinkedList<Utente> utenti;
    private String path;
    //ATTRIBUTI JACKSON
    private final ObjectMapper objectMapper;
    //METODI
    public UtenteRepository(String path)
    {
        this.path = path;
        this.objectMapper = new ObjectMapper();
    }

    public LinkedList<Utente> getUtenti() {
        return utenti;
    }

    public List<Utente> getAll() {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                throw new RuntimeException("File non trovato: " + path);
            }

            return objectMapper.readValue(is, new TypeReference<List<Utente>>() {

                    }
            );

        } catch (Exception e) {
            throw new RuntimeException("Errore nella lettura del file", e);
        }
    }

}