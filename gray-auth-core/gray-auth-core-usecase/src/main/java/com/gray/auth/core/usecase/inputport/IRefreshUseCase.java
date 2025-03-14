package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.function.Function;

public interface IRefreshUseCase extends Function<IRefreshUseCase.RefreshRequest , IRefreshUseCase.RefreshResponse> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    class RefreshRequest {
        String refreshToken ;

    }
    @Value
    class RefreshResponse {
        String accessToken ;
    }


    static IRefreshUseCase newInstance(final IAccountRepo accountRepo , final  ITokenProcessor tokenProcessor) {
        return  new DefaultRefreshUseCase(accountRepo , tokenProcessor) ;
    }
    @RequiredArgsConstructor
    final class DefaultRefreshUseCase implements IRefreshUseCase {

        private final IAccountRepo accountRepo ;
        private final ITokenProcessor tokenProcessor ;

        @Override
        public RefreshResponse apply(RefreshRequest request) {

            SignedToken refreshToken = this.tokenProcessor.transform(request.refreshToken);
            Email email = Email.of(refreshToken.getEmailFromClaim());
            Account acc =  this.accountRepo.findByEmail(email.value()).map(IAccountRepo.DBAccount::toAccount)
                    .orElseThrow(()->new IllegalInputException("For some reasons this account is deleted or blocked")) ;

            try {

                Token accessToken =  acc.refresh(refreshToken) ;
                SignedToken signedACToken =   this.tokenProcessor.transform(accessToken) ;

                return new RefreshResponse(String.valueOf(signedACToken)) ;
            }catch (Exception e) {
                throw new IllegalInputException("Working with invalid refresh token .") ;
            }
        }
    }

}