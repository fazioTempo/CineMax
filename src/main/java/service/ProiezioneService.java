package service;

import model.Film;
import model.Proiezione;
import repository.FilmRepository;
import repository.ProiezioneRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Operazioni di ricerca sul catalogo delle proiezioni caricato dai repository. */
public class ProiezioneService {

    private final ProiezioneRepository proiezioneRepository;
    private final FilmRepository filmRepository;

    /** Costruttore utile per le ricerche che non richiedono i dati dei film. */
    public ProiezioneService(ProiezioneRepository proiezioneRepository) {
        this(proiezioneRepository, null);
    }

    /** Il repository dei film serve per filtrare le proiezioni per titolo o genere. */
    public ProiezioneService(ProiezioneRepository proiezioneRepository, FilmRepository filmRepository) {
        this.proiezioneRepository = Objects.requireNonNull(proiezioneRepository);
        this.filmRepository = filmRepository;
    }

    /** Cerca tutte le proiezioni di un film, ordinate per data e ora. */
    public List<Proiezione> cercaProiezioniPerFilm(int idFilm) {
        return proiezioni().stream()
                .filter(p -> p.getIdFilm() == idFilm)
                .sorted(Comparator.comparing(Proiezione::getDataOra,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    // Nomi mantenuti compatibili con le operazioni del precedente GestoreProiezioni.
    public List<Proiezione> cercaProiezione(int idFilm) {
        return cercaProiezioniPerFilm(idFilm);
    }

    /** Cerca le proiezioni nell'intervallo indicato, con estremi inclusivi. */
    public List<Proiezione> cercaProiezioniPerIntervallo(LocalDateTime da, LocalDateTime a) {
        if (da == null || a == null || da.isAfter(a)) return List.of();
        return proiezioni().stream()
                .filter(p -> p.getDataOra() != null && !p.getDataOra().isBefore(da)
                        && !p.getDataOra().isAfter(a))
                .sorted(Comparator.comparing(Proiezione::getDataOra))
                .toList();
    }

    public List<Proiezione> cercaProiezione(LocalDateTime da, LocalDateTime a) {
        return cercaProiezioniPerIntervallo(da, a);
    }

    /** Restituisce le proiezioni con l'ID richiesto, oppure null se non esistono. */
    public Proiezione visualizzaProiezione(int id) {
        return proiezioni().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public Film visualizzaFilm(int idFilm) {
        if (filmRepository == null || filmRepository.getFilms() == null) return null;
        return filmRepository.getFilms().stream().filter(f -> f.getId() == idFilm).findFirst().orElse(null);
    }

    /**
     * Filtri opzionali combinabili. Le date e i prezzi hanno estremi inclusivi;
     * titolo e genere sono confrontati senza distinzione tra maiuscole e minuscole.
     */
    public List<Proiezione> cercaProiezioni(String titolo, String genere,
                                            LocalDateTime da, LocalDateTime a,
                                            Double prezzoMin, Double prezzoMax) {
        if ((da != null && a != null && da.isAfter(a))
                || (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax)) {
            return List.of();
        }

        String titoloFiltro = normalizza(titolo);
        String genereFiltro = normalizza(genere);
        List<Film> films = filmRepository == null || filmRepository.getFilms() == null
                ? List.of() : filmRepository.getFilms();

        return proiezioni().stream().filter(p -> {
                    if (p.getDataOra() == null
                            || (da != null && p.getDataOra().isBefore(da))
                            || (a != null && p.getDataOra().isAfter(a))
                            || (prezzoMin != null && p.getPrezzoBiglietto() < prezzoMin)
                            || (prezzoMax != null && p.getPrezzoBiglietto() > prezzoMax)) {
                        return false;
                    }
                    if (titoloFiltro.isEmpty() && genereFiltro.isEmpty()) return true;
                    return films.stream().filter(f -> f.getId() == p.getIdFilm()).anyMatch(f ->
                            (titoloFiltro.isEmpty() || normalizza(f.getTitolo_film()).contains(titoloFiltro))
                                    && (genereFiltro.isEmpty() || normalizza(f.getGenere()).contains(genereFiltro)));
                })
                .sorted(Comparator.comparing(Proiezione::getDataOra))
                .toList();
    }

    public List<Proiezione> cercaProiezione(String titolo, String genere,
                                            LocalDateTime da, LocalDateTime a,
                                            Double prezzoMin, Double prezzoMax) {
        return cercaProiezioni(titolo, genere, da, a, prezzoMin, prezzoMax);
    }

    private List<Proiezione> proiezioni() {
        List<Proiezione> elenco = proiezioneRepository.getProiezioni();
        return elenco == null ? List.of() : elenco;
    }

    private String normalizza(String valore) {
        return valore == null ? "" : valore.trim().toLowerCase(Locale.ROOT);
    }
}
