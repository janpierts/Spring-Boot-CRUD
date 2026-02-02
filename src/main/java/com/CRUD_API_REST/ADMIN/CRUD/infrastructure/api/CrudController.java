package com.CRUD_API_REST.ADMIN.CRUD.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.service.Crud_Service;
import com.CRUD_API_REST.COMMON.utils.helperEndpoints;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

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
        Object createdEntities = crudService.save_multi_Crud_Entity(repositoryType,crudEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping("{repositoryType}/create_multiple_JDBC_SP")
    public ResponseEntity<Object> createMultipleEntities_JDBC_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        Object createdEntities = crudService.save_multi_Crud_Entity_JDBC_SP(repositoryType,crudEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping("{repositoryType}/create_multiple_JPA_SP")
    public ResponseEntity<Object> createMultipleEntities_JPA_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        Object createdEntities = crudService.save_multi_Crud_Entity_JPA_SP(repositoryType,crudEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }
    //endregion

    //region import entities from file
    /*@Param MultipartFile: archivo con datos de entidades a importar */
    @PostMapping(value ="{repositoryType}/import_save",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Object createdEntities = crudService.save_import_Crud_Entity(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping(value ="{repositoryType}/import_save_JDBC_SP",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities_JDBC_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Object createdEntities = crudService.save_import_Crud_Entity_JDBC_SP(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping(value ="{repositoryType}/import_save_JPA_SP",consumes = "multipart/form-data")
    public ResponseEntity<Object> importSaveEntities_JPA_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Object createdEntities = crudService.save_import_Crud_Entity_JPA_SP(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }
    //endregion
    
    //region get entities by id
    /*@Param Long id: identificador único de la entidad a buscar */
    @GetMapping("{repositoryType}/find/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable String repositoryType,@PathVariable Long id) {
        return crudService.find_Crud_EntityById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("{repositoryType}/find_JDBC_SP/{id}")
    public ResponseEntity<?> getEntity_JDBC_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        return crudService.find_Crud_Entity_JDBC_SP_ById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("{repositoryType}/find_JPA_SP/{id}")
    public ResponseEntity<?> getEntity_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
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