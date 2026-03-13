package grupo10.olympo_academy.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToOne;

@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String trainer;
    private List<String> difficulty;
    private List<String> day;   
    private List<String> startTime;
    private int duration;


    @OneToOne
	private Image classesImage;

    @OneToOne
    private Facility facility;

    public Classes() {}

    public Classes(String name, String description, String trainer) {
        this.name = name;
        this.description = description;
        this.trainer = trainer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDay() {
        return day;
    }
    public void setDay(List<String> day) {
        this.day = day;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getTrainer() {
        return trainer;
    }


    public List<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(List<String> difficulty) {
        this.difficulty = difficulty;
    }
    public Image getClassesImage() {
        return classesImage;
    }

    public void setClassesImage(Image classesImage) {
        this.classesImage = classesImage;
    }    

}
