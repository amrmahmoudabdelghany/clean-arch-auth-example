package com.gray.auth.infra;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.infra.repo.jpa.repo.IVCodeRepo;
import com.gray.auth.infra.repo.jpa.repo.models.DBVCode;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Optional;

@AllArgsConstructor
public class DefaultVCodeManager implements IVCodeManager {
    private final IVCodeRepo repo ;
    private final TaskScheduler taskScheduler ;

    @Override
    public boolean revokeVCode(String code) {
        return false;
    }

    @Override
    public Optional<VCodeRecord> findCodeByEmail(Email email) {
        return repo.findByEmail(email.toString()).map((dbCodeRecord)->VCodeRecord.newInstance(Email.of(dbCodeRecord.getEmail()) , VCode.of(dbCodeRecord.getVcode() , dbCodeRecord.getExpire())));
    }


    @Override
    public VCodeRecord generateVCodeFor(Email email) {

        if(repo.existsByEmail(email.toString())) {
            Integer id =  repo.deleteByEmail(email.toString()) ;
            if(id == null) {
                throw  new IllegalInputException("Unknown error happens , please try again later") ;
            }
        }

        VCode code = null ;

        do {
            code = VCode.newInstance() ;
        }while (this.repo.existsByVcode(code.value())) ;


        final DBVCode newCode = this.repo.save(DBVCode.of(email , code)) ;

        this.taskScheduler.schedule(()->{
          repo.delete(newCode);
          } , Instant.now().plusMillis(VCode.CODE_EXPIRE_DURATION_MILLIS)) ;

      //  this.taskScheduler.schedule(() -> repo.delete(newCode), VCode.CODE_EXPIRE_DURATION , TimeUnit.MILLISECONDS) ;

        return  VCodeRecord.newInstance( email , code) ;
    }
}
