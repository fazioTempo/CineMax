package model;

import java.time.LocalDateTime;

public class Proiezione {
    private int id;
    private int idFilm;
    private LocalDateTime dataOra;
    private int etaMinima;
    private double prezzoBiglietto;

    public Proiezione(int id, int idFilm, LocalDateTime dataOra, int etaMinima, double prezzoBiglietto) {
        this.id = id;
        this.idFilm = idFilm;
        this.dataOra = dataOra;
        this.etaMinima = etaMinima;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public int getEtaMinima() { return etaMinima; }
    public void setEtaMinima(int etaMinima) { this.etaMinima = etaMinima; }

    public double getPrezzoBiglietto() { return prezzoBiglietto; }
    public void setPrezzoBiglietto(double prezzoBiglietto) { this.prezzoBiglietto = prezzoBiglietto; }
}
