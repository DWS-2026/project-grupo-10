package grupo10.olympo_academy.services;

import grupo10.olympo_academy.repository.ImageRepository;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.FacilityRepository;
import grupo10.olympo_academy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.repository.ClassesRepository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Service
public class DataBaseUsage {

    private final FacilityService facilityService;
    private final ClassesRepository classesRepository;
    private final FacilityRepository facilityRepository;
    private final ImageRepository imageRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public DataBaseUsage(
            FacilityService facilityService,
            ClassesRepository classesRepository,
            FacilityRepository facilityRepository,
            ImageRepository imageRepository,
            UserService userService,
            UserRepository userRepository,
            ImageService imageService
    ) {
        this.facilityService = facilityService;
        this.classesRepository = classesRepository;
        this.facilityRepository = facilityRepository;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }
   
    @PostConstruct
    public void init() {
        try {
            // Verify if data already exists to avoid duplicates on application restart
            boolean hasClasses = classesRepository.count() > 0;
            boolean hasFacilities = facilityRepository.count() > 0;
            boolean hasImages = imageRepository.count() > 0;

            // --- FACILITIES ---
            if (hasFacilities) {
                System.out.println("La base de datos ya contiene instalaciones. No se cargaron instalaciones.");
            } else {
                Facility facility1 = new Facility("Pista de Tenis", "Pista",
                        "Pista de tenis de superficie dura con iluminación LED y gradas para espectadores. Perfecta para entrenamiento y competiciones tanto diurnas como nocturnas.",
                        null);
                setFacilityImage(facility1, "static/assets/images/instalaciones/tenis.png");
                Facility facility2 = new Facility("Pista de Padel", "Pista",
                        "Pista de pádel con césped sintético de última generación, paredes de cristal templado e iluminación profesional. Disfruta del deporte de moda en las mejores condiciones.",
                        null);
                setFacilityImage(facility2, "static/assets/images/instalaciones/padel.png");
                Facility facility3 = new Facility("Gimnasio", "Pista",
                        "Amplio gimnasio equipado con máquinas de última generación, zona de cardio, peso libre y entrenamiento funcional. Cuenta con instructores profesionales y ambiente climatizado.",
                        null);
                setFacilityImage(facility3, "static/assets/images/instalaciones/gym_Olympo.png");
                Facility facility4 = new Facility("Piscina ", "Piscina",
                        "Piscina semiolímpica de 25 metros con temperatura controlada, sistema de depuración ecológica y vestuarios adaptados. Ideal para natación, aquagym y entrenamiento deportivo.",
                        null);
                setFacilityImage(facility4, "static/assets/images/instalaciones/natacion.png");
                Facility facility5 = new Facility("Pista de Baloncesto", "Pista",
                        "Pista cubierta con pavimento de parquet certificado, canastas regulables y marcador electrónico. Espacio versátil para partidos, entrenamientos y eventos deportivos.",
                        null);
                setFacilityImage(facility5, "static/assets/images/instalaciones/baloncesto.jpeg");
                Facility facility6 = new Facility("Campo de Fútbol", "Campo",
                        "Campo de fútbol 11 con césped sintético de última generación, sistema de drenaje avanzado y torretas de iluminación LED. Cumple con las medidas reglamentarias para competiciones oficiales.",
                        null);
                setFacilityImage(facility6, "static/assets/images/instalaciones/futbol.jpeg");

                // Save facilities
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
            Classes class1 = new Classes("Clase de Tenis",
                    "Aprende o perfecciona tu técnica de tenis con Javichu, entrenador titulado. Clases adaptadas a todos los niveles, desde iniciación hasta competición, con enfoque en técnica, táctica y condición física.",
                    "Javichu", null);
            class1.setDifficulty(List.of("Principiante"));
            class1.setDays(List.of("Lunes", "Miércoles"));
            class1.setStartTime(List.of("10:00", "18:00"));
            class1.setDuration(60);
            class1.setFacility(facilityService.getFacilityByName("Pista de Tenis"));
            setClassesImage(class1, "static/assets/images/clases/clase_tenis.jpg");

            Classes class2 = new Classes("Clase de Padel",
                    "Domina el pádel con Javichu, jugador profesional. Trabaja tu posicionamiento, bandejas, voleas y estrategia de juego. Clases dinámicas y adaptadas a tu nivel, ya sea principiante o avanzado.",
                    "Javichu", null);
            class2.setDifficulty(List.of("Intermedio"));
            class2.setDays(List.of("Martes", "Jueves"));
            class2.setStartTime(List.of("12:00", "19:00"));
            class2.setDuration(60);
            class2.setFacility(facilityService.getFacilityByName("Pista de Padel"));
            setClassesImage(class2, "static/assets/images/clases/clase_padel.jpg");

            Classes class3 = new Classes("Clase de CrossFit",
                    "Entrenamiento funcional de alta intensidad con Dieguss, coach certificado. Mejora tu resistencia, fuerza y condición física general mediante WODs variados y adaptados a tu nivel. ¡Supera tus límites!",
                    "Dieguss", null);
            class3.setDifficulty(List.of("Avanzado"));
            class3.setDays(List.of("Lunes", "Viernes"));
            class3.setStartTime(List.of("19:00", "20:00"));
            class3.setDuration(60);
            class3.setFacility(facilityService.getFacilityByName("Gimnasio"));
            setClassesImage(class3, "static/assets/images/clases/clase_crossfit.jpg");

            Classes class4 = new Classes("Clase de Natación",
                    "Aprende a nadar o perfecciona tu técnica con Luxx, especialista en enseñanza acuática. Clases para todas las edades y niveles, desde iniciación hasta entrenamiento de estilo, en piscina climatizada.",
                    "Luxx", null);
            class4.setDifficulty(List.of("Principiante"));
            class4.setDays(List.of("Miércoles"));
            class4.setStartTime(List.of("17:00", "18:30"));
            class4.setDuration(60);
            class4.setFacility(facilityService.getFacilityByName("Piscina "));
            setClassesImage(class4, "static/assets/images/clases/clase_natacion.jpg");

            Classes class5 = new Classes("Clase de Baloncesto",
                    "Desarrolla tus habilidades en la cancha con Dieguss, entrenador con experiencia en formación. Trabaja el bote, tiro, pases y fundamentos defensivos en un ambiente dinámico y divertido para todas las edades.",
                    "Dieguss", null);
            class5.setDifficulty(List.of("Intermedio"));
            class5.setDays(List.of("Viernes"));
            class5.setStartTime(List.of("18:00", "20:00"));
            class5.setDuration(90);
            class5.setFacility(facilityService.getFacilityByName("Pista de Baloncesto"));
            setClassesImage(class5, "static/assets/images/clases/clase_baloncesto.jpg");

            Classes class6 = new Classes("Clase de Fútbol",
                    "Mejora tu técnica, control de balón y visión de juego con Adriii, exjugador profesional. Entrenamientos específicos para desarrollar tus habilidades y llevar tu juego al siguiente nivel.",
                    "Adriii", null);
            class6.setDifficulty(List.of("Intermedio"));
            class6.setDays(List.of("Sábado"));
            class6.setStartTime(List.of("11:00", "13:00"));
            class6.setDuration(90);
            class6.setFacility(facilityService.getFacilityByName("Campo de Fútbol"));
            setClassesImage(class6, "static/assets/images/clases/clase_futbol.jpg");

                // Save classes
                classesRepository.saveAll(List.of(class1, class2, class3, class4, class5, class6));

            }

            // --- IMAGES ---
            if (hasImages) {
                System.out.println("La base de datos ya contiene imágenes. No se cargaron imágenes.");
            } else {
                System.out.println("Se cargaron imágenes asociadas a instalaciones y clases.");
            }
            
            // --- USERS ---
            ensureUserExists(new User("user1", "user1@example.com", "600000001", "user1", "user1", "USER"));
            ensureUserExists(new User("user2", "user2@example.com", "600000002", "user2", "user2", "USER"));
            ensureUserExists(new User("admin", "admin@example.com", "600000003", "potato", "admin", "USER", "ADMIN"));
            
            System.out.println("Usuarios en base de datos: " + userRepository.count());
        
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar recursos iniciales", e);
        }
    }

    private void ensureUserExists(User user) {
        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();
        boolean usernameExists = userRepository.findByUsername(user.getUsername()).isPresent();

        if (emailExists || usernameExists) {
            System.out.println("Usuario ya existe (email/username): " + user.getEmail() + " / " + user.getUsername());
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
