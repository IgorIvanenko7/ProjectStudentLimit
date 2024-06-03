package com.example.ProjectStudent.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "limits")
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iduser", referencedColumnName="id")
    private UserEntity user;

    @Column(name = "sumdaylimit")
    private BigDecimal sumDaylimit;

    @Column(name = "dateinstall")
    private Instant dateInstall;

    @Column(name = "sumbaselimit")
    private BigDecimal sumBaselimit;
}


