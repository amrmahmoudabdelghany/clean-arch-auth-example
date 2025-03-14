package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class VCodeMailMessage {

    public static int MESSAGE_SUBJECT_MAX_LEN = 255;
    public static String DEFAULT_SUBJECT = "Verification Code";


    private Email to;
    private String subject;
    private VCode vCode;

    private VCodeMailMessage() {
    }

    ;

    public static VCodeMailMessage newInstance(Email to, String subject, VCode code) {

        if (subject == null || subject.isBlank()) {
            throw new MissedInputException("Email message subject is required");
        }

         subject = subject.trim() ;

        if (subject.length() > MESSAGE_SUBJECT_MAX_LEN) {
            throw new InvalidInputException("Email message subject is too long , its length is limited to  " + MESSAGE_SUBJECT_MAX_LEN);
        }

        if (!subject.matches("^(\\S+(\\s\\S+)*)?$")) {
            throw new InvalidInputException("Email message subject should contains only Alphanumeric characters");
        }

        VCodeMailMessage message = new VCodeMailMessage();
        message.to = to;
        message.vCode = code;
        message.subject = subject;

        return message;
    }

    public static VCodeMailMessage newInstance(Email to, VCode code) {
        return newInstance(to, DEFAULT_SUBJECT, code);
    }


}
