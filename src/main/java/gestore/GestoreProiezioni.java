package gestore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Film;
import domain.Proiezione;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Gestisce il catalogo e il palinsesto delle proiezioni letto dal CSV. */
public class GestoreProiezioni {
    private static final String RISORSA_PROIEZIONI = "/public/assets/proiezioni.csv";
    private static final String RISORSA_PRENOTAZIONI = "/public/assets/prenotazioni.json";
    private static final Path FILE_PROIEZIONI = Path.of("src/main/resources/public/assets/proiezioni.csv");
    private static final Path FILE_PRENOTAZIONI = Path.of("src/main/resources/public/assets/prenotazioni.json");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String INTESTAZIONE_CSV = "id,id_film,data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto";

    private final ObjectMapper mapper = new ObjectMapper();
    private Path fileProiezioni;
    private List<Proiezione> proiezioni;
    private Map<Integer, Film> catalogoFilm;

    public GestoreProiezioni() throws IOException {
        this(FILE_PROIEZIONI);
    }

    public GestoreProiezioni(Path fileProiezioni) throws IOException {
        this.fileProiezioni = Objects.requireNonNull(fileProiezioni);
        Importazione importazione = leggiCsv(fileProiezioni);
        this.proiezioni = importazione.proiezioni();
        this.catalogoFilm = importazione.filmPerId();
    }

    public synchronized List<Proiezione> cercaProiezione(int idFilm) {
        return proiezioni.stream().filter(p -> p.getIdFilm() == idFilm)
                .sorted((a, b) -> a.getDataOra().compareTo(b.getDataOra())).toList();
    }

    public synchronized List<Proiezione> cercaProiezione(LocalDateTime da, LocalDateTime a) {
        if (da == null || a == null || da.isAfter(a)) return List.of();
        return proiezioni.stream().filter(p -> !p.getDataOra().isBefore(da) && !p.getDataOra().isAfter(a))
                .sorted((x, y) -> x.getDataOra().compareTo(y.getDataOra())).toList();
    }

    /** Filtri opzionali combinabili; date e prezzi hanno estremi inclusivi. */
    public synchronized List<Proiezione> cercaProiezione(String titolo, String genere,
                                                           LocalDateTime da, LocalDateTime a,
                                                           Double prezzoMin, Double prezzoMax) {
        if ((da != null && a != null && da.isAfter(a))
                || (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax)) return List.of();
        String titoloFiltro = normalizza(titolo);
        String genereFiltro = normalizza(genere);
        return proiezioni.stream().filter(p -> {
            Film film = catalogoFilm.get(p.getIdFilm());
            if (film == null) return false;
            return (titoloFiltro.isEmpty() || normalizza(film.getTitolo_film()).contains(titoloFiltro))
                    && (genereFiltro.isEmpty() || normalizza(film.getGenere()).contains(genereFiltro))
                    && (da == null || !p.getDataOra().isBefore(da))
                    && (a == null || !p.getDataOra().isAfter(a))
                    && (prezzoMin == null || p.getPrezzoBiglietto() >= prezzoMin)
                    && (prezzoMax == null || p.getPrezzoBiglietto() <= prezzoMax);
        }).sorted((x, y) -> x.getDataOra().compareTo(y.getDataOra())).toList();
    }

