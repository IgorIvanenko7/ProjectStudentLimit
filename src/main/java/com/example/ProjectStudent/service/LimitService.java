package com.example.ProjectStudent.service;

import com.example.ProjectStudent.Utils.CollectionModelMapper;
import com.example.ProjectStudent.Utils.DateTimeUtils;
import com.example.ProjectStudent.dto.*;
import com.example.ProjectStudent.entity.PaymentEntity;
import com.example.ProjectStudent.handleExeption.HandlerExeptionLimit;
import com.example.ProjectStudent.repo.LimitRepo;
import com.example.ProjectStudent.repo.PaymentRepo;
import com.example.ProjectStudent.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LimitService {

    private final UserRepo userRepo;
    private final PaymentRepo paymentRepo;
    private final LimitRepo limitRepo;
    private final CollectionModelMapper collectionModelMapper;

    public RevisionResponseLimit<LimitDto> changeLimitUser(Long userId, BigDecimal sumNewBaseLimit) {

         Instant dateInstallLimit = DateTimeUtils.uniqueTimestampMicros();
         var limitEntityResp = Optional.ofNullable(limitRepo.findLimitForUser(userId))
                .map(limitEntity -> collectionModelMapper.map(limitEntity, LimitDto.class))
                .map(limitDto -> {
                    /* Вычисление разницы в месяцах Лимита пользователя
                     * с корректным учетом перехода между годами
                     * Если > 2 мес. Тогда обновление Лимта
                     */
                    ZonedDateTime  dateTimeCurrentLimit = limitDto.getDateInstall().atZone(ZoneId.systemDefault());
                    ZonedDateTime  dateTimeNow  = Instant.now().atZone(ZoneId.systemDefault());
                    int monthsDiff = Period.between(dateTimeCurrentLimit.toLocalDate(), dateTimeNow.toLocalDate()).getMonths();

                    if (monthsDiff < 2) {
                        throw new HandlerExeptionLimit(
                                "Изменение базового лимита отменено, пользователь ID:" + userId,
                                "; текущий базовый лимит изменен(создан) < 2 мес. назад");
                    }
                    return limitRepo.updateBaseLimit(userId, sumNewBaseLimit, dateInstallLimit);
                })
                .orElseThrow(() -> new HandlerExeptionLimit(
                        "Лимит пользователь c ID:" + userId," не найден"));

        return RevisionResponseLimit.of(dateInstallLimit,
                collectionModelMapper.map(limitEntityResp, LimitDto.class));
    }

    @Transactional
    public RevisionResponseLimit<PaymentResponseDto> runPayment(Long userId, BigDecimal sumPay) {

        if (userRepo.findUserId(userId) == null) {
            throw new HandlerExeptionLimit(
                    "Пользователь c ID:" + userId," не найден, платеж не выполнен");
        }
        Instant revisionPay = DateTimeUtils.uniqueTimestampMicros();
        PaymentDto paymentDto  = PaymentDto.createNewPayment(
                userId, sumPay, revisionPay);

        var paymentEntity = Optional.ofNullable(collectionModelMapper.map(paymentDto, PaymentEntity.class))
                .map(paymentEnt ->{
                    log.info("Convert PayDto to Entity: {}", paymentEnt);
                    return paymentEnt;})
                .orElseThrow(() -> new HandlerExeptionLimit(
                        "Ошибка Маппирования в PaymentEntity, ID пользователя:", userId.toString()));
        // Создание платежа
        var paySaveEntity = paymentRepo.save(paymentEntity);
        paymentDto = collectionModelMapper.map(paySaveEntity, PaymentDto.class);

        /* Проверка наличия лимита для пользователя, уменьшение лимита на сумму платежа
         * В случае отсутствия лимита -> создание лимита = 10000.00 "минус" сумма текущего платежа
         */
        var limitEntity = limitRepo.runLimit(userId, sumPay , revisionPay);
        limitEntity.setSumDaylimit(limitEntity.getSumDaylimit().subtract(sumPay));

        var limitDto = Optional.ofNullable(collectionModelMapper.map(limitEntity, LimitDto.class))
                .map(limitDtoo -> {
                    if (limitDtoo.getSumDaylimit().compareTo(BigDecimal.ZERO) < 0){
                        log.info("Выход за дневной лимит:{}", limitDtoo.getSumDaylimit());
                        throw new HandlerExeptionLimit(
                                "Проведение платежа отменено (откат транзакции включая платеж), пользователь ID:" + userId,
                                "; выход за лимит:" + limitDtoo.getSumDaylimit());
                    }
                    return limitDtoo;})
                .orElseThrow(() -> new HandlerExeptionLimit(
                        "Платеж не проведен, ID пользователя:", userId.toString()));

        return RevisionResponseLimit.of(revisionPay,
                PaymentResponseDto.createPaymentResponseDto(paymentDto, limitDto));
    }

    // Реализация через JPA Join
    public RevisionResponseLimit<List<PaymentDto>> getPaymensForUserId(Long idUser) {
        var paymentEntityList = userRepo.findUserId(idUser).getPaymentEntityList();
        List<PaymentDto> paymentDtoList = collectionModelMapper.mapAsList(
                paymentEntityList, PaymentDto.class);
        return RevisionResponseLimit.of(DateTimeUtils.uniqueTimestampMicros(), paymentDtoList);
    }

    public ResponseEntity<UserDto> deleteUser(Long idUser) {
        if (userRepo.findUserId(idUser) == null) {
            throw new HandlerExeptionLimit(
                    "Пользователь c ID:" + idUser," не найден");
        }
        var deleteUser = userRepo.deleteUserId(idUser);
        return Optional.of(collectionModelMapper.map(deleteUser, UserDto.class))
                .map(delU -> new ResponseEntity<>(delU, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    public RevisionResponseLimit<List<UserDto>> getAllUsers() {
       var userEntityList = userRepo.findAll();
       return RevisionResponseLimit.of(DateTimeUtils.uniqueTimestampMicros(),
               collectionModelMapper.mapAsList(userEntityList, UserDto.class));
    }

}
