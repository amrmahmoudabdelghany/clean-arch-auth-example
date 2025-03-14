package com.gray.auth.core.usecase.outputport;

import com.gary.auth.core.domain.APIPrinciple;
import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Application;
import com.gary.auth.core.domain.EncodedPassword;
import lombok.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface IAuthPersistenceGateway {

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    class DBApplication {
        @NonNull
        private UUID id;
        @NonNull
        private String applicationName;
        @NonNull
        private EnumSet<AccountRole> actorRoles;

        public static DBApplication from(Application application) {
            DBApplication dbApplication = new DBApplication();
            dbApplication.id = application.getId();
            dbApplication.applicationName = application.getApplicationName();
            dbApplication.actorRoles = application.getActorRoles();
            return dbApplication;
        }

        public static Application toApplication(DBApplication dbApplication) {
            return Application.load(dbApplication.getId(),
                    dbApplication.getApplicationName(),
                    dbApplication.getActorRoles());
        }

    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    class DBAPIPrinciple {
        private UUID id;
        private UUID activationId;
        private String userName;
        private String password;
        private String description;
        private String role;
        private Set<DBApplication> apps;

        public static DBAPIPrinciple from(APIPrinciple apiPrinciple) {
            DBAPIPrinciple principle = new DBAPIPrinciple();

            principle.id = apiPrinciple.getId();
            principle.userName = apiPrinciple.getUserName();
            principle.activationId = UUID.fromString(apiPrinciple.getActivationId());
            principle.description = apiPrinciple.getDescription();
            principle.password = apiPrinciple.getEncodedPassword().value();
            principle.role = apiPrinciple.getRole().toString();
            principle.apps = apiPrinciple.getAccessibleApps()
                    .stream().map(DBApplication::from).collect(Collectors.toSet());
            return principle;
        }

        public static APIPrinciple toAPIPrinciple(DBAPIPrinciple apiPrinciple) {
            System.out.println(apiPrinciple.toString());
            return APIPrinciple.load()
                    .withId(apiPrinciple.id)
                    .withActivationId(apiPrinciple.activationId)
                    .withServiceName(apiPrinciple.userName)
                    .withEncodedPassword(EncodedPassword.of(apiPrinciple.password))
                    .withApplications(apiPrinciple.apps.stream()
                            .map(DBApplication::toApplication).collect(Collectors.toSet()))
                    .withDescription(apiPrinciple.description)
                    .perform();
        }

    }


    boolean isAppExistsByName(String name);

    boolean isAppExistsById(UUID id);

    void persist(DBApplication dbApplication);

    List<DBApplication> findAllApps();

    Set<DBApplication> findAllAppsById(Set<UUID> ids);

    Set<DBApplication> findAllAppsByActorRole(AccountRole actorRole);


    boolean isPrincipleExistsByAppName(String name);

    void persist(DBAPIPrinciple dbapiPrinciple);

    List<DBAPIPrinciple> findAllPrinciples();

    DBAPIPrinciple findByUserName(String username);


}
