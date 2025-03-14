package com.gray.auth.infra;

import com.gary.auth.core.domain.VCodeMailMessage;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@AllArgsConstructor
public class DefaultVCodeMailSender implements IVCodeMailSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine ;
    private final TaskExecutor taskExecutor ;

    @Override
   // @Async
    public void sendVCodeMail(VCodeMailMessage confirmation) {

         taskExecutor.execute(()->{
             mailSender.send(getMimeMessage(confirmation));
         });
    }

    private MimeMessage getMimeMessage(VCodeMailMessage confirmation)   {

        try {
            MimeMessage message = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("no-reply@vfcco.com", "[1st] Authentication");
            helper.setSubject(confirmation.getSubject());
            helper.setTo(confirmation.getTo().value());
            helper.setSentDate(new Date());
            helper.setPriority(1);
            helper.setText(getHtmlMessage(confirmation) , true);
            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHtmlMessage(VCodeMailMessage confirmation) {
        Context context = new Context() ;
        context.setVariable("vcode" , confirmation.getVCode().value());
        context.setVariable("expire" , confirmation.getVCode().expire());
        return  templateEngine.process("auth/email-confirmation", context) ;
    }

}
