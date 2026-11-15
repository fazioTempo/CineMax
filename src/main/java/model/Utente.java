package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "ruolo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Cliente.class, name = "cliente"),
        @JsonSubTypes.Type(value = Bigliettaio.class, name = "bigliettaio"),
        @JsonSubTypes.Type(value = Proiezionista.class, name = "proiezionista")
})
public abstract class Utente {

    protected String nome;
    protected String cognome;
    protected String username;
    protected String password;

    @JsonProperty("data_di_nascita")
    protected String dataDiNascita;

    @JsonProperty("luogo_del_domicilio")
    protected String luogoDelDomicilio;

    protected int id;
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

//METODI GET
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

// METODI SET
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
