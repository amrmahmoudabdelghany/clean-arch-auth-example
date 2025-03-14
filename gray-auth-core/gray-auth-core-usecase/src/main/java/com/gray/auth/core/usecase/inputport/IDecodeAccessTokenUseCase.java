package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.Optional;
import java.util.function.Function;

import static com.gray.auth.core.usecase.inputport.IDecodeAccessTokenUseCase.*;

public interface IDecodeAccessTokenUseCase extends Function<AccessTokenDecodeRequest, DecodeAccessTokenResponse> {

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class AccessTokenDecodeRequest {
        private String accessToken;
    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class DecodeAccessTokenResponse {
        private String id;
        private String email;
        private String userName;
        private String phoneNumber;
        private String role;

        static DecodeAccessTokenResponse from(Account account) {
            DecodeAccessTokenResponse response = new DecodeAccessTokenResponse(
                    account.getId(),
                    account.getEmail(),
                    account.getFullName(),
                    account.getPhone(),
                    account.getRole()
            );
            return response;
        }
    }

    static IDecodeAccessTokenUseCase newInstance(final IAccountRepo accountRepo, final ITokenProcessor tokenProcessor) {
        return new DefaultDecodeAccessTokenUseCase(accountRepo, tokenProcessor);
    }

    @RequiredArgsConstructor
    final class DefaultDecodeAccessTokenUseCase implements IDecodeAccessTokenUseCase {

        private final IAccountRepo accountRepo;
        private final ITokenProcessor tokenProcessor;


        @Override
        public DecodeAccessTokenResponse apply(AccessTokenDecodeRequest accountInfoRequest) {

            SignedToken signedToken = tokenProcessor.transform(accountInfoRequest.accessToken);
            String email = signedToken.getEmailFromClaim();

            Optional<IAccountRepo.DBAccount> optionalDbAccount = accountRepo.findByEmail(email);

            IAccountRepo.DBAccount dbAccount = optionalDbAccount.orElseThrow(
                    () -> new IllegalInputException("Account with email " + email + " not found.")
            );

            Account account = dbAccount.toAccount();
            return DecodeAccessTokenResponse.from(account);
        }

    }
}
