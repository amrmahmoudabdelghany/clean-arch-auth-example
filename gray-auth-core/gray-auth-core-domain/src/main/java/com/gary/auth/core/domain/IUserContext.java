package com.gary.auth.core.domain;

import java.util.UUID;

public interface IUserContext {

    UUID getId();
    String getUserName() ;
    String getEmail() ;


}
