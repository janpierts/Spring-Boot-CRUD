package com.CRUD_API_REST.CRUD.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;

public interface Crud_RepositoryPort {
    Crud_Entity save_Crud_Entity(String typeBean,Crud_Entity entity);
    Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity entity);
    Crud_Entity save_Crud_Entity_JPA_SP(String typeBean,Crud_Entity entity);
    Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id);
    Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean,Long id);
    Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean,Long id);
    List<Crud_Entity> findAll_Crud_entity(String typeBean);
    List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean);
    List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean);
    Crud_Entity update_Crud_Entity(String typeBean,Crud_Entity entity);
    Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity entity);
    Crud_Entity update_Crud_Entity_JPA_SP(String typeBean,Crud_Entity entity);
    void delete_Crud_Entity_phisical_ById(String typeBean,Long id);
    void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean,Long id);
    void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean,Long id);
    Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity);
    Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity);
    Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity);
}
