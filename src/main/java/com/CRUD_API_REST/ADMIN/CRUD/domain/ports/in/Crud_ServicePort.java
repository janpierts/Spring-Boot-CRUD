package com.CRUD_API_REST.ADMIN.CRUD.domain.ports.in;

import java.util.List;
import java.util.Optional;
import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;

public interface Crud_ServicePort {
    Object save_Crud_Entity(String typeBean,Crud_Entity entity);
    Object save_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity entity);
    Object save_Crud_Entity_JPA_SP(String typeBean,Crud_Entity entity);
    Object save_multi_Crud_Entity(String typeBean,List<Crud_Entity> entityList);
    Object save_multi_Crud_Entity_JDBC_SP(String typeBean,List<Crud_Entity> entityList);
    Object save_multi_Crud_Entity_JPA_SP(String typeBean,List<Crud_Entity> entityList);
    Object save_import_Crud_Entity(String typeBean,List<Crud_Entity> entityList);
    Object save_import_Crud_Entity_JDBC_SP(String typeBean,List<Crud_Entity> entityList);
    Object save_import_Crud_Entity_JPA_SP(String typeBean,List<Crud_Entity> entityLigst);
    Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id);
    Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean,Long id);
    Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean,Long id);
    Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name);
    Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name);
    Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name);
    Optional<List<Crud_Entity>> find_Crud_EntityByNames(String typeBean, List<Crud_Entity> names);
    Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> names);
    Optional<List<Crud_Entity>> find_Crud_Entity_JPA_SP_ByNames(String typeBean, List<Crud_Entity> names);
    List<Crud_Entity> findAll_Crud_entity(String typeBean);
    List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean);
    List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean);
    Object update_Crud_Entity(String typeBean,Crud_Entity entity);
    Object update_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity entity);
    Object update_Crud_Entity_JPA_SP(String typeBean,Crud_Entity entity);
    void delete_Crud_Entity_phisical_ById(String typeBean,Long id);
    void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean,Long id);
    void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean,Long id);
    Object delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity);
    Object delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity);
    Object delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity);
}