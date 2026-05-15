package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.repository.FacilityRepository;
import grupo10.olympo_academy.security.HtmlSanitizer;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private HtmlSanitizer htmlSanitizer;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepository.findById(id);
    }
    //for dataBase usage 
    public Facility saveFacilityDB(Facility facility) {
        return facilityRepository.save(facility);
    }

    public Facility saveFacility(Facility facility) {
        facility= sanitize(facility);
        return facilityRepository.save(facility);
    }

    public void deleteFacility(Long id) {
        facilityRepository.deleteById(id);
    }

    public Facility updateFacility(Long id, Facility updatedFacility) {
        Facility oldFacility = facilityRepository.findById(id).orElseThrow();
        if (updatedFacility.getFacilityImage() == null) {
            updatedFacility.setFacilityImage(oldFacility.getFacilityImage());
        }
        if (updatedFacility.getReviews() == null) {
            updatedFacility.setReviews(oldFacility.getReviews());
        }
        if (updatedFacility.getName() == null) {
            updatedFacility.setName(oldFacility.getName());
        }
        if (updatedFacility.getDescription() == null) {
            updatedFacility.setDescription(oldFacility.getDescription());
        }
        if (updatedFacility.getType() == null) {
            updatedFacility.setType(oldFacility.getType());
        }
        updatedFacility.setId(id);
        updatedFacility=sanitize(updatedFacility);
        facilityRepository.save(updatedFacility);
        return updatedFacility;

    }

    public Optional<Facility> getFacilityByName(String name) {
        return facilityRepository.findByName(name);
    }

    // REST: GET FACILITIES
    public Page<Facility> getFacilities(Pageable pageable) {
        return facilityRepository.findAll(pageable);
    }

    public void removeImageFacility(Long facId, Image image) {
        Optional<Facility> facilityOpt = facilityRepository.findById(facId);
        Facility facility = facilityOpt.get();
        facility.setFacilityImage(null);
    }

    private Facility sanitize(Facility f) {
        if (f.getName() != null) {
            String cleanName = htmlSanitizer.clean(f.getName());
            if (!cleanName.equals(f.getName())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            f.setName(cleanName);
        }
        if (f.getDescription() != null) {
            String cleanDescription = htmlSanitizer.clean(f.getDescription());
            if (!cleanDescription.equals(f.getDescription())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            f.setDescription(cleanDescription);
        }
        if(f.getType()!= null){
            String cleanType = htmlSanitizer.clean(f.getType());
            if (!cleanType.equals(f.getType())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            f.setType(cleanType);
        }
        return f;
    }

}
