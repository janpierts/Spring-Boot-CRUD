package com.rj.MONOLIT.ADMIN.CRUD.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.MONOLIT.ADMIN.CRUD.application.dto.InsertMulti_Crud_Model;
import com.rj.MONOLIT.ADMIN.CRUD.application.dto.InsertUpdate_Crud_Model;
import com.rj.MONOLIT.ADMIN.CRUD.application.ports.out.Crud_RepositoryPort;
import com.rj.MONOLIT.ADMIN.CRUD.domain.model.Crud_Entity;
import com.rj.MONOLIT.ADMIN.CRUD.domain.readmodel.Crud_multiReadModel;
import com.rj.MONOLIT.ADMIN.CRUD.infrastructure.persistence.entity.CrudEntityJpa;
import com.rj.MONOLIT.ADMIN.CRUD.infrastructure.persistence.springdata.crudSpringDataRepository;
import com.rj.MONOLIT.COMMON.utils.settings.JPAConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import java.sql.Timestamp; 

@Component("inMysqlAdapter_JPA")
public class inMysqlAdapter_JPA implements Crud_RepositoryPort {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;
    private final crudSpringDataRepository jpaRepository;
    private final JPAConfig jpaConfig;
    private volatile EntityManager entityManager; 
    
    public inMysqlAdapter_JPA(crudSpringDataRepository jpaRepository,JPAConfig jpaConfig) {
        this.jpaRepository = jpaRepository;
        this.jpaConfig = jpaConfig;
        // this.entityManager = jpaConfig.entityManager();
    }

    private EntityManager getDynamicEntityManager() {
        if (this.entityManager == null) {
            synchronized (this) {
                if (this.entityManager == null) {
                    List<String> packagesToScan = List.of("com.rj.MONOLIT.ADMIN.CRUD.infrastructure.persistence.entity");
                    this.entityManager = jpaConfig.buildEntityManager(datasourceUrl, datasourceUsername, datasourcePassword, datasourceDriverClassName, packagesToScan);
                }
            }
        }
        return this.entityManager;
    }
    //region create entity
    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, InsertUpdate_Crud_Model entity) {
        Optional<Crud_Entity> existName = find_Crud_EntityByName(typeBean, entity.name());
        if(existName.isPresent()){
            throw new RuntimeException("El nombre '"+entity.name()+"' ya existe en la base de datos.");
        }
        EntityManager em = getDynamicEntityManager();
        Crud_Entity newEntity = new Crud_Entity();
        try{
            newEntity.setName(entity.name());
            newEntity.setEmail(entity.email());
            em.getTransaction().begin();
            CrudEntityJpa jpaEntity = new CrudEntityJpa(newEntity); 
            /* This bellow comment is when use repository SpringData JPA and this only use unique principal datasource  */
            //CrudEntityJpa savedJpaEntity = jpaRepository.save(jpaEntity);
            em.persist(jpaEntity);
            em.flush();
            em.getTransaction().commit();

            return jpaEntity.toDomainEntity(); 
        }catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Error al guardar la entidad CRUD: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, InsertUpdate_Crud_Model entity) {
        Optional<Crud_Entity> existName = find_Crud_Entity_JPA_SP_ByName(typeBean, entity.name());
        if(existName.isPresent()){
            throw new RuntimeException("El nombre '"+entity.name()+"' ya existe en la base de datos.");
        }
        Crud_Entity newEntity = new Crud_Entity();
        newEntity.setName(entity.name());
        newEntity.setEmail(entity.email());
        try{
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_insert_query");
            query.setParameter("p_name", entity.name());
            query.setParameter("p_email", entity.email());
            query.execute();
            Long generatedId = (Long) query.getOutputParameterValue("p_id");
            Timestamp createdTimestamp = (Timestamp) query.getOutputParameterValue("p_created"); 
            newEntity.setId(generatedId);
        
            if (createdTimestamp != null) {
                newEntity.setCreated(createdTimestamp.toLocalDateTime());
            } else {
                newEntity.setCreated(LocalDateTime.now());
            }
            newEntity.setState(true);
            return newEntity;   
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //endregion
    
    //region create multiple entities
    @Override
    @Transactional
    public List<Crud_multiReadModel> save_multi_Crud_Entity(String typeBean, List<InsertMulti_Crud_Model> entityList) {
        List<String> namesToValidate = entityList.stream()
            .filter(InsertMulti_Crud_Model::isValid)
            .map(InsertMulti_Crud_Model::name)
            .collect(Collectors.toList());
        List<CrudEntityJpa> existingEntities = new ArrayList<>(jpaRepository.findByNameIn(namesToValidate));
        Set<String> existingNames = existingEntities.stream()
            .map(CrudEntityJpa::getName)
            .collect(Collectors.toSet());
        List<CrudEntityJpa> filteredEntities = entityList.stream()
            .filter(e -> e.isValid() &&  !existingNames.contains(e.name()))
            .map(item -> new CrudEntityJpa(new Crud_Entity(null, item.name(), item.email(), null, null, true)))
            .collect(Collectors.toList());
        List<Crud_multiReadModel> readmodel = new ArrayList<>();
        try{
            List<CrudEntityJpa> savedJpaEntities = jpaRepository.saveAll(filteredEntities);
            if(!savedJpaEntities.isEmpty()){
                readmodel.addAll(savedJpaEntities.stream()
                    .map(k -> new Crud_multiReadModel(k.getId(), k.getName(), k.getEmail(), k.getCreated(), k.getUpdated(), k.getState(), true, "Registro insertado correctamente"))
                    .collect(Collectors.toList())
                );
                if(savedJpaEntities.size()!=filteredEntities.size()){
                    List<String> ListResult = savedJpaEntities.stream().map(CrudEntityJpa::getName).collect(Collectors.toList());
                    readmodel.addAll(filteredEntities.stream()
                        .filter(k -> !ListResult.contains(k.getName()))
                        .map(item -> new Crud_multiReadModel(null, item.getName(), item.getEmail(), null, null, null, false, "Hubo un error con el registro"))
                        .collect(Collectors.toList())
                    );
                }
            }else{
                readmodel.addAll(filteredEntities.stream()
                    .map(k -> new Crud_multiReadModel(null, k.getName(), k.getEmail(), null, null, null, false, "Hubo un error con el registro, a nivel base de datos intente nuevamente"))
                    .collect(Collectors.toList())
                );
            }
            readmodel.addAll(existingEntities.stream()
                .map(k -> new Crud_multiReadModel(k.getId(), k.getName(), k.getEmail(), k.getCreated(), k.getUpdated(), k.getState(), false, "Registro ya existe en la BD"))
                .collect(Collectors.toList())
            );
            readmodel.addAll(entityList.stream()
                .filter(k -> !k.isValid())
                .map(item -> new Crud_multiReadModel(null, item.name(), item.email(), null, null, null, false, item.message()))
                .collect(Collectors.toList())
            );
            
           return readmodel;
        }catch(Exception e){
            throw new RuntimeException("Error al insertar las entidades CRUD: " + e.getMessage());
        }
    }

    @Override
    public List<Crud_multiReadModel> save_multi_Crud_Entity_JPA_SP(String typeBean, List<InsertMulti_Crud_Model> entityList) {
        List<Crud_Entity> SearchList = entityList.stream()
            .filter(InsertMulti_Crud_Model::isValid)
            .map(k -> new Crud_Entity(null,k.name(),null,null,null,null))
            .collect(Collectors.toList()); 
        List<Crud_Entity> alreadyNames = find_Crud_Entity_JPA_SP_ByNames(typeBean, SearchList).orElseGet(ArrayList::new);
        Set<String> existingNames = alreadyNames.stream()
            .map(Crud_Entity::getName)
            .collect(Collectors.toSet());
        List<Crud_Entity> filteredEntities = SearchList.stream()
            .filter(e -> !existingNames.contains(e.getName()))
            .collect(Collectors.toList());
        List<Crud_multiReadModel> readmodel = new ArrayList<>();
        try {
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_insert_multi_query");
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonObject = objectMapper.writeValueAsString(filteredEntities);
            query.setParameter("p_data_json", jsonObject);
            query.execute();
            List<Crud_Entity> result = find_Crud_Entity_JPA_SP_ByNames(typeBean, filteredEntities).orElseGet(ArrayList::new);
            if(!result.isEmpty()){
                readmodel.addAll(result.stream()
                    .map(k -> new Crud_multiReadModel(k.getId(), k.getName(), k.getEmail(), k.getCreated(), k.getUpdated(), k.getState(), true, "Registro insertado correctamente"))
                    .collect(Collectors.toList())
                );
                if(result.size() != filteredEntities.size()){
                    List<String> ListResult = result.stream().map(Crud_Entity::getName).collect(Collectors.toList());
                    readmodel.addAll(filteredEntities.stream()
                        .filter(k -> !ListResult.contains(k.getName()))
                        .map(item -> new Crud_multiReadModel(null, item.getName(), item.getEmail(), null, null, null, false, "Hubo un error con el registro"))
                        .collect(Collectors.toList())
                    );
                }
            }else{
                readmodel.addAll(filteredEntities.stream()
                    .map(k -> new Crud_multiReadModel(null, k.getName(), k.getEmail(), null, null, null, false, "Hubo un error con el registro, a nivel base de datos intente nuevamente"))
                    .collect(Collectors.toList())
                );
            }
            readmodel.addAll(alreadyNames.stream()
                .map(k -> new Crud_multiReadModel(k.getId(), k.getName(), k.getEmail(), k.getCreated(), k.getUpdated(), k.getState(), false, "Registro ya existe en la BD"))
                .collect(Collectors.toList())
            );
            readmodel.addAll(entityList.stream()
                .filter(k -> !k.isValid())
                .map(item -> new Crud_multiReadModel(null, item.name(), item.email(), null, null, null, false, item.message()))
                .collect(Collectors.toList())
            );

            return readmodel;
        }catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar lista a JSON", e);
        }catch (Exception e) {
            throw new RuntimeException("Error al buscar las entidades CRUD por nombres: " + e.getMessage());
        }
    }
    //endregion

    //region save import entities
    @Override
    public List<Crud_multiReadModel> save_import_Crud_Entity(String typeBean,List<InsertMulti_Crud_Model>entityList) {
        return save_multi_Crud_Entity(typeBean, entityList);
    }

    @Override
    public List<Crud_multiReadModel> save_import_Crud_Entity_JPA_SP(String typeBean, List<InsertMulti_Crud_Model>entityList) {
        return save_multi_Crud_Entity_JPA_SP(typeBean, entityList);
    }
    //endregion

    //region find entity by id and name(s)
    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        try{
            Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findById(id);
            if(!jpaEntityOpt.isPresent()) throw new RuntimeException("El identificador ingresado no existe");
            return jpaEntityOpt.map(CrudEntityJpa::toDomainEntity);
        }catch(Exception e) {
            throw new RuntimeException("Error: "+e.getMessage());
        }
        
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        try{
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_find_by_id_query");
            query.setParameter("p_id", id);
            @SuppressWarnings("unchecked")
            List<CrudEntityJpa> results = query.getResultList();
            if(results.isEmpty()) throw new RuntimeException("El identificador ingresado no existe");
            return results.stream()
                .findFirst()
                .map(CrudEntityJpa::toDomainEntity);
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        try{
            EntityManager em = getDynamicEntityManager();

            var cb = em.getCriteriaBuilder();
            var query = cb.createQuery(CrudEntityJpa.class);
            var root = query.from(CrudEntityJpa.class);

            query.select(root).where(cb.and(
                cb.equal(root.get("name"), name)
            ));
            Optional<CrudEntityJpa> jpaEntityOpt = em.createQuery(query).getResultStream().findFirst();
            /* This bellow comment is when use repository SpringData JPA and this only use unique principal datasource  */
            //Optional<CrudEntityJpa> jpaEntityOpt = jpaRepository.findByName(name);
            return jpaEntityOpt.map(CrudEntityJpa::toDomainEntity);
        }catch(Exception e) {
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name) {
        try{
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_find_by_name_query");
            query.setParameter("p_name", name);
            @SuppressWarnings("unchecked")
            List<CrudEntityJpa> results = query.getResultList();
            return results.stream()
                .findFirst()
                .map(CrudEntityJpa::toDomainEntity);
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_EntityByNames(String typeBean, List<Crud_Entity> names) {
        try{
            Set<String> namesToValidate = names.stream()
                .map(Crud_Entity::getName)
                .collect(Collectors.toSet());
    
            List<CrudEntityJpa> existingEntities = jpaRepository.findByNameIn(namesToValidate);
            List<Crud_Entity> domainEntities = existingEntities.stream()
            .map(CrudEntityJpa::toDomainEntity)
            .toList();
    
            return domainEntities.isEmpty() ? Optional.empty() : Optional.of(domainEntities);
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JPA_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        try {
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_list_byNames_query");
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonObject = objectMapper.writeValueAsString(names);
            query.setParameter("p_data_json", jsonObject);
            @SuppressWarnings("unchecked")
            List<CrudEntityJpa> resultList = query.getResultList();
            List<Crud_Entity> domainEntity = resultList.stream()
                .map(CrudEntityJpa::toDomainEntity)
                .toList();
            return Optional.of(domainEntity);
        }catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar lista a JSON", e);
        }catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    //endregion

    //region find all entities
    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        try{
            List<CrudEntityJpa> jpaEntityJpas = jpaRepository.findAll();
            return jpaEntityJpas.stream()
                    .map(CrudEntityJpa::toDomainEntity)
                    .toList();
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        try{
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_list_query");
            @SuppressWarnings("unchecked")
            List<CrudEntityJpa> jpaEntityJpas = query.getResultList();
            return jpaEntityJpas.stream()
                    .map(CrudEntityJpa::toDomainEntity)
                    .toList();
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }
    //endregion
    
    //region update entity
    @Override
    public Crud_Entity update_Crud_Entity(String typeBean, InsertUpdate_Crud_Model entity) {
        try{
            Long id = entity.id();
            CrudEntityJpa jpaEntity_update = jpaRepository.findById(id).filter(a -> Boolean.TRUE.equals(a.getState())).orElseThrow(() -> new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+id));
            jpaEntity_update.setName(entity.name());
            jpaEntity_update.setEmail(entity.email());
            CrudEntityJpa updatedJpaEntity = jpaRepository.save(jpaEntity_update);
            return updatedJpaEntity.toDomainEntity();
        }catch(Exception e) {
            throw new RuntimeException("Error: "+e.getMessage());
        }
        
    }

    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, InsertUpdate_Crud_Model entity) {
        try{
            Optional<Crud_Entity> exisEntity = find_Crud_Entity_JPA_SP_ById(typeBean, entity.id()).filter(a -> Boolean.TRUE.equals(a.getState()));
            if(!exisEntity.isPresent()){
                throw new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+entity.id());
            }
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_update_query");
            query.setParameter("p_id", entity.id());
            query.setParameter("p_name", entity.name());
            query.setParameter("p_email", entity.email());
            query.execute();
    
            em.flush();
            em.clear();
            return find_Crud_Entity_JPA_SP_ById(typeBean, entity.id())
               .orElseThrow(() -> 
                   new RuntimeException("Error al verificar la actualización del ID: " + entity.id())
               );

        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }
    //endregion

    //region physical delete
    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        try{
            jpaRepository.deleteById(id);
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        try{
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_delete_physical_query");
            query.setParameter("p_id", id);
            query.execute();
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }
    //endregion

    //region logical delete
    @Override
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
        try{
            Long id = entity.getId();
            if (!jpaRepository.existsById(id)) {
                throw new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+entity.getId());
            }
            CrudEntityJpa jpaEntity_update = jpaRepository.findById(id).filter(a -> Boolean.TRUE.equals(a.getState())).orElseThrow(() -> new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+entity.getId()));
            jpaEntity_update.setState(false);
            CrudEntityJpa updatedJpaEntity = jpaRepository.save(jpaEntity_update);
            return updatedJpaEntity.toDomainEntity();

        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        try{
            Optional<Crud_Entity> exisEntity = find_Crud_Entity_JPA_SP_ById(typeBean, entity.getId()).filter(a -> Boolean.TRUE.equals(a.getState()));
            if(!exisEntity.isPresent()){
                throw new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+entity.getId());
            }
            EntityManager em = getDynamicEntityManager();
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("jbAPI_crud_delete_logical_query");
            query.setParameter("p_id", entity.getId());
            query.execute();
    
            return find_Crud_Entity_JPA_SP_ById(typeBean, entity.getId())
               .orElseThrow(() -> 
                   new RuntimeException("Error al verificar la actualización del ID: " + entity.getId())
               );
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }
    //endregion

    //region unimplemented methods
    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean, InsertUpdate_Crud_Model entity) {
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
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean, InsertUpdate_Crud_Model entity) {
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
    @Override
    public List<Crud_multiReadModel> save_multi_Crud_Entity_JDBC_SP(String typeBean, List<InsertMulti_Crud_Model> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_multi_Crud_Entity_JDBC_SP'");
    }
    @Override
    public List<Crud_multiReadModel> save_import_Crud_Entity_JDBC_SP(String typeBean, List<InsertMulti_Crud_Model>entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_import_Crud_Entity'");
    }    
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name){
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ByName'");
    }
    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> names){
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JDBC_SP_ByNames'");
    }
    //endregion
}