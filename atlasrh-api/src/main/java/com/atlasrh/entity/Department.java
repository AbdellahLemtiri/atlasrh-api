package com.atlasrh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.*;

@Entity 
@Table (name="departments")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 

public class Department {
    
    @Id 
    @GeneratedValue 
    private Long id ;

    @Column(nullable=false,unique=true,length=100)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany 
    @Builder.Default
    private List<Employee> employees =  new ArrayList<>();



}
