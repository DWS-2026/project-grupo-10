package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.repository.FacilityRepository;
import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepository.findById(id);
    }

    public Facility saveFacility(Facility facility) {
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
        facilityRepository.save(updatedFacility);
        return updatedFacility;

    }

    public Optional<Facility> getFacilityByName(String name) {
        return facilityRepository.findByName(name);
    }
}
