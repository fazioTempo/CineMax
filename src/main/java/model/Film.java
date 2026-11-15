package model;

public class Film {
    private int id;
    private String titolo_film;
    private String genere;
    private String regista;
    private int anno;
    private int durata_minuti;

    private String descrizione;

    public Film(int id, String titolo,String genere, String regista, int anno,  int durata) {
        this.id = id;
        this.titolo_film = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata_minuti = durata;
    }
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitolo_film() { return titolo_film; }
    public void setTitolo_film(String titolo_film) { this.titolo_film = titolo_film; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public String getRegista() { return regista; }
    public void setRegista(String regista) { this.regista = regista; }

    public int getAnno() { return anno; }
    public void setAnno(int anno) { this.anno = anno; }

    public int getDurata_minuti() { return durata_minuti; }
    public void setDurata_minuti(int durata_minuti) { this.durata_minuti = durata_minuti; }
}

