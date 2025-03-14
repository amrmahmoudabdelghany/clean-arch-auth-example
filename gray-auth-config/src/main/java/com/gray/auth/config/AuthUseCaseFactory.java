package com.gray.auth.config;


import com.gray.auth.core.usecase.inputport.*;
import com.gray.auth.core.usecase.inputport.administration.admin.IAdminCreateAccountUseCase;
import com.gray.auth.core.usecase.inputport.administration.admin.IAdminDeleteAccountUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.*;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IApiPrincipleSignInUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IRetrieveAllApiPrinciplesUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.ISubmitAPIPrincipleUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRegisterApplicationUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRetrieveAllApplicationsUseCase;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AuthUseCaseFactory {


//    @Bean
//    @RequestScope
//    public IRequestSignUpOTPUseCase requestSignUpOTPUseCase(IVCodeManager vcodeManager, IVCodeMailSender emailClient) {
//        return IRequestSignUpOTPUseCase.newInstance(vcodeManager, emailClient);
//    }
//
//    @Bean
//    @RequestScope
//    public IRequestResetPasswordOTPUseCase requestResetPasswordOTPUseCase(IVCodeManager vcodeManager, IVCodeMailSender emailClient, IAccountRepo accountRepo) {
//        return IRequestResetPasswordOTPUseCase.newInstance(vcodeManager, emailClient, accountRepo);
//    }

    @Bean
    @RequestScope
    public ISubmitEmailUseCase submitEmailUseCase(IVCodeManager vcodeManager, IVCodeMailSender emailClient) {
        return ISubmitEmailUseCase.newInstance(vcodeManager, emailClient);
    }

    @Bean
    @RequestScope
    public IVerifyEmailUseCase verifyEmailUseCase(IVCodeManager vcodeManager, ITokenProcessor tokenProcessor, IAccountRepo accountRepo) {
        return IVerifyEmailUseCase.newInstance(vcodeManager, tokenProcessor, accountRepo);
    }

    @Bean
    @RequestScope
    public ISignUpUseCase signUpUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo, IPasswordEncoder passwordEncoder, IAuthPersistenceGateway gateway) {
        return ISignUpUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder, gateway);
    }

    @Bean
    @RequestScope
    public IGetAllAccountsUseCase getAllAccountsUseCase(IAccountRepo accountRepo) {
        return IGetAllAccountsUseCase.newInstance(accountRepo);
    }


    @Bean
    @RequestScope
    public IAdminCreateAccountUseCase adminCreateAccountUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo, IPasswordEncoder passwordEncoder, IAuthPersistenceGateway gateway) {
        return IAdminCreateAccountUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder, gateway);
    }

    @Bean
    @RequestScope
    public ISuperAdminCreateAccountUseCase superAdminCreateAccountUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo, IPasswordEncoder passwordEncoder, IAuthPersistenceGateway gateway) {
        return ISuperAdminCreateAccountUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder, gateway);
    }

    @Bean
    @RequestScope
    public IAdminDeleteAccountUseCase adminDeleteAccountUseCase(IAccountRepo accountRepo) {
        return IAdminDeleteAccountUseCase.newInstance(accountRepo);
    }

    @Bean
    @RequestScope
    public ISuperAdminDeleteAccountUseCase superAdminDeleteAccountUseCase(IAccountRepo accountRepo) {
        return ISuperAdminDeleteAccountUseCase.newInstance(accountRepo);
    }

    @Bean
    @RequestScope
    public ISignInUseCase signInUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo, IPasswordEncoder passwordEncoder) {
        return ISignInUseCase.newInstance(accountRepo, tokenProcessor, passwordEncoder);
    }

    @Bean
    @RequestScope
    public IDecodeAccessTokenUseCase getAccountInfoUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo) {
        return IDecodeAccessTokenUseCase.newInstance(accountRepo, tokenProcessor);
    }

    @Bean
    @RequestScope
    public IRevokeUseCase revokeUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo) {
        return IRevokeUseCase.newInstance(accountRepo, tokenProcessor);
    }

    @Bean
    @RequestScope
    public IRefreshUseCase refreshUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo) {
        return IRefreshUseCase.newInstance(accountRepo, tokenProcessor);
    }

    @Bean
    @RequestScope
    public IResetPasswordUseCase resetPasswordUseCase(ITokenProcessor tokenProcessor, IAccountRepo accountRepo, IPasswordEncoder passwordEncoder) {
        return IResetPasswordUseCase.newInstance(accountRepo, tokenProcessor, passwordEncoder);
    }

    @Bean
    @RequestScope
    public IRegisterApplicationUseCase registerApplicationUseCase(IAuthPersistenceGateway applicationRepo) {
        return IRegisterApplicationUseCase.newInstance(applicationRepo);
    }

    @Bean
    @RequestScope
    public IRetrieveAllApplicationsUseCase retrieveAllApplicationsUseCase(IAuthPersistenceGateway applicationRepo) {
        return IRetrieveAllApplicationsUseCase.newInstance(applicationRepo);
    }

    @Bean
    @RequestScope
    public ISubmitAPIPrincipleUseCase submitAPIPrincipleUseCase(IAuthPersistenceGateway gateway, ITokenProcessor tokenProcessor, IPasswordEncoder passwordEncoder) {
        return ISubmitAPIPrincipleUseCase.newInstance(gateway, tokenProcessor, passwordEncoder);
    }

    @Bean
    @RequestScope
    public IRetrieveAllApiPrinciplesUseCase retrieveAllApiPrinciplesUseCase(IAuthPersistenceGateway gateway) {
        return IRetrieveAllApiPrinciplesUseCase.newInstance(gateway);
    }

    @Bean
    @RequestScope
    public IApiPrincipleSignInUseCase apiPrincipleSignInUseCase(IAuthPersistenceGateway gateway, ITokenProcessor tokenProcessor, IPasswordEncoder passwordEncoder) {
        return IApiPrincipleSignInUseCase.newInstance(gateway, tokenProcessor, passwordEncoder);
    }

}
