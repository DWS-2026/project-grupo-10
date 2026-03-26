package grupo10.olympo_academy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    private String name;
    @ManyToOne
    private User user;

    @ManyToOne
    private Facility facility;
    @ManyToOne
    private Classes classes;
    
    private String day;
    private String startTime;
    private int duration;
    private String status;
    private Boolean material;
 
    public Reservation() {}

     public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
     public Classes getClasses() {
        return classes;
    }
    
    public void setClasses(Classes classes) {
        this.classes = classes;
    }
     public Facility getFacility() {
        return facility;
    }
    
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
     public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }

       public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
     public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMaterial() {
        return material;
    }

    public void setMaterial(Boolean material) {
        this.material = material;
    }  
  
}
