package com.rj.MONOLIT.ADMIN.CRUD.infrastructure.api;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.rj.MONOLIT.ADMIN.CRUD.application.dto.InsertMulti_Crud_Model;
import com.rj.MONOLIT.ADMIN.CRUD.application.dto.InsertUpdate_Crud_Model;
import com.rj.MONOLIT.ADMIN.CRUD.application.service.Crud_Service;
import com.rj.MONOLIT.ADMIN.CRUD.domain.model.Crud_Entity;
import com.rj.MONOLIT.COMMON.utils.filesProcessor;
import com.rj.MONOLIT.COMMON.utils.helperEndpoints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public ResponseEntity<Object> createEntity(@PathVariable String repositoryType,@RequestBody InsertUpdate_Crud_Model crudEntity) {
        int state = 0;
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntity = (Map<String,Object>) crudService.save_Crud_Entity(repositoryType,crudEntity);
            state = (int) createdEntity.getOrDefault("state",0);
            return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), null, crudEntity));
        }
    }
    
    @PostMapping("{repositoryType}/create_JDBC_SP")
    public ResponseEntity<Object> createEntity_JDBC_SP(@PathVariable String repositoryType,@RequestBody InsertUpdate_Crud_Model crudEntity) {
        int state = 0;
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntity = (Map<String, Object>)crudService.save_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
            state = (int) createdEntity.getOrDefault("state",0);
            return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), null, crudEntity));
        }
    }

    @PostMapping("{repositoryType}/create_JPA_SP")
    public ResponseEntity<Object> createEntity_JPA_SP(@PathVariable String repositoryType,@RequestBody InsertUpdate_Crud_Model crudEntity) {
        int state = 0;
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntity = (Map<String, Object>)crudService.save_Crud_Entity_JPA_SP(repositoryType,crudEntity);
            state = (int) createdEntity.getOrDefault("state",0);
            return ResponseEntity.status(state == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(createdEntity);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), null, crudEntity));
        }
    }
    //endregion
    
    //region create multiple entities
    /*@Param List<Crud_Entity> crudEntities: lista de entidades para agregar  */
    @PostMapping("{repositoryType}/create_multiple")
    public ResponseEntity<Object> createMultipleEntities(@PathVariable String repositoryType,@RequestBody List<InsertMulti_Crud_Model> crudEntities) {
        List<InsertMulti_Crud_Model> Validation = crudEntities.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));
        /* --> Lista inmutable
        List<InsertMulti_Crud_Model> firstValidation = crudEntities.stream()
        .map(item -> {
            InsertMulti_Crud_Model validatedItem = item.validate();
            if (!validatedItem.isValid()) {
                return validatedItem;
            }
            return validatedItem;
        }).toList();
         */

        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la informacion ingresada.", Validation));            
        }

        List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
            InsertMulti_Crud_Model::name,
            InsertMulti_Crud_Model::email
        );
        int count = 0;
        List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
        Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter(InsertMulti_Crud_Model::isValid).toList(), myKeys);
        if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
            Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }
        
        Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"), InsertMulti_Crud_Model::name);
        if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }

        Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), InsertMulti_Crud_Model::email);
        if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
            Validation.addAll(duplicates);
        }

        Validation.addAll(splitListMapEmails.get("successBody"));
        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato correcto y/ó existen duplicados.",Validation));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity(repositoryType,Validation);
        int state = (int) createdEntities.getOrDefault("state", 0);
        if(state == -1){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping("{repositoryType}/create_multiple_JDBC_SP")
    public ResponseEntity<Object> createMultipleEntities_JDBC_SP(@PathVariable String repositoryType,@RequestBody List<InsertMulti_Crud_Model> crudEntities) {
        List<InsertMulti_Crud_Model> Validation = crudEntities.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));
        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la informacion ingresada.", Validation));            
        }

        List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
            InsertMulti_Crud_Model::name,
            InsertMulti_Crud_Model::email
        );
        int count = 0;
        List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
        Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter(InsertMulti_Crud_Model::isValid).toList(), myKeys);
        if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
            Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }
        
        Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"), InsertMulti_Crud_Model::name);
        if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }

        Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), InsertMulti_Crud_Model::email);
        if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
            Validation.addAll(duplicates);
        }

        Validation.addAll(splitListMapEmails.get("successBody"));
        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato correcto y/ó existen duplicados.",Validation));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity_JDBC_SP(repositoryType,Validation);
        int state = (int) createdEntities.getOrDefault("state", 0);
        if(state == -1){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping("{repositoryType}/create_multiple_JPA_SP")
    public ResponseEntity<Object> createMultipleEntities_JPA_SP(@PathVariable String repositoryType,@RequestBody List<InsertMulti_Crud_Model> crudEntities) {
        List<InsertMulti_Crud_Model> Validation = crudEntities.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));
        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la informacion ingresada.", Validation));            
        }

        List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
            InsertMulti_Crud_Model::name,
            InsertMulti_Crud_Model::email
        );
        int count = 0;
        List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
        Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter(InsertMulti_Crud_Model::isValid).toList(), myKeys);
        if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
            Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }
        
        Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"), InsertMulti_Crud_Model::name);
        if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
            Validation.addAll(duplicates);
            count++;
        }

        Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"), InsertMulti_Crud_Model::email);
        if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
            if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
            duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
            Validation.addAll(duplicates);
        }

        Validation.addAll(splitListMapEmails.get("successBody"));
        if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato correcto y/ó existen duplicados.",Validation));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_multi_Crud_Entity_JPA_SP(repositoryType,Validation);
        int state = (int) createdEntities.getOrDefault("state", 0);
        if(state == -1){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
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
            Function<Row, InsertMulti_Crud_Model> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                InsertMulti_Crud_Model entity = new InsertMulti_Crud_Model(name, email, null, null);
                return entity;
            };
            List<InsertMulti_Crud_Model> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }

            List<InsertMulti_Crud_Model> Validation = entitiesFromFileList.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));

            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la  informacion ingresada.", Validation));            
            }

            List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
                InsertMulti_Crud_Model::name,
                InsertMulti_Crud_Model::email
            );
            int count = 0;
            List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
            Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter (InsertMulti_Crud_Model::isValid).toList(), myKeys);
            if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
                Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"),  InsertMulti_Crud_Model::name);
            if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"),  InsertMulti_Crud_Model::email);
            if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
                Validation.addAll(duplicates);
            }

            Validation.addAll(splitListMapEmails.get("successBody"));
            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato    correcto y/ó existen duplicados.",Validation));
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity(repositoryType,Validation);
            int state = (int) createdEntities.getOrDefault("state", 0);
            if(state == -1){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
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
            Function<Row, InsertMulti_Crud_Model> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                InsertMulti_Crud_Model entity = new InsertMulti_Crud_Model(name, email, null, null);
                return entity;
            };
            List<InsertMulti_Crud_Model> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }

            List<InsertMulti_Crud_Model> Validation = entitiesFromFileList.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));
            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la  informacion ingresada.", Validation));            
            }

            List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
                InsertMulti_Crud_Model::name,
                InsertMulti_Crud_Model::email
            );
            int count = 0;
            List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
            Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter (InsertMulti_Crud_Model::isValid).toList(), myKeys);
            if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
                Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"),  InsertMulti_Crud_Model::name);
            if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"),  InsertMulti_Crud_Model::email);
            if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
                Validation.addAll(duplicates);
            }

            Validation.addAll(splitListMapEmails.get("successBody"));
            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato    correcto y/ó existen duplicados.",Validation));
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity_JDBC_SP(repositoryType,Validation);
            int state = (int) createdEntities.getOrDefault("state", 0);
            if(state == -1){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
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
            Function<Row, InsertMulti_Crud_Model> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0));
                String email = filesProcessor.getCellValueAsString(row.getCell(1));
                if(name == null || name.trim().isEmpty()){
                    return null;
                }
                InsertMulti_Crud_Model entity = new InsertMulti_Crud_Model(name, email, null, null);
                return entity;
            };
            List<InsertMulti_Crud_Model> entitiesFromFileList = filesProcessor.excelToEntities(file, rowMapper);
            if (entitiesFromFileList.isEmpty()) {
                throw new RuntimeException("Error al procesar el archivo Excel: El archivo Excel está vacío o no tiene el formato correcto");
            }

            List<InsertMulti_Crud_Model> Validation = entitiesFromFileList.stream().map(InsertMulti_Crud_Model::validate).collect(Collectors.toCollection(ArrayList::new));
            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Los datos ingresados tienen errores valide la  informacion ingresada.", Validation));            
            }

            List<Function<InsertMulti_Crud_Model, ?>> myKeys = List.of(
                InsertMulti_Crud_Model::name,
                InsertMulti_Crud_Model::email
            );
            int count = 0;
            List<InsertMulti_Crud_Model> duplicates = new ArrayList<>();
            Map<String, List<InsertMulti_Crud_Model>> splitMapValidation = helperEndpoints.splitDuplicatesByMultipleKeys(Validation.stream().filter (InsertMulti_Crud_Model::isValid).toList(), myKeys);
            if(splitMapValidation.getOrDefault("errorBody", List.of()).size() > 0){
                Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitMapValidation.get("errorBody").stream().map(item -> item.addMessage("Registro duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapNames = helperEndpoints.splitByDuplicates(splitMapValidation.get("successBody"),  InsertMulti_Crud_Model::name);
            if(splitListMapNames.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapNames.get("errorBody").stream().map(item -> item.addMessage("Nombre duplicado")).toList());
                Validation.addAll(duplicates);
                count++;
            }

            Map<String, List<InsertMulti_Crud_Model>> splitListMapEmails = helperEndpoints.splitByDuplicates(splitListMapNames.get("successBody"),  InsertMulti_Crud_Model::email);
            if(splitListMapEmails.getOrDefault("errorBody", List.of()).size() > 0){
                if(count == 0)Validation.removeIf(InsertMulti_Crud_Model::isValid);
                duplicates.addAll(splitListMapEmails.get("errorBody").stream().map(item -> item.addMessage("Email duplicado")).toList());
                Validation.addAll(duplicates);
            }

            Validation.addAll(splitListMapEmails.get("successBody"));
            if(Validation.stream().filter(InsertMulti_Crud_Model::isValid).count()==0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Todos los datos ingresados no tienen el formato    correcto y/ó existen duplicados.",Validation));
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> createdEntities = (Map<String, Object>)crudService.save_import_Crud_Entity_JPA_SP(repositoryType,Validation);
            int state = (int) createdEntities.getOrDefault("state", 0);
            if(state == -1){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createdEntities);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
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
        name = helperEndpoints.sanitizeForSearch(name.trim());
        return crudService.find_Crud_EntityByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @GetMapping("{repositoryType}/find/name_JDBC_SP/{name}")
    public ResponseEntity<?> getEntity_JDBC_SP_ByName(@PathVariable String repositoryType,@PathVariable String name) {
        name = helperEndpoints.sanitizeForSearch(name.trim());
        return crudService.find_Crud_Entity_JDBC_SP_ByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @GetMapping("{repositoryType}/find/name_JPA_SP/{name}")
    public ResponseEntity<?> getEntity_JPA_SP_ByName(@PathVariable String repositoryType,@PathVariable String name) {
        name = helperEndpoints.sanitizeForSearch(name.trim());
        return crudService.find_Crud_Entity_JPA_SP_ByName(repositoryType,name)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    //endregion

    //region find entities by names
    /*@Param List<Crud_Entity> names: lista de nombres de entidades a buscar */
    @PostMapping("{repositoryType}/find/names")
    public ResponseEntity<?> getEntityByNames(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        names = names.stream()
            .filter(entity -> entity.getName() != null)
            .map(entity -> {
                String nameValString = helperEndpoints.sanitizeForSearch(entity.getName().trim());
                entity.setName(nameValString);
                return entity;
            })
            .collect(Collectors.toList());
        return crudService.find_Crud_EntityByNames(repositoryType,names)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @PostMapping("{repositoryType}/find/names_JDBC_SP")
    public ResponseEntity<?> getEntity_JDBC_SP_ByNames(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        names = names.stream()
            .filter(entity -> entity.getName() != null)
            .map(entity -> {
                String nameValString = helperEndpoints.sanitizeForSearch(entity.getName().trim());
                entity.setName(nameValString);
                return entity;
            })
            .collect(Collectors.toList());
        return crudService.find_Crud_Entity_JDBC_SP_ByNames(repositoryType,names)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @PostMapping("{repositoryType}/find/names_JPA_SP")
    public ResponseEntity<?> getEntity_JPA_SP_ByNames(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> names) {
        names = names.stream()
            .filter(entity -> entity.getName() != null)
            .map(entity -> {
                String nameValString = helperEndpoints.sanitizeForSearch(entity.getName().trim());
                entity.setName(nameValString);
                return entity;
            })
            .collect(Collectors.toList());
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
    public ResponseEntity<Object> updateEntity(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody InsertUpdate_Crud_Model crudEntity) {
        if(id == null || id <= 0 || id != crudEntity.id() ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "El Id no es permitido: "+id, crudEntity));
        }
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String,Object> resultv = (Map<String,Object>)crudService.update_Crud_Entity(repositoryType,crudEntity);
            int state = (int)resultv.getOrDefault("state", 0);
            return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resultv);

        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), crudEntity));
        }        
    }
    
    @PutMapping("{repositoryType}/update_JDBC_SP/{id}")
    public ResponseEntity<Object> updateEntity_JDBC_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody InsertUpdate_Crud_Model crudEntity) {
        if(id == null || id <= 0 || id != crudEntity.id()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "El Id no es permitido: "+id, crudEntity));
        }
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String,Object> resultv = (Map<String,Object>)crudService.update_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
            int state = (int)resultv.getOrDefault("state", 0);
            return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resultv);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), crudEntity));
        }
    }

    @PutMapping("{repositoryType}/update_JPA_SP/{id}")
    public ResponseEntity<Object> updateEntity_JPA_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody InsertUpdate_Crud_Model crudEntity) {
        if(id == null || id <= 0 || id != crudEntity.id()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "El Id no es permitido: "+id, crudEntity));
        }
        try{
            crudEntity.validate();
            @SuppressWarnings("unchecked")
            Map<String,Object> resultv = (Map<String,Object>)crudService.update_Crud_Entity_JPA_SP(repositoryType,crudEntity);
            int state = (int)resultv.getOrDefault("state", 0);
            return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(resultv);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, e.getMessage(), crudEntity));
        }
    }
    //endregion

    //region delete phisical entity by id
    /*@Param Long id: identificador único de la entidad a eliminar */
    @DeleteMapping("{repositoryType}/delete_phisical/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().build();
        crudService.delete_Crud_Entity_phisical_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{repositoryType}/delete_phisical_JDBC_SP/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_JDBC_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().build();
        crudService.delete_Crud_Entity_phisical_JDBC_SP_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{repositoryType}/delete_phisical_JPA_SP/{id}")
    public ResponseEntity<Void> deleteEntity_phisical_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null ||id <= 0) return ResponseEntity.badRequest().build();
        crudService.delete_Crud_Entity_phisical_JPA_SP_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region delete logical entity by id
    /*@Param Long id: identificador único de la entidad a eliminar lógicamente */
    @PutMapping("{repositoryType}/delete_logical/{id}")
    public ResponseEntity<Object> deleteEntity_logical_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Id a eliminar es incorrecto, Id: "+id));
        }
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        @SuppressWarnings("unchecked")
        Map<String,Object> updatedEntity = (Map<String,Object>)crudService.delete_Crud_Entity_logical_ById(repositoryType,entityToDelete);
        int state = (int)updatedEntity.getOrDefault("state", 0);
        return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(updatedEntity);
    }

    @PutMapping("{repositoryType}/delete_logical_JDBC_SP/{id}")
    public ResponseEntity<Object> deleteEntity_logical_JDBC_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Id a eliminar es incorrecto, Id: "+id));
        }
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        @SuppressWarnings("unchecked")
        Map<String,Object> updatedEntity = (Map<String,Object>)crudService.delete_Crud_Entity_logical_JDBC_SP_ById(repositoryType,entityToDelete);
        int state = (int)updatedEntity.getOrDefault("state", 0);
        return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(updatedEntity);
    }

    @PutMapping("{repositoryType}/delete_logical_JPA_SP/{id}")
    public ResponseEntity<Object> deleteEntity_logical_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        if(id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(helperEndpoints.buildResponse(-1, "Id a eliminar es incorrecto, Id: "+id));
        }
        Crud_Entity entityToDelete = new Crud_Entity();
        entityToDelete.setId(id);
        @SuppressWarnings("unchecked")
        Map<String,Object> updatedEntity = (Map<String,Object>)crudService.delete_Crud_Entity_logical_JPA_SP_ById(repositoryType,entityToDelete);
        int state = (int)updatedEntity.getOrDefault("state", 0);
        return ResponseEntity.status(state == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(updatedEntity);        
    }
    //endregion
}