package com.example.table_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "records")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

}