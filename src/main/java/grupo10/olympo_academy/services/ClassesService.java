package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.repository.ClassesRepository;


import java.util.List;
import java.util.Optional;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    // WEB + REST
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    // REST: Optional
    public Optional<Classes> getClassById(Long id) {
        return classesRepository.findById(id);
    }

    // CREATE / SAVE
    public Classes saveClass(Classes classes) {
        return classesRepository.save(classes);
    }

    // DELETE
    public void deleteClass(Long id) {
        classesRepository.deleteById(id);
    }

    public boolean hasClassesUsingFacility(Facility facility) {
    return !classesRepository.findByFacility(facility).isEmpty();
}


    // UPDATE
    public Classes updateClass(Long id, Classes updatedClass) {

        Classes oldClass = classesRepository.findById(id).orElseThrow();

       
        if (updatedClass.getClassesImage() == null) {
            updatedClass.setClassesImage(oldClass.getClassesImage());
        }

        
        if (updatedClass.getReviews() == null) {
            updatedClass.setReviews(oldClass.getReviews());
        }

       
        if (updatedClass.getFacility() == null) {
            updatedClass.setFacility(oldClass.getFacility());
        }

    
        if (updatedClass.getName() == null) {
            updatedClass.setName(oldClass.getName());
        }

       
        if (updatedClass.getDescription() == null) {
            updatedClass.setDescription(oldClass.getDescription());
        }

       
        if (updatedClass.getTrainer() == null) {
            updatedClass.setTrainer(oldClass.getTrainer());
        }

        
        if (updatedClass.getDifficulty() == null) {
            updatedClass.setDifficulty(oldClass.getDifficulty());
        }

       
        if (updatedClass.getDays() == null) {
            updatedClass.setDays(oldClass.getDays());
        }

        
        if (updatedClass.getStartTime() == null) {
            updatedClass.setStartTime(oldClass.getStartTime());
        }

       
        if (updatedClass.getDuration() == 0) {
            updatedClass.setDuration(oldClass.getDuration());
        }

        updatedClass.setId(id);
        return classesRepository.save(updatedClass);
    }

    public Page<Classes> getClasses(Pageable pageable) {
        return classesRepository.findAll(pageable);
    }
}

