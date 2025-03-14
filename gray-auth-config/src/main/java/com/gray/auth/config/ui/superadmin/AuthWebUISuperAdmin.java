package com.gray.auth.config.ui.superadmin;


import com.gary.auth.core.domain.IUserContext;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import com.gray.auth.core.usecase.inputport.administration.superadmin.*;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IRetrieveAllApiPrinciplesUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.ISubmitAPIPrincipleUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRegisterApplicationUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRegisterApplicationUseCase.RegisterApplicationRequest;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRegisterApplicationUseCase.RegisterApplicationResponse;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IRetrieveAllApiPrinciplesUseCase.RetrieveAllApiPrinciplesResponse;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRetrieveAllApplicationsUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.application.IRetrieveAllApplicationsUseCase.RetrieveAllApplicationsResponse;

import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.ISubmitAPIPrincipleUseCase.SubmitAPIPrincipleResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/superadmin/auth")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@SuppressWarnings("unused")
public class AuthWebUISuperAdmin {


    @Autowired
    private ISuperAdminCreateAccountUseCase superAdminCreateAccountUseCase;

    @Autowired
    private ISuperAdminDeleteAccountUseCase superAdminDeleteAccountUseCase;

    @Autowired
    private IRegisterApplicationUseCase registerServiceUseCase;

    @Autowired
    private IRetrieveAllApplicationsUseCase retrieveAllServicesUseCase;

    @Autowired
    private ISubmitAPIPrincipleUseCase submitAPIPrincipleUseCase;

    @Autowired
    private IRetrieveAllApiPrinciplesUseCase retrieveAllApiPrinciplesUseCase;

    @PostMapping("/account/create")
    public ResponseEntity<ISuperAdminCreateAccountUseCase.CreateAccountResponse>
    createAccount(@RequestBody ISuperAdminCreateAccountUseCase.CreateAccountRequest req
            , @AuthenticationPrincipal IUserContext userContext) {

        return ResponseEntity.ok(superAdminCreateAccountUseCase.apply(req));
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<ISuperAdminDeleteAccountUseCase.DeleteAccountByIdResponse>
    deleteAccount(@RequestBody ISuperAdminDeleteAccountUseCase.DeleteAccountByIdRequest req
            ,@AuthenticationPrincipal IUserContext userContext){
        return  ResponseEntity.ok(superAdminDeleteAccountUseCase.apply(req));
    }

    @ExceptionHandler(IllegalInputException.class)
    public ResponseEntity<ErrorResponse> handleIllegalInputException(IllegalInputException ex){
        return ResponseEntity.unprocessableEntity().body(new ErrorResponse(ex)) ;
    }

    @ExceptionHandler({InvalidInputException.class , MissedInputException.class})
    public ResponseEntity<ErrorResponse> handleInvalidInputException(RuntimeException ex){
        return ResponseEntity.badRequest().body(new ErrorResponse(ex)) ;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.unprocessableEntity().body(new ErrorResponse(ex)) ;
    }

    @PostMapping("/application")
    public ResponseEntity<RegisterApplicationResponse> registerApplication(@RequestBody RegisterApplicationRequest req) {
        return ResponseEntity.ok(registerServiceUseCase.apply(req));
    }

    @GetMapping("/application")
    public ResponseEntity<RetrieveAllApplicationsResponse> getAllApplications() {
        return ResponseEntity.ok(retrieveAllServicesUseCase.get());
    }

    @PostMapping("/apiPrinciple")
    public ResponseEntity<SubmitAPIPrincipleResponse> submitApiPrinciple(@RequestBody ISubmitAPIPrincipleUseCase.SubmitAPIPrincipleRequest req) {
        return ResponseEntity.ok(submitAPIPrincipleUseCase.apply(req));
    }

    @GetMapping("/apiPrinciple")
    public ResponseEntity<RetrieveAllApiPrinciplesResponse> getAllApiPrinciples() {
        return ResponseEntity.ok(retrieveAllApiPrinciplesUseCase.get());
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
