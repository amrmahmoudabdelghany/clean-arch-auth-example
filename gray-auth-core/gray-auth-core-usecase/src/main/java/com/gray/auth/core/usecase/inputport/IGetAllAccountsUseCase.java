package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gray.auth.core.usecase.outputport.repo.IAccountRepo.*;

public interface IGetAllAccountsUseCase extends Function<IGetAllAccountsUseCase.GetAllAccountsRequest, IGetAllAccountsUseCase.GetAllAccountsResponse> {


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class GetAllAccountsRequest {
        private int pageNumber;     //pageNumber Zero-based
        private int pageSize;
    }


    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class AccountResponse {
        private String id;
        private String email;
        private String userName;
        private String phoneNumber;

        static AccountResponse from(Account account) {
            AccountResponse response = new AccountResponse(
                    account.getId(),
                    account.getEmail(),
                    account.getFullName(),
                    account.getPhone()
            );
            return response;
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class GetAllAccountsResponse {
        private List<AccountResponse> accounts;
        private int currentPage;
        private int totalPages;
        private long totalAccounts;
        private int pageSize;

        static GetAllAccountsResponse from(DBPage DBPage) {

            List<AccountResponse> accountResponseList = DBPage.getDbAccounts().stream()
                    .map(DBAccount::toAccount)
                    .map(AccountResponse::from)
                    .collect(Collectors.toList());

            GetAllAccountsResponse response = new GetAllAccountsResponse(
                    accountResponseList,
                    DBPage.getCurrentPage(),
                    DBPage.getTotalPages(),
                    DBPage.getTotalAccounts(),
                    DBPage.getPageSize()
            );

            return response;
        }
    }


    static IGetAllAccountsUseCase newInstance(final IAccountRepo accountRepo) {
        return new IGetAllAccountsUseCase.DefaultGetAllAccountsUseCase(accountRepo);
    }


    @RequiredArgsConstructor
    final class DefaultGetAllAccountsUseCase implements IGetAllAccountsUseCase {

        private final IAccountRepo accountRepo;

        @Override
        public GetAllAccountsResponse apply(GetAllAccountsRequest getAllAccountsRequest) {

            int pageNumber = getAllAccountsRequest.pageNumber;
            int pageSize = getAllAccountsRequest.pageSize;

            if (pageNumber < 0) {
                throw new IllegalArgumentException("Page number must not be negative");
            }
            if (pageSize <= 0) {
                throw new IllegalArgumentException("Page Size must not be 0 or negative");
            }

            DBPageRequest dbPageRequest = new DBPageRequest(pageNumber, pageSize);

            DBPage dbPage = accountRepo.findPage(dbPageRequest);

            return GetAllAccountsResponse.from(dbPage);
        }
    }
}