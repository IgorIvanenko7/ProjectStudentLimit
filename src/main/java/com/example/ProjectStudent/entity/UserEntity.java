package com.example.ProjectStudent.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<PaymentEntity> paymentEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<LimitEntity> limitEntityList = new ArrayList<>();
}