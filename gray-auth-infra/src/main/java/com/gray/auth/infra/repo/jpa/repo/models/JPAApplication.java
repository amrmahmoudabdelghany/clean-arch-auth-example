package com.gray.auth.infra.repo.jpa.repo.models;

import com.gary.auth.core.domain.AccountRole;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "application")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class JPAApplication {

    @Id
    private UUID id;

    @Column(unique = true)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<AccountRole> actorRole;


    public JPAApplication(DBApplication dbApplication) {
        this.id = dbApplication.getId();
        this.name = dbApplication.getApplicationName();
        this.actorRole = dbApplication.getActorRoles();
    }
    public static DBApplication from(JPAApplication jpaApplication){
        return DBApplication.builder()
                .id(jpaApplication.id)
                .applicationName(jpaApplication.name)
                .actorRoles(EnumSet.copyOf(jpaApplication.actorRole))
                .build();
    }
}
