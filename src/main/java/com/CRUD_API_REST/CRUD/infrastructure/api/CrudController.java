package com.CRUD_API_REST.CRUD.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.service.Crud_Service;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crud-entities")
public class CrudController {
    private final Crud_Service crudService;
    public CrudController(Crud_Service crudService) {
        this.crudService = crudService;
    }
    @PostMapping("{repositoryType}/create")
    public ResponseEntity<Crud_Entity> createEntity(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        com.CRUD_API_REST.CRUD.domain.model.Crud_Entity createdEntity = crudService.save_Crud_Entity(repositoryType,crudEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

    @PostMapping("{repositoryType}/create_JDBC_SP")
    public ResponseEntity<Crud_Entity> createEntity_JDBC_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        com.CRUD_API_REST.CRUD.domain.model.Crud_Entity createdEntity = crudService.save_Crud_Entity_JDBC_SP(repositoryType,crudEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

    @PostMapping("{repositoryType}/create_JPA_SP")
    public ResponseEntity<Crud_Entity> createEntity_JPA_SP(@PathVariable String repositoryType,@RequestBody Crud_Entity crudEntity) {
        com.CRUD_API_REST.CRUD.domain.model.Crud_Entity createdEntity = crudService.save_Crud_Entity_JPA_SP(repositoryType,crudEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }
         
    @GetMapping("find/{repositoryType}/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable String repositoryType,@PathVariable Long id) {
        return crudService.find_Crud_EntityById(repositoryType,id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
    @GetMapping("{repositoryType}/find/all")
    public ResponseEntity<List<Crud_Entity>> getAllEntities(@PathVariable String repositoryType) {
        return ResponseEntity.ok(crudService.findAll_Crud_entity(repositoryType));
    }
    @PutMapping("{repositoryType}/update/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable String repositoryType,@PathVariable Long id, @RequestBody Crud_Entity crudEntity) {
        crudEntity.setId(id);
        Crud_Entity updatedEntity = crudService.update_Crud_Entity(repositoryType,crudEntity);
        return ResponseEntity.ok(updatedEntity);
    }
    @DeleteMapping("{repositoryType}/delete/{id}")
    public ResponseEntity<Void> deleteEntityById(@PathVariable String repositoryType,@PathVariable Long id) {
        crudService.delete_Crud_Entity_ById(repositoryType,id);
        return ResponseEntity.noContent().build();
    }
}