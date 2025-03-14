package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.RefreshToken;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.UUID;
import java.util.function.Function;

public interface IRevokeUseCase extends Function<IRevokeUseCase.RevokeRequest , IRevokeUseCase.RevokeResponse> {


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RevokeRequest {
       private  String refreshToken ;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RevokeResponse {
      private  String message ;
    }


    static IRevokeUseCase newInstance(final IAccountRepo accountRepo , ITokenProcessor tokenProcessor) {
        return  new DefaultRevokeUseCase(accountRepo , tokenProcessor) ;
    }

    @RequiredArgsConstructor
    final class DefaultRevokeUseCase implements IRevokeUseCase {

        private final IAccountRepo accountRepo ;
        private final ITokenProcessor tokenProcessor ;

        @Override
        public RevokeResponse apply(RevokeRequest request) {

            SignedToken signedRefreshToken = this.tokenProcessor.transform(request.refreshToken);
            RefreshToken refreshToken = RefreshToken.from(signedRefreshToken) ;

            UUID accountId = refreshToken.getAccountId() ;

            Account acc =  this.accountRepo.findById(accountId)
                    .map(IAccountRepo.DBAccount::toAccount)
                    .orElseThrow(()->new IllegalInputException("For some reasons this account is deleted or blocked")) ;

            try {

                acc.deactivate(signedRefreshToken);
                return new RevokeResponse("Your token has been revoked successfully. Please signIn to reactivate") ;
            }catch (Exception e) {
                throw new IllegalInputException("Working with invalid refresh token ." , e) ;
            }

        }
    }

}
