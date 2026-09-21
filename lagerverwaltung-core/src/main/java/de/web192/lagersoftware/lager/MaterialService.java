package de.web192.lagersoftware.lager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> alle() {
        return materialRepository.findAll();
    }

    public Optional<Material> findeById(Long id) {
        return materialRepository.findById(id);
    }

    public Material speichern(Material material) {
        return materialRepository.save(material);
    }

    public void loeschen(Long id) {
        materialRepository.deleteById(id);
    }
}
