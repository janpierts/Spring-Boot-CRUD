package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;

@Component("inMysqlAdapter_JDBC")
public class inMysqlAdapter_JDBC implements Crud_RepositoryPort {
    private final JdbcTemplate jdbcTemplate;
    public inMysqlAdapter_JDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP (String typeBean,Crud_Entity entity) {
        String sql = "{ call jbAPI_crud_insert(?,?,?,?) }";        
        try {
            if(entity.getName() == null || entity.getName().isEmpty()) {
                throw new RuntimeException("El nombre no puede estar vacío.");
            }
            Optional<Crud_Entity> existingEntityOpt = find_Crud_Entity_JDBC_SP_ByName(typeBean,entity.getName());
            if (existingEntityOpt.isPresent()) {
                throw new RuntimeException("Error al guardar: el nombre ya existe.");
            }
            jdbcTemplate.execute(sql, (CallableStatementCallback<Long>) cs -> {
                cs.setString(1, entity.getName());
                cs.setString(2, entity.getEmail());
                cs.registerOutParameter(3, Types.BIGINT);
                cs.registerOutParameter(4, Types.TIMESTAMP);
                cs.execute();
                entity.setId(cs.getLong(3));
                entity.setCreated(cs.getTimestamp(4).toLocalDateTime());
                entity.setState(true);
                return entity.getId();
            });
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return entity;
    }

    @Override
    public Optional<List<Crud_Entity>> save_multi_Crud_Entity_JDBC_SP(String typeBean, List<Crud_Entity> entityList) {
        String sql = "{ call jbAPI_crud_insert_multi(?) }";
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> namesSet = entityList.stream()
                .map(Crud_Entity::getName)
                .collect(Collectors.toSet());
        List<Crud_Entity> uniqueEntities = entityList.stream()
                .filter(e -> namesSet.contains(e.getName()))
                .collect(Collectors.toMap(
                    Crud_Entity::getName,
                    e -> e, 
                    (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
        List<String> existingNames = find_Crud_Entity_JDBC_SP_ByNames(typeBean, uniqueEntities)
                .map(list -> list.stream()
                        .map(Crud_Entity::getName)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        List<Crud_Entity> filteredEntities = uniqueEntities.stream()
                .filter(entity -> !existingNames.contains(entity.getName()))
                .collect(Collectors.toList());
        if(filteredEntities.isEmpty()) {
            return Optional.of(filteredEntities);
        }
        try{
            String jsonEntities = objectMapper.writeValueAsString(filteredEntities);
            jdbcTemplate.execute(sql, (CallableStatementCallback<Void>) cs -> {
                cs.setString(1, jsonEntities);
                cs.execute();
                return null;
            });

            List<Crud_Entity> savedEntities = this.find_Crud_Entity_JDBC_SP_ByNames(typeBean, filteredEntities)
                    .orElse(new ArrayList<>());
            return Optional.of(savedEntities);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar las entidades CRUD: " + e.getMessage(), e);
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

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
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
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean,Long id) {
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
        List<Crud_Entity> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty()? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ByName(String typeBean, String name) {
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
        List<Crud_Entity> results = jdbcTemplate.query(sql, rowMapper, name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<List<Crud_Entity>> find_Crud_Entity_JDBC_SP_ByNames(String typeBean, List<Crud_Entity> entityList) {
        String sql = "{ call jbAPI_crud_list_byNames(?) }";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String jsonEntities = objectMapper.writeValueAsString(entityList);
            List<Crud_Entity> result = jdbcTemplate.execute(sql, (CallableStatementCallback<List<Crud_Entity>>) cs -> {
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

    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity Entity) {
        String sql = "{call jbAPI_crud_update(?,?,?)}";
        jdbcTemplate.update(sql,  Entity.getId(), Entity.getName(), Entity.getEmail());
        Optional<Crud_Entity> updatedEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.getId());
        if (updatedEntityOpt.isPresent()) {
            Entity = updatedEntityOpt.get();
        }
        return Entity;
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean,Long id) {
        String sql = "{call jbAPI_crud_delete_phisical(?)}";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean,Crud_Entity Entity) {
        String sql = "{call jbAPI_crud_delete_logical(?)}";
        jdbcTemplate.update(sql, Entity.getId());
        Optional<Crud_Entity> updatedEntityOpt = find_Crud_Entity_JDBC_SP_ById(typeBean, Entity.getId());
        if (updatedEntityOpt.isPresent()) {
            Entity = updatedEntityOpt.get();
        }
        return Entity;
    }

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity'");
    }
    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
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
    public Crud_Entity update_Crud_Entity(String typeBean, Crud_Entity entity) {
        throw new UnsupportedOperationException("Unimplemented method 'update_Crud_Entity'");
    }
    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
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
}
