package com.gray.auth.infra.repo.jpa.repo.models;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
    @Getter
    @NoArgsConstructor
   public class DBVCode implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Integer id ;

        @Column(unique = true , nullable = false)
        String email ;

        @Column(unique = true , nullable = false)
        String vcode ;

        Date expire ;


       public static  DBVCode of(Email email , VCode code){
            DBVCode dbcode = new DBVCode() ;

            dbcode.email = email.toString() ;
            dbcode.vcode = code.value() ;
            dbcode.expire = code.expire() ;

            return dbcode ;
        }

    }