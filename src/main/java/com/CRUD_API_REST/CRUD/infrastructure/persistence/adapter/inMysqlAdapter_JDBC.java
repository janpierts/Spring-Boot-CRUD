package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
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
            jdbcTemplate.execute(sql, (CallableStatementCallback<Long>) cs -> {
                cs.setString(1, entity.getName());
                cs.setString(2, entity.getEmail());
                cs.registerOutParameter(3, Types.BIGINT);
                cs.registerOutParameter(4, Types.TIMESTAMP);
                cs.execute();
                entity.setId(cs.getLong(3));
                entity.setCreated(cs.getTimestamp(4).toLocalDateTime());
                return entity.getId();
            });
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al insertar la entidad CRUD: " + e.getMessage(), e);
        }
        return entity;
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
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean,Crud_Entity Entity) {
        String sql = "UPDATE usuarios_crud SET name = ?, email = ? WHERE id = ?";
        jdbcTemplate.update(sql, Entity.getName(), Entity.getEmail(), Entity.getId());
        return Entity;
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean,Long id) {
        String sql = "DELETE FROM usuarios_crud WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean,Crud_Entity Entity) {
        String sql = "UPDATE usuarios_crud SET state = ? WHERE id = ?";
        jdbcTemplate.update(sql, Entity.getState(), Entity.getId());
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
}
