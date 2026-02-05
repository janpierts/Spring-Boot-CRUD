package com.CRUD_API_REST.ADMIN.CRUD.domain.service;

import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.in.Crud_ServicePort;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.COMMON.utils.helperEndpoints;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class Crud_Service implements Crud_ServicePort {
    private final Map<String, Crud_RepositoryPort> crudRepositoryPort;

    public Crud_Service(Map<String, Crud_RepositoryPort> crudRepositoryPort) {
        this.crudRepositoryPort = crudRepositoryPort;
    }
    /*@Param typeBean: tipo de bean para usar el tipo de repositorio(adapter"inMemoryRepository,inMysqlAdapter_JDBC, inMysqlAdapter_JPA") */

    private Crud_RepositoryPort getRepositoryPort(String typeBean) {
        Crud_RepositoryPort repositoryPort = crudRepositoryPort.get(typeBean);
        if (repositoryPort == null) {
            throw new IllegalArgumentException("No repository found for type: " + typeBean);
        }
        return repositoryPort;
    }
    //region SaveSimpleEntity
    /*@Param Crud_Entity: entidad a guardar */
    @Override
    public Object save_Crud_Entity(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Object result = repositoryPort.save_Crud_Entity(typeBean, entity);

            return helperEndpoints.buildResponse(1, "Registro exitoso", result, null);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }
    }
 
    @Override
    public Object save_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Object result = repositoryPort.save_Crud_Entity_JDBC_SP(typeBean, entity);

            return helperEndpoints.buildResponse(1, "Registro exitoso", result, null);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }
    }

    @Override
    public Object save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Object result = repositoryPort.save_Crud_Entity_JPA_SP(typeBean, entity);

            return helperEndpoints.buildResponse(1, "Registro exitoso", result, null);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), null, entity);
        }
    }
    //endregion

    //region SaveMultiEntity
    /*@Param List<Crud_Entity> lista de entidades a guardar */
    @Override
    public Object save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }

    @Override
    public Object save_multi_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity_JDBC_SP(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }

    @Override
    public Object save_multi_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity_JPA_SP(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }
    //endregion

    //region SaveImportEntity
    /*@Param MultipartFile file to decode */
    @Override
    public Object save_import_Crud_Entity(String typeBean, List<Crud_Entity> entityList){
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }

    @Override
    public Object save_import_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList){
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity_JDBC_SP(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }

    @Override
    public Object save_import_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList){
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity_JPA_SP(typeBean, entityList).get();
            return helperEndpoints.buildResponse(1,"", result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entityList);
        }
    }
    //endregion

    //region FindEntityById
    /*@Param id: entidad a buscar */
    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityById(typeBean, id);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JDBC_SP_ById(typeBean, id);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JPA_SP_ById(typeBean, id);
    }
    //endregion

    //region FindEntityByName
    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityByName(typeBean, name);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JDBC_SP_ByName(typeBean, name);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JPA_SP_ByName(typeBean, name);
    }
    //endregion

    //region FindEntitiesByNames
    @Override
    public Optional<List<Crud_Entity>> find_Crud_EntityByNames(String typeBean, List<Crud_Entity> names) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityByNames(typeBean, names);
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JDBC_SP_ByNames(typeBean, names);
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JPA_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JPA_SP_ByNames(typeBean, names);
    }
    //endregion

    //region FindAllEntities
    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity(typeBean);
    }
    
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity_JDBC_SP(typeBean);
    }
    
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity_JPA_SP(typeBean);
    }
    //endregion

    //region UpdateEntity
    /*@Param Crud_Entity: entidad por actualizar */
    @Override
    public Object update_Crud_Entity(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity_JDBC_SP(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity_JPA_SP(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }
    //endregion

    //region DeletePhisicalEntityById
    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_ById(typeBean, id);
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_JDBC_SP_ById(typeBean, id);
    }
    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_JPA_SP_ById(typeBean, id);
    }
    //endregion

    //region DeleteLogicalEntityById
    @Override
    public Object delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_JDBC_SP_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        try{
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_JPA_SP_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }
    //endregion
}