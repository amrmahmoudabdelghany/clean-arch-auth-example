package com.gray.auth.core.usecase.inputport.administration.superadmin;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.UUID;
import java.util.function.Function;

public interface ISuperAdminDeleteAccountUseCase extends Function<ISuperAdminDeleteAccountUseCase.DeleteAccountByIdRequest, ISuperAdminDeleteAccountUseCase.DeleteAccountByIdResponse> {


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class DeleteAccountByIdRequest {
        private String accountId;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class DeleteAccountByIdResponse {
        private String message;
    }


    static ISuperAdminDeleteAccountUseCase newInstance(final IAccountRepo accountRepo) {
        return new DefaultSuperAdminDeleteAccountUseCase(accountRepo);
    }

    @RequiredArgsConstructor
    final class DefaultSuperAdminDeleteAccountUseCase implements ISuperAdminDeleteAccountUseCase {

        private final IAccountRepo accountRepo;

        @Override
        public DeleteAccountByIdResponse apply(DeleteAccountByIdRequest deleteRequest) {

            UUID accountId;
            try {
                accountId = UUID.fromString(deleteRequest.getAccountId());
            } catch (Exception e) {
                throw new IllegalInputException("Invalid Account ID");
            }

            Account account = this.accountRepo.findById(accountId)
                    .map(IAccountRepo.DBAccount::toAccount)
                    .orElseThrow(() -> new IllegalInputException("This account does not exist"));


            if (account.isSuperAdmin())
                throw new IllegalInputException("SuperAdmin can not delete SuperAdmin accounts.");

            accountRepo.deleteById(accountId);
            return new DeleteAccountByIdResponse("Account deleted successfully");
        }

    }
}