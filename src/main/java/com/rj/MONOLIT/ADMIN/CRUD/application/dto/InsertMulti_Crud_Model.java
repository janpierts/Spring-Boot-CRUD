package com.rj.MONOLIT.ADMIN.CRUD.application.dto;

import com.rj.MONOLIT.COMMON.utils.helperEndpoints;

public record InsertMulti_Crud_Model(String name, String email, Boolean isValid, String message) {
    public InsertMulti_Crud_Model validate(){
        StringBuilder validationMessage = new StringBuilder();
        boolean valid = true;
        if(name == null || name.isBlank()) validationMessage.append("Name is required. ");
        if(email == null || email.isBlank()) validationMessage.append("Email is required. ");
        if(helperEndpoints.isAlphabeticWithSpaces(name) == false || helperEndpoints.isValidEmail(email) == false){
                validationMessage.append("Name(only Alphabetic) or Email format is invalid. ");
        }
        if(validationMessage.length() > 0) valid = false;
        return new InsertMulti_Crud_Model(name, email, valid, validationMessage.toString().trim());
    }
    public InsertMulti_Crud_Model addMessage(String message){
        return new InsertMulti_Crud_Model(name, email, false, message);
    }
}
