package com.rj.MONOLIT.ADMIN.CRUD.application.dto;

import com.rj.MONOLIT.COMMON.utils.helperEndpoints;

public record InsertMulti_Crud_Model(String name, String email, Boolean isValid, String message) {
    public InsertMulti_Crud_Model validate(){
        StringBuilder validationMessage = new StringBuilder();
        boolean valid = true;
        boolean namemissing = (name == null || name.isBlank());
        boolean emailmissing = (email == null || email.isBlank());
        if(namemissing) validationMessage.append("Name is required. ");
        if(emailmissing) validationMessage.append("Email is required. ");
        if(!namemissing && !emailmissing){
            if(!helperEndpoints.isAlphabeticWithSpaces(name) || !helperEndpoints.isValidEmail(email)){
                    validationMessage.append("Name(only Alphabetic) or Email format is invalid. ");
            }
        }else{
            if(!namemissing && emailmissing){
                if(!helperEndpoints.isAlphabeticWithSpaces(name)){
                    validationMessage.append("Name(only Alphabetic) is invalid. ");
                }
            }
            if(namemissing && !emailmissing){
                if(!helperEndpoints.isValidEmail(email)){
                    validationMessage.append("Email format is invalid. ");
                }
            }
        }
        if(validationMessage.length() > 0) valid = false;
        return new InsertMulti_Crud_Model(name, email, valid, validationMessage.toString().trim());
    }
    public InsertMulti_Crud_Model addMessage(String message){
        return new InsertMulti_Crud_Model(name, email, false, message);
    }
}
