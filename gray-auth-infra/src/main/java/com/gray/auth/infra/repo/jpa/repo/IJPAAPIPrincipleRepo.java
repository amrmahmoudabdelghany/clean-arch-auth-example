package com.gray.auth.infra.repo.jpa.repo;

import com.gray.auth.infra.repo.jpa.repo.models.JPAAPIPrinciple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface IJPAAPIPrincipleRepo extends JpaRepository<JPAAPIPrinciple, UUID> {

    @Transactional
    default JPAAPIPrinciple saveOrUpdate(JPAAPIPrinciple jpaapiPrinciple){
        return save(jpaapiPrinciple);
    }

    boolean existsByUserName(String name);

    Optional<JPAAPIPrinciple> findByUserName(String username);
}

