package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestParamters {

    public static class TestAccount {
        public static final Email email = Email.of("test@email.com") ;
        public static final PlainPassword plainPassword = PlainPassword.of("Test1234@") ;
        public static final EncodedPassword encodedPassword = EncodedPassword.of(plainPassword.value());
        public static final String firstName = "Test" ;
        public static final String lastName = "Test" ;
        public static final PhoneNumber phone = PhoneNumber.of("1323456789") ;
        public static final UserName userName = UserName.of(firstName , lastName) ;
        public static final AccountRole role = AccountRole.USER;
        public static final Account account = Account.create()
                .email(email)
                .password(encodedPassword)
                .userName(userName)
                .phoneNumber(phone)
                .role(role)
                .execute();
        public static final IAccountRepo.DBAccount dbAccount = IAccountRepo.DBAccount.from(account) ;
        public static final Token refreshToken = account.generateRefreshToken() ;
        public static final SignedToken signedRefreshToken = new SignedToken(refreshToken , "signed-token") ;
    }

    public static class Confirmation {
        public static final VCode code  = VCode.of("1234" , new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))) ;
        public static final  IVCodeManager.VCodeRecord codeRecord = IVCodeManager.VCodeRecord.newInstance(TestAccount.email , code) ;
        public static final String ticketStr = "ticket" ;

    }


}