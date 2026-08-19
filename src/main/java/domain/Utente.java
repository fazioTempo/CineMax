package domain;

public class Utente {
    protected int id;
    protected String username;
    protected String password;
    protected String type; // proiezionista, bigliettaio, cliente

    public Utente(int id, String username, String password, String type) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        // logout logic
    }

    // Getters & Setters
}
