package domain;

import java.util.List;

public class Prenotazione {
    private int id;
    private int idCliente;
    private int idProiezione;
    private List<Integer> posti;

    public Prenotazione(int id, int idCliente, int idProiezione, List<Integer> posti) {
        this.id = id;
        this.idCliente = idCliente;
        this.idProiezione = idProiezione;
        this.posti = posti;
    }

    // Getters & Setters
}
