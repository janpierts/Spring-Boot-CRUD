package com.rj.MONOLIT.ADMIN.CRUD.infrastructure.persistence.adapter;

import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.MONOLIT.ADMIN.CRUD.application.dto.InsertUpdate_Crud_Model;
import com.rj.MONOLIT.ADMIN.CRUD.application.ports.out.Crud_RepositoryPort;
import com.rj.MONOLIT.ADMIN.CRUD.domain.model.Crud_Entity;
import com.rj.MONOLIT.COMMON.utils.settings.JDBCConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component("inMysqlAdapter_JDBC")
public class inMysqlAdapter_JDBC implements Crud_RepositoryPort {
    //region dynamic JdbcTemplate configuration
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;
    private final JDBCConfig DBConfig;
    private volatile JdbcTemplate jdbcTemplate;

    public inMysqlAdapter_JDBC(JDBCConfig DBConfig) {
        this.DBConfig = DBConfig;
    }
    private JdbcTemplate getDynamicJdbcTemplate(){
        if(this.jdbcTemplate == null) {
            synchronized(this){
                if(this.jdbcTemplate == null) {
                    DataSource ds = DBConfig.createDataSource(datasourceUrl, datasourceUsername, datasourcePassword, datasourceDriverClassName);
                    this.jdbcTemplate = new JdbcTemplate(ds);
                }
            }
        }
        return this.jdbcTemplate;
    }
    //endregion

    /* ->  this method is used to get a default JdbcTemplate connection */
    /*
    private final JdbcTemplate jdbcTemplate;
    public inMysqlAdapter_JDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
     */
    
