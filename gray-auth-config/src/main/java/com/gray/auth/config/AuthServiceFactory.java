package com.gray.auth.config;

import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.infra.DefaultPasswordEncoderAdapter;
import com.gray.auth.infra.DefaultVCodeMailSender;
import com.gray.auth.infra.DefaultVCodeManager;
import com.gray.auth.infra.TokenProcessor;
import com.gray.auth.infra.repo.DefaultAccountRepo;
import com.gray.auth.infra.repo.DefaultAuthPersistenceGateway;
import com.gray.auth.infra.repo.jpa.repo.IJPAAPIPrincipleRepo;
import com.gray.auth.infra.repo.jpa.repo.IJPAAccountRepo;
import com.gray.auth.infra.repo.jpa.repo.IJPAApplicationRepo;
import com.gray.auth.infra.repo.jpa.repo.IVCodeRepo;
import com.gray.auth.config.mailsender.DkimJavaMailSender;
import com.gray.auth.config.mailsender.JavaMailSenderDecorator;

import org.simplejavamail.utils.mail.dkim.DkimSigner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.TemplateEngine;


@Configuration
public class AuthServiceFactory {


    public JavaMailSenderDecorator mailSenderDecorator(JavaMailSender mailSender , DkimSigner signer) {
        return new DkimJavaMailSender(mailSender , signer) ;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);  // Configure the pool size
        scheduler.setThreadNamePrefix("Scheduled-Task-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public TaskExecutor mailTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // using one thread to prevent server
                                    // to make multiple connection to the mail server in the same time

        executor.setMaxPoolSize(1);
       // executor.setQueueCapacity(25);
        executor.initialize();
        return executor;
    }
//
//    @Bean
//    public ScheduledExecutorService scheduledExecutorService() {
//        return Executors.newScheduledThreadPool(2) ;
//    }
    @Bean
    public IVCodeManager vcodeManager(IVCodeRepo repo) {
        return new DefaultVCodeManager(repo , taskScheduler()) ;
    }

    @Bean
    public IVCodeMailSender vcodeMailSender(JavaMailSender mailSender , TemplateEngine templateEngine , DkimSigner signer) {
        return  new DefaultVCodeMailSender(mailSenderDecorator(mailSender , signer) ,  templateEngine , mailTaskExecutor()) ;
    }

    @Bean
    public ITokenProcessor tokenProcessor(@Value("${jwt.secret-key}") String key) {
        return  new TokenProcessor(key) ;
    }


    @Bean
    public IPasswordEncoder passwordEncoder(){
        return new DefaultPasswordEncoderAdapter(new BCryptPasswordEncoder()) ;

    }

    @Bean
    public IAccountRepo accountRepo(IJPAAccountRepo jpaAccountRepo) {
        return new DefaultAccountRepo(jpaAccountRepo);
    }

    @Bean
    public IAuthPersistenceGateway applicationRepo(IJPAApplicationRepo applicationRepo, IJPAAPIPrincipleRepo principleRepo){
        return new DefaultAuthPersistenceGateway(applicationRepo,principleRepo);
    }
}
