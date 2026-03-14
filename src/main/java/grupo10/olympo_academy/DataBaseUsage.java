package grupo10.olympo_academy;

import grupo10.olympo_academy.repository.ImageRepository;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.FacilityRepository;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.repository.UserRepository;
import grupo10.olympo_academy.services.UserService;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.repository.ClassesRepository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Component
public class DataBaseUsage implements CommandLineRunner {
    
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    DataBaseUsage(FacilityRepository facilityRepository, ImageRepository imageRepository) {
        this.facilityRepository = facilityRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        boolean hasClasses = classesRepository.count() > 0;
        boolean hasFacilities = facilityRepository.count() > 0;
        boolean hasImages = imageRepository.count() > 0;

        if (hasFacilities) {
            System.out.println("La base de datos ya contiene instalaciones. No se cargaron instalaciones.");
        } else {
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

            // save facilities
            facilityService.saveFacility(facility1);
            facilityService.saveFacility(facility2);
            facilityService.saveFacility(facility3);
            facilityService.saveFacility(facility4);
            facilityService.saveFacility(facility5);
            facilityService.saveFacility(facility6);
        }

        if (hasClasses) {
            System.out.println("La base de datos ya contiene clases. No se cargaron clases.");
        } else {
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

            // save classes
            classesService.saveClass(class1);
            classesService.saveClass(class2);
            classesService.saveClass(class3);
            classesService.saveClass(class4);
            classesService.saveClass(class5);
            classesService.saveClass(class6);
        }

        if (hasImages) {
            System.out.println("La base de datos ya contiene imágenes. No se cargaron imágenes.");
        } else {
            System.out.println("Se cargaron imágenes asociadas a instalaciones y clases.");
        }

        User user1 = new User("user1", "user1@example.com", "600000001", "user1", "user1", "USER");
        User user2 = new User("user2", "user2@example.com", "600000002", "user2", "user2", "USER");
        User admin = new User("admin", "admin@example.com", "600000003", "potato", "admin", "USER", "ADMIN");

        ensureUserExists(user1);
        ensureUserExists(user2);
        ensureUserExists(admin);

        System.out.println("Usuarios en base de datos: " + userRepository.count());
    }

    private void ensureUserExists(User user) throws Exception {
        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();
        boolean usernameExists = userRepository.findByUsername(user.getUserName()).isPresent();

        if (emailExists || usernameExists) {
            System.out.println("Usuario ya existe (email/username): " + user.getEmail() + " / " + user.getUserName());
            return;
        }

        userService.register(user);
        System.out.println("Usuario creado: " + user.getEmail());
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
