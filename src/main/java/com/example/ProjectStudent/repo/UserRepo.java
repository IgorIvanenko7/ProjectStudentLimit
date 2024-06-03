package com.example.ProjectStudent.repo;


import com.example.ProjectStudent.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserRepo extends CrudRepository<UserEntity, Long> {

      @Query("SELECT u FROM UserEntity u WHERE u.id = :idUser")
      UserEntity findUserId(Long idUser);

      @Query(value = "select * from users "
                   + "where username ilike :nameUser ", nativeQuery = true)
      List<UserEntity> getUsers(String nameUser);

      @Query(value = "delete from users "
                   + "where username = :nameUser "
                   + "returning id, username", nativeQuery = true)
      UserEntity deleteUser(String nameUser);

      @Query(value = "delete from users "
                   + "where id = :idUser "
                   + "returning id, username", nativeQuery = true)
      UserEntity deleteUserId(Long idUser);
}
