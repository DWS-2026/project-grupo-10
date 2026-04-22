package grupo10.olympo_academy.controllers.web;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.UserService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @Autowired
    private ReviewService reviewService;

    /////////////////////////////////////////////////////////////////// LOGIN
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "blocked", required = false) String blocked, Model model) {

        if (error != null) {
            model.addAttribute("error", "Las credenciales son incorrectas, inténtalo de nuevo");
        }
        if (blocked != null) {
            model.addAttribute("blocked",
                    "Has sido bloqueado por un administrador, contacta con el soporte para más información");
        }
        return "login";
    }

    /////////////////////////////////////////////////////////////////////// USER
    /////////////////////////////////////////////////////////////////////// PROFILE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/userProfile")
    public String getProfile(Model model, Principal principal) {

        String email = principal.getName();

        try {
            User user = userService.getUserProfile(email);

            List<Reservation> reservations = reservationService.getReservationsByUser(user);
            reservations.sort((a, b) -> {
                LocalDate dateA = parseReservationDate(a.getDay());
                LocalDate dateB = parseReservationDate(b.getDay());

                if (dateA != null && dateB != null) {
                    int cmp = dateA.compareTo(dateB);
                    if (cmp != 0)
                        return cmp;
                } else if (dateA != null) {
                    return -1;
                } else if (dateB != null) {
                    return 1;
                }

                String dayA = a.getDay() == null ? "" : a.getDay();
                String dayB = b.getDay() == null ? "" : b.getDay();
                int cmp = dayA.compareToIgnoreCase(dayB);
                if (cmp != 0)
                    return cmp;

                String timeA = a.getStartTime() == null ? "" : a.getStartTime();
                String timeB = b.getStartTime() == null ? "" : b.getStartTime();
                return timeA.compareToIgnoreCase(timeB);
            });

            model.addAttribute("user", user);
            model.addAttribute("reservations", reservations);

            List<Review> reviews = reviewService.getReviewsByUser(user);
            model.addAttribute("reviews", reviews);

            return "userProfile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    private LocalDate parseReservationDate(String day) {
        if (day == null || day.isBlank())
            return null;
        try {
            return LocalDate.parse(day, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignored) {
            return null;
        }
    }

    @PostMapping("/updateProfile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String phone,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String currentUserEmail = principal.getName();

        try {
            userService.updateProfile(currentUserEmail, name, username, phone);
        } catch (Exception e) {
            //this line gets the error message from UserService and adds it to the model, so we can show it in the view
            model.addAttribute("error", e.getMessage());
            return "userProfile";
        }
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
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
            //this line gets the error message from UserService and adds it to the model, so we can show it in the view
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(User user,
            @RequestParam String password2,
            Model model,
            RedirectAttributes redirectAttrs) {

        if (!user.getPassword().equals(password2)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        try {
            userService.register(user);
            redirectAttrs.addFlashAttribute("success", "Registrado correctamente, ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    ////////////////////////////////////////////////////////////////// ADMIN
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("classes", classesService.getAllClasses());
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin";
    }

    //////////////////////////////////////// Facility
    //////////////////////////////////////////////////////////////////////////////////////////////

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

            // Image
            if (!photoFile.isEmpty()) {
                Image image = imageService.createImage(photoFile.getInputStream());
                facility.setFacilityImage(image);
            }

            // Save
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
            // We look for the facility in the database
            Facility facility = facilityService.getFacilityById(id);
            if (facility == null) {
                model.addAttribute("error", "La instalación no existe");
                return "admin";
            }

            // We update the fields
            facility.setName(name);
            facility.setType(tipo);
            facility.setDescription(description);

            // If a new image is uploaded, we replace the old one
            if (!photoFile.isEmpty()) {
                // Delete the old image if it exists
                if (facility.getFacilityImage() != null) {
                    imageService.deleteImage(facility.getFacilityImage().getId());
                }
                Image image = imageService.createImage(photoFile.getInputStream());
                facility.setFacilityImage(image);
            }

            // Save changes
            facilityService.saveFacility(facility);

            return "redirect:/admin";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin";
        }
    }

    @GetMapping("/admin/facility/delete/{id}")
    public String deleteFacility(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        Facility facility = facilityService.getFacilityById(id);

        if (facility == null) {
            redirectAttributes.addFlashAttribute("error", "La instalación no existe");
            return "redirect:/admin";
        }

        // Before deleting the facility, we check if it has active reservations. If it
        // does, we prevent deletion and show an error message.
        boolean hasActiveReservations = reservationService.hasActiveReservations(facility);

        if (hasActiveReservations) {
            redirectAttributes.addFlashAttribute("error","No se puede eliminar: tiene reservas activas.");
            return "redirect:/admin";
        }

        // Check if the facility has associated classes
        boolean hasAssociatedClasses = classesService.hasClassesUsingFacility(facility);

        if (hasAssociatedClasses) {
            redirectAttributes.addFlashAttribute("error","No se puede eliminar: tiene clases asociadas.");
            return "redirect:/admin";
        }

        // Delete associated image
        if (facility.getFacilityImage() != null) {
            Long imageId = facility.getFacilityImage().getId();
            // Set the reference to null and save to avoid transient reference issues
            facility.setFacilityImage(null);
            facilityService.saveFacility(facility);
            imageService.deleteImage(imageId);
        }

        // Delete facility
        facilityService.deleteFacility(id);

        return "redirect:/admin";
    }

    @PostMapping("/admin/user/update/{id}")
    public String updateUserFromAdmin(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) MultipartFile photoFile,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateUserFromAdmin(id, name, username, email, phone, photoFile);
            redirectAttributes.addFlashAttribute("successAdmin", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorAdmin", e.getMessage());
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/user/block/{id}")
    public String blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/unblock/{id}")
    public String unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String deleteUserFromAdmin(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successAdmin", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorAdmin", e.getMessage());
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserProfileAsAdmin(@PathVariable Long id, Model model) {

        User user = userService.getById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        List<Reservation> reservations = reservationService.getReservationsByUser(user);
        reservations.sort((a, b) -> {
            LocalDate dateA = parseReservationDate(a.getDay());
            LocalDate dateB = parseReservationDate(b.getDay());

            if (dateA != null && dateB != null) {
                int cmp = dateA.compareTo(dateB);
                if (cmp != 0)
                    return cmp;
            } else if (dateA != null) {
                return -1;
            } else if (dateB != null) {
                return 1;
            }

            String dayA = a.getDay() == null ? "" : a.getDay();
            String dayB = b.getDay() == null ? "" : b.getDay();
            int cmp = dayA.compareToIgnoreCase(dayB);
            if (cmp != 0)
                return cmp;

            String timeA = a.getStartTime() == null ? "" : a.getStartTime();
            String timeB = b.getStartTime() == null ? "" : b.getStartTime();
            return timeA.compareToIgnoreCase(timeB);
        });

        model.addAttribute("user", user);
        model.addAttribute("reservations", reservations);

        List<Review> reviews = reviewService.getReviewsByUser(user);
        model.addAttribute("reviews", reviews);

        // To indicate in the view that we are in admin mode, so we can show/hide
        // certain options
        model.addAttribute("adminView", true);
        return "userProfile";
    }
                            /////////////////////Reservations///////////////////////////
    @PostMapping("/admin/reservations/update/{id}")
    public String updateReservationAsAdmin(
            @PathVariable Long id,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam int duration,
            @RequestParam(required = false) Boolean material,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationService.getById(id);

        if (reservation == null) {
            redirectAttributes.addFlashAttribute("errorAdmin", "La reserva no existe.");
            return "redirect:/admin";
        }

        reservation.setDay(day);
        reservation.setStartTime(startTime);
        reservation.setDuration(duration);
        reservation.setMaterial(material != null && material);

        reservationService.save(reservation);

        redirectAttributes.addFlashAttribute("successAdmin", "Reserva actualizada correctamente.");

        return "redirect:/admin/user/" + reservation.getUser().getId();
    }

    @GetMapping("/admin/reservations/delete/{id}")
    public String deleteReservationAsAdmin(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationService.getById(id);

        if (reservation == null) {
            redirectAttributes.addFlashAttribute("errorAdmin", "La reserva no existe.");
            return "redirect:/admin";
        }

        Long userId = reservation.getUser().getId();

        reservationService.delete(reservation);

        redirectAttributes.addFlashAttribute("successAdmin", "Reserva eliminada correctamente.");

        return "redirect:/admin/user/" + userId;
    }

    ///////////////////////////// Classes///////////////////////////

    @PostMapping("/admin/classes/save")
    public String processClasses(
            Classes classes,
            @RequestParam Long facility,
            @RequestParam String durationRAW,
            @RequestParam("photoFile") MultipartFile photoFile,
            Model model) {

        try {

            int durationMinutes = convertDurationToMinutes(durationRAW);
            classes.setDuration(durationMinutes);

            // Set the facility
            Facility selectedFacility = facilityService.getFacilityById(facility);
            classes.setFacility(selectedFacility);

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
            Classes classesModify,
            @RequestParam String durationRAW,
            @RequestParam(required = false) Long facility,
            @RequestParam MultipartFile photoFile,
            Model model) {

        try {
            // Validate ID
            if (id == null || id <= 0) {
                model.addAttribute("error", "ID de clase no válido");
                return "admin";
            }

            // Fetch existing class
            Classes classes = classesService.getClassById(id);
            if (classes == null) {
                model.addAttribute("error", "La clase no existe");
                return "admin";
            }

            // Update fields
            classes.setName(classesModify.getName());
            classes.setDescription(classesModify.getDescription());
            classes.setTrainer(classesModify.getTrainer());
            classes.setDifficulty(classesModify.getDifficulty());
            classes.setDays(classesModify.getDays());
            classes.setStartTime(classesModify.getStartTime());

            int durationMinutes = convertDurationToMinutes(durationRAW);
            classes.setDuration(durationMinutes);

            // Update facility if provided
            if (facility != null && facility > 0) {
                Facility selectedFacility = facilityService.getFacilityById(facility);
                if (selectedFacility != null) {
                    classes.setFacility(selectedFacility);
                }
            }

            // Update image if a new one is uploaded
            if (photoFile != null && !photoFile.isEmpty()) {
                // Delete the old image if it exists
                if (classes.getClassesImage() != null) {
                    imageService.deleteImage(classes.getClassesImage().getId());
                }
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
    public String deleteClasses(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        Classes classes = classesService.getClassById(id);

        if (classes == null) {
            redirectAttributes.addFlashAttribute("error", "La clase no existe");
            return "redirect:/admin";
        }

        boolean hasActiveReservations = reservationService.hasActiveReservationsForClasses(classes);

        if (hasActiveReservations) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: tiene reservas activas.");
            return "redirect:/admin";
        }

        // Delete associated image
        if (classes.getClassesImage() != null) {
            Long imageId = classes.getClassesImage().getId();
            // Set the reference to null and save to avoid transient reference issues
            classes.setClassesImage(null);
            classesService.saveClass(classes);
            imageService.deleteImage(imageId);
        }

        // Delete class
        classesService.deleteClass(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteReviewAsAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        Review review = reviewService.getById(id);

        if (review == null) {
            redirectAttributes.addFlashAttribute("errorAdmin", "La reseña no existe.");
            return "redirect:/admin";
        }

        reviewService.deleteReview(id);

        redirectAttributes.addFlashAttribute("successAdmin", "Reseña eliminada correctamente.");

        return "redirect:/admin";
    }
}
