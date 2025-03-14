package com.gray.auth.core.usecase.outputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCodeMailMessage;
import lombok.Builder;
import lombok.Getter;

public interface IVCodeMailSender {



    void sendVCodeMail(VCodeMailMessage message) ;

}
