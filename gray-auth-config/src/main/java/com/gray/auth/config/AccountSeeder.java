package com.gray.auth.config;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import com.gray.auth.infra.repo.jpa.repo.models.JPAApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountSeeder implements CommandLineRunner {

    private final IPasswordEncoder passwordEncoder ;
    private final IAccountRepo accountRepo ;
    private final ITokenProcessor tokenProcessor ;
    private final IAuthPersistenceGateway gateway;


    static void  seedAccounts(IAccountRepo accountRepo ,
                              IPasswordEncoder passwordEncoder ,
                              ITokenProcessor tokenProcessor , Account account
                                , IAuthPersistenceGateway gateway
    ){
        IAccountRepo.DBAccount dbAccount = IAccountRepo.DBAccount.from(account);
        accountRepo.save(dbAccount);

        Token token =  account.generateRefreshToken() ;

        SignedToken refreshToken =  tokenProcessor.transform(token) ;
        account.activate(refreshToken);
        token =  account.refresh(refreshToken) ;
        SignedToken accessToken = tokenProcessor.transform(token) ;
        System.out.println("Account [" + account.getEmail() + "] Token is : " + accessToken.toString()) ;
    }



    @Override
    public void run(String... args) throws Exception {

        JPAApplication defaultApplication = new JPAApplication();
        defaultApplication.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        defaultApplication.setName("defaultApplicationName");
        defaultApplication.setActorRole(EnumSet.of(AccountRole.ADMIN,AccountRole.USER));


        gateway.persist(JPAApplication.from(defaultApplication));


        Set<Application> allAppsByActorRole = gateway.findAllAppsByActorRole(AccountRole.ADMIN)
                .stream().map(IAuthPersistenceGateway.DBApplication::toApplication).collect(Collectors.toSet());


        Account account =  Account.create()
                .email(Email.of("email1@gmail.com"))
                .password(passwordEncoder.encode(PlainPassword.of("AmrAccount2000@!")))
                .userName(UserName.of("Amr", "account"))
                .phoneNumber(PhoneNumber.of("1234566"))
                .role(AccountRole.SUPER_ADMIN)
                .withApplication(allAppsByActorRole) //TODO
                .execute();

        seedAccounts(accountRepo , passwordEncoder , tokenProcessor , account,gateway);

        account = Account.create()
                .email(Email.of("email2@gmail.com"))
                .password(passwordEncoder.encode(PlainPassword.of("SaidAccount2000@!")))
                .userName(UserName.of("Said", "account"))
                .phoneNumber(PhoneNumber.of("1234566"))
                .role(AccountRole.ADMIN)
                .withApplication(allAppsByActorRole) //TODO
                .execute();

        seedAccounts(accountRepo , passwordEncoder , tokenProcessor , account,gateway);
        account = Account.create()
                .email(Email.of("email3@gmail.com"))
                .password(passwordEncoder.encode(PlainPassword.of("AliAccount2000@!")))
                .userName(UserName.of("Hesham", "account"))
                .phoneNumber(PhoneNumber.of("1234566"))
                .role(AccountRole.USER)
                .withApplication(allAppsByActorRole) //TODO
                .execute();

        seedAccounts(accountRepo , passwordEncoder , tokenProcessor , account,gateway);

    }
}
