package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями пользователей в базе данных.
 * Предоставляет методы для выполнения операций с данными о пользователях.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Находит пользователя по email адресу.
     *
     * @param email email адрес для поиска
     * @return Optional с найденным пользователем или пустой, если не найден
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email адресом.
     *
     * @param email email адрес для проверки
     * @return true если пользователь с таким email существует, false в противном случае
     */
    boolean existsByEmail(String email);
}