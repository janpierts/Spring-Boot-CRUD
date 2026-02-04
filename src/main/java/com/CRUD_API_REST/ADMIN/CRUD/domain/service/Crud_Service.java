package com.CRUD_API_REST.ADMIN.CRUD.domain.service;

import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.in.Crud_ServicePort;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.COMMON.utils.helperEndpoints;
import jakarta.persistence.EntityNotFoundException;
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
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        }
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        Optional<Crud_Entity> result = repositoryPort.find_Crud_EntityById(typeBean, id);
        if(result.isEmpty()){
            throw new EntityNotFoundException("No se encontró ninguna entidad con el ID proporcionado: " + id);
        }
        return result;
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean, Long id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        }
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        Optional<Crud_Entity> result = repositoryPort.find_Crud_Entity_JDBC_SP_ById(typeBean, id);
        if(result.isEmpty()){
            throw new EntityNotFoundException("No se encontró ninguna entidad con el ID proporcionado: " + id);
        }
        return result;
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        }
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        Optional<Crud_Entity> result = repositoryPort.find_Crud_Entity_JPA_SP_ById(typeBean, id);
        if(result.isEmpty()){
            throw new EntityNotFoundException("No se encontró ninguna entidad con el ID proporcionado: " + id);
        }
        return result;
    }
    //endregion

    //region FindEntityByName
    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        name = helperEndpoints.sanitizeForSearch(name.trim());
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityByName(typeBean, name);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name) {
        name = helperEndpoints.sanitizeForSearch(name.trim());
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JDBC_SP_ByName(typeBean, name);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name) {
        name = helperEndpoints.sanitizeForSearch(name.trim());
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
            String mssg = "";
            if(entity.getId() == null || entity.getId() <= 0){
                mssg += "El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId();
            }
            if (!entity.getName().isEmpty() && entity.getName() != null) {
                if (!helperEndpoints.isAlphabeticWithSpaces(entity.getName())) {
                    if(mssg.length()>0) mssg += " | ";
                    mssg += "El nombre no puede contener números o caracteres especiales";
                }
            } else mssg += "El nombre no puede ser nulo o vacío";
            if(!entity.getEmail().isEmpty() && entity.getEmail() != null){
                if(!helperEndpoints.isValidEmail(entity.getEmail())){
                    if(mssg.length()>0) mssg += " | ";
                    
                    mssg += "El correo electrónico no tiene un formato válido";
                }
            }else{
                if(mssg.length()>0) mssg += " | ";
                mssg += "El correo electrónico no puede ser nulo o vacío";
            }
            if(mssg.length()>0){
                throw new IllegalArgumentException(mssg);
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        try{
            String mssg = "";
            if(entity.getId() == null || entity.getId() <= 0){
                mssg += "El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId();
            }
            if (!entity.getName().isEmpty() && entity.getName() != null) {
                if (!helperEndpoints.isAlphabeticWithSpaces(entity.getName())) {
                    if(mssg.length()>0) mssg += " | ";
                    mssg += "El nombre no puede contener números o caracteres especiales";
                }
            } else mssg += "El nombre no puede ser nulo o vacío";
            if(!entity.getEmail().isEmpty() && entity.getEmail() != null){
                if(!helperEndpoints.isValidEmail(entity.getEmail())){
                    if(mssg.length()>0) mssg += " | ";
                    
                    mssg += "El correo electrónico no tiene un formato válido";
                }
            }else{
                if(mssg.length()>0) mssg += " | ";
                mssg += "El correo electrónico no puede ser nulo o vacío";
            }
            if(mssg.length()>0){
                throw new IllegalArgumentException(mssg);
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity_JDBC_SP(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        try{
            String mssg = "";
            if(entity.getId() == null || entity.getId() <= 0){
                mssg += "El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId();
            }
            if (!entity.getName().isEmpty() && entity.getName() != null) {
                if (!helperEndpoints.isAlphabeticWithSpaces(entity.getName())) {
                    if(mssg.length()>0) mssg += " | ";
                    mssg += "El nombre no puede contener números o caracteres especiales";
                }
            } else mssg += "El nombre no puede ser nulo o vacío";
            if(!entity.getEmail().isEmpty() && entity.getEmail() != null){
                if(!helperEndpoints.isValidEmail(entity.getEmail())){
                    if(mssg.length()>0) mssg += " | ";
                    
                    mssg += "El correo electrónico no tiene un formato válido";
                }
            }else{
                if(mssg.length()>0) mssg += " | ";
                mssg += "El correo electrónico no puede ser nulo o vacío";
            }
            if(mssg.length()>0){
                throw new IllegalArgumentException(mssg);
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            Crud_Entity result = repositoryPort.update_Crud_Entity_JPA_SP(typeBean, entity);
            return helperEndpoints.buildResponse(1, "Actualización exitosa", null,null,result);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
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
            if(entity.getId() == null || entity.getId() <= 0){
                throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId());
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        try{
            if(entity.getId() == null || entity.getId() <= 0){
                throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId());
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_JDBC_SP_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }

    @Override
    public Object delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        try{
            if(entity.getId() == null || entity.getId() <= 0){
                throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + entity.getId());
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            entity = repositoryPort.delete_Crud_Entity_logical_JPA_SP_ById(typeBean, entity);
            
            return helperEndpoints.buildResponse(1, "Eliminación lógica exitosa", null, null, entity);
        }catch(IllegalArgumentException e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }catch(Exception e){
            return helperEndpoints.buildResponse(-1, e.getMessage(), entity);
        }
    }
    //endregion
}