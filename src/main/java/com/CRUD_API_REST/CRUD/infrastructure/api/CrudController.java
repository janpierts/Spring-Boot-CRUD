package com.CRUD_API_REST.CRUD.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.service.Crud_Service;
import com.CRUD_API_REST.CRUD.infrastructure.utils.helperEndpoints;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/crud-entities")
public class CrudController {
    private final Crud_Service crudService;
    public CrudController(Crud_Service crudService) {
        this.crudService = crudService;
    }

    @PostMapping("{repositoryType}/create")
    public ResponseEntity<?> createEntity(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        try {
            Crud_Entity createdEntity = crudService.save_Crud_Entity(repositoryType,crudEntity);
            return ResponseEntity.ok(helperEndpoints.buildResponse(1, "Registro exitoso", createdEntity, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
            .body(helperEndpoints.buildResponse(-1,e.getMessage(), null, crudEntity));
        }
    }

    @PostMapping("{repositoryType}/create_JDBC_SP")
    public ResponseEntity<?> createEntity_JDBC_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        try{
            Crud_Entity createdEntity = crudService.save_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
            return ResponseEntity.ok(helperEndpoints.buildResponse(1, "Registro exitoso", createdEntity, null));
        }catch(Exception e){
            return ResponseEntity.badRequest()
            .body(helperEndpoints.buildResponse(-1,e.getMessage(), null, crudEntity));
        }
    }

    @PostMapping("{repositoryType}/create_JPA_SP")
    public ResponseEntity<?> createEntity_JPA_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        try{
            Crud_Entity createdEntity = crudService.save_Crud_Entity_JPA_SP(repositoryType,crudEntity);
            return ResponseEntity.ok(helperEndpoints.buildResponse(1, "Registro exitoso", createdEntity, null));
        }catch(Exception e){
            return ResponseEntity.badRequest()
            .body(helperEndpoints.buildResponse(-1, e.getMessage(), null, crudEntity));
        }
    }

    @PostMapping("{repositoryType}/create_multiple")
    public ResponseEntity<?> createMultipleEntities(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        Map<String, List<Crud_Entity>> splitList = helperEndpoints.splitByDuplicates(crudEntities, Crud_Entity::getName);
        try{
            int state = -1;
            List<Crud_Entity> diffEntities = null;
            List<Crud_Entity> errorEntities = null;
            if(splitList.getOrDefault("errorBody", List.of()).isEmpty()){
                state = 1;
            }
            List<Crud_Entity> createdEntities = crudService.save_multi_Crud_Entity(repositoryType,splitList.getOrDefault("successBody", List.of()));
            if(createdEntities.size() == 0){
                state = -1;
            }else{
                diffEntities = helperEndpoints.getDifference(splitList.getOrDefault("successBody", List.of()), createdEntities);
                state = diffEntities.isEmpty() && state == 1 ? 1 : 0;
            }
            List<Crud_Entity> duplicaList = splitList.getOrDefault("errorBody", List.of()); 
            errorEntities = duplicaList;
            if(diffEntities != null && !diffEntities.isEmpty()){
                errorEntities.addAll(diffEntities);
            }    
            if(state == 1){
                return ResponseEntity.ok(helperEndpoints.buildResponse(state, "Registros exitosos", createdEntities, null));
            }else if(state == 0){
                return ResponseEntity.ok(helperEndpoints.buildResponse(state, "Algunos registros no se pudieron crear por duplicados y/o vacios", createdEntities, errorEntities));
            }else{
                throw new RuntimeException("Ningun registro fue creado"); 
            }
        }catch(Exception e){
            return ResponseEntity.badRequest()
            .body(helperEndpoints.buildResponse(-1, e.getMessage(), null, crudEntities));
        }
    }

    @PostMapping("{repositoryType}/create_multiple_JDBC_SP")
    public ResponseEntity<List<Crud_Entity>> createMultipleEntities_JDBC_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        List<Crud_Entity> createdEntities = crudService.save_multi_Crud_Entity_JDBC_SP(repositoryType,crudEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping("{repositoryType}/create_multiple_JPA_SP")
    public ResponseEntity<List<Crud_Entity>> createMultipleEntities_JPA_SP(@PathVariable String repositoryType,@RequestBody List<Crud_Entity> crudEntities) {
        List<Crud_Entity> createdEntities = crudService.save_multi_Crud_Entity_JPA_SP(repositoryType,crudEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities);
    }

    @PostMapping(value ="{repositoryType}/import_save",consumes = "multipart/form-data")
    public ResponseEntity<List<Crud_Entity>> importSaveEntities(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Optional<List<Crud_Entity>> createdEntities = crudService.save_import_Crud_Entity(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities.isPresent() ? createdEntities.get() : List.of());
    }

    @PostMapping(value ="{repositoryType}/import_save_JDBC_SP",consumes = "multipart/form-data")
    public ResponseEntity<List<Crud_Entity>> importSaveEntities_JDBC_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Optional<List<Crud_Entity>> createdEntities = crudService.save_import_Crud_Entity_JDBC_SP(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities.isPresent() ? createdEntities.get() : List.of());
    }

    @PostMapping(value ="{repositoryType}/import_save_JPA_SP",consumes = "multipart/form-data")
    public ResponseEntity<List<Crud_Entity>> importSaveEntities_JPA_SP(@PathVariable String repositoryType,@RequestParam("file") MultipartFile file) throws IOException {
        Optional<List<Crud_Entity>> createdEntities = crudService.save_import_Crud_Entity(repositoryType,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntities.isPresent() ? createdEntities.get() : List.of());
    }
         
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

    @PutMapping("{repositoryType}/update/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Crud_Entity updatedEntity = crudService.update_Crud_Entity(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }
    
    @PutMapping("{repositoryType}/update_JDBC_SP/{id}")
    public ResponseEntity<?> updateEntity_JDBC_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Crud_Entity updatedEntity = crudService.update_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }

    @PutMapping("{repositoryType}/update_JPA_SP/{id}")
    public ResponseEntity<?> updateEntity_JPA_SP(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Crud_Entity updatedEntity = crudService.update_Crud_Entity_JPA_SP(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }

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

    @PutMapping("{repositoryType}/delete_logical/{id}")
    public ResponseEntity<?> deleteEntity_logical_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_EntityById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Crud_Entity updatedEntity = crudService.delete_Crud_Entity_logical_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }

    @PutMapping("{repositoryType}/delete_logical_JDBC_SP/{id}")
    public ResponseEntity<?> deleteEntity_logical_JDBC_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_Entity_JDBC_SP_ById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Crud_Entity updatedEntity = crudService.delete_Crud_Entity_logical_JDBC_SP_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }

    @PutMapping("{repositoryType}/delete_logical_JPA_SP/{id}")
    public ResponseEntity<?> deleteEntity_logical_JPA_SP_ById(@PathVariable String repositoryType,@PathVariable Long id) {
        Optional<Crud_Entity> existingEntityOpt = crudService.find_Crud_Entity_JPA_SP_ById(repositoryType, id);
        if (existingEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Crud_Entity crudEntity = existingEntityOpt.get();
        crudEntity.setState(false); 
        Crud_Entity updatedEntity = crudService.delete_Crud_Entity_logical_JPA_SP_ById(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }
}