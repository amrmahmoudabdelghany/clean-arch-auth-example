package com.gray.auth.core.usecase.inputport.administration.admin;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
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

public interface IAdminCreateAccountUseCase extends Function<IAdminCreateAccountUseCase.CreateAccountRequest, IAdminCreateAccountUseCase.CreateAccountResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class CreateAccountRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private String role;
    }


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class CreateAccountResponse {
        private String message;
    }

    static IAdminCreateAccountUseCase newInstance(ITokenProcessor tokenProcessor, IAccountRepo repo, IPasswordEncoder passwordEncoder,IAuthPersistenceGateway gateway) {
        return new DefaultAdminCreateAccount(repo, passwordEncoder, tokenProcessor,gateway);
    }

    @AllArgsConstructor
    class DefaultAdminCreateAccount implements IAdminCreateAccountUseCase {

        private final IAccountRepo accountRepo;
        private final IPasswordEncoder passwordEncoder;
        private final ITokenProcessor tokenProcessor;
        private final IAuthPersistenceGateway gateway;


        @Override
        public CreateAccountResponse apply(CreateAccountRequest request) {

            Email email = Email.of(request.email);
            if (this.accountRepo.existsByEmail(email.value())) {
                throw new IllegalInputException("Email is already exist");
            }

            PlainPassword password = PlainPassword.of(request.password);
            EncodedPassword encodedPassword = this.passwordEncoder.encode(password);
            UserName userName = UserName.of(request.firstName, request.lastName);
            PhoneNumber phoneNumber = PhoneNumber.of(request.phone);
            AccountRole accountRole = AccountRole.of(request.role);


            Set<Application> allAppsByActorRole = gateway.findAllAppsByActorRole(accountRole)
                    .stream().map(IAuthPersistenceGateway.DBApplication::toApplication).collect(Collectors.toSet());


            Account newAccount = Account.create()
                    .email(email)
                    .password(encodedPassword)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .role(accountRole)
                    .withApplication(allAppsByActorRole)
                    .execute();

            if (newAccount.isAdmin() || newAccount.isSuperAdmin()) {
                throw new IllegalInputException("Admin can not create Admin or SuperAdmin accounts.");
            }


            Token refreshToken = newAccount.generateRefreshToken();
            SignedToken signedRefreshToken = this.tokenProcessor.transform(refreshToken);

            newAccount.activate(signedRefreshToken);

            IAccountRepo.DBAccount dbAccount = IAccountRepo.DBAccount.from(newAccount);
            this.accountRepo.save(dbAccount);

            return new IAdminCreateAccountUseCase.CreateAccountResponse("Admin Create Account Successfully");
        }
    }

}