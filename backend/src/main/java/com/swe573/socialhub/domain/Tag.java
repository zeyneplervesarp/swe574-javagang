//package com.swe573.socialhub.model;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "tag")
//public class Tag {
//
//    public Tag() {
//
//    }
//
//    public Tag(String name) {
//
//        this.name = name;
//    }
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    @Column(name = "name")
//    private String name;
//
//
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//}





package com.swe573.socialhub.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Objects;
import java.util.Set;

@Entity
public class Tag {

    private @Id @GeneratedValue Long id;
    private String name;
    @ManyToMany(mappedBy = "userTags")
    private Set<User> users;
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    public Tag() {}

    public Tag(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Tag))
            return false;
        Tag tag = (Tag) o;
        return Objects.equals(this.id, tag.id) && Objects.equals(this.name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Tag{" + "id=" + this.id + ", name='" + this.name + '\'' +'}';
    }
}