package repository;

import model.Film;

import java.util.LinkedList;

public class FilmRepository {
    //ATTRIBUTI
    private LinkedList<Film> films;
    private String path;

    //METODI
    public FilmRepository(String path)
    {
        this.path = path;
    }

    public LinkedList<Film> getFilms() {
        return films;
    }
}
