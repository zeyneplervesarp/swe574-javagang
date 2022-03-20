package com.swe573.socialhub.domain;

import javax.persistence.*;

@Entity
public class Rating {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service")
    private Service service;

    @Column
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "rater")
    private User rater;

    public Rating() {

    }

    public Rating(Long id, Service service, Integer rating, User rater) {
        this.id = id;
        this.service = service;
        this.rating = rating;
        this.rater = rater;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }
}
