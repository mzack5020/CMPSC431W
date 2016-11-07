package edu.psu.elancer.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Bids.
 */
@Entity
@Table(name = "bids")
@Document(indexName = "bids")
public class Bids implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private String amount;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Services services;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Contractor contractor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public Bids amount(String amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Services getServices() {
        return services;
    }

    public Bids services(Services services) {
        this.services = services;
        return this;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public Bids contractor(Contractor contractor) {
        this.contractor = contractor;
        return this;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bids bids = (Bids) o;
        if(bids.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, bids.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Bids{" +
            "id=" + id +
            ", amount='" + amount + "'" +
            '}';
    }
}
