package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import com.gary.auth.core.domain.jwt.*;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@ToString
public class Account {


    private  UUID id ;
    private  Email email ;
    private EncodedPassword password ;
    private PhoneNumber phone ;
    private UUID activationId ;
    private UserName userName ;
    private AccountRole role;
    private Set<Application> applications;



    private Account(DefaultEditor defaultEditor) {
        this.id = defaultEditor.id ;
        this.email = defaultEditor.email ;
        this.password = defaultEditor.password ;
        this.phone = defaultEditor.phone ;
        this.activationId = defaultEditor.activationId ;
        this.userName = defaultEditor.userName ;
        this.role = defaultEditor.role;
        this.applications = defaultEditor.applications;

    }
    private Account(DefaultFormer defaultFormer) {
        if(defaultFormer.id == null) defaultFormer.id = UUID.randomUUID() ;

        setId(defaultFormer.id);
        setActivationId(defaultFormer.activationId);
        setEmail(defaultFormer.email);
        setEncodedPassword(defaultFormer.password);
        setPhone(defaultFormer.phoneNumber);
        setUserName(defaultFormer.userName);
        setAccountRole(defaultFormer.role);
        setApplications(defaultFormer.applications);
    }

    public String getActivationId() {
        if(this.activationId != null)
            return this.activationId.toString();
        else return null ;
    }
    public String getPhone() {
        return this.phone.value() ;
    }

    public String getFirstName() {
        return this.userName.firstName() ;
    }

    public String getLastName() {
        return this.userName.lastName() ;
    }
    public String getFullName() {
        return this.userName.fullName() ;
    }
    private void setActivationId(UUID id) {
        this.activationId = id ;
    }
    private void setId(UUID id) {
        if(id == null) {
            throw new MissedInputException("Account id is required") ;
        }
        this.id = id ;
    }
    private void setEmail(Email email) {
        if(email == null) {
            throw  new MissedInputException("Email is required") ;
        }
        this.email = email ;
    }

    private void setUserName(UserName userName) {
        if(userName == null) {
            throw new MissedInputException("User name is required") ;
        }
        this.userName = userName ;
    }

    private void setPhone(PhoneNumber phone) {
        if(phone == null) {
            throw new MissedInputException("Phone number is required");
        }
        this.phone =  phone ;
    }

    private void setEncodedPassword(EncodedPassword password) {
        if(password == null) {
            throw new MissedInputException("Password is required");
        }
        this.password = password ;
    }

    private void setApplications(Set<Application> applications){ //TODO
        this.applications = Set.copyOf(applications);
    }
    public Set<Application> getApplications(){
        return this.applications;
    }

    public  String getId() {
        return this.id.toString();
    }
    public String getEmail() {
        return this.email.value() ;
    }

    public String getPassword() {
        return this.password.value();
    }

    public void setAccountRole(AccountRole role) {
        if (role == null) {
            throw new MissedInputException("AccountRole is required");
        }
        this.role = role;
    }

    public String getRole(){
        return this.role.toString();
    }

    public boolean isSuperAdmin(){
        return this.role == AccountRole.SUPER_ADMIN;
    }
    public boolean isAdmin() {
        return this.role == AccountRole.ADMIN;
    }

    public Token generateRefreshToken() {
        // return RefreshToken.create(UUID.randomUUID() , this.email) ;
        return RefreshToken.create()
                .accountId(this.id)
                .tokenId(UUID.randomUUID())
                .email(this.email)
                .execute() ;
    }

    public void activate(SignedToken refreshToken) {

        try {

            RefreshToken token = RefreshToken.from(refreshToken) ;


            if(token.getAccountId().equals(this.id)) {

                this.activationId = token.getTokenId() ;

            }else {
                throw new IllegalStateException("Illegal refresh token , Actual account id is different than token account id") ;
            }
        }catch (Exception e) {
            throw  new IllegalInputException("Could not activate account" , e) ;
        }
    }

    public void deactivate(SignedToken refreshToken){

        try {

            RefreshToken token = RefreshToken.from(refreshToken) ;

            if(!token.getTokenId().equals(this.activationId))
                throw new IllegalStateException() ;

            this.activationId = null ;

        }catch (Exception e) {
            throw new IllegalInputException("Could not deactivate account") ;
        }
    }



    public Token refresh(SignedToken refreshToken){

        try {

            RefreshToken token =  RefreshToken.from(refreshToken) ;

            if(this.activationId == null)
                throw  new IllegalStateException("Account is not activated yet") ;

            if(!token.getTokenId().equals(this.activationId))
                throw new IllegalStateException() ;

            // return  AccessToken.create(this.email , this.userName) ;
            return AccessToken.create()
                    .accountId(this.id)
                    .email(this.email)
                    .userName(this.userName)
                    .withRole(this.role)
                    .execute();

        }catch (Exception e) {
            throw new IllegalInputException("Could not refresh account" + e.getMessage()) ;
        }

    }




