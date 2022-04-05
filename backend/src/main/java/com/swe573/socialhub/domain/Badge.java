package com.swe573.socialhub.domain;

import javax.persistence.*;

@Entity
public class Badge {
    private @Id
    @GeneratedValue
    Long id;
    @ManyToOne
    @JoinColumn(name = "owner")
    User owner;

}
