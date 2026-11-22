package repository;

import model.Prenotazione;
import model.Proiezione;

import java.util.LinkedList;

public class ProiezioneRepository {

    //ATTRIBUTI
    private LinkedList<Proiezione> proiezioni;
    private String path;
    //METODI
    public ProiezioneRepository(String path)
    {
        this.path = path;
    }

    public LinkedList<Proiezione> getProiezioni() {
        return proiezioni;
    }
}
