package edu.psu.elancer.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Services.
 */
@Entity
@Table(name = "services")
@Document(indexName = "services")
public class Services implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Min(value = 10000)
    @Max(value = 99999)
    @Column(name = "location_zip", nullable = false)
    private Integer locationZip;

    @Column(name = "date_posted")
    private LocalDate datePosted;

    @Column(name = "reported_count")
    private Integer reportedCount;

    @Column(name = "photo_path")
    private String photoPath;

    @NotNull
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "completed")
    private Boolean completed;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Customer customer;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Categories categories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Services name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Services description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLocationZip() {
        return locationZip;
    }

    public Services locationZip(Integer locationZip) {
        this.locationZip = locationZip;
        return this;
    }

    public void setLocationZip(Integer locationZip) {
        this.locationZip = locationZip;
    }

    public LocalDate getDatePosted() {
        return datePosted;
    }

    public Services datePosted(LocalDate datePosted) {
        this.datePosted = datePosted;
        return this;
    }

    public void setDatePosted(LocalDate datePosted) {
        this.datePosted = datePosted;
    }

    public Integer getReportedCount() {
        return reportedCount;
    }

    public Services reportedCount(Integer reportedCount) {
        this.reportedCount = reportedCount;
        return this;
    }

    public void setReportedCount(Integer reportedCount) {
        this.reportedCount = reportedCount;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public Services photoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Services expirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public Services completed(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Services customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Categories getCategories() {
        return categories;
    }

    public Services categories(Categories categories) {
        this.categories = categories;
        return this;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Services services = (Services) o;
        if(services.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, services.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Services{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", locationZip='" + locationZip + "'" +
            ", datePosted='" + datePosted + "'" +
            ", reportedCount='" + reportedCount + "'" +
            ", photoPath='" + photoPath + "'" +
            ", expirationDate='" + expirationDate + "'" +
            ", completed='" + completed + "'" +
            '}';
    }
}
