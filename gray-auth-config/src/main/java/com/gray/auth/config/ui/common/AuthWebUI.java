package com.gray.auth.config.ui.common;


import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import com.gray.auth.config.PerUserRateLimiterService;
import com.gray.auth.core.usecase.inputport.*;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IApiPrincipleSignInUseCase;
import com.gray.auth.core.usecase.inputport.administration.superadmin.apiprinciple.IApiPrincipleSignInUseCase.ApiSignInResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.gray.auth.core.usecase.inputport.IDecodeAccessTokenUseCase.* ;
import static com.gray.auth.core.usecase.inputport.IGetAllAccountsUseCase.* ;
import static com.gray.auth.core.usecase.inputport.ISubmitEmailUseCase.* ;



@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@SuppressWarnings("unused")
public class AuthWebUI {

//    @Autowired
//    private IRequestSignUpOTPUseCase requestSignUpOTPUseCase;
//
//    @Autowired
//    private IRequestResetPasswordOTPUseCase requestResetPasswordOTPUseCase;

    @Autowired
    private IVerifyEmailUseCase verifyEmailUseCase ;

    @Autowired
    private ISignUpUseCase signUpUseCase ;

    @Autowired
    private ISignInUseCase signInUseCase ;

    @Autowired
    private IRefreshUseCase refreshTokenUseCase ;

    @Autowired
    private IRevokeUseCase revokeUseCase ;

    @Autowired
    private IResetPasswordUseCase resetPasswordUseCase ;

    @Autowired
    private IGetAllAccountsUseCase getAllAccountsUseCase;

    @Autowired
    private IDecodeAccessTokenUseCase decodeAccessTokenUseCase;


    @Autowired
    private ISubmitEmailUseCase submitEmailUseCase ;

    @Autowired
    private IApiPrincipleSignInUseCase apiPrincipleSignInUseCase;


    @Autowired
    private PerUserRateLimiterService limiterService;


//    @PostMapping("/signup/email/submit")
//    public ResponseEntity<IRequestSignUpOTPUseCase.EmailSubmitResponse> requestSignUpOTP(@RequestBody IRequestSignUpOTPUseCase.EmailSubmitRequest reqDTO,
//                                                           HttpServletRequest request){
//        String clientIp = request.getRemoteAddr();
//
//        if (!limiterService.isAllowed(clientIp)) {
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
//        }
//
//        return  ResponseEntity.ok(this.requestSignUpOTPUseCase.apply(reqDTO)) ;
//    }
//
//    @PostMapping("/resetpassword/email/submit")
//    public ResponseEntity<IRequestResetPasswordOTPUseCase.EmailSubmitResponse> requestResetPasswordOTP(@RequestBody IRequestResetPasswordOTPUseCase.EmailSubmitRequest reqDTO,
//                                                           HttpServletRequest request){
//        String clientIp = request.getRemoteAddr();
//
//        if (!limiterService.isAllowed(clientIp)) {
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
//        }
//
//        return  ResponseEntity.ok(this.requestResetPasswordOTPUseCase.apply(reqDTO)) ;
//    }

    @PostMapping("/email/submit")
    public ResponseEntity<EmailSubmitResponse> submitEmail(@RequestBody EmailSubmitRequest reqDTO,
                                                           HttpServletRequest request){
        String clientIp = request.getRemoteAddr();

        if (!limiterService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        return  ResponseEntity.ok(this.submitEmailUseCase.apply(reqDTO)) ;
    }

    @PostMapping("/email/verify")
    public ResponseEntity<IVerifyEmailUseCase.EmailVerificationResponse> verifyEmail(@RequestBody IVerifyEmailUseCase.EmailVerificationRequest reqDTO) {
        return ResponseEntity.ok(verifyEmailUseCase.apply(reqDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<ISignUpUseCase.SignUpResponse> signup(@RequestBody ISignUpUseCase.SignUpRequest req) {
      return ResponseEntity.ok(signUpUseCase.apply(req)) ;
    }

    @PostMapping("/signin")
    public ResponseEntity<ISignInUseCase.SignInResponse> signin(@RequestBody ISignInUseCase.SignInRequest req) {
        return ResponseEntity.ok(signInUseCase.apply(req)) ;
    }

    @PostMapping("/principle/signin")
    public ResponseEntity<ApiSignInResponse> apiSignIn(@RequestBody IApiPrincipleSignInUseCase.ApiSignInRequest req) {
        return ResponseEntity.ok(apiPrincipleSignInUseCase.apply(req)) ;
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<IRefreshUseCase.RefreshResponse> refreshToken(@RequestBody IRefreshUseCase.RefreshRequest req) {
        return ResponseEntity.ok(refreshTokenUseCase.apply(req)) ;
    }

    @PostMapping("/token/decode")
    public ResponseEntity<DecodeAccessTokenResponse> decodeAccessToken(
            @RequestBody AccessTokenDecodeRequest req) {
        return ResponseEntity.ok(decodeAccessTokenUseCase.apply(req));
    }

    @PostMapping("/token/revoke")
    public ResponseEntity<IRevokeUseCase.RevokeResponse> revokeToken(@RequestBody IRevokeUseCase.RevokeRequest req) {
        return ResponseEntity.ok(revokeUseCase.apply(req)) ;
    }

    @PostMapping("/password/reset")
    public ResponseEntity<IResetPasswordUseCase.ResetPasswordResponse> assignPassword(@RequestBody IResetPasswordUseCase.ResetPasswordRequest req) {
        return ResponseEntity.ok(resetPasswordUseCase.apply(req)) ;
    }

    @GetMapping("/account/list")
    public ResponseEntity<GetAllAccountsResponse> getAllAccounts(
            @RequestParam int pageNumber, @RequestParam int pageSize){
        GetAllAccountsRequest req = new GetAllAccountsRequest(pageNumber,pageSize);
        return ResponseEntity.ok(getAllAccountsUseCase.apply(req));
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
