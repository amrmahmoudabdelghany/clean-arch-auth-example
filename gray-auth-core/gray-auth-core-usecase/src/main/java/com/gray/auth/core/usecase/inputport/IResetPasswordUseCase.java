package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.EncodedPassword;
import com.gary.auth.core.domain.PlainPassword;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.function.Function;

public interface IResetPasswordUseCase extends Function<IResetPasswordUseCase.ResetPasswordRequest , IResetPasswordUseCase.ResetPasswordResponse> {


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class ResetPasswordRequest {
       private String ticket ;
       private String password ;
    }


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class ResetPasswordResponse {
        private String message ;
    }


    static IResetPasswordUseCase newInstance(IAccountRepo accountRepo , ITokenProcessor tokenProcessor , IPasswordEncoder passwordEncoder) {
        return new DefaultResetPasswordUseCase(accountRepo , tokenProcessor , passwordEncoder ) ;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
   final class DefaultResetPasswordUseCase implements  IResetPasswordUseCase {

        private final IAccountRepo accountRepo ;
        private final ITokenProcessor tokenProcessor ;
        private final IPasswordEncoder passwordEncoder ;


        @Override
        public ResetPasswordResponse apply(ResetPasswordRequest request) {

            SignedToken ticketToken  = this.tokenProcessor.transform(request.ticket);
            Ticket ticket = Ticket.of(ticketToken) ;

            if(!ticket.isValid() || !ticket.isEmailVerified()) {
                 throw new IllegalInputException("Reset password refused.");
            }

            Email email =  ticket.getEmail() ;
            EncodedPassword password = this.passwordEncoder.encode(PlainPassword.of(request.password)) ;

            Account acc = this.accountRepo.findByEmail(email.value())
                    .map(IAccountRepo.DBAccount::toAccount)
                    .orElseThrow(()->new IllegalInputException("Illegal use of ticket")) ;


           acc =  Account.edit(acc)
                   .password(password)
                   .execute();

           this.accountRepo.save(IAccountRepo.DBAccount.from(acc)) ;

            return new ResetPasswordResponse("Password has been rest successfully");
        }

    }


}
