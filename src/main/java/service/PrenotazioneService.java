package service;

import model.Prenotazione;
import repository.PrenotazioneRepository;

import java.util.List;
import java.util.Objects;

public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = Objects.requireNonNull(prenotazioneRepository);
    }

    public Prenotazione visualizzaPrenotazione(int id) {
        return prenotazioni().stream()
                .filter(prenotazione -> prenotazione.getId() == id)
                .findFirst().orElse(null);
    }

    /** Filtri combinabili; i parametri null vengono ignorati. */
    public List<Prenotazione> cercaPrenotazioni(Integer idCliente, Integer idProiezione) {
        return prenotazioni().stream()
                .filter(prenotazione -> idCliente == null || prenotazione.getIdCliente() == idCliente)
                .filter(prenotazione -> idProiezione == null || prenotazione.getIdProiezione() == idProiezione)
                .toList();
    }

    public List<Prenotazione> cercaPrenotazioniPerCliente(int idCliente) {
        return cercaPrenotazioni(idCliente, null);
    }

    public List<Prenotazione> cercaPrenotazioniPerProiezione(int idProiezione) {
        return cercaPrenotazioni(null, idProiezione);
    }

    private List<Prenotazione> prenotazioni() {
        List<Prenotazione> elenco = prenotazioneRepository.getPrenotazioni();
        return elenco == null ? List.of() : elenco;
    }
}
