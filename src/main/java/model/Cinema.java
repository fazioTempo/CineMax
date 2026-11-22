package model;

import repository.FilmRepository;
import repository.PrenotazioneRepository;
import repository.ProiezioneRepository;
import repository.UtenteRepository;

import java.util.LinkedList;

public class Cinema {
    //ATTRIBUTI
    private String nome;
    private int numPosti;
    private LinkedList<Film> films;
    private LinkedList<Prenotazione> prenotazioni;
    private LinkedList<Proiezione> proiezioni;
    private LinkedList<Utente> utenti;
    //METODI
    public Cinema(String nome, int numPosti )
    {
        this.nome=nome;
        this.numPosti=numPosti;
        FilmRepository filmR1 = new FilmRepository("assets/film.json");
        PrenotazioneRepository prenotazioneR1 = new PrenotazioneRepository("assets/prenotazioni.json");
        ProiezioneRepository proiezioneR1 = new ProiezioneRepository("assets/proiezioni.json");
        UtenteRepository utenteR1 = new UtenteRepository("assets/utenti.json");

        this.films = filmR1.getFilms();
        this.prenotazioni = prenotazioneR1.getPrenotazioni();
        this.proiezioni = proiezioneR1.getProiezioni();
        this.utenti = utenteR1.getUtenti();


    }
}
