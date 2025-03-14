package com.gary.auth.core.domain;

public class PhoneNumber {

    private final  String phone ;


    private PhoneNumber(String phone) {
        this.phone = phone ;
    }

    @Override
    public String toString() {
        return this.phone;
    }

    public static PhoneNumber  of(String phone) {
        /*
                TODO check if is a valid phone number
         */
        return new PhoneNumber(phone) ;
    }

    public String value() {
        return this.phone ;
    }
}
