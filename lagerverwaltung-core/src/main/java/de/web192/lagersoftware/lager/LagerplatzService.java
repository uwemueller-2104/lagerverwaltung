package de.web192.lagersoftware.lager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LagerplatzService {

    private final LagerplatzRepository lagerplatzRepository;

    public LagerplatzService(LagerplatzRepository lagerplatzRepository) {
        this.lagerplatzRepository = lagerplatzRepository;
    }

    public List<Lagerplatz> alle() {
        return lagerplatzRepository.findAll();
    }

    public List<Lagerplatz> alleFuerLager(Long lagerId) {
        return lagerplatzRepository.findByLagerId(lagerId);
    }

    public Optional<Lagerplatz> findeById(Long id) {
        return lagerplatzRepository.findById(id);
    }

    public Lagerplatz speichern(Lagerplatz lagerplatz) {
        return lagerplatzRepository.save(lagerplatz);
    }

    public void loeschen(Long id) {
        lagerplatzRepository.deleteById(id);
    }
}
