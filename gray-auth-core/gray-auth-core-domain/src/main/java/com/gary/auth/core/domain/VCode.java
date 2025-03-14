package com.gary.auth.core.domain;


import com.gary.auth.core.domain.exception.InvalidInputException;

import java.util.Date;
import java.util.Random;

public class VCode {
    private static final Random random = new Random() ;
    public static final int CODE_EXPIRE_DURATION_MILLIS = 600000 ; // 10 mints


    private String code ;
    private Date expire ;

    private VCode(){}



    public String value() {
        return this.code ;
    }

    public Date expire() {
        return this.expire ;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expire.getTime() ;
    }
    private  String generateNewCode() {
        StringBuilder res = new StringBuilder();

        for(int i = 0 ; i < 4 ; i++) {
            res.append(String.valueOf(random.nextInt(10)));
        }
        return res.toString();
    }


    public static VCode of(String code , Date expire) {
        VCode c = new VCode() ;

        if(code.length() != 4) {
            throw new InvalidInputException("Invalid verification code format , Verification code should consists of 4-digits only") ;
        }

        c.code = code ;
        c.expire = expire ;

        return c ;
    }

    public static VCode newInstance() {
        VCode c = new VCode() ;
        c.code = c.generateNewCode() ;
        c.expire = new Date(System.currentTimeMillis() + CODE_EXPIRE_DURATION_MILLIS);
        return c  ;
    }
}