    public synchronized Proiezione visualizzaProiezione(int id) {
        return proiezioni.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    /** Restituisce i dati completi del film associato a una proiezione. */
    public synchronized Film visualizzaFilm(int idFilm) {
        return catalogoFilm.get(idFilm);
    }

    public synchronized boolean aggiungiProiezione(Proiezione proiezione) {
        if (!valida(proiezione) || visualizzaProiezione(proiezione.getId()) != null
                || haSovrapposizioni(proiezione, -1)) return false;
        List<Proiezione> aggiornate = new ArrayList<>(proiezioni);
        aggiornate.add(proiezione);
        return salvaEApplica(aggiornate, catalogoFilm);
    }

    public synchronized boolean modificaProiezione(Proiezione proiezione) {
        if (!valida(proiezione)) return false;
        int indice = indicePerId(proiezione.getId());
        if (indice < 0 || haPrenotazioni(proiezione.getId())
                || haSovrapposizioni(proiezione, proiezione.getId())) return false;
        List<Proiezione> aggiornate = new ArrayList<>(proiezioni);
        aggiornate.set(indice, proiezione);
        return salvaEApplica(aggiornate, catalogoFilm);
    }

    public synchronized boolean eliminaProiezione(int id) {
        int indice = indicePerId(id);
        if (indice < 0 || haPrenotazioni(id)) return false;
        List<Proiezione> aggiornate = new ArrayList<>(proiezioni);
        aggiornate.remove(indice);
        return salvaEApplica(aggiornate, catalogoFilm);
    }

    /** Importa il CSV del docente; il palinsesto attivo cambia solo se tutte le righe sono valide. */
    public synchronized boolean caricaProiezioniDaFile(String percorsoFile) {
        if (percorsoFile == null || percorsoFile.isBlank()) return false;
        try {
            Importazione importazione = leggiCsv(Path.of(percorsoFile));
            this.proiezioni = importazione.proiezioni();
            this.catalogoFilm = importazione.filmPerId();
            this.fileProiezioni = Path.of(percorsoFile);
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    private Importazione leggiCsv(Path percorso) throws IOException {
        List<String> righe;
        if (Files.exists(percorso)) {
            righe = Files.readAllLines(percorso, StandardCharsets.UTF_8);
        } else {
            try (InputStream input = GestoreProiezioni.class.getResourceAsStream(RISORSA_PROIEZIONI)) {
                if (input == null) throw new IOException("File proiezioni non trovato: " + percorso);
                righe = new String(input.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
            }
        }
        return parseCsv(righe);
    }

    private Importazione parseCsv(List<String> righe) throws IOException {
        List<String> contenuto = righe.stream().filter(r -> !r.isBlank()).toList();
        if (contenuto.isEmpty()) throw new IOException("Il CSV delle proiezioni è vuoto");
        char separatore = contenuto.get(0).contains(";") ? ';' : ',';
        List<String> primaRiga = dividiCsv(contenuto.get(0), separatore);
        Map<String, Integer> intestazione = new HashMap<>();
        for (int i = 0; i < primaRiga.size(); i++) {
            intestazione.put(normalizza(primaRiga.get(i).replace("\uFEFF", "")), i);
        }
        boolean conIntestazione = intestazione.containsKey("data_ora_proiezione")
                || intestazione.containsKey("titolo_film");
        int rigaIniziale = conIntestazione ? 1 : 0;
        List<Proiezione> elenco = new ArrayList<>();
        Map<Integer, Film> filmPerId = new LinkedHashMap<>();
        Map<String, Integer> idPerFilm = new LinkedHashMap<>();
        Set<Integer> idProiezioni = new HashSet<>();

        for (int r = rigaIniziale; r < contenuto.size(); r++) {
            List<String> campi = dividiCsv(contenuto.get(r), separatore);
            try {
                String data = valore(campi, intestazione, conIntestazione, "data_ora_proiezione", 0);
                String titolo = valore(campi, intestazione, conIntestazione, "titolo_film", 1);
                String genere = valore(campi, intestazione, conIntestazione, "genere", 2);
                String regista = valore(campi, intestazione, conIntestazione, "regista", 3);
                int anno = Integer.parseInt(valore(campi, intestazione, conIntestazione, "anno", 4));
                int durata = Integer.parseInt(valore(campi, intestazione, conIntestazione, "durata_minuti", 5));
                int eta = Integer.parseInt(valore(campi, intestazione, conIntestazione, "eta_minima", 6));
                double prezzo = Double.parseDouble(valore(campi, intestazione, conIntestazione, "prezzo_biglietto", 7).replace(',', '.'));

                String chiaveFilm = normalizza(titolo) + "|" + normalizza(genere) + "|" + normalizza(regista) + "|" + anno + "|" + durata;
                int idFilm = valoreOpzionale(campi, intestazione, conIntestazione, "id_film", 0);
                if (idFilm <= 0) idFilm = idPerFilm.computeIfAbsent(chiaveFilm, chiave -> idPerFilm.size() + 1);
                idPerFilm.putIfAbsent(chiaveFilm, idFilm);
                filmPerId.putIfAbsent(idFilm, new Film(idFilm, titolo, genere, regista, anno, durata));
                int id = valoreOpzionale(campi, intestazione, conIntestazione, "id", r - rigaIniziale + 1);
                // Il CSV del docente non include gli ID: genera ID di riga nell'ordine del file.
                if (!conIntestazione || !intestazione.containsKey("id")) id = r - rigaIniziale + 1;
                if (!idProiezioni.add(id) || id <= 0 || idFilm <= 0 || durata <= 0 || eta < 0 || prezzo < 0) {
                    throw new IOException("Valori non validi alla riga " + (r + 1));
                }
                Proiezione p = new Proiezione(id, idFilm, parseData(data), eta, prezzo);
                if (!valida(p)) throw new IOException("Dati proiezione non validi alla riga " + (r + 1));
                elenco.add(p);
            } catch (IndexOutOfBoundsException | NumberFormatException | DateTimeParseException e) {
                throw new IOException("CSV non valido alla riga " + (r + 1), e);
            }
        }
        if (elenco.isEmpty()) throw new IOException("Il CSV non contiene proiezioni");
        return new Importazione(elenco, filmPerId);
    }

    private String valore(List<String> campi, Map<String, Integer> intestazione,
                          boolean conIntestazione, String nome, int indiceSenzaIntestazione) throws IOException {
        int indice = conIntestazione ? intestazione.getOrDefault(nome, -1) : indiceSenzaIntestazione;
        if (indice < 0 || indice >= campi.size()) throw new IOException("Colonna CSV mancante: " + nome);
        return campi.get(indice).trim();
    }

    private int valoreOpzionale(List<String> campi, Map<String, Integer> intestazione,
                                boolean conIntestazione, String nome, int predefinito) {
        if (!conIntestazione || !intestazione.containsKey(nome)) return predefinito;
        return Integer.parseInt(campi.get(intestazione.get(nome)).trim());
    }

    private LocalDateTime parseData(String valore) {
        try {
            return LocalDateTime.parse(valore, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(valore);
        }
    }

    private List<String> dividiCsv(String riga, char separatore) throws IOException {
        List<String> colonne = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean traVirgolette = false;
        for (int i = 0; i < riga.length(); i++) {
            char c = riga.charAt(i);
            if (c == '"') {
                if (traVirgolette && i + 1 < riga.length() && riga.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else traVirgolette = !traVirgolette;
            } else if (c == separatore && !traVirgolette) {
                colonne.add(campo.toString());
                campo.setLength(0);
            } else campo.append(c);
        }
        if (traVirgolette) throw new IOException("Virgolette non chiuse nel CSV");
        colonne.add(campo.toString());
        return colonne;
    }

    private boolean valida(Proiezione p) {
        return p != null && p.getId() > 0 && p.getIdFilm() > 0 && p.getDataOra() != null
                && p.getEtaMinima() >= 0 && p.getPrezzoBiglietto() >= 0;
    }

    private boolean haSovrapposizioni(Proiezione candidata, int idEscluso) {
        Film filmCandidato = catalogoFilm.get(candidata.getIdFilm());
        if (filmCandidato == null) return true;
        LocalDateTime inizio = candidata.getDataOra();
        LocalDateTime fine = inizio.plusMinutes(filmCandidato.getDurata_minuti());
        return proiezioni.stream().filter(p -> p.getId() != idEscluso).anyMatch(esistente -> {
            Film film = catalogoFilm.get(esistente.getIdFilm());
            if (film == null) return true;
            LocalDateTime fineEsistente = esistente.getDataOra().plusMinutes(film.getDurata_minuti());
            return inizio.isBefore(fineEsistente) && esistente.getDataOra().isBefore(fine);
        });
    }

    private boolean haPrenotazioni(int idProiezione) {
        try {
            JsonNode prenotazioni;
            if (Files.exists(FILE_PRENOTAZIONI)) prenotazioni = mapper.readTree(FILE_PRENOTAZIONI.toFile());
            else try (InputStream input = GestoreProiezioni.class.getResourceAsStream(RISORSA_PRENOTAZIONI)) {
                if (input == null) return false;
                prenotazioni = mapper.readTree(input);
            }
            if (!prenotazioni.isArray()) return true;
            for (JsonNode prenotazione : prenotazioni) {
                if (prenotazione.has("id_proiezione") && prenotazione.get("id_proiezione").asInt() == idProiezione) return true;
            }
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private String normalizza(String valore) {
        return valore == null ? "" : valore.trim().toLowerCase(Locale.ROOT);
    }

    private int indicePerId(int id) {
        for (int i = 0; i < proiezioni.size(); i++) if (proiezioni.get(i).getId() == id) return i;
        return -1;
    }

    private boolean salvaEApplica(List<Proiezione> aggiornate, Map<Integer, Film> film) {
        try {
            salvaCsv(aggiornate, film);
            proiezioni = aggiornate;
            catalogoFilm = new HashMap<>(film);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void salvaCsv(List<Proiezione> elenco, Map<Integer, Film> filmPerId) throws IOException {
        Path cartella = fileProiezioni.toAbsolutePath().getParent();
        if (cartella != null) Files.createDirectories(cartella);
        List<String> righe = new ArrayList<>();
        righe.add(INTESTAZIONE_CSV);
        for (Proiezione p : elenco) {
            Film film = filmPerId.get(p.getIdFilm());
            if (film == null) throw new IOException("Film non trovato per la proiezione " + p.getId());
            righe.add(String.join(",", Integer.toString(p.getId()), Integer.toString(p.getIdFilm()),
                    csv(p.getDataOra().format(FORMATO_DATA)), csv(film.getTitolo_film()), csv(film.getGenere()),
                    csv(film.getRegista()), Integer.toString(film.getAnno()), Integer.toString(film.getDurata_minuti()),
                    Integer.toString(p.getEtaMinima()), Double.toString(p.getPrezzoBiglietto())));
        }
        Files.write(fileProiezioni, righe, StandardCharsets.UTF_8);
    }

    private String csv(String valore) {
        String safe = valore == null ? "" : valore;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private record Importazione(List<Proiezione> proiezioni, Map<Integer, Film> filmPerId) {}
}
