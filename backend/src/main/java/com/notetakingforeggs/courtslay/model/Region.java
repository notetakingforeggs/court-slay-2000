package com.notetakingforeggs.courtslay.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Region {

    @Id
    private long id;

    private String regionName;
    @JsonManagedReference
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private Set<Court> courts;


}
