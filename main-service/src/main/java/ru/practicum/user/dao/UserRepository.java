package ru.practicum.user.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(Set<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsById(Long userId);
}
