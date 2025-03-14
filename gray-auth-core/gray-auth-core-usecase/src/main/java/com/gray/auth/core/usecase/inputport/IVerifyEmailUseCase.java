package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.*;

import java.util.function.Function;

public interface IVerifyEmailUseCase extends Function<IVerifyEmailUseCase.EmailVerificationRequest, IVerifyEmailUseCase.EmailVerificationResponse> {

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    final class EmailVerificationRequest {
        private String email;
        private String code;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    final class EmailVerificationResponse {
        private String ticket;
        private boolean emailExists;
    }


    static IVerifyEmailUseCase newInstance(IVCodeManager codeManager, ITokenProcessor tokenProcessor, IAccountRepo accountRepo) {
        return new DefaultVerifyEmailUseCase(codeManager, tokenProcessor, accountRepo);
    }


    @RequiredArgsConstructor
    class DefaultVerifyEmailUseCase implements IVerifyEmailUseCase {

        private final IVCodeManager codeManager;
        private final ITokenProcessor tokenProcessor;
        private final IAccountRepo accountRepo;


        @Override
        public EmailVerificationResponse apply(EmailVerificationRequest request) {

            Email email = Email.of(request.email);
            IVCodeManager.VCodeRecord record = codeManager.findCodeByEmail(email).orElseThrow(() ->
                    new IllegalInputException("Submitted email is not registered yet or it was canceled."));
            VCode requesterVCode = VCode.of(record.getCode(), record.getExpire());

            if (requesterVCode.isExpired() || !requesterVCode.value().equals(request.code)) {
                throw new IllegalInputException("Email verification refused.");
            }

            Ticket ticket = Ticket.create(email);
            SignedToken signedToken = this.tokenProcessor.transform(ticket);

            boolean emailExists = accountRepo.existsByEmail(email.value());

            return new EmailVerificationResponse(String.valueOf(signedToken), emailExists);
        }
    }


}
