package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.dto.ClassesDTO;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.repository.ClassesRepository;
import grupo10.olympo_academy.repository.FacilityRepository;
import grupo10.olympo_academy.repository.ImageRepository;

import java.util.List;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ImageRepository imageRepository;

    
    // WEB + REST: GET ALL
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    // WEB + REST: GET BY ID
    public Classes getClassById(Long id) {
        return classesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    // WEB: SAVE
    public Classes saveClass(Classes classes) {
        return classesRepository.save(classes);
    }

    // WEB: DELETE
    public void deleteClass(Long id) {
        if (!classesRepository.existsById(id)) {
            throw new RuntimeException("Class not found");
        }
        classesRepository.deleteById(id);
    }

    // WEB: CHECK FACILITY USAGE
    public boolean hasClassesUsingFacility(Facility facility) {
        return !classesRepository.findByFacility(facility).isEmpty();
    }

    //REST: GET CLASSES
    public Page<Classes> getClasses(Pageable pageable) {
        return classesRepository.findAll(pageable);
    }
    
    // REST: CREATE CLASS FROM DTO
    public Classes createClass(ClassesDTO dto) {

        Classes classes = new Classes();

        classes.setName(dto.name());
        classes.setDescription(dto.description());
        classes.setTrainer(dto.trainer());
        classes.setDifficulty(dto.difficulty());
        classes.setDays(dto.days());
        classes.setStartTime(dto.startTime());
        classes.setDuration(dto.duration());

        // FACILITY
        if (dto.facilityId() != null) {
            Facility facility = facilityRepository.findById(dto.facilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            classes.setFacility(facility);
        }

        // IMAGE
        if (dto.imageId() != null) {
            Image image = imageRepository.findById(dto.imageId())
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            classes.setClassesImage(image);
        }

        return classesRepository.save(classes);
    }

    // REST: UPDATE CLASS FROM DTO
    public Classes updateClass(Long id, ClassesDTO dto) {

        Classes classes = getClassById(id);

        classes.setName(dto.name());
        classes.setDescription(dto.description());
        classes.setTrainer(dto.trainer());
        classes.setDifficulty(dto.difficulty());
        classes.setDays(dto.days());
        classes.setStartTime(dto.startTime());
        classes.setDuration(dto.duration());

        // FACILITY
        if (dto.facilityId() != null) {
            Facility facility = facilityRepository.findById(dto.facilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            classes.setFacility(facility);
        }

        // IMAGE
        if (dto.imageId() != null) {
            Image image = imageRepository.findById(dto.imageId())
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            classes.setClassesImage(image);
        }

        return classesRepository.save(classes);
    }
}

