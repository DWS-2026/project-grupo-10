package grupo10.olympo_academy.controller;

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    /////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Las credenciales son incorrectas, inténtalo de nuevo");
        }
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

    @PostMapping("/updateProfile")
    public String updateProfile(
            @RequestParam String name,
            //@RequestParam String email,
            @RequestParam String username,
            @RequestParam String phone,
            Model model,
            Principal principal) {

        // Spring Security provides user email through Principal object, we can use it
        // to fetch the user details from the database
        String currentUserEmail = principal.getName();
        
        try {
            userService.updateProfile(currentUserEmail, name, username, phone);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "userProfile";
        }

        return "redirect:/userProfile";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        String currentUserEmail = principal.getName();

        try {
            userService.updatePassword(currentUserEmail, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
            return "redirect:/userProfile";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "userProfile";
        }

    }

    @PostMapping("/updateProfileImage")
    public String updateProfileImage(
            @RequestParam("photoFile") MultipartFile photoFile,
            Model model,
            Principal principal) {

        String currentUserEmail = principal.getName();

        try {
            userService.updateProfileImage(currentUserEmail, photoFile);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "userProfile";
        }

        return "redirect:/userProfile";
    }

    /////////////////////////////////////////////////////////////////// REGISTER
    /////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("classes", classesService.getAllClasses());
        return "admin";
    }

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
        boolean hasActiveReservations = reservationService.hasActiveReservations(facility);

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

}
