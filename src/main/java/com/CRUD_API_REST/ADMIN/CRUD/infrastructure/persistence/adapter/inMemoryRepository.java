package com.CRUD_API_REST.ADMIN.CRUD.infrastructure.persistence.adapter;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import com.CRUD_API_REST.ADMIN.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.ADMIN.CRUD.domain.ports.out.Crud_RepositoryPort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("inMemoryRepository")
public class inMemoryRepository implements Crud_RepositoryPort{
    private final List<Crud_Entity> entities = new ArrayList<>();
    private Long nextId = 1L;

    //region implemented methods

    //region save entity
    @Override
    public Crud_Entity save_Crud_Entity(String typeBean,Crud_Entity entity) {
        if (entity.getId() == null) {
            try{
                if(entity.getName() == null || entity.getName().isEmpty()) {
                    throw new RuntimeException("El nombre no puede estar vacío.");
                }
                Optional<Crud_Entity> existingEntityOpt = find_Crud_EntityByName(typeBean,entity.getName());
                if (existingEntityOpt.isEmpty()) {
                    LocalDateTime now = LocalDateTime.now();
                    entity.setId(nextId++);
                    entity.setCreated(now);
                    entity.setState(true);
                    entities.add(entity);
                    return entity;
                }
                else {
                    throw new RuntimeException("Error al guardar: el nombre ya existe.");
                }
            } catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            return update_Crud_Entity(typeBean,entity); 
        }
    }
    //endregion

    //region save multiple entities
    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
        List<Crud_Entity> entitiesToSave = entityList.stream()
                .filter(e -> e.getName() != null && !e.getName().isEmpty())
                .collect(Collectors.toList());
        try {
            if (entitiesToSave.isEmpty()) {
                throw new RuntimeException("La lista de entidades a guardar está vacía o no tiene nombres válidos.");
            }
            List<String> existingNames = find_Crud_EntityByNames(typeBean, entitiesToSave)
                .map(list -> list.stream()
                        .map(Crud_Entity::getName)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
                
            List<Crud_Entity> filteredEntities = entitiesToSave.stream()
                .filter(entity -> !existingNames.contains(entity.getName()))
                .collect(Collectors.toList());
            
            if(filteredEntities.isEmpty()) {
                throw new RuntimeException("Ninguna entidad para guardar después de filtrar los nombres existentes en base de datos.");
            }
            for (Crud_Entity entity : filteredEntities) {
                LocalDateTime now = LocalDateTime.now();
                entity.setId(nextId++);
                entity.setCreated(now);
                entity.setState(true);
                entities.add(entity);
            }
            return Optional.of(filteredEntities);
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //endregion

    //region import entities
    @Override
    @Transactional
    public Optional<List<Crud_Entity>> save_import_Crud_Entity(String typeBean,List<Crud_Entity> entityList) {
        try{
            return save_multi_Crud_Entity(typeBean, entityList);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //endregion

    //region find entity by id and name
    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id) {
        return entities.stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst();
    }
    //endregion
    
    //region find entity by name and name
    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        return entities.stream()
                .filter(e -> e.getName() != null && e.getName().equals(name))
                .findFirst();
    }
    //endregion

    //region find entities by names
    @Override
    public Optional<List<Crud_Entity>> find_Crud_EntityByNames(String typeBean, List<Crud_Entity> names) {
        List<Crud_Entity> result = entities.stream()
        .filter(e -> e.getName() != null && 
                names.stream().anyMatch(n -> n.getName().equals(e.getName())))
        .collect(Collectors.toList());

        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }
    //endregion

    //region find all entities
    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        return new ArrayList<>(entities);
    }
    //endregion

    //region update entity
    @Override
    public Crud_Entity update_Crud_Entity(String typeBean,Crud_Entity entity) {
        Optional<Crud_Entity> existingEntityOpt = find_Crud_EntityById(typeBean,entity.getId());
        if (existingEntityOpt.isEmpty()) {
            throw new RuntimeException("Entidad CRUD no encontrada con ID: " + entity.getId());
        }
        Crud_Entity existingEntity = existingEntityOpt.get();
        delete_Crud_Entity_phisical_ById(typeBean,entity.getId());
        entity.setCreated(existingEntity.getCreated());
        entity.setUpdated(LocalDateTime.now());
        entity.setState(existingEntity.getState());
        entities.add(entity);
        return entity;
    }
    //endregion

    //region delete entity phisical and logical
    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean,Long id) {
        entities.removeIf(e -> e.getId() != null && e.getId().equals(id));
    }
    //endregion
    
    //region delete entity logical
    @Override
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean,Crud_Entity entity) {
        Optional<Crud_Entity> existingEntityOpt = find_Crud_EntityById(typeBean,entity.getId());
        if (existingEntityOpt.isEmpty()) {
            throw new RuntimeException("Entidad CRUD no encontrada con ID: " + entity.getId());
        }
        Crud_Entity existingEntity = existingEntityOpt.get();
        delete_Crud_Entity_phisical_ById(typeBean,entity.getId());
        entity.setId(existingEntity.getId());
        entity.setName(existingEntity.getName());
        entity.setEmail(existingEntity.getEmail());
        entity.setCreated(existingEntity.getCreated());
        entity.setUpdated(LocalDateTime.now());
        entity.setState(false);
        entities.add(entity);
        return entity;
    }
    //endregion
    
    //endregion

    //region unimplemented methods
    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JDBC_SP'");
    }
    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JPA_SP'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ById'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ById'");
    }
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll_Crud_entity_JDBC_SP'");
    }
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll_Crud_entity_JPA_SP'");
    }
    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity_JDBC_SP'");
    }
    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity_JPA_SP'");
    }
    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_phisical_JDBC_SP_ById'");
    }
    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_phisical_JPA_SP_ById'");
    }
    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_logical_JDBC_SP_ById'");
    }
    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_logical_JPA_SP_ById'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name){
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ByName'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name){
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ByName'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_multi_Crud_Entity_JDBC_SP'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_multi_Crud_Entity_JPA_SP'");
    }
    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ByNames'");
    }
    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JPA_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ByNames'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_import_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_import_Crud_Entity_JDBC_SP'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_import_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_import_Crud_Entity_JPA_SP'");
    }
    //endregion
}
