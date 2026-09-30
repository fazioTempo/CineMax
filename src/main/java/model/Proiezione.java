package model;

import java.time.LocalDateTime;

public class Proiezione {
    private int id;
    private int idFilm;
    private String sala;
    private LocalDateTime dataOra;

    public Proiezione(int id, int idFilm, String sala, LocalDateTime dataOra) {
        this.id = id;
        this.idFilm = idFilm;
        this.sala = sala;
        this.dataOra = dataOra;
    }

    //METODI GETTERS

    public int getId() {
        return id;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public String getSala() {
        return sala;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    //METODI SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

}
