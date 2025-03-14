package com.gray.auth.infra.repo.jpa.repo;

import com.gray.auth.infra.repo.jpa.repo.models.JPAAccount;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface IJPAAccountRepo extends JpaRepository<JPAAccount, UUID> {




    @Transactional
    default JPAAccount saveOrUpdate(JPAAccount jpaAccount){
        return save(jpaAccount);
    }

    boolean existsByEmail(String email) ;

    Optional<JPAAccount> findByEmail(String email);

    @NonNull
    Page<JPAAccount> findAll(@NonNull Pageable pageable);

    void deleteById(UUID id);

}
