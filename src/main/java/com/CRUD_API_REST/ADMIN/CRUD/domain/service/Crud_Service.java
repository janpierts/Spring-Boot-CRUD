package com.CRUD_API_REST.ADMIN.CRUD.domain.service;

import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.in.Crud_ServicePort;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.COMMON.utils.filesProcessor;
import com.CRUD_API_REST.COMMON.utils.helperEndpoints;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
            if(entity.getName() == null || entity.getName().trim().isEmpty()){
                throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
            }
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
            if(entity.getName() == null || entity.getName().trim().isEmpty()){
                throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
            }
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
            if(entity.getName() == null || entity.getName().trim().isEmpty()){
                throw new RuntimeException("El nombre no puede ser nulo o vacío");
            }
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
        int state = -1;
        try{
            if(entityList == null || entityList.isEmpty()){
                throw new RuntimeException("Ningun dato para procesar"); 
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entityList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            List<Crud_Entity> ErrorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(ErrorEntities == null){
                ErrorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            if(ErrorEntities == null || ErrorEntities.isEmpty()){
                state = 1;
            }
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    ErrorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? ErrorEntities : null);
            }
            else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, entityList);
        }
    }

    @Override
    public Object save_multi_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        int state = -1;
        try{
            if(entityList == null || entityList.isEmpty()){
                throw new RuntimeException("Ningun dato para procesar"); 
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entityList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            List<Crud_Entity> ErrorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(ErrorEntities == null){
                ErrorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            if(ErrorEntities == null || ErrorEntities.isEmpty()){
                state = 1;
            }
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity_JDBC_SP(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    ErrorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? ErrorEntities : null);
            }
            else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, entityList);
        }
    }

    @Override
    public Object save_multi_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList) {
        int state = -1;
        try{
            if(entityList == null || entityList.isEmpty()){
                throw new RuntimeException("Ningun dato para procesar"); 
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entityList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            List<Crud_Entity> ErrorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(ErrorEntities == null){
                ErrorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            if(ErrorEntities == null || ErrorEntities.isEmpty()){
                state = 1;
            }
            List<Crud_Entity> result = repositoryPort.save_multi_Crud_Entity_JPA_SP(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    ErrorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? ErrorEntities : null);
            }
            else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, entityList);
        }
    }
    //endregion

    //region SaveImportEntity
    /*@Param MultipartFile file to decode */
    @Override
    public Object save_import_Crud_Entity(String typeBean, MultipartFile file){
        int state = -1;
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
        List<Crud_Entity> errorEntities = new ArrayList<>();
        try{
            String fileName = file.getOriginalFilename();
            if(fileName == null || fileName.trim().isEmpty()){
                throw new RuntimeException("El nombre del archivo no puede ser nulo o vacío");
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if(!ExtentionsDone.contains(fileExtension)){
                throw new RuntimeException("Extensión de archivo no soportada: " + fileExtension + ". Solo se permiten: " + String.join(", ", ExtentionsDone));
            }
            Function<Row, Crud_Entity> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                Crud_Entity entity = new Crud_Entity();
                entity.setName(name.trim());
                entity.setEmail(email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entitiesFromFileList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            errorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(errorEntities == null || errorEntities.isEmpty()){
                state = 1;
                errorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    errorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? errorEntities : null);
            }else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, errorEntities);
        }
    }

    @Override
    public Object save_import_Crud_Entity_JDBC_SP(String typeBean, MultipartFile file){
        int state = -1;
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
        List<Crud_Entity> errorEntities = new ArrayList<>();
        try{
            String fileName = file.getOriginalFilename();
            if(fileName == null || fileName.trim().isEmpty()){
                throw new RuntimeException("El nombre del archivo no puede ser nulo o vacío");
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if(!ExtentionsDone.contains(fileExtension)){
                throw new RuntimeException("Extensión de archivo no soportada: " + fileExtension + ". Solo se permiten: " + String.join(", ", ExtentionsDone));
            }
            Function<Row, Crud_Entity> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                Crud_Entity entity = new Crud_Entity();
                entity.setName(name.trim());
                entity.setEmail(email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entitiesFromFileList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            errorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(errorEntities == null || errorEntities.isEmpty()){
                state = 1;
                errorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity_JDBC_SP(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    errorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? errorEntities : null);
            }else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, errorEntities);
        }
    }

    @Override
    public Object save_import_Crud_Entity_JPA_SP(String typeBean, MultipartFile file){
        int state = -1;
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
        List<Crud_Entity> errorEntities = new ArrayList<>();
        try{
            String fileName = file.getOriginalFilename();
            if(fileName == null || fileName.trim().isEmpty()){
                throw new RuntimeException("El nombre del archivo no puede ser nulo o vacío");
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if(!ExtentionsDone.contains(fileExtension)){
                throw new RuntimeException("Extensión de archivo no soportada: " + fileExtension + ". Solo se permiten: " + String.join(", ", ExtentionsDone));
            }
            Function<Row, Crud_Entity> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                Crud_Entity entity = new Crud_Entity();
                entity.setName(name.trim());
                entity.setEmail(email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            Map<String, List<Crud_Entity>> splitListMap = helperEndpoints.splitByDuplicates(entitiesFromFileList, Crud_Entity::getName);
            List<Crud_Entity> uniqueEntities = splitListMap.getOrDefault("successBody", List.of());
            errorEntities = splitListMap.getOrDefault("errorBody", new ArrayList<>());
            if(errorEntities == null || errorEntities.isEmpty()){
                state = 1;
                errorEntities = new ArrayList<>();
            }
            Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
            List<Crud_Entity> result = repositoryPort.save_import_Crud_Entity_JPA_SP(typeBean, uniqueEntities).get();
            if(result.size()>0){
                List<Crud_Entity> diffEntities = helperEndpoints.getDifference(uniqueEntities, result, Crud_Entity::getName);
                if(diffEntities!=null && diffEntities.size()>0){
                    state = 0;
                    errorEntities.addAll(diffEntities);
                }else{
                    state = state == -1 ? 0 : state;
                }
                return helperEndpoints.buildResponse(state, state == 0 ? "Algunos registros no se pudieron guardar" : "Todos los registros guardados exitosamente", result, state == 0 ? errorEntities : null);
            }else{
                state = -1;
                throw new RuntimeException("Error al guardar los datos, no se guardó ningun registro");
            }
        }catch(Exception e){
            return helperEndpoints.buildResponse(state, e.getMessage(), null, errorEntities);
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
    @Override
    public Crud_Entity update_Crud_Entity(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity(typeBean, entity);
    }

    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity_JDBC_SP(typeBean, entity);
    }

    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity_JPA_SP(typeBean, entity);
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
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_ById(typeBean, entity);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_JDBC_SP_ById(typeBean, entity);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_JPA_SP_ById(typeBean, entity);
    }
    //endregion
}