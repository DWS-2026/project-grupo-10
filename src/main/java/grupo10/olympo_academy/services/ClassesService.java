package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.repository.ClassesRepository;
import grupo10.olympo_academy.security.HtmlSanitizer;

import java.util.List;
import java.util.Optional;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;
    
    @Autowired
    private HtmlSanitizer htmlSanitizer;

    // WEB + REST
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    // REST: Optional
    public Optional<Classes> getClassById(Long id) {
        return classesRepository.findById(id);
    }
    //for Database usage
    public Classes saveClassDB(Classes classes) {
        classes= sanitize(classes);
        return classesRepository.save(classes);
    }

    // CREATE / SAVE
    public Classes saveClass(Classes classes) {
        classes= sanitize(classes);
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
        updatedClass=sanitize(updatedClass);
        return classesRepository.save(updatedClass);
    }

    public Page<Classes> getClasses(Pageable pageable) {
        return classesRepository.findAll(pageable);
    }

     public void removeImageClass(Long classId, Image image){
        Optional <Classes> classesOpt= classesRepository.findById(classId);
        Classes classes = classesOpt.get();
        classes.setClassesImage(null);       
    }
    private Classes sanitize(Classes c) {
        if (c.getName() != null) {
            String cleanName = htmlSanitizer.clean(c.getName());
            if (!cleanName.equals(c.getName())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setName(cleanName);
        }
        if (c.getDescription() != null) {
            String cleanDescription = htmlSanitizer.clean(c.getDescription());
            if (!cleanDescription.equals(c.getDescription())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setDescription(cleanDescription);
        }
        if(c.getTrainer()!= null){
            String cleanTrainer = htmlSanitizer.clean(c.getTrainer());
            if (!cleanTrainer.equals(c.getTrainer())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setTrainer(cleanTrainer);
        }
        if(c.getDays()!= null){
            List<String> cleanDays = htmlSanitizer.cleanList(c.getDays());
            if (!cleanDays.equals(c.getDays())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setDays(cleanDays);
        }
        if(c.getDifficulty()!= null){
            List<String> cleanDifficulty = htmlSanitizer.cleanList(c.getDifficulty());
            if (!cleanDifficulty.equals(c.getDifficulty())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setDifficulty(cleanDifficulty);
        }
        if(c.getStartTime()!= null){
            List<String> cleanStartTime = htmlSanitizer.cleanList(c.getStartTime());
            if (!cleanStartTime.equals(c.getStartTime())) {
                throw new IllegalArgumentException("Entrada no valida");
            }
            c.setStartTime(cleanStartTime);
        }
        return c;
    }
   


}

