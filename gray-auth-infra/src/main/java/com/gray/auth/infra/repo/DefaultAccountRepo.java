package com.gray.auth.infra.repo;

import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import com.gray.auth.infra.repo.jpa.repo.IJPAAccountRepo;
import com.gray.auth.infra.repo.jpa.repo.models.JPAAccount;
import com.gray.auth.infra.repo.jpa.repo.models.JPAApplication;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultAccountRepo implements IAccountRepo {

    private IJPAAccountRepo jpaAccountRepo;

    @Override
    public boolean existsByEmail(String email) {
        return jpaAccountRepo.existsByEmail(email);
    }

    @Override
    public DBAccount save(DBAccount dbAccount) {
        JPAAccount jpaAccount = new JPAAccount(dbAccount);
        jpaAccount = this.jpaAccountRepo.saveOrUpdate(jpaAccount);
        return map(jpaAccount);
    }

    @Override
    public Optional<DBAccount> findById(UUID id) {
        return this.jpaAccountRepo.findById(id).map(this::map);
    }

    @Override
    public DBPage findPage(DBPageRequest dbPageRequest) {
        Pageable pageable = PageRequest.of(dbPageRequest.getPageNumber(), dbPageRequest.getPageSize());
        Page<JPAAccount> page = this.jpaAccountRepo.findAll(pageable);

        DBPage dbPage = new DBPage(
                page.getContent().stream().map(this::map).toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );

        return dbPage;
    }

    @Override
    public void deleteById(UUID accountId) {
        jpaAccountRepo.deleteById(accountId);
    }

    @Override
    public Optional<DBAccount> findByEmail(String email) {

        return this.jpaAccountRepo.findByEmail(email).map(this::map);
    }

    private DBAccount map(JPAAccount jpaAccount) {
        return DBAccount.builder()
                .id(jpaAccount.getId())
                .activationId(jpaAccount.getActivationId())
                .email(jpaAccount.getEmail())
                .password(jpaAccount.getPassword())
                .phoneNumber(jpaAccount.getPhone())
                .firstName(jpaAccount.getFirstName())
                .lastName(jpaAccount.getLastName())
                .role(jpaAccount.getRole())
                .applications(jpaAccount.getAllowedApps().stream()
                        .map(JPAApplication::from).collect(Collectors.toSet()))
                .build();
    }
}
