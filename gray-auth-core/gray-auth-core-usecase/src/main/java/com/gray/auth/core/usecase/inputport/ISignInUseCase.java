package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.function.Function;

public interface ISignInUseCase extends Function<ISignInUseCase.SignInRequest, ISignInUseCase.SignInResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SignInRequest {
        private String email;
        private String password;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SignInResponse {
        private String refreshToken;
        private String accessToken;
    }


    static ISignInUseCase newInstance(final IAccountRepo accountRepo,
                                      final ITokenProcessor tokenProcessor,
                                      final IPasswordEncoder passwordEncoder) {
        return new DefaultSignInUseCase(accountRepo, tokenProcessor, passwordEncoder);
    }

    @RequiredArgsConstructor
    final class DefaultSignInUseCase implements ISignInUseCase {

        private final IAccountRepo accountRepo;
        private final ITokenProcessor tokenProcessor;
        private final IPasswordEncoder passwordEncoder;

        @Override
        public SignInResponse apply(SignInRequest signInRequest) {

            try {
                Email email = Email.of(signInRequest.email);
                Account account = this.accountRepo.findByEmail(email.value())
                        .map(IAccountRepo.DBAccount::toAccount).orElseThrow();
                PlainPassword requesterPassword = PlainPassword.of(signInRequest.password);
                EncodedPassword accountPassword = EncodedPassword.of(account.getPassword());

                if (!this.passwordEncoder.match(requesterPassword, accountPassword))
                    throw new IllegalStateException();

                Token token = account.generateRefreshToken();
                SignedToken signedRefreshToken = this.tokenProcessor.transform(token);
                account.activate(signedRefreshToken);
                token = account.refresh(signedRefreshToken);
                SignedToken signedAccessToken = this.tokenProcessor.transform(token);

                this.accountRepo.save(IAccountRepo.DBAccount.from(account));

                return new SignInResponse(String.valueOf(signedRefreshToken), String.valueOf(signedAccessToken));

            } catch (Throwable e) {
                throw new IllegalInputException("Could not sign in to this account !!!", e);
            }

        }

    }


}
