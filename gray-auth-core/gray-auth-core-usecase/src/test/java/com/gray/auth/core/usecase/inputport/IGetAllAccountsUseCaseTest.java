package com.gray.auth.core.usecase.inputport;

import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo.DBAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.gray.auth.core.usecase.inputport.IGetAllAccountsUseCase.*;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class IGetAllAccountsUseCaseTest {

    @Mock
    private IAccountRepo accountRepo;

    @InjectMocks
    private IGetAllAccountsUseCase.DefaultGetAllAccountsUseCase getAllAccountsUseCase;



    @Test
    void apply_withValidRequest_shouldReturnResponse() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 5;

        List<DBAccount> dbAccounts = Arrays.asList(
                new DBAccount(UUID.randomUUID(), UUID.randomUUID(), "email1@example.com", "password", "FirstNameOne", "LastNameOne", "1234567890","USER"),
                new DBAccount(UUID.randomUUID(), UUID.randomUUID(), "email2@example.com", "password", "FirstNameTwo", "LastNameTwo", "0987654321","USER")
        );

        IAccountRepo.DBPage dbPage = new IAccountRepo.DBPage(dbAccounts, pageNumber, 10, 2, pageSize);
        when(accountRepo.findPage(any(IAccountRepo.DBPageRequest.class))).thenReturn(dbPage);

        IGetAllAccountsUseCase.GetAllAccountsRequest request = new IGetAllAccountsUseCase.GetAllAccountsRequest(pageNumber, pageSize);

        // Act
        IGetAllAccountsUseCase.GetAllAccountsResponse response = getAllAccountsUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAccounts().size());
        assertEquals(1, response.getCurrentPage());
        assertEquals(10, response.getTotalPages());
        assertEquals(2, response.getTotalAccounts());
        assertEquals(pageSize, response.getPageSize());
    }

    @Test
    void apply_withNegativePageNumber_shouldThrowException() {
        GetAllAccountsRequest request = new GetAllAccountsRequest(-1, 5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            getAllAccountsUseCase.apply(request);
        });

        assertEquals("Page number must not be negative", exception.getMessage());
    }

    @Test
    void apply_withZeroPageSize_shouldThrowException() {
        IGetAllAccountsUseCase.GetAllAccountsRequest request = new IGetAllAccountsUseCase.GetAllAccountsRequest(1, 0);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            getAllAccountsUseCase.apply(request);
        });

        Assertions.assertEquals("Page Size must not be 0 or negative", exception.getMessage());
    }

}
