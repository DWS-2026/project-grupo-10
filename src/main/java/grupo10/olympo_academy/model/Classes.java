package grupo10.olympo_academy.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String trainer;

    @ElementCollection
    private List<String> difficulty;

    @ElementCollection
    private List<String> days;

    @ElementCollection
    private List<String> startTime;

    private int duration;

    @OneToOne 
    private Image classesImage;

    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne
    private Facility facility;

    public Classes() {
    }

    public Classes(String name, String description, String trainer, Image classesImage) {
        this.name = name;
        this.description = description;
        this.trainer = trainer;
        this.classesImage = classesImage;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public List<String> getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(List<String> difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> day) {
        this.days = day;
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

    public Image getClassesImage() {
        return classesImage;
    }

    public void setClassesImage(Image classesImage) {
        this.classesImage = classesImage;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Long getFacilityId() {
        return facility != null ? facility.getId() : null;
    }

    public String getFacilityIdString() {
        return facility != null && facility.getId() != null ? facility.getId().toString() : "";
    }

  public List<Review> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setClasses(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setClasses(null);
    }

    public void saveReview(Review review) {
        this.reviews.add(review);
        review.setClasses(this);
    }

    public void deleteReview(Long id) {
        this.reviews.removeIf(review -> review.getId().equals(id));
    }
}
