package com.example.ProjectStudent.repo;


import com.example.ProjectStudent.entity.LimitEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface LimitRepo extends CrudRepository<LimitEntity, Long> {

      /* Создание нового, уменьшение текущего лимита, реализовано в одном запросе с помощью табличных обобщений (CTE)
       * Если в сущности(таблице) Лимитов нет соответствующего пользователя(клиента) -> запрос создаст новый дефолтный
       * базовый лимит (10 000.00).
       * Иначе возьмет текущий лимит по заданному пользователю(клиенту) и понизит на сумму платежа
       */
      @Query(value = "WITH new_values(idUser, sumPay, dateinstall) "
                   + "   AS (values (:idUser, :sumPay, CAST(:revisionPay AS timestamp))), "
                   + "upsert AS (UPDATE limits l "
                   + "              SET sumDaylimit = (l.sumDaylimit - nv.sumPay) "
                   + "              FROM new_values nv "
                   + "              WHERE l.idUser = nv.idUser "
                   + "              RETURNING l.id, l.idUser, l.sumDaylimit, l.dateinstall, l.sumBaselimit), "
                   + "insertt as (INSERT INTO limits(idUser, sumDaylimit, dateinstall, sumBaselimit) "
                   + "              SELECT idUser, 10000.00, dateinstall, 10000.00"
                   + "              FROM new_values "
                   + "              WHERE NOT EXISTS (SELECT 1 FROM upsert) "
                   + "              RETURNING *) "
                   + "SELECT * FROM upsert"
                   + " UNION ALL "
                   + "SELECT * FROM insertt", nativeQuery = true)
      LimitEntity runLimit(Long idUser, BigDecimal sumPay, Instant revisionPay);


      @Query(value = "SELECT * FROM limits l "
                   + "WHERE l.idUser = :idUser", nativeQuery = true)
      LimitEntity findLimitForUser(Long idUser);

      //Обновление базового лимита (по запросу)
      @Query(value = "UPDATE limits "
                   + "SET dateinstall = CAST(:dateInstallLimit AS timestamp), "
                   + "    sumBaselimit = :newBaseLimit "
                   + "WHERE idUser = :idUser "
                   + "RETURNING *", nativeQuery = true)
      LimitEntity updateBaseLimit(Long idUser, BigDecimal newBaseLimit, Instant dateInstallLimit);

      //Ежедневное обновление дневного лимита базовым лимитом по шедуллеру
      @Query(value = "UPDATE limits "
                   + "SET sumDaylimit = sumBaselimit "
                   + "RETURNING *", nativeQuery = true)
      List<LimitEntity> updateDayLimit();
}


