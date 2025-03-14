package com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple;

import com.gary.auth.core.domain.APIPrinciple;
import com.gary.auth.core.domain.Application;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface IRetrieveAllApiPrinciplesUseCase extends Supplier<IRetrieveAllApiPrinciplesUseCase.RetrieveAllApiPrinciplesResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RetrieveAllApiPrinciplesResponse {
        List<ApplicationDetails> payload;

        static RetrieveAllApiPrinciplesResponse response(List<ApplicationDetails> applicationDetails) {
            return new RetrieveAllApiPrinciplesResponse(applicationDetails);
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
        private Set<Application> applications;

        static ApplicationDetails from(APIPrinciple apiPrinciple) {
            return ApplicationDetails.builder()
                    .applicationId(apiPrinciple.getId().toString())
                    .applicationName(apiPrinciple.getUserName())
                    .applications(apiPrinciple.getApplications())
                    .build();
        }
    }


    static IRetrieveAllApiPrinciplesUseCase newInstance(IAuthPersistenceGateway gateway) {
        return new DefaultRetrieveAllApplicationsUseCase(gateway);
    }

    @AllArgsConstructor
    class DefaultRetrieveAllApplicationsUseCase implements IRetrieveAllApiPrinciplesUseCase {

        private final IAuthPersistenceGateway gateway;

        @Override
        public RetrieveAllApiPrinciplesResponse get() {

            return RetrieveAllApiPrinciplesResponse
                    .response(
                            gateway.findAllPrinciples()
                                    .stream()
                                    .map(IAuthPersistenceGateway.DBAPIPrinciple::toAPIPrinciple)
                                    .map(ApplicationDetails::from)
                                    .toList());
        }
    }
}
