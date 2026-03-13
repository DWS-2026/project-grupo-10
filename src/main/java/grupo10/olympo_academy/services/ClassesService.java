package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.repository.ClassesRepository;


import java.util.List;

@Service
public class ClassesService {
    @Autowired
    private ClassesRepository classesRepository;
    
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }
    
    public Classes getClassById(Long id) {
        return classesRepository.findById(id).orElse(null);
    }
    
    public Classes saveClass(Classes classes) {
        return classesRepository.save(classes);
    }
}
