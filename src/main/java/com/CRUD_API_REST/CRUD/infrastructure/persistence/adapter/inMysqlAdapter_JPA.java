package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.CRUD.infrastructure.persistence.entity.CrudEntityJpa;
import com.CRUD_API_REST.CRUD.infrastructure.persistence.springdata.crudSpringDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
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

    // 2. Setear los parámetros de ENTRADA (IN)
    // Usamos los datos de la entidad limpia del Dominio que recibimos
    query.setParameter("p_name", entity.getName());
    query.setParameter("p_email", entity.getEmail());

    // 3. Ejecutar el Stored Procedure
    query.execute();

    // 4. Obtener los parámetros de SALIDA (OUT)
    // Estos valores son asignados por la DB (LAST_INSERT_ID(), etc.)
    Long generatedId = (Long) query.getOutputParameterValue("p_id");
    // El parámetro OUT de fecha debe ser casteado a java.sql.Timestamp
    Timestamp createdTimestamp = (Timestamp) query.getOutputParameterValue("p_created"); 

    // 5. Actualizar la Entidad del Dominio con los valores generados
    // NOTA: No necesitamos mapear a CrudEntityJpa aquí; actualizamos y retornamos la entidad limpia
    entity.setId(generatedId);
    
    // Convertir java.sql.Timestamp a LocalDateTime, que usa tu Entidad de Dominio
    if (createdTimestamp != null) {
        entity.setCreated(createdTimestamp.toLocalDateTime());
    } else {
        // Manejo de error o asignación de un valor por defecto si el SP no lo devuelve
        entity.setCreated(LocalDateTime.now());
    }
    
    // 6. Retornar la entidad del Dominio actualizada
    return entity;   
    }
    
    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findById(id);
        return jpaEntityOpt.map(CrudEntityJpa::toDomainEntity);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findById(id);
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
        List<CrudEntityJpa> jpaEntityJpas = jpaRepository.findAll();
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
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        jpaRepository.deleteById(id);
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
