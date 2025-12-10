package com.CRUD_API_REST.CRUD.domain.ports.in;

import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;

import java.util.List;
import java.util.Optional;

public interface Crud_ServicePort {
    Crud_Entity save_Crud_Entity(String typeBean,Crud_Entity entity);
    Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity entity);
    Crud_Entity save_Crud_Entity_JPA_SP(String typeBean,Crud_Entity entity);
    Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id);
    List<Crud_Entity> findAll_Crud_entity(String typeBean);
    Crud_Entity update_Crud_Entity(String typeBean,Crud_Entity entity);
    void delete_Crud_Entity_ById(String typeBean,Long id);
}