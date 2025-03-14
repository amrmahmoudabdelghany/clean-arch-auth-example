package com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple;

import com.gary.auth.core.domain.APIPrinciple;
import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Application;
import com.gary.auth.core.domain.PlainPassword;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBAPIPrinciple;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ISubmitAPIPrincipleUseCase extends Function<ISubmitAPIPrincipleUseCase.SubmitAPIPrincipleRequest, ISubmitAPIPrincipleUseCase.SubmitAPIPrincipleResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SubmitAPIPrincipleRequest {
        private String userName;
        private String password;
        private Set<String> scopeIds;
        private String description;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SubmitAPIPrincipleResponse {
        private String message;
    }

    static ISubmitAPIPrincipleUseCase newInstance(IAuthPersistenceGateway gateway, ITokenProcessor tokenProcessor, IPasswordEncoder passwordEncoder) {
        return new DefaultSubmitAPIPrincipleUseCase(gateway, tokenProcessor, passwordEncoder);
    }

    @AllArgsConstructor
    class DefaultSubmitAPIPrincipleUseCase implements ISubmitAPIPrincipleUseCase {

        private final IAuthPersistenceGateway gateway;
        private final ITokenProcessor tokenProcessor;
        private final IPasswordEncoder passwordEncoder;


        @Override
        public SubmitAPIPrincipleResponse apply(SubmitAPIPrincipleRequest request) {

            try {
                Set<UUID> ids = request.getScopeIds().stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());

                Set<Application> applicationSet = gateway.findAllAppsById(ids).stream()
                        .map(DBApplication::toApplication)
                        .collect(Collectors.toSet());

                boolean containsAll = ids
                        .equals(applicationSet.stream().map(Application::getId).collect(Collectors.toSet()));
                if (!containsAll) {
                    throw new IllegalInputException("Not all ids found");
                }

                Set<Application> allAppsByActorRole = gateway.findAllAppsByActorRole(AccountRole.ADMIN).stream()
                        .map(DBApplication::toApplication).collect(Collectors.toSet());

                boolean containsAll1 = allAppsByActorRole.containsAll(applicationSet);

                if(!containsAll1){
                    throw new IllegalInputException("UnSupported Application");
                }

                PlainPassword plainPassword = PlainPassword.of(request.password);

                APIPrinciple principle = APIPrinciple.create()
                        .withServiceName(request.userName)
                        .withEncodedPassword(passwordEncoder.encode(plainPassword))
                        .withApplications(applicationSet)
                        .withDescription(request.getDescription())
                        .perform();

                if (gateway.isPrincipleExistsByAppName(principle.getUserName())) {
                    throw new IllegalInputException("this application name is already exits");
                }

                Token refreshToken = principle.generateRefreshToken();
                SignedToken signedRefreshToken = tokenProcessor.transform(refreshToken);

                principle.activate(signedRefreshToken);

                DBAPIPrinciple dbApiPrinciple = DBAPIPrinciple.from(principle);
                gateway.persist(dbApiPrinciple);

                return new SubmitAPIPrincipleResponse("API principle successfully created.");
            } catch (IllegalInputException e) {
                return new SubmitAPIPrincipleResponse("Failed to create API principle: Invalid input.");
            } catch (Exception e) {
                System.out.println(e);
                return new SubmitAPIPrincipleResponse("Failed to create API principle: Internal error.");
            } finally {
            }
        }

    }

}
