package com.gray.auth.infra.repo.jpa.repo;

import com.gary.auth.core.domain.AccountRole;
import com.gray.auth.infra.repo.jpa.repo.models.JPAApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

public interface IJPAApplicationRepo extends JpaRepository<JPAApplication, UUID> {

    boolean existsByName(String name);
    
    @Transactional
    default JPAApplication saveOrUpdate(JPAApplication jpaApplication){
        return save(jpaApplication);
    }

    Set<JPAApplication> findByActorRole(AccountRole role);
    
}
