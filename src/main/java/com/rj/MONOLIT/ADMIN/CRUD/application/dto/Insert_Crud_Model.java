package com.rj.MONOLIT.ADMIN.CRUD.application.dto;

import com.rj.MONOLIT.COMMON.utils.helperEndpoints;

public record Insert_Crud_Model(
    String name,
    String email
) {
    public Insert_Crud_Model{
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name is required");
        }
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email is required");
        }
        if(helperEndpoints.isAlphabeticWithSpaces(name) == false || helperEndpoints.isValidEmail(email) == false){
            throw new IllegalArgumentException("Name(only Alphabetic) or Email format is invalid");
        }
    }
}
