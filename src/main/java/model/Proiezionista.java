package model;

public class Proiezionista extends Utente {

    public Proiezionista(int id, String username, String password) {
        super(id, username, password, "proiezionista");
    }

    public boolean inserisciFilm(Film film) {
        return true;
    }

    public boolean creaProiezione(Proiezione proiezione) {
        return true;
    }

    public boolean modificaProiezione(int idProiezione) {
        return true;
    }

    public boolean eliminaProiezione(int idProiezione) {
        return true;
    }
}
