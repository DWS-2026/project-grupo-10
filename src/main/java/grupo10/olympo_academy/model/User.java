package grupo10.olympo_academy.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name= "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String encodedPassword;
    @ElementCollection(fetch = FetchType.EAGER) // we use EAGER because we know we have to load few roles per user, and we need them for authentication
    private List<String> roles;
    private String username;

    @OneToOne
    private Image profileImage;

    @OneToMany(cascade = CascadeType.ALL)
	private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
	private List<Review> reviews = new ArrayList<>();

    public User() {}

    public User(String name, String email, String phone, String password, String username, String... roles) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.encodedPassword = password;
        this.username = username;
        this.roles = List.of(roles);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return encodedPassword;
    }

    public void setPassword(String password) {
        this.encodedPassword = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

    