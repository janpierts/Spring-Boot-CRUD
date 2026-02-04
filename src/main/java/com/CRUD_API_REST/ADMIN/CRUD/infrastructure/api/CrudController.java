package com.CRUD_API_REST.ADMIN.CRUD.infrastructure.api;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.service.Crud_Service;
import com.CRUD_API_REST.COMMON.utils.filesProcessor;
import com.CRUD_API_REST.COMMON.utils.helperEndpoints;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/crud-entities")
public class CrudController {
    private final Crud_Service crudService;
    public CrudController(Crud_Service crudService) {
        this.crudService = crudService;
    }
    /*@Param repositoryType: bean para direccionar logica entre inMemoryRepository, inMysqlAdapter, inMysqlAdapter_JPA  */

    //region create simple entity
    /*@Param Crud_Entity: entidad para agregar  */
    @PostMapping("{repositoryType}/create")
    public ResponseEntity<Object> createEntity(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        String mssg = "";
        int state = 0;
        if(crudEntity.getName() == null || crudEntity.getEmail() == null || crudEntity.getName().isEmpty() || crudEntity.getEmail().isEmpty() || crudEntity.getName().isBlank() || crudEntity.getEmail().isBlank()){
            mssg = "Name and Email are required fields.";
        }
        if(helperEndpoints.isAlphabeticWithSpaces(crudEntity.getName()) == false || helperEndpoints.isValidEmail(crudEntity.getEmail()) == false){
            if(!mssg.isEmpty()) mssg = " | ";
            mssg += " Name(only Alphabethict) or Email format is invalid.";
        }
        if(!mssg.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg, null, crudEntity));
        }
        Object createdEntity = crudService.save_Crud_Entity(repositoryType,crudEntity);
        try{
            Field createdEntityField = createdEntity.getClass().getDeclaredField("state");
            createdEntityField.setAccessible(true);
            state = (int) createdEntityField.get(createdEntity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
    }

    @PostMapping("{repositoryType}/create_JDBC_SP")
    public ResponseEntity<Object> createEntity_JDBC_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        String mssg = "";
        int state = 0;
        if(crudEntity.getName() == null || crudEntity.getEmail() == null || crudEntity.getName().isEmpty() || crudEntity.getEmail().isEmpty() || crudEntity.getName().isBlank() || crudEntity.getEmail().isBlank()){
            mssg = "Name and Email are required fields.";
        }
        if(helperEndpoints.isAlphabeticWithSpaces(crudEntity.getName()) == false || helperEndpoints.isValidEmail(crudEntity.getEmail()) == false){
            if(!mssg.isEmpty()) mssg = " | ";
            mssg += " Name(only Alphabethict) or Email format is invalid.";
        }
        if(!mssg.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg, null, crudEntity));
        }
        Object createdEntity = crudService.save_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
        try{
            Field createdEntityField = createdEntity.getClass().getDeclaredField("state");
            createdEntityField.setAccessible(true);
            state = (int) createdEntityField.get(createdEntity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
    }

    @PostMapping("{repositoryType}/create_JPA_SP")
    public ResponseEntity<Object> createEntity_JPA_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        String mssg = "";
        int state = 0;
        if(crudEntity.getName() == null || crudEntity.getEmail() == null || crudEntity.getName().isEmpty() || crudEntity.getEmail().isEmpty() || crudEntity.getName().isBlank() || crudEntity.getEmail().isBlank()){
            mssg = "Name and Email are required fields.";
        }
        if(helperEndpoints.isAlphabeticWithSpaces(crudEntity.getName()) == false || helperEndpoints.isValidEmail(crudEntity.getEmail()) == false){
            if(!mssg.isEmpty()) mssg = " | ";
            mssg += " Name(only Alphabethict) or Email format is invalid.";
        }
        if(!mssg.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg, null, crudEntity));
        }
        Object createdEntity = crudService.save_Crud_Entity_JPA_SP(repositoryType,crudEntity);
        try{
            Field createdEntityField = createdEntity.getClass().getDeclaredField("state");
            createdEntityField.setAccessible(true);
            state = (int) createdEntityField.get(createdEntity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
    }
    //endregion
    
    //region create multiple entities
    /*@Param List<Crud_Entity> crudEntities: lista de entidades para agregar  */
    @PostMapping("{repositoryType}/create_multiple")
    public ResponseEntity<Object> createMultipleEntities(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        String mssg = "";
        int state = 0;
        List<Crud_Entity> responseCollect = new ArrayList<>();
        if(crudEntities == null || crudEntities.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
        }
        List<Crud_Entity> invalidEntities = crudEntities.stream().filter(entity ->
                entity.getName() == null || entity.getEmail() == null ||
                entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                entity.getName().isBlank() || entity.getEmail().isBlank() ||
                !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                !helperEndpoints.isValidEmail(entity.getEmail())
        ).toList();
        List<Crud_Entity> ErrorEntities = new ArrayList<>();
        if(!invalidEntities.isEmpty()){
            mssg = "Algunos datos como name y email no tienen el formato correcto.";
            if(invalidEntities.size() == crudEntities.size()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
            }
            ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
        }
        List<Crud_Entity> diffEntitiesbv = crudEntities.stream()
                .filter(entity -> !invalidEntities.contains(entity))
                .toList();
        List<Function<Crud_Entity, ?>> myKeys = List.of(
            Crud_Entity::getName,
            Crud_Entity::getEmail
        );
        Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
        ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
        ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
        ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
        List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
        if(uniqueEntities.isEmpty()){
            if(mssg.isEmpty()) mssg += " | ";
            mssg += "Todos los datos ingresados no tienen el formato correcto.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity(repositoryType,uniqueEntities);
        state = (int) createdEntities.getOrDefault("state", 0);
        if(state != -1){
            Object toCollect = createdEntities.get("successBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += "Some entities could not be created due to duplicate entries.";
                List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                ErrorEntities.addAll(lastdiff);
                state = 0;
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
            }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
            }
        } else {
            Object toCollect = createdEntities.get("errorBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(!mssg.isEmpty()) mssg += " | ";
            mssg += (String) createdEntities.getOrDefault("message", "");
            ErrorEntities.addAll(responseCollect);    
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
    }

    @PostMapping("{repositoryType}/create_multiple_JDBC_SP")
    public ResponseEntity<Object> createMultipleEntities_JDBC_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        String mssg = "";
        int state = 0;
        List<Crud_Entity> responseCollect = new ArrayList<>();
        if(crudEntities == null || crudEntities.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
        }
        List<Crud_Entity> invalidEntities = crudEntities.stream().filter(entity ->
                entity.getName() == null || entity.getEmail() == null ||
                entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                entity.getName().isBlank() || entity.getEmail().isBlank() ||
                !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                !helperEndpoints.isValidEmail(entity.getEmail())
        ).toList();
        List<Crud_Entity> ErrorEntities = new ArrayList<>();
        if(!invalidEntities.isEmpty()){
            mssg = "Algunos datos como name y email no tienen el formato correcto.";
            if(invalidEntities.size() == crudEntities.size()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
            }
            ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
        }
        List<Crud_Entity> diffEntitiesbv = crudEntities.stream()
                .filter(entity -> !invalidEntities.contains(entity))
                .toList();
        List<Function<Crud_Entity, ?>> myKeys = List.of(
            Crud_Entity::getName,
            Crud_Entity::getEmail
        );
        Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
        ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
        ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
        ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
        List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
        if(uniqueEntities.isEmpty()){
            if(mssg.isEmpty()) mssg += " | ";
            mssg += "Todos los datos ingresados no tienen el formato correcto.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity_JDBC_SP(repositoryType,uniqueEntities);
        state = (int) createdEntities.getOrDefault("state", 0);
        if(state != -1){
            Object toCollect = createdEntities.get("successBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += "Some entities could not be created due to duplicate entries.";
                List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                ErrorEntities.addAll(lastdiff);
                state = 0;
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
            }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
            }
        } else {
            Object toCollect = createdEntities.get("errorBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(!mssg.isEmpty()) mssg += " | ";
            mssg += (String) createdEntities.getOrDefault("message", "");
            ErrorEntities.addAll(responseCollect);    
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
    }

    @PostMapping("{repositoryType}/create_multiple_JPA_SP")
    public ResponseEntity<Object> createMultipleEntities_JPA_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        String mssg = "";
        int state = 0;
        List<Crud_Entity> responseCollect = new ArrayList<>();
        if(crudEntities == null || crudEntities.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
        }
        List<Crud_Entity> invalidEntities = crudEntities.stream().filter(entity ->
                entity.getName() == null || entity.getEmail() == null ||
                entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                entity.getName().isBlank() || entity.getEmail().isBlank() ||
                !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                !helperEndpoints.isValidEmail(entity.getEmail())
        ).toList();
        List<Crud_Entity> ErrorEntities = new ArrayList<>();
        if(!invalidEntities.isEmpty()){
            mssg = "Algunos datos como name y email no tienen el formato correcto.";
            if(invalidEntities.size() == crudEntities.size()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
            }
            ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
        }
        List<Crud_Entity> diffEntitiesbv = crudEntities.stream()
                .filter(entity -> !invalidEntities.contains(entity))
                .toList();
        List<Function<Crud_Entity, ?>> myKeys = List.of(
            Crud_Entity::getName,
            Crud_Entity::getEmail
        );
        Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
        ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
        ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
        Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
        ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
        List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
        if(uniqueEntities.isEmpty()){
            if(mssg.isEmpty()) mssg += " | ";
            mssg += "Todos los datos ingresados no tienen el formato correcto.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity_JPA_SP(repositoryType,uniqueEntities);
        state = (int) createdEntities.getOrDefault("state", 0);
        if(state != -1){
            Object toCollect = createdEntities.get("successBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += "Some entities could not be created due to duplicate entries.";
                List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                ErrorEntities.addAll(lastdiff);
                state = 0;
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
            }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
            }
        } else {
            Object toCollect = createdEntities.get("errorBody");
            if (toCollect instanceof List) {
                @SuppressWarnings("unchecked")
                List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                responseCollect = new ArrayList<>(castedList);
            }
            if(!mssg.isEmpty()) mssg += " | ";
            mssg += (String) createdEntities.getOrDefault("message", "");
            ErrorEntities.addAll(responseCollect);    
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
    }
    //endregion

    //region import entities from file
    /*@Param MultipartFile: archivo con datos de entidades a importar */
    @PostMapping(value ="{repositoryType}/import_save",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
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
                entity.setEmail(email == null ? null : email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            String mssg = "";
            int state = 0;
            List<Crud_Entity> responseCollect = new ArrayList<>();
            if(entitiesFromFileList == null || entitiesFromFileList.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
            }
            List<Crud_Entity> invalidEntities = entitiesFromFileList.stream().filter(entity ->
                    entity.getName() == null || entity.getEmail() == null ||
                    entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                    entity.getName().isBlank() || entity.getEmail().isBlank() ||
                    !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                    !helperEndpoints.isValidEmail(entity.getEmail())
            ).toList();
            List<Crud_Entity> ErrorEntities = new ArrayList<>();
            if(!invalidEntities.isEmpty()){
                mssg = "Algunos datos como name y email no tienen el formato correcto.";
                if(invalidEntities.size() == entitiesFromFileList.size()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
                }
                ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
            }
            List<Crud_Entity> diffEntitiesbv = entitiesFromFileList.stream()
                    .filter(entity -> !invalidEntities.contains(entity))
                    .toList();
            List<Function<Crud_Entity, ?>> myKeys = List.of(
                Crud_Entity::getName,
                Crud_Entity::getEmail
            );
            Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
            ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
            ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
            ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
            List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
            if(uniqueEntities.isEmpty()){
                if(mssg.isEmpty()) mssg += " | ";
                mssg += "Todos los datos ingresados no tienen el formato correcto.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity(repositoryType,uniqueEntities);
            state = (int) createdEntities.getOrDefault("state", 0);
            if(state != -1){
                Object toCollect = createdEntities.get("successBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                    if(!mssg.isEmpty()) mssg += " | ";
                    mssg += "Some entities could not be created due to duplicate entries.";
                    List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                    ErrorEntities.addAll(lastdiff);
                    state = 0;
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
                }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
                }
            } else {
                Object toCollect = createdEntities.get("errorBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += (String) createdEntities.getOrDefault("message", "");
                ErrorEntities.addAll(responseCollect);    
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage()));
        }
    }

    @PostMapping(value ="{repositoryType}/import_save_JDBC_SP",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities_JDBC_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
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
                entity.setEmail(email == null ? null : email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            String mssg = "";
            int state = 0;
            List<Crud_Entity> responseCollect = new ArrayList<>();
            if(entitiesFromFileList == null || entitiesFromFileList.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
            }
            List<Crud_Entity> invalidEntities = entitiesFromFileList.stream().filter(entity ->
                    entity.getName() == null || entity.getEmail() == null ||
                    entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                    entity.getName().isBlank() || entity.getEmail().isBlank() ||
                    !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                    !helperEndpoints.isValidEmail(entity.getEmail())
            ).toList();
            List<Crud_Entity> ErrorEntities = new ArrayList<>();
            if(!invalidEntities.isEmpty()){
                mssg = "Algunos datos como name y email no tienen el formato correcto.";
                if(invalidEntities.size() == entitiesFromFileList.size()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
                }
                ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
            }
            List<Crud_Entity> diffEntitiesbv = entitiesFromFileList.stream()
                    .filter(entity -> !invalidEntities.contains(entity))
                    .toList();
            List<Function<Crud_Entity, ?>> myKeys = List.of(
                Crud_Entity::getName,
                Crud_Entity::getEmail
            );
            Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
            ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
            ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
            ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
            List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
            if(uniqueEntities.isEmpty()){
                if(mssg.isEmpty()) mssg += " | ";
                mssg += "Todos los datos ingresados no tienen el formato correcto.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity_JDBC_SP(repositoryType,uniqueEntities);
            state = (int) createdEntities.getOrDefault("state", 0);
            if(state != -1){
                Object toCollect = createdEntities.get("successBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                    if(!mssg.isEmpty()) mssg += " | ";
                    mssg += "Some entities could not be created due to duplicate entries.";
                    List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                    ErrorEntities.addAll(lastdiff);
                    state = 0;
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
                }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
                }
            } else {
                Object toCollect = createdEntities.get("errorBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += (String) createdEntities.getOrDefault("message", "");
                ErrorEntities.addAll(responseCollect);    
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage()));
        }
    }

    @PostMapping(value ="{repositoryType}/import_save_JPA_SP",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities_JPA_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        List<String>ExtentionsDone = List.of("csv","xlsx","xls");
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
                entity.setEmail(email == null ? null : email.trim());
                return entity;
            };
            List<Crud_Entity> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }
            String mssg = "";
            int state = 0;
            List<Crud_Entity> responseCollect = new ArrayList<>();
            if(entitiesFromFileList == null || entitiesFromFileList.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "The list of entities to create cannot be null or empty."));
            }
            List<Crud_Entity> invalidEntities = entitiesFromFileList.stream().filter(entity ->
                    entity.getName() == null || entity.getEmail() == null ||
                    entity.getName().isEmpty() || entity.getEmail().isEmpty() ||
                    entity.getName().isBlank() || entity.getEmail().isBlank() ||
                    !helperEndpoints.isAlphabeticWithSpaces(entity.getName()) ||
                    !helperEndpoints.isValidEmail(entity.getEmail())
            ).toList();
            List<Crud_Entity> ErrorEntities = new ArrayList<>();
            if(!invalidEntities.isEmpty()){
                mssg = "Algunos datos como name y email no tienen el formato correcto.";
                if(invalidEntities.size() == entitiesFromFileList.size()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,invalidEntities));
                }
                ErrorEntities = invalidEntities.size()>0 ? new ArrayList<>(invalidEntities) : new ArrayList<>() ;
            }
            List<Crud_Entity> diffEntitiesbv = entitiesFromFileList.stream()
                    .filter(entity -> !invalidEntities.contains(entity))
                    .toList();
            List<Function<Crud_Entity, ?>> myKeys = List.of(
                Crud_Entity::getName,
                Crud_Entity::getEmail
            );
            Map<String, List<Crud_Entity>> duplicatesc = helperEndpoints.splitDuplicatesByMultipleKeys(diffEntitiesbv, myKeys);
            ErrorEntities.addAll(duplicatesc.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapNames = helperEndpoints.splitByDuplicates(duplicatesc.get("successBody"), Crud_Entity::getName);
            ErrorEntities.addAll(splitListMapNames.getOrDefault("errorBody", List.of()));
            Map<String, List<Crud_Entity>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), Crud_Entity::getEmail);
            ErrorEntities.addAll(splitListMapEmails.getOrDefault("errorBody", List.of()));
            List<Crud_Entity> uniqueEntities = splitListMapEmails.getOrDefault("successBody", List.of());
            if(uniqueEntities.isEmpty()){
                if(mssg.isEmpty()) mssg += " | ";
                mssg += "Todos los datos ingresados no tienen el formato correcto.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, mssg,ErrorEntities));
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity_JPA_SP(repositoryType,uniqueEntities);
            state = (int) createdEntities.getOrDefault("state", 0);
            if(state != -1){
                Object toCollect = createdEntities.get("successBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(state == 1 && uniqueEntities.size() != responseCollect.size()){
                    if(!mssg.isEmpty()) mssg += " | ";
                    mssg += "Some entities could not be created due to duplicate entries.";
                    List<Crud_Entity> lastdiff = helperEndpoints.getDifference(uniqueEntities, responseCollect, Crud_Entity::getName);
                    ErrorEntities.addAll(lastdiff);
                    state = 0;
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(state, mssg,responseCollect,ErrorEntities));
                }else if(state == 1 && uniqueEntities.size() == responseCollect.size()){
                    return ResponseEntity.status(HttpStatus.CREATED).body(helperEndpoints.buildResponse(mssg.isEmpty() ? state : 0, mssg.isEmpty() ? "All entities already save successfull" : mssg, responseCollect,ErrorEntities));
                }
            } else {
                Object toCollect = createdEntities.get("errorBody");
                if (toCollect instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Crud_Entity> castedList = (List<Crud_Entity>) toCollect;
                    responseCollect = new ArrayList<>(castedList);
                }
                if(!mssg.isEmpty()) mssg += " | ";
                mssg += (String) createdEntities.getOrDefault("message", "");
                ErrorEntities.addAll(responseCollect);    
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(state, mssg,ErrorEntities));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage()));
        }
    }
    //endregion
    
    //region get entities by id
    /*@Param Long id: identificador único de la entidad a buscar */
    @GetMapping("{repositoryType}/find/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <=0) throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        return crudService.find_Crud_EntityById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("{repositoryType}/find_JDBC_SP/{id}")
    public ResponseEntity<?> getEntity_JDBC_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <=0) throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        return crudService.find_Crud_Entity_JDBC_SP_ById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("{repositoryType}/find_JPA_SP/{id}")
    public ResponseEntity<?> getEntity_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <=0) throw new IllegalArgumentException("El ID no puede ser nulo o menor o igual a cero, ID proporcionado: " + id);
        return crudService.find_Crud_Entity_JPA_SP_ById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    //endregion

    //region get entities by name
    /*@Param String name: nombre de la entidad a buscar */
    @GetMapping("{repositoryType}/find/name/{name}")
    public ResponseEntity<?> getEntityByName(@PathVariable String repositoryType,@PathVariable String name) {
        return crudService.find_Crud_EntityByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @GetMapping("{repositoryType}/find/name_JDBC_SP/{name}")
    public ResponseEntity<?> getEntity_JDBC_SP_ByName(@PathVariable String repositoryType,@PathVariable String name) {
        return crudService.find_Crud_Entity_JDBC_SP_ByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @GetMapping("{repositoryType}/find/name_JPA_SP/{name}")
    public ResponseEntity<?> getEntity_JPA_SP_ByName(@PathVariable String repositoryType,@PathVariable String name) {
        return crudService.find_Crud_Entity_JPA_SP_ByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    //endregion

    //region find entities by names
    /*@Param List<Crud_Entity> names: lista de nombres de entidades a buscar */
    @PostMapping("{repositoryType}/find/names")
    public ResponseEntity<?> getEntityByNames(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        return crudService.find_Crud_EntityByNames(repositoryType,names)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @PostMapping("{repositoryType}/find/names_JDBC_SP")
    public ResponseEntity<?> getEntity_JDBC_SP_ByName(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        return crudService.find_Crud_Entity_JDBC_SP_ByNames(repositoryType,names)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @PostMapping("{repositoryType}/find/names_JPA_SP")
    public ResponseEntity<?> getEntity_JPA_SP_ByNames(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        return crudService.find_Crud_Entity_JPA_SP_ByNames(repositoryType,names)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    //endregion

    //region get all entities
    @GetMapping("{repositoryType}/find/all")
    public ResponseEntity<List<Crud_Entity>> getAllEntities(@PathVariable String repositoryType) {
        return ResponseEntity.ok(crudService.findAll_Crud_entity(repositoryType));
    }

    @GetMapping("{repositoryType}/find/all_JDBC_SP")
    public ResponseEntity<List<Crud_Entity>> getAllEntities_JDBC_SP(@PathVariable String repositoryType) {
        return ResponseEntity.ok(crudService.findAll_Crud_entity_JDBC_SP(repositoryType));
    }

    @GetMapping("{repositoryType}/find/all_JPA_SP")
    public ResponseEntity<List<Crud_Entity>> getAllEntities_JPA_SP(@PathVariable String repositoryType) {
        return ResponseEntity.ok(crudService.findAll_Crud_entity_JPA_SP(repositoryType));
    }
    //endregion

    //region update entity by id
    /*@Param Long id: identificador único de la entidad a actualizar && Crud_Entity crudEntity: entidad con los nuevos valores */
    @PutMapping("{repositoryType}/update/{id}")
    public ResponseEntity<Object> updateEntity(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Object updatedEntity = crudService.update_Crud_Entity(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }
    
    @PutMapping("{repositoryType}/update_JDBC_SP/{id}")
    public ResponseEntity<Object> updateEntity_JDBC_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Object updatedEntity = crudService.update_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }

    @PutMapping("{repositoryType}/update_JPA_SP/{id}")
    public ResponseEntity<Object> updateEntity_JPA_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Object updatedEntity = crudService.update_Crud_Entity_JPA_SP(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }
    //endregion

    //region delete phisical entity by id
    /*@Param Long id: identificador único de la entidad a eliminar */
    @DeleteMapping("{repositoryType}/delete_phisical/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        crudService.delete_Crud_Entity_phisical_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{repositoryType}/delete_phisical_JDBC_SP/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_JDBC_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        crudService.delete_Crud_Entity_phisical_JDBC_SP_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{repositoryType}/delete_phisical_JPA_SP/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        crudService.delete_Crud_Entity_phisical_JPA_SP_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region delete logical entity by id
    /*@Param Long id: identificador único de la entidad a eliminar lógicamente */
    @PutMapping("{repositoryType}/delete_logical/{id}")
    public ResponseEntity<Object> deleteEntity_logical_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        Object updatedEntity = crudService.delete_Crud_Entity_logical_ById(repositoryType,entityToDelete);
        return ResponseEntity.ok(updatedEntity);
        /* Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_EntityById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Object updatedEntity = crudService.delete_Crud_Entity_logical_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity); */
    }

    @PutMapping("{repositoryType}/delete_logical_JDBC_SP/{id}")
    public ResponseEntity<Object> deleteEntity_logical_JDBC_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        Object updatedEntity = crudService.delete_Crud_Entity_logical_JDBC_SP_ById(repositoryType,entityToDelete);
        return ResponseEntity.ok(updatedEntity);
        /* Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_Entity_JDBC_SP_ById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Object updatedEntity = crudService.delete_Crud_Entity_logical_JDBC_SP_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity); */
    }

    @PutMapping("{repositoryType}/delete_logical_JPA_SP/{id}")
    public ResponseEntity<Object> deleteEntity_logical_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        Object updatedEntity = crudService.delete_Crud_Entity_logical_JPA_SP_ById(repositoryType,entityToDelete);
        return ResponseEntity.ok(updatedEntity);
        /* Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_Entity_JPA_SP_ById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Object updatedEntity = crudService.delete_Crud_Entity_logical_JPA_SP_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity); */
    }
    //endregion
}