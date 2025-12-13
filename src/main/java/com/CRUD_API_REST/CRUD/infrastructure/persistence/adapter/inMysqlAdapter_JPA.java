package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.CRUD.infrastructure.persistence.entity.CrudEntityJpa;
import com.CRUD_API_REST.CRUD.infrastructure.persistence.springdata.crudSpringDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;

import java.sql.Timestamp; 

@Component("inMysqlAdapter_JPA")
public class inMysqlAdapter_JPA implements Crud_RepositoryPort {
    private final crudSpringDataRepository jpaRepository;
    private final EntityManager entityManager; 
    public inMysqlAdapter_JPA(crudSpringDataRepository jpaRepository,EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, Crud_Entity entity) {
        CrudEntityJpa jpaEntity = new CrudEntityJpa(entity); 
        CrudEntityJpa savedJpaEntity = jpaRepository.save(jpaEntity);
        return savedJpaEntity.toDomainEntity(); 
    }
    
    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
       
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_insert_query");
        query.setParameter("p_name", entity.getName());
        query.setParameter("p_email", entity.getEmail());
        query.execute();
        Long generatedId = (Long) query.getOutputParameterValue("p_id");
        Timestamp createdTimestamp = (Timestamp) query.getOutputParameterValue("p_created"); 
        entity.setId(generatedId);
    
        if (createdTimestamp != null) {
            entity.setCreated(createdTimestamp.toLocalDateTime());
        } else {
            entity.setCreated(LocalDateTime.now());
        }
        entity.setState(true);
        return entity;   
    }
    

    @Override
    @Transactional
    public List<Crud_Entity> save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
    
        Set<String> namesToValidate = entityList.stream()
            .map(Crud_Entity::getName)
            .collect(Collectors.toSet());

        List<CrudEntityJpa> existingEntities = jpaRepository.findByNameIn(namesToValidate);
        Set<String> existingNamesInDB = existingEntities.stream()
            .map(CrudEntityJpa::getName)
            .collect(Collectors.toSet());
        
        Set<String> namesAlreadySeenInBatch = new HashSet<>(); 
        List<CrudEntityJpa> entitiesToSave = entityList.stream()        
            .filter(e -> !existingNamesInDB.contains(e.getName())) 
            .filter(e -> namesAlreadySeenInBatch.add(e.getName())) 
            .map(e -> {
                CrudEntityJpa jpa = new CrudEntityJpa(e);
                return jpa;
            })
            .toList();

        List<CrudEntityJpa> savedJpaEntities = jpaRepository.saveAll(entitiesToSave);
        
        return savedJpaEntities.stream()
            .map(CrudEntityJpa::toDomainEntity)
            .toList();
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findById(id);
        return jpaEntityOpt.map(CrudEntityJpa::toDomainEntity);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_find_by_id_query");
        query.setParameter("p_id", id);
        @SuppressWarnings("unchecked")
        List<CrudEntityJpa> results = query.getResultList();

        return results.stream()
            .findFirst()
            .map(CrudEntityJpa::toDomainEntity);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
       Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findByName(name);
        return jpaEntityOpt.map(CrudEntityJpa::toDomainEntity);
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        List<CrudEntityJpa> jpaEntityJpas = jpaRepository.findAll();
        return jpaEntityJpas.stream()
                .map(CrudEntityJpa::toDomainEntity)
                .toList();
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_list_query");
        @SuppressWarnings("unchecked")
        List<CrudEntityJpa> jpaEntityJpas = query.getResultList();
        return jpaEntityJpas.stream()
                .map(CrudEntityJpa::toDomainEntity)
                .toList();
        }

    @Override
    public Crud_Entity update_Crud_Entity(String typeBean, Crud_Entity entity) {
        Long id = entity.getId();
        if (id == null || !jpaRepository.existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        CrudEntityJpa jpaEntity_update = jpaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Entity with ID " + id + " does not exist."));
        jpaEntity_update.setName(entity.getName());
        jpaEntity_update.setEmail(entity.getEmail());
        CrudEntityJpa updatedJpaEntity = jpaRepository.save(jpaEntity_update);
        return updatedJpaEntity.toDomainEntity();
    }

    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_update_query");
        query.setParameter("p_id", entity.getId());
        query.setParameter("p_name", entity.getName());
        query.setParameter("p_email", entity.getEmail());
        query.execute();

        return find_Crud_Entity_JPA_SP_ById(typeBean, entity.getId())
           .orElseThrow(() -> 
               new RuntimeException("Error al verificar la actualización del ID: " + entity.getId())
           );
    }

    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_delete_physical_query");
        query.setParameter("p_id", id);
        query.execute();
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
       Long id = entity.getId();
        if (id == null || !jpaRepository.existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        
        CrudEntityJpa jpaEntity_update = jpaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Entity with ID " + id + " does not exist."));

        jpaEntity_update.setState(entity.getState());
        
        CrudEntityJpa updatedJpaEntity = jpaRepository.save(jpaEntity_update);
        return updatedJpaEntity.toDomainEntity();
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("jbAPI_crud_delete_logical_query");
        query.setParameter("p_id", entity.getId());
        query.execute();

        return find_Crud_Entity_JPA_SP_ById(typeBean, entity.getId())
           .orElseThrow(() -> 
               new RuntimeException("Error al verificar la actualización del ID: " + entity.getId())
           );
    }

    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JDBC_SP'");
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ById'");
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll_Crud_entity_JDBC_SP'");
    }

    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity_JDBC_SP'");
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_phisical_JDBC_SP_ById'");
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_logical_JDBC_SP_ById'");
    }
}
