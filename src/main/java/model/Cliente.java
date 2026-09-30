package model;

import java.util.List;

public class Cliente extends Utente {

    public Cliente(int id, String username, String password) {
        super(id, username, password, "cliente");
    }

    public List<Proiezione> cercaProiezioni(Object filtro) {
        return List.of();
    }

    public Proiezione visualizzaProiezione(int idProiezione) {
        return null;
    }

    public String registrazione(Object datiReg) {
        return "OK";
    }

    public int prenota() {
        return 0;
    }

    public List<Prenotazione> cercaPrenotazioni(Object filtro) {
        return List.of();
    }

    public boolean modificaPrenotazione(int idPrenotazione) {
        return true;
    }

    public boolean cancellaPrenotazione(int idPrenotazione) {
        return true;
    }
}
