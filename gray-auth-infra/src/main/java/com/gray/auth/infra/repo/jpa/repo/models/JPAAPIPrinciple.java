package com.gray.auth.infra.repo.jpa.repo.models;

import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBAPIPrinciple;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "api_principle")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class JPAAPIPrinciple {

    @Id
    private UUID id;

    @Column(unique = true)
    private UUID activationId;
    @Column(nullable = false, unique = true)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String role;

//    @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<JPAApplication> allowedApps;


    public JPAAPIPrinciple(DBAPIPrinciple dbapiPrinciple) {
        this.id = dbapiPrinciple.getId();
        this.activationId = dbapiPrinciple.getActivationId();
        this.userName = dbapiPrinciple.getUserName();
        this.password = dbapiPrinciple.getPassword();
        this.description = dbapiPrinciple.getDescription();
        this.role = dbapiPrinciple.getRole();
        this.allowedApps = dbapiPrinciple.getApps()
                .stream()
                .map(JPAApplication::new)
                .collect(Collectors.toSet());
    }
}