    //region save simple, multi and import methods
    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP (String typeBean,InsertUpdate_Crud_Model entity) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        String sql = "{ call jbAPI_crud_insert(?,?,?,?) }";        
        Optional<Crud_Entity> existingEntityOpt = find_Crud_Entity_JDBC_SP_ByName(typeBean,entity.name());
        Crud_Entity newEntity = new Crud_Entity();
        if (existingEntityOpt.isPresent()) {
            throw new RuntimeException("El nombre '"+entity.name()+"' ya existe en la base de datos.");
        }
        try{
            newEntity.setName(entity.name());
            newEntity.setEmail(entity.email());
            currentTemplate.execute(sql, (CallableStatementCallback<Long>) cs -> {
                cs.setString(1, newEntity.getName());
                cs.setString(2, newEntity.getEmail());
                cs.registerOutParameter(3, Types.BIGINT);
                cs.registerOutParameter(4, Types.TIMESTAMP);
                cs.execute();
                newEntity.setId(cs.getLong(3));
                newEntity.setCreated(cs.getTimestamp(4).toLocalDateTime());
                newEntity.setState(true);
                return newEntity.getId();
            });
        }catch(Exception e){
            throw new RuntimeException("Error al insertar la entidad CRUD: " + e.getMessage());
        }
        return newEntity;
    }

    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        String sql = "{ call jbAPI_crud_insert_multi(?) }";
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> existingNames = find_Crud_Entity_JDBC_SP_ByNames(typeBean, entityList)
                .map(list -> list.stream()
                        .map(Crud_Entity::getName)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        if(!existingNames.isEmpty()) {
            List<String> NonDuplicateNames = entityList.stream()
                    .map(Crud_Entity::getName)
                    .filter(name -> !existingNames.contains(name))
                    .collect(Collectors.toList());
            if(NonDuplicateNames.isEmpty()) {
                throw new RuntimeException("Error al insertar las entidades ya se encuentran la base de datos: ");
            }
        }
        List<Crud_Entity> filteredEntities = entityList.stream()
                .filter(entity -> !existingNames.contains(entity.getName()))
                .collect(Collectors.toList());
        try{
            String jsonEntities = objectMapper.writeValueAsString(filteredEntities);
            currentTemplate.execute(sql, (CallableStatementCallback<Void>) cs -> {
                cs.setString(1, jsonEntities);
                cs.execute();
                return null;
            });

            List<Crud_Entity> savedEntities = this.find_Crud_Entity_JDBC_SP_ByNames(typeBean, filteredEntities)
                    .orElse(new ArrayList<>());
            return Optional.of(savedEntities);
        }catch(JsonProcessingException e) {
            throw new RuntimeException("Error al serializar lista a JSON", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar las entidades CRUD: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Optional<List<Crud_Entity>> save_import_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        try{
            return save_multi_Crud_Entity_JDBC_SP(typeBean, entityList);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //endregion

    //region find methods
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        try{
            String sql = "{call jbAPI_crud_list()}";
            RowMapper<Crud_Entity> rowMapper = (rs, rowNum) -> {
                Crud_Entity entity = new Crud_Entity();
                entity.setId(rs.getLong("id"));
                entity.setName(rs.getString("name"));
                entity.setEmail(rs.getString("email"));
                entity.setCreated(rs.getObject("created",LocalDateTime.class));
                entity.setUpdated(rs.getObject("updated",LocalDateTime.class));
                entity.setState(rs.getBoolean("state"));
                return entity;
            };
            return currentTemplate.query(sql, rowMapper);
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean,Long id) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        try{
            String sql = "{call jbAPI_crud_listId(?)}";
            RowMapper<Crud_Entity> rowMapper = (rs, rowNum) -> {
                Crud_Entity entity = new Crud_Entity();
                entity.setId(rs.getLong("id"));
                entity.setName(rs.getString("name"));
                entity.setEmail(rs.getString("email"));
                entity.setCreated(rs.getObject("created",LocalDateTime.class));
                entity.setUpdated(rs.getObject("updated",LocalDateTime.class));
                entity.setState(rs.getBoolean("state"));
                return entity;
            };
            List<Crud_Entity> results = currentTemplate.query(sql, rowMapper, id);
            if(results.isEmpty()) throw new RuntimeException("El identificador ingresado no existe");
            return results.isEmpty()? Optional.empty() : Optional.of(results.get(0));
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        try{
            String sql = "{call jbAPI_crud_list_byName(?)}";
            RowMapper<Crud_Entity> rowMapper = (rs, rowNum) -> {
                Crud_Entity entity = new Crud_Entity();
                entity.setId(rs.getLong("id"));
                entity.setName(rs.getString("name"));
                entity.setEmail(rs.getString("email"));
                entity.setCreated(rs.getObject("created",LocalDateTime.class));
                entity.setUpdated(rs.getObject("updated",LocalDateTime.class));
                entity.setState(rs.getBoolean("state"));
                return entity;
            };
            List<Crud_Entity> results = currentTemplate.query(sql, rowMapper, name);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }catch(Exception e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> entityList) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        try{
            String sql = "{ call jbAPI_crud_list_byNames(?) }";
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonEntities = objectMapper.writeValueAsString(entityList);
            List<Crud_Entity> result = currentTemplate.execute(sql, (CallableStatementCallback<List<Crud_Entity>>) cs -> {
                cs.setString(1, jsonEntities);
                ResultSet rs = cs.executeQuery();
                BeanPropertyRowMapper<Crud_Entity> rowMapper = new BeanPropertyRowMapper<>(Crud_Entity.class);
                List<Crud_Entity> list = new ArrayList<>();
                int rowNum = 0;
                while (rs.next()) {
                    list.add(rowMapper.mapRow(rs, rowNum++));
                }
                return list;
            });
            return Optional.ofNullable(result.isEmpty() ? null : result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar entityList a JSON", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar las entidades CRUD: " + e.getMessage(), e);
        }
    }
    //endregion

    //region update and delete methods
    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean,InsertUpdate_Crud_Model Entity) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        String sql = "{call jbAPI_crud_update(?,?,?)}";
        try{
            Optional<Crud_Entity> existingEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.id()).filter(a -> Boolean.TRUE.equals(a.getState()));
            if (existingEntityOpt.isEmpty()) {
                throw new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+Entity.id());
            }
            currentTemplate.update(sql,  Entity.id(), Entity.name(), Entity.email());
            Optional<Crud_Entity> updatedEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.id());
            return updatedEntityOpt.get();
        }catch(DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean,Long id) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        String sql = "{call jbAPI_crud_delete_phisical(?)}";
        try{
            Optional<Crud_Entity> existingEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, id);
            if (existingEntityOpt.isEmpty()) {
                throw new RuntimeException("Error al eliminar físicamente: el ID: "+id+" no existe.");
            }
            currentTemplate.update(sql, id);
        }catch(DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean,Crud_Entity Entity) {
        JdbcTemplate currentTemplate = getDynamicJdbcTemplate();
        String sql = "{call jbAPI_crud_delete_logical(?)}";
        try{
            Optional<Crud_Entity> existingEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.getId()).filter(a -> Boolean.TRUE.equals(a.getState()));
            if (existingEntityOpt.isEmpty()) {
                throw new RuntimeException("El identificador mencionado no existe o se encuntra eliminado/anulado, Id: "+Entity.getId());
            }
            currentTemplate.update(sql, Entity.getId());
            Optional<Crud_Entity> updatedEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.getId());
            if (updatedEntityOpt.isPresent()) {
                Entity = updatedEntityOpt.get();
            }
            return Entity;
        }catch(DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //endregion

    //region unimplemented methods
    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, InsertUpdate_Crud_Model entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity'");
    }
    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, InsertUpdate_Crud_Model entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JPA_SP'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_EntityById'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ById'");
    }
    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll_Crud_entity'");
    }
    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll_Crud_entity_JPA_SP'");
    }
    @Override
    public Crud_Entity update_Crud_Entity(String typeBean, InsertUpdate_Crud_Model entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity'");
    }
    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, InsertUpdate_Crud_Model entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity_JPA_SP'");
    }
    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_phisical_ById'");
    }
    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_phisical_JPA_SP_ById'");
    }
    @Override
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_logical_ById'");
    }
    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'delete_Crud_Entity_logical_JPA_SP_ById'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_multi_Crud_Entity'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_EntityByName'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_import_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_import_Crud_Entity'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_import_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_import_Crud_Entity'");
    }
    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ByName(String typeBean, String name){
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ByName'");
    }
    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity_JPA_SP(String typeBean, List<Crud_Entity> entityList) {
        throw new UnsupportedOperationException("Unimplemented method 'save_multi_Crud_Entity_JPA_SP'");
    }
    @Override
    public Optional<List<Crud_Entity>> find_Crud_EntityByNames(String typeBean, List<Crud_Entity> names) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_EntityByNames'");
    }
    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JPA_SP_ByNames(String typeBean, List<Crud_Entity> names) {
        throw new UnsupportedOperationException("Unimplemented method 'find_Crud_Entity_JPA_SP_ByNames'");
    }
    //endregion
}
