package com.gray.auth.infra.repo.jpa.repo.models;


import com.gray.auth.core.usecase.outputport.repo.IAccountRepo.DBAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "account")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class JPAAccount {
    @Id
    private UUID id;

    @Column(unique = true)
    private UUID activationId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String role;

    @ManyToMany
    @JoinTable(
            name = "account_allowed_apps",
            joinColumns = @JoinColumn(name = "jpaaccount_id"),
            inverseJoinColumns = @JoinColumn(name = "allowed_apps_id")
    )
    private Set<JPAApplication> allowedApps;

    public JPAAccount(DBAccount account) {
        this.id = account.getId();
        this.activationId = account.getActivationId();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.phone = account.getPhoneNumber();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.role = account.getRole();
        this.allowedApps = account.getApplications()
                .stream()
                .map(JPAApplication::new)
                .collect(Collectors.toSet());
    }

}
