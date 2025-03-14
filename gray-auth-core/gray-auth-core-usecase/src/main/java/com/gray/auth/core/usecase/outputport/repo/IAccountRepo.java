package com.gray.auth.core.usecase.outputport.repo;

import com.gary.auth.core.domain.*;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface IAccountRepo {


    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    class DBPageRequest {
        private int pageNumber;
        private int pageSize;
    }


    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    class DBPage {
        private List<DBAccount> dbAccounts;
        private int currentPage;
        private int totalPages;
        private long totalAccounts;
        private int pageSize;
    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    class DBAccount {

        private UUID id;
        private UUID activationId;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String role;
        private Set<DBApplication> applications;


        public static DBAccount from(Account account) {
            DBAccount dbAccount = new DBAccount();
            dbAccount.id = UUID.fromString(account.getId());
            if (account.getActivationId() != null) {
                dbAccount.activationId = UUID.fromString(account.getActivationId());
            }
            dbAccount.email = account.getEmail();
            dbAccount.password = account.getPassword();
            dbAccount.firstName = account.getFirstName();
            dbAccount.lastName = account.getLastName();
            dbAccount.phoneNumber = account.getPhone();
            dbAccount.role = account.getRole();
            dbAccount.applications = account.getApplications()
                    .stream()
                    .map(DBApplication::from)
                    .collect(Collectors.toSet());
            return dbAccount;
        }

        public Account toAccount() {
            Account acc = Account.load()
                    .id(this.id)
                    .activationId(this.activationId)
                    .email(Email.of(this.email))
                    .password(EncodedPassword.of(this.password))
                    .userName(UserName.of(this.firstName, this.lastName))
                    .phoneNumber(PhoneNumber.of(this.phoneNumber))
                    .role(AccountRole.of(this.role))
                    .withApplication(this.applications.stream()
                            .map(DBApplication::toApplication).collect(Collectors.toSet()))
                    .execute();
            System.out.println("Account Loaded is : " + acc);
            return acc;
        }

    }


    boolean existsByEmail(String email);

    DBAccount save(DBAccount dbAccount);

    Optional<DBAccount> findByEmail(String email);

    Optional<DBAccount> findById(UUID id);

    DBPage findPage(DBPageRequest dbPage);

    void deleteById(UUID accountId);

}