    public static EmailFormer create(){
        return new DefaultFormer() ;
    }
    public static IDFormer load() {
        return new DefaultFormer() ;
    }
    public static Editor edit(Account acc) {
        if(acc.id == null){
            throw new IllegalInputException("Trying edit unidentified Account instance") ;
        }
        return new DefaultEditor(acc) ;
    }
    public  interface IDFormer {
        ActivationIdFormer id(UUID id) ;
    }
    public interface ActivationIdFormer {
        EmailFormer activationId(UUID activationId) ;
    }
    public interface EmailFormer {
        PasswordFormer email(com.gary.auth.core.domain.Email email) ;
    }
    public interface PasswordFormer {
        UserNameFormer password(EncodedPassword encodedPassword) ;
    }

    public interface UserNameFormer {
        PhoneNumberFormer userName(UserName userName) ;
    }
    public interface PhoneNumberFormer {
        AccountRoleFormer  phoneNumber(PhoneNumber phone) ;
    }
    public interface AccountRoleFormer{
        ApplicationFormer role(AccountRole role);
    }
    public interface ApplicationFormer{
        Former withApplication(Set<Application> applications);
    }

    public interface  Former {
        Account execute();
    }




    public interface  Editor {
        Editor activationId(UUID id) ;
        Editor email(Email email) ;
        Editor password(EncodedPassword password) ;
        Editor userName(UserName  userName);
        Editor phoneNumber(PhoneNumber phone);
        Editor role(AccountRole role);
        Editor applications(Set<Application> applications);
        Account execute() ;

    }

    final static class DefaultEditor implements  Editor {

        private final UUID  id ;
        private UUID activationId ;
        private Email email ;
        private EncodedPassword password ;
        private UserName userName ;
        private PhoneNumber phone ;
        private AccountRole role;
        private Set<Application> applications;

        private DefaultEditor(Account account) {
            this.id = account.id ;
            this.activationId = account.activationId ;
            this.email =  account.email ;
            this.password = account.password ;
            this.userName = account.userName ;
            this.phone = account.phone ;
            this.role = account.role;
        }

        @Override
        public Editor activationId(UUID id) {
            this.activationId = id ;
            return this;
        }

        @Override
        public Editor email(Email email) {
            this.email = email ;
            return this ;
        }

        @Override
        public Editor password(EncodedPassword password) {
            this.password = password ;
            return this;
        }

        @Override
        public Editor userName(UserName userName) {
            this.userName = userName ;
            return this;
        }

        @Override
        public Editor phoneNumber(PhoneNumber phone) {
            this.phone = phone ;
            return this;
        }

        @Override
        public Editor role(AccountRole role) {
            this.role = role;
            return this;
        }

        @Override
        public Editor applications(Set<Application> applications) {
            this.applications = Set.copyOf(applications);
            return this;
        }

        @Override
        public Account execute() {
            return new Account(this);
        }
    }

    final static class DefaultFormer implements IDFormer, ActivationIdFormer, EmailFormer, PasswordFormer, UserNameFormer, PhoneNumberFormer, AccountRoleFormer ,ApplicationFormer, Former {

        private UUID id ;
        private UUID activationId ;
        private Email email ;
        private EncodedPassword password ;
        private PhoneNumber phoneNumber ;
        private UserName userName ;
        private AccountRole role;
        private Set<Application> applications;

        private DefaultFormer(UUID id , UUID activationId) {
            this.id = id ;
            this.activationId = activationId ;
        }
        private DefaultFormer(){}



        @Override
        public EmailFormer activationId(UUID activationId) {
            this.activationId = activationId ;
            return this;
        }

        @Override
        public PasswordFormer email(Email  email) {
            this.email = email ;
            return this;
        }

        @Override
        public PhoneNumberFormer userName(UserName userName) {
            this.userName = userName ;
            return this ;
        }

        @Override
        public ActivationIdFormer id(UUID id) {
            this.id = id ;
            return this;
        }

        @Override
        public AccountRoleFormer phoneNumber(PhoneNumber phone) {
            this.phoneNumber = phone ;
            return this;
        }

        @Override
        public UserNameFormer password(EncodedPassword encodedPassword) {
            this.password = encodedPassword ;
            return this;
        }

        @Override
        public ApplicationFormer role(AccountRole role) {
            this.role = role;
            return this;
        }

        @Override
        public Former withApplication(Set<Application> applications) {
            this.applications = Set.copyOf(applications);
            return this;
        }

        @Override
        public Account execute() {
            return new Account(this);
        }
    }

}