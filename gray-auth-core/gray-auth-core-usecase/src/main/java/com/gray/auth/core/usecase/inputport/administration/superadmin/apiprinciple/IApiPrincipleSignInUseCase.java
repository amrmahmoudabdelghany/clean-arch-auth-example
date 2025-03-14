package com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.function.Function;

public interface IApiPrincipleSignInUseCase extends Function<IApiPrincipleSignInUseCase.ApiSignInRequest, IApiPrincipleSignInUseCase.ApiSignInResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class ApiSignInRequest {
        private String email;
        private String password;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class ApiSignInResponse {
        private String refreshToken;
        private String accessToken;
    }


    static IApiPrincipleSignInUseCase newInstance(final IAuthPersistenceGateway gateway,
                                                  final ITokenProcessor tokenProcessor,
                                                  final IPasswordEncoder passwordEncoder) {
        return new DefaultApiPrincipleSignInUseCase(gateway, tokenProcessor, passwordEncoder);
    }

    @RequiredArgsConstructor
    final class DefaultApiPrincipleSignInUseCase implements IApiPrincipleSignInUseCase {

        private final IAuthPersistenceGateway gateway;
        private final ITokenProcessor tokenProcessor;
        private final IPasswordEncoder passwordEncoder;

        @Override
        public ApiSignInResponse apply(ApiSignInRequest req) {

//            try {
                APIPrinciple principle = IAuthPersistenceGateway.DBAPIPrinciple
                        .toAPIPrinciple(this.gateway.findByUserName(req.email));

                PlainPassword requesterPassword = PlainPassword.of(req.password);
                EncodedPassword accountPassword = EncodedPassword.of(principle.getEncodedPassword().value());

                if (!this.passwordEncoder.match(requesterPassword, accountPassword))
                    throw new IllegalStateException();

                Token token = principle.generateRefreshToken();
                SignedToken signedRefreshToken = this.tokenProcessor.transform(token);
                principle.activate(signedRefreshToken);
                token = principle.refresh(signedRefreshToken);
                SignedToken signedAccessToken = this.tokenProcessor.transform(token);

                this.gateway.persist(IAuthPersistenceGateway.DBAPIPrinciple.from(principle));

                return new ApiSignInResponse(String.valueOf(signedRefreshToken), String.valueOf(signedAccessToken));

//            } catch (Throwable e) {
//                throw new IllegalInputException("Could not sign in to this account !!!", e);
//            }

        }

    }


}
