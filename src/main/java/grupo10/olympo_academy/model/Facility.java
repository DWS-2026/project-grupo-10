package grupo10.olympo_academy.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String type;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne 
    private Image facilityImage;

    public Facility() {
    }

    public Facility(String name, String type, String description, Image facilityImage) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.facilityImage = facilityImage;
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

    public Image getFacilityImage() {
        return facilityImage;
    }

    public void setFacilityImage(Image facilityImage) {
        this.facilityImage = facilityImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setFacility(this);
    }

    // Método helper para eliminar una reseña
    public void removeReview(Review review) {
        reviews.remove(review);
        review.setFacility(null);
    }

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void saveReview(Review review) {
        this.reviews.add(review);
        review.setFacility(this);
    }

    public void deleteReview(Long id) {
        this.reviews.removeIf(review -> review.getId().equals(id));
    }

}
