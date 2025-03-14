package com.gray.auth.core.usecase.outputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import lombok.Getter;
import lombok.Value;

import java.util.Date;
import java.util.Optional;

public interface IVCodeManager {



    @Getter
    class VCodeRecord {

        private String email ;

        private String code ;

        private Date expire ;


        private VCodeRecord(){}


        public static VCodeRecord newInstance(Email email , VCode code) {
            VCodeRecord newCode = new VCodeRecord();
            newCode.email = email.toString() ;
            newCode.code = code.value() ;
            newCode.expire = code.expire() ;
            return newCode;
        }

    }
    VCodeRecord generateVCodeFor(Email email) ;
    boolean revokeVCode(String code ) ;
    Optional<VCodeRecord> findCodeByEmail(Email email) ;
}
