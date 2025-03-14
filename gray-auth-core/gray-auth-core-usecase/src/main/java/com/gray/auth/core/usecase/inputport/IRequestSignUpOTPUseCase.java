package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import com.gary.auth.core.domain.VCodeMailMessage;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import lombok.*;

import static com.gray.auth.core.usecase.outputport.IVCodeManager.VCodeRecord;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

@Deprecated
public interface IRequestSignUpOTPUseCase extends Function<IRequestSignUpOTPUseCase.EmailSubmitRequest, IRequestSignUpOTPUseCase.EmailSubmitResponse> {


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


    static IRequestSignUpOTPUseCase newInstance(IVCodeManager vcodeManager, IVCodeMailSender emailClient) {
        return new DefaultRequestSignUpOTPUseCase(vcodeManager, emailClient);
    }


    final class DefaultRequestSignUpOTPUseCase implements IRequestSignUpOTPUseCase {

        private final IVCodeManager vcodeManager;
        private final IVCodeMailSender emailClient;

        DefaultRequestSignUpOTPUseCase(IVCodeManager vcodeManager, IVCodeMailSender emailClient) {
            this.vcodeManager = vcodeManager;
            this.emailClient = emailClient;
        }

        @Override
        public EmailSubmitResponse apply(EmailSubmitRequest request) {

            Email email = Email.of(request.email);

            VCodeRecord vcodeRecord = this.vcodeManager.generateVCodeFor(email);
            this.emailClient.sendVCodeMail(VCodeMailMessage.newInstance(email, VCode.of(vcodeRecord.getCode(), vcodeRecord.getExpire())));

            return new EmailSubmitResponse("Verification code sent to " + email, vcodeRecord.getExpire());

        }
    }
}
