package com.gray.auth.config;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final IAccountRepo accountRepo;
    private final IPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
//        String superAdminEmail = "superadmin@example.com";
//
//        // Check if the super admin account already exists
//        if (accountRepo.findByEmail(superAdminEmail).isEmpty()) {
//            Set<Application> applications = new HashSet<>();
//            applications.add(Application.create("app1",EnumSet.of(AccountRole.ADMIN)));
//            // Create the super admin account
//            Account superAdmin = Account.create()
//                    .email(Email.of(superAdminEmail))
//                    .password(passwordEncoder.encode(PlainPassword.of("superSecurePassword@1!")))
//                    .userName(UserName.of("Super", "Admin"))
//                    .phoneNumber(PhoneNumber.of("+123456789"))
//                    .role(AccountRole.SUPER_ADMIN)
//                    .withApplication(applications)
//                    .execute();
//
//            // Generate a refresh token for the super admin
//            Token refreshToken = superAdmin.generateRefreshToken();
//            SignedToken signedToken = new SignedToken(refreshToken,"TokenString");
//            superAdmin.activate(signedToken);
//
//            // Save the account to the repository
//            accountRepo.save(IAccountRepo.DBAccount.from(superAdmin));
//
//            // Optionally, log or store the token securely
//            System.out.println("Super Admin Refresh Token: " + refreshToken);
//        } else {
//            System.out.println("Super Admin account already exists. Skipping creation.");
//        }
    }
}

