package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gray.auth.core.usecase.outputport.repo.IAccountRepo.*;

public interface ISignUpUseCase extends Function<ISignUpUseCase.SignUpRequest , ISignUpUseCase.SignUpResponse> {

    AccountRole DEFAULT_ACCOUNT_ROLE = AccountRole.USER;

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SignUpRequest {
        private String ticket ;
        private String password ;
        private String firstName ;
        private String lastName ;
        private String phone ;


    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class SignUpResponse {
        private String refreshToken ;
        private String accessToken ;


    }



    static ISignUpUseCase newInstance(ITokenProcessor tokenProcessor , IAccountRepo repo , IPasswordEncoder passwordEncoder,IAuthPersistenceGateway gateway) {
        return new DefaultSignUpUesCase(tokenProcessor , repo , passwordEncoder , gateway) ;
    }

    @AllArgsConstructor
    class DefaultSignUpUesCase implements ISignUpUseCase {

        private final ITokenProcessor tokenProcessor ;
        private final IAccountRepo accountRepo ;
        private final IPasswordEncoder passwordEncoder ;
        private final IAuthPersistenceGateway gateway;

        @Override
        public SignUpResponse apply(SignUpRequest request) {

            SignedToken token  = null ;
            Ticket ticket = null  ;



            token = this.tokenProcessor.transform(request.ticket) ;


            ticket = Ticket.of(token) ;

            if(!ticket.isValid() || !ticket.isEmailVerified() ) {
                throw new IllegalInputException("Signup refused.") ;
            }

            Email email = ticket.getEmail() ;

            if (this.accountRepo.existsByEmail(email.value())) {
                throw new IllegalInputException("The email '" + email.value() + "' already exists.");
            }


            PlainPassword password = PlainPassword.of(request.password) ;
            EncodedPassword encodedPassword = this.passwordEncoder.encode(password) ;

            UserName userName = UserName.of(request.firstName , request.lastName) ;

            PhoneNumber phoneNumber = PhoneNumber.of(request.phone) ;


            Set<Application> allAppsByActorRole = gateway.findAllAppsByActorRole(DEFAULT_ACCOUNT_ROLE)
                    .stream().map(DBApplication::toApplication).collect(Collectors.toSet());


            Account newAccount =  Account.create()
                    .email(email)
                    .password(encodedPassword)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .role(DEFAULT_ACCOUNT_ROLE)
                    .withApplication(allAppsByActorRole)
                    .execute();




            Token refreshToken = newAccount.generateRefreshToken() ;
            SignedToken signedRefreshToken =this.tokenProcessor.transform(refreshToken) ;

            newAccount.activate(signedRefreshToken);

            Token accessToken = newAccount.refresh(signedRefreshToken) ;
            SignedToken signedAccessToken = this.tokenProcessor.transform(accessToken) ;

            DBAccount dbAccount = DBAccount.from(newAccount) ;

            this.accountRepo.save(dbAccount) ;

            return new SignUpResponse(String.valueOf(signedRefreshToken) , String.valueOf(signedAccessToken));
        }
    }
}
