package service;

import model.Film;
import repository.FilmRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = Objects.requireNonNull(filmRepository);
    }

    public Film visualizzaFilm(int id) {
        return films().stream().filter(film -> film.getId() == id).findFirst().orElse(null);
    }

    public List<Film> cercaFilm(int id) {
        return cercaFilm(id, null, null, null, null, null, null);
    }

    /** Cerca film per ID esatto e per testo parziale negli altri campi testuali. */
    public List<Film> cercaFilm(String titolo, String genere, String regista,
                                Integer anno, Integer durataMinima, Integer durataMassima) {
        return cercaFilm(null, titolo, genere, regista, anno, durataMinima, durataMassima);
    }

    public List<Film> cercaFilm(Integer id, String titolo, String genere, String regista,
                                Integer anno, Integer durataMinima, Integer durataMassima) {
        if (durataMinima != null && durataMassima != null && durataMinima > durataMassima) {
            return List.of();
        }

        String titoloFiltro = normalizza(titolo);
        String genereFiltro = normalizza(genere);
        String registaFiltro = normalizza(regista);
        return films().stream().filter(film ->
                        (id == null || film.getId() == id)
                                && (titoloFiltro.isEmpty() || normalizza(film.getTitolo_film()).contains(titoloFiltro))
                                && (genereFiltro.isEmpty() || normalizza(film.getGenere()).contains(genereFiltro))
                                && (registaFiltro.isEmpty() || normalizza(film.getRegista()).contains(registaFiltro))
                                && (anno == null || film.getAnno() == anno)
                                && (durataMinima == null || film.getDurata_minuti() >= durataMinima)
                                && (durataMassima == null || film.getDurata_minuti() <= durataMassima))
                .toList();
    }

    public List<Film> cercaFilmPerTitolo(String titolo) {
        return cercaFilm(titolo, null, null, null, null, null);
    }

    public List<Film> cercaFilmPerGenere(String genere) {
        return cercaFilm(null, genere, null, null, null, null);
    }

    private List<Film> films() {
        List<Film> elenco = filmRepository.getFilms();
        return elenco == null ? List.of() : elenco;
    }

    private String normalizza(String valore) {
        return valore == null ? "" : valore.trim().toLowerCase(Locale.ROOT);
    }
}
