package domain;

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

    // Getters & Setters
}
