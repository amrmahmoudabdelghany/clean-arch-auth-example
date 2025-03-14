package com.gray.auth.config.ui.admin;

import com.gary.auth.core.domain.IUserContext;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import com.gray.auth.core.usecase.inputport.administration.admin.IAdminCreateAccountUseCase;
import com.gray.auth.core.usecase.inputport.administration.admin.IAdminDeleteAccountUseCase;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/auth")
@PreAuthorize("hasRole('ADMIN')")
@SuppressWarnings("unused")
public class AuthWebUIAdmin {


    @Autowired
    private IAdminCreateAccountUseCase adminCreateAccountUseCase;

    @Autowired
    private IAdminDeleteAccountUseCase adminDeleteAccountUseCase;


    @PostMapping("/account/create")
    public ResponseEntity<IAdminCreateAccountUseCase.CreateAccountResponse>
    createAccount(@RequestBody IAdminCreateAccountUseCase.CreateAccountRequest req
            ,@AuthenticationPrincipal IUserContext userContext) {
        return ResponseEntity.ok(adminCreateAccountUseCase.apply(req));
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<IAdminDeleteAccountUseCase.DeleteAccountByIdResponse>
    deleteAccount(@RequestBody IAdminDeleteAccountUseCase.DeleteAccountByIdRequest req
            ,@AuthenticationPrincipal IUserContext userContext){
        return  ResponseEntity.ok(adminDeleteAccountUseCase.apply(req));
    }

    @ExceptionHandler(IllegalInputException.class)
    public ResponseEntity<ErrorResponse> handleIllegalInputException(IllegalInputException ex){
        return ResponseEntity.unprocessableEntity().body(new ErrorResponse(ex)) ;
    }

    @ExceptionHandler({InvalidInputException.class , MissedInputException.class})
    public ResponseEntity<ErrorResponse> handleInvalidInputException(RuntimeException ex){
        return ResponseEntity.badRequest().body(new ErrorResponse(ex)) ;
    }


    @NoArgsConstructor
    @Data
    public static class ErrorResponse {
        private  String message ;
        public ErrorResponse(Throwable t){
            this.message = t.getMessage();
        }
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
