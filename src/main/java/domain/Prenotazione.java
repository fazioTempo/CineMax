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

    //METODI GETTERS

    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdProiezione() {
        return idProiezione;
    }

    //METODI SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setIdProiezione(int idProiezione) {
        this.idProiezione = idProiezione;
    }

    public void setPosti(List<Integer> posti) {
        this.posti = posti;
    }
}
