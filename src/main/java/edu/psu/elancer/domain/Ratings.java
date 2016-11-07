package edu.psu.elancer.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Ratings.
 */
@Entity
@Table(name = "ratings")
@Document(indexName = "ratings")
public class Ratings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(name = "rating", nullable = false)
    private Double rating;

    @NotNull
    @Column(name = "comments", nullable = false)
    private String comments;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Customer customer;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Contractor contractor;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Services services;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public Ratings rating(Double rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public Ratings comments(String comments) {
        this.comments = comments;
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Ratings customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public Ratings contractor(Contractor contractor) {
        this.contractor = contractor;
        return this;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public Services getServices() {
        return services;
    }

    public Ratings services(Services services) {
        this.services = services;
        return this;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ratings ratings = (Ratings) o;
        if(ratings.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ratings.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ratings{" +
            "id=" + id +
            ", rating='" + rating + "'" +
            ", comments='" + comments + "'" +
            '}';
    }
}
