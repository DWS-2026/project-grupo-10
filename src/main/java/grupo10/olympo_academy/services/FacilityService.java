package grupo10.olympo_academy.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.repository.FacilityRepository;
import java.util.List;

@Service
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepository;
    
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }
    
    public Facility getFacilityById(Long id) {
        return facilityRepository.findById(id).orElse(null);
    }
    
    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }
}
