package domain;

import java.util.List;

public class Bigliettaio extends Utente {

    public Bigliettaio(int id, String username, String password) {
        super(id, username, password, "bigliettaio");
    }

    public List<Prenotazione> cercaPrenotazioniOdierna() {
        return List.of();
    }

    public List<Prenotazione> cercaPrenotazioni(Object filtro) {
        return List.of();
    }
}
