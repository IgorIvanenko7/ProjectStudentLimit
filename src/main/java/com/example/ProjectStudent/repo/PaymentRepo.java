package com.example.ProjectStudent.repo;


import com.example.ProjectStudent.entity.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepo extends CrudRepository<PaymentEntity, Long> {
}


