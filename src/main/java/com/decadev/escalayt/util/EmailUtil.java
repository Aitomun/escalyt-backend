package com.decadev.escalayt.util;

public class EmailUtil {


    public static String getVerificationUrl( String token){
        return  "http://localhost:8080/api/auth/confirm?token=" + token ;
    }

    public static String getResetPasswordUrl(String token) {
//        return  "http://localhost:8080/api/auth/confirm-forget-password-token?token=" + token ;

        //        this to redirect from email token to frontend
        return  "http://localhost:5173/reset-password?token=" + token ;
    }
}
