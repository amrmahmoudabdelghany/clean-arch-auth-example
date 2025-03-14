package com.gray.auth.core.usecase.inputport.administration.superadmin.application;

import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Application;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import lombok.*;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public interface IRetrieveAllApplicationsUseCase extends Supplier<IRetrieveAllApplicationsUseCase.RetrieveAllApplicationsResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RetrieveAllApplicationsResponse {
        List<ApplicationDetails> payload;

        static RetrieveAllApplicationsResponse response(List<ApplicationDetails> applicationDetails) {
            return new RetrieveAllApplicationsResponse(applicationDetails);
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    final class ApplicationDetails {
        private String applicationId;
        private String applicationName;
        private EnumSet<AccountRole> actorRoles;

        static ApplicationDetails from(Application application) {
            return ApplicationDetails.builder()
                    .applicationId(application.getId().toString())
                    .applicationName(application.getApplicationName())
                    .actorRoles(application.getActorRoles())
                    .build();
        }
    }


    static IRetrieveAllApplicationsUseCase newInstance(IAuthPersistenceGateway gateway) {
        return new DefaultRetrieveAllApplicationsUseCase(gateway);
    }

    @AllArgsConstructor
    class DefaultRetrieveAllApplicationsUseCase implements IRetrieveAllApplicationsUseCase {

        private final IAuthPersistenceGateway gateway;

        @Override
        public RetrieveAllApplicationsResponse get() {

            return RetrieveAllApplicationsResponse
                    .response(
                            gateway.findAllApps()
                                    .stream()
                                    .map(DBApplication::toApplication)
                                    .map(ApplicationDetails::from)
                                    .toList());
        }
    }
}
