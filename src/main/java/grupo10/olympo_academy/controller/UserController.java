package grupo10.olympo_academy.controller;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ReservationService reservationService;

    /////////////////////////////////////////////////////////////////// LOGIN
    /////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////////

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    /////////////////////////////////////////////////////////////////////// USER
    /////////////////////////////////////////////////////////////////////// PROFILE
    /////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/userProfile")
    public String getProfile(Model model, Principal principal) {

        // Spring Security provides user email through Principal object, we can use it
        // to fetch the user details from the database
        String email = principal.getName();

        User user = userService.findByEmail(email);

        model.addAttribute("user", user);

        return "userProfile";
    }

    /////////////////////////////////////////////////////////////////// REGISTER
    /////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////////
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(User user,
            @RequestParam String password2,
            Model model) {

        if (!user.getPassword().equals(password2)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        try {
            userService.register(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    ////////////////////////////////////////////////////////////////// ADMIN
    ////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("classes", classesService.getAllClasses());
        return "admin";
    }
    //////////////////////////////////////// Facility
    //////////////////////////////////////// //////////////////////////////////////////////////////

    @PostMapping("/admin/facility/save")
    public String processFacility(
            @RequestParam String name,
            @RequestParam("type") String tipo,
            @RequestParam String description,
            @RequestParam("photoFile") MultipartFile photoFile,
            Model model) {

        try {
            Facility facility = new Facility();
            facility.setName(name);
            facility.setDescription(description);
            facility.setType(tipo);
            facility.setMaterial(false);

            // Imagen
            if (!photoFile.isEmpty()) {
                Image image = imageService.createImage(photoFile.getInputStream());
                facility.setFacilityImage(image);
            }

            // Guardar
            facilityService.saveFacility(facility);

            return "redirect:/admin";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin";
        }
    }

    @PostMapping("/admin/facility/update")
    public String updateFacility(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam("type") String tipo,
            @RequestParam String description,
            @RequestParam("photoFile") MultipartFile photoFile,
            Model model) {

        try {
            // Buscamos si la instalación existe
            Facility facility = facilityService.getFacilityById(id);
            if (facility == null) {
                model.addAttribute("error", "La instalación no existe");
                return "admin";
            }

            // Actualizamos los campos
            facility.setName(name);
            facility.setType(tipo);
            facility.setDescription(description);

            // Si sube nueva imagen reemplazamos la antigua
            if (!photoFile.isEmpty()) {
                Image image = imageService.createImage(photoFile.getInputStream());
                facility.setFacilityImage(image);
            }

            // Guardar cambios
            facilityService.saveFacility(facility);

            return "redirect:/admin";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin";
        }
    }

    @GetMapping("/admin/facility/delete/{id}")
    public String deleteFacility(@PathVariable Long id, Model model) {

        Facility facility = facilityService.getFacilityById(id);

        if (facility == null) {
            model.addAttribute("error", "La instalación no existe");
            return "redirect:/admin";
        }

        // Comprobar si tiene reservas activas
        boolean hasActiveReservations = reservationService.hasActiveReservationsForFacility(facility);

        if (hasActiveReservations) {
            model.addAttribute("error", "No se puede eliminar: tiene reservas activas.");
            return "redirect:/admin";
        }

        // Eliminar imagen asociada (opcional pero recomendable)
        if (facility.getFacilityImage() != null) {
            imageService.deleteImage(facility.getFacilityImage().getId());
        }

        // Eliminar instalación
        facilityService.deleteFacility(id);

        return "redirect:/admin";
    }
    ///////////////////////////////// Classes//////////////////////////////////////////////////////

    
@PostMapping("/admin/classes/save")
public String processClasses(
        Classes classes,
        //@RequestParam String name,
        //@RequestParam String description,
        //@RequestParam String trainer,
        //@RequestParam List<String> difficulty,
        //@RequestParam List<String> day,
        //@RequestParam List<String> startTime,
        @RequestParam String durationRAW,
        @RequestParam("photoFile") MultipartFile photoFile,
        Model model) {

    try {
        /*Classes classes = new Classes();
        classes.setName(name);
        classes.setDescription(description);
        classes.setTrainer(trainer);
        classes.setStartTime(startTime);
        classes.setDifficulty(difficulty);
        classes.setDay(day);*/


        int durationMinutes = convertDurationToMinutes(durationRAW);
        classes.setDuration(durationMinutes);

        if (!photoFile.isEmpty()) {
            Image image = imageService.createImage(photoFile.getInputStream());
           classes.setClassesImage(image);
        }

        classesService.saveClass(classes);

        return "redirect:/admin";

    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "admin";
    }
}


    @PostMapping("/admin/classes/update")
    public String updateClasses(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String trainer,
            @RequestParam List<String> difficulty,
            @RequestParam List<String> day,
            @RequestParam List<String> startTime,
            @RequestParam String duration,
            @RequestParam("photoFile") MultipartFile photoFile,
            Model model) {

        try {
            // Buscamos si la instalación existe
            Classes classes = classesService.getClassById(id);
            if (classes == null) {
                model.addAttribute("error", "La clase no existe");
                return "admin";
            }

            // Actualizamos los campos
            classes.setName(name);
            classes.setDescription(description);
            classes.setTrainer(trainer);
            classes.setDifficulty(difficulty);
            classes.setDay(day);
            classes.setStartTime(startTime);

            int durationMinutes = convertDurationToMinutes(duration);
            classes.setDuration(durationMinutes);

            // Si sube nueva imagen reemplazamos la antigua
            if (!photoFile.isEmpty()) {
                Image image = imageService.createImage(photoFile.getInputStream());
                classes.setClassesImage(image);
            }

            // Guardar cambios
            classesService.saveClass(classes);

            return "redirect:/admin";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin";
        }

    }

    private int convertDurationToMinutes(String duration) {
        switch (duration) {
            case "1h":
                return 60;
            case "1:30h":
                return 90;
            case "2h":
                return 120;
            default:
                return 60;
        }
    }

    @GetMapping("/admin/classes/delete/{id}")
    public String deleteClasses(@PathVariable Long id, Model model) {

        Classes classes = classesService.getClassById(id);

        if (classes == null) {
            model.addAttribute("error", "La clase no existe");
            return "redirect:/admin";
        }

        boolean hasActiveReservations = reservationService.hasActiveReservationsForClasses(classes);

        if (hasActiveReservations) {
            model.addAttribute("error", "No se puede eliminar: tiene reservas activas.");
            return "redirect:/admin";
        }

        // Eliminar imagen asociada (opcional pero recomendable)
        if (classes.getClassesImage() != null) {
            imageService.deleteImage(classes.getClassesImage().getId());
        }

        // Eliminar instalación
        classesService.deleteClass(id);

        return "redirect:/admin";
    }

}
