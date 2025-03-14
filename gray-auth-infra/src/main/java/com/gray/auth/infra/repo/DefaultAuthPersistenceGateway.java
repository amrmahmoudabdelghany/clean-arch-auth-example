package com.gray.auth.infra.repo;

import com.gary.auth.core.domain.AccountRole;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.infra.repo.jpa.repo.IJPAAPIPrincipleRepo;
import com.gray.auth.infra.repo.jpa.repo.IJPAApplicationRepo;
import com.gray.auth.infra.repo.jpa.repo.models.JPAAPIPrinciple;
import com.gray.auth.infra.repo.jpa.repo.models.JPAApplication;
import lombok.AllArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultAuthPersistenceGateway implements IAuthPersistenceGateway {

    private IJPAApplicationRepo jpaApplicationRepo;
    private IJPAAPIPrincipleRepo jpaAPIPrincipleRepo;

    @Override
    public boolean isAppExistsByName(String name) {
        return jpaApplicationRepo.existsByName(name);
    }

    @Override
    public void persist(DBApplication dbApplication) {
        JPAApplication jpaApplication = new JPAApplication(dbApplication);
        this.jpaApplicationRepo.saveOrUpdate(jpaApplication);
    }

    @Override
    public List<DBApplication> findAllApps() {
        List<JPAApplication> allApplicationsJPA = this.jpaApplicationRepo.findAll();
        return allApplicationsJPA.stream().map(DefaultAuthPersistenceGateway::map).toList();
    }

    @Override
    public Set<DBApplication> findAllAppsById(Set<UUID> ids) {
        return this.jpaApplicationRepo.findAllById(ids)
                .stream().map(DefaultAuthPersistenceGateway::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<DBApplication> findAllAppsByActorRole(AccountRole actorRole) {
        return this.jpaApplicationRepo.findByActorRole(actorRole)
                .stream().map(DefaultAuthPersistenceGateway::map).collect(Collectors.toSet());
    }

    @Override
    public boolean isAppExistsById(UUID id) {
        return jpaApplicationRepo.existsById(id);
    }

    private static DBApplication map(JPAApplication jpaApplication) {
        return DBApplication.builder()
                .id(jpaApplication.getId())
                .applicationName(jpaApplication.getName())
                .actorRoles(EnumSet.copyOf(jpaApplication.getActorRole()))
                .build();
    }

    @Override
    public boolean isPrincipleExistsByAppName(String name) {
        return jpaAPIPrincipleRepo.existsByUserName(name);
    }

    @Override
    public void persist(DBAPIPrinciple dbapiPrinciple) {
        JPAAPIPrinciple principle = new JPAAPIPrinciple(dbapiPrinciple);
        jpaAPIPrincipleRepo.saveOrUpdate(principle);
    }

    @Override
    public List<DBAPIPrinciple> findAllPrinciples() {
        return this.jpaAPIPrincipleRepo.findAll().stream()
                .map(DefaultAuthPersistenceGateway::map).toList();
    }

    @Override
    public DBAPIPrinciple findByUserName(String username) {
        return DefaultAuthPersistenceGateway
                .map(this.jpaAPIPrincipleRepo.findByUserName(username).orElseThrow(
                        () -> new IllegalStateException("username not found")
                ));
    }

    private static DBAPIPrinciple map(JPAAPIPrinciple jpaapiPrinciple) {
        return DBAPIPrinciple.builder()
                .id(jpaapiPrinciple.getId())
                .activationId(jpaapiPrinciple.getActivationId())
                .userName(jpaapiPrinciple.getUserName())
                .password(jpaapiPrinciple.getPassword())
                .description(jpaapiPrinciple.getDescription())
                .role(jpaapiPrinciple.getRole())
                .apps(jpaapiPrinciple.getAllowedApps().stream().map(JPAApplication::from).collect(Collectors.toSet()))
                .build();
    }
}
