package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import com.gary.auth.core.domain.VCodeMailMessage;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import static com.gray.auth.core.usecase.outputport.IVCodeManager.VCodeRecord;

@Deprecated
public interface IRequestResetPasswordOTPUseCase extends Function<IRequestResetPasswordOTPUseCase.EmailSubmitRequest, IRequestResetPasswordOTPUseCase.EmailSubmitResponse> {


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class EmailSubmitRequest implements Serializable {
        private String email;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class EmailSubmitResponse {
        private String message;
        private Date expireAt;
    }


    static IRequestResetPasswordOTPUseCase newInstance(IVCodeManager vcodeManager, IVCodeMailSender emailClient, IAccountRepo accountRepo) {
        return new DefaultRequestSignUpOTPUseCase(vcodeManager, emailClient, accountRepo);
    }


    final class DefaultRequestSignUpOTPUseCase implements IRequestResetPasswordOTPUseCase {

        private final IVCodeManager vcodeManager;
        private final IVCodeMailSender emailClient;
        private final IAccountRepo accountRepo;

        DefaultRequestSignUpOTPUseCase(IVCodeManager vcodeManager, IVCodeMailSender emailClient, IAccountRepo accountRepo) {
            this.vcodeManager = vcodeManager;
            this.emailClient = emailClient;
            this.accountRepo = accountRepo;
        }

        @Override
        public EmailSubmitResponse apply(EmailSubmitRequest request) {

            Email email = Email.of(request.email);
            if (!accountRepo.existsByEmail(email.value())) {
                throw new IllegalInputException("Email not Found");
            }
            VCodeRecord vcodeRecord = this.vcodeManager.generateVCodeFor(email);
            this.emailClient.sendVCodeMail(VCodeMailMessage.newInstance(email, VCode.of(vcodeRecord.getCode(), vcodeRecord.getExpire())));

            return new EmailSubmitResponse("Verification code sent to " + email, vcodeRecord.getExpire());

        }
    }
}
