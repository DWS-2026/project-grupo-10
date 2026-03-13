package grupo10.olympo_academy;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.repository.FacilityRepository;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.repository.ClassesRepository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Component
public class DataBaseUsage implements CommandLineRunner {

    private final FacilityRepository facilityRepository;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private ImageService imageService;

    DataBaseUsage(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(classesRepository.count() > 0) {
            System.out.println("La base de datos ya contiene datos. No se cargaron clases.");
            return;
        }
        if(facilityRepository.count() > 0) {
            System.out.println("La base de datos ya contiene datos. No se cargaron instalaciones.");
            return;
        }

        Facility facility1 = new Facility("Pista de Tenis", "Pista de tenis de superficie dura con iluminación");
        setFacilityImage(facility1, "static/assets/images/instalaciones/tenis.png");
        Facility facility2 = new Facility("Pista de Padel", "Pista de padel con superficie de concreto");
        setFacilityImage(facility2, "static/assets/images/instalaciones/padel.png");
        Facility facility3 = new Facility("Gimnasio",
                "Gimnasio completamente equipado con máquinas de última generación");
        setFacilityImage(facility3, "static/assets/images/instalaciones/gym_Olympo.png");
        Facility facility4 = new Facility("Piscina ", "Piscina semiolímpica con temperatura controlada");
        setFacilityImage(facility4, "static/assets/images/instalaciones/natacion.png");
        Facility facility5 = new Facility("Pista de Baloncesto", "Pista cubierta con piso de parquet");
        setFacilityImage(facility5, "static/assets/images/instalaciones/baloncesto.jpeg");
        Facility facility6 = new Facility("Campo de Fútbol", "Campo de fútbol 11 con césped sintético");
        setFacilityImage(facility6, "static/assets/images/instalaciones/futbol.jpeg");

        Classes class1 = new Classes("Clase de Tenis", "Clases de tenis para principiantes y avanzados", "Javichu");
        setClassesImage(class1, "static/assets/images/instalaciones/tenis.png");
        Classes class2 = new Classes("Clase de Padel", "Clases de padel para todos los niveles", "Javichu");
        setClassesImage(class2, "static/assets/images/instalaciones/padel.png");
        Classes class3 = new Classes("Clase de CrossFit", "Clases de CrossFit para mejorar tu condición física",
                "Dieguss");
        setClassesImage(class3, "static/assets/images/instalaciones/gym_Olympo.png");
        Classes class4 = new Classes("Clase de Piscina", "Clases de natación para todas las edades", "Luxx");
        setClassesImage(class4, "static/assets/images/instalaciones/natacion.png");
        Classes class5 = new Classes("Clase de Baloncesto", "Clases de baloncesto para niños y adultos", "Dieguss");
        setClassesImage(class5, "static/assets/images/instalaciones/baloncesto.jpeg");
        Classes class6 = new Classes("Clase de Fútbol", "Clases de fútbol para  que puedas mejorar tus habilidades",
                "Adriii");
        setClassesImage(class6, "static/assets/images/instalaciones/futbol.jpeg");
            // save a couple of facilities
            facilityService.saveFacility(facility1);
            facilityService.saveFacility(facility2);
            facilityService.saveFacility(facility3);
            facilityService.saveFacility(facility4);
            facilityService.saveFacility(facility5);
            facilityService.saveFacility(facility6);

            // save a couple of facilities
            classesService.saveClass(class1);
            classesService.saveClass(class2);
            classesService.saveClass(class3);
            classesService.saveClass(class4);
            classesService.saveClass(class5);
            classesService.saveClass(class6);

    }

    public void setFacilityImage(Facility facility, String classpathResource) throws IOException {
        Resource image = new ClassPathResource(classpathResource);

        Image createdImage = imageService.createImage(image.getInputStream());
        facility.setFacilityImage(createdImage);
    }

    public void setClassesImage(Classes classes, String classpathResource) throws IOException {
        Resource image = new ClassPathResource(classpathResource);

        Image createdImage = imageService.createImage(image.getInputStream());
        classes.setClassesImage(createdImage);
    }

}
