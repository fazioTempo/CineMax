package repository;

import model.Film;
import model.Prenotazione;

import java.util.LinkedList;

public class PrenotazioneRepository {
    //ATTRIBUTI
    private LinkedList<Prenotazione> prenotazioni;
    private String path;

    public PrenotazioneRepository(String path)
    {
        this.path = path;
    }

    public LinkedList<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }
}
