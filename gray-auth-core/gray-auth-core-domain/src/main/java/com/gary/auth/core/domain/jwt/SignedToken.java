package com.gary.auth.core.domain.jwt;



public  class SignedToken extends Token{
        private final String tokenStr ;

        public SignedToken(Token token , String tokenStr) {
            super(token.claims);
            this.tokenStr = tokenStr ;
        }


        @Override
        public String toString() {
            return tokenStr ;
        }

    }
