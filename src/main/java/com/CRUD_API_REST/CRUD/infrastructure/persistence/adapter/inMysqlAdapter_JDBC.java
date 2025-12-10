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
        String sql = "{ call jbAPI_crud_insert(?,?,?,?) }";//"INSERT INTO usuarios_crud (name, email) VALUES (?, ?)";
        /*
        jdbcTemplate.update(sql, entity.getName(), entity.getEmail());
        Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        entity.setId(generatedId);
        return entity;
         */
        
        try {
            // Ejecución del CallableStatement
            jdbcTemplate.execute(sql, (CallableStatementCallback<Long>) cs -> {
                cs.setString(1, entity.getName());
                cs.setString(2, entity.getEmail());
                cs.registerOutParameter(3, Types.BIGINT);
                cs.registerOutParameter(4, Types.TIMESTAMP);
                cs.execute();
                entity.setId(cs.getLong(3));
                entity.setCreated(cs.getTimestamp(4).toLocalDateTime());
                //return cs.getLong(3); // Obtiene el ID generado
                return entity.getId();
            });
            //entity.setId(generatedId); // Asigna el ID al objeto entity
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al insertar la entidad CRUD: " + e.getMessage(), e);
        }
        return entity; // Devuelve la entidad con el ID
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id) {
        String sql = "SELECT id,name,email,created,updated FROM usuarios_crud WHERE id = ?";
        RowMapper<Crud_Entity> rowMapper = (rs, rowNum) -> {
            Crud_Entity entity = new Crud_Entity();
            entity.setId(rs.getLong("id"));
            entity.setName(rs.getString("name"));
            entity.setEmail(rs.getString("email"));
            entity.setCreated(rs.getObject("created",LocalDateTime.class));
            entity.setUpdated(rs.getObject("updated",LocalDateTime.class));
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
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        String sql = "SELECT id,name,email,created,updated FROM usuarios_crud";
        RowMapper<Crud_Entity> rowMapper = (rs, rowNum) -> {
            Crud_Entity entity = new Crud_Entity();
            entity.setId(rs.getLong("id"));
            entity.setName(rs.getString("name"));
            entity.setEmail(rs.getString("email"));
            entity.setCreated(rs.getObject("created",LocalDateTime.class));
            entity.setUpdated(rs.getObject("updated",LocalDateTime.class));
            return entity;
        };
        return jdbcTemplate.query(sql, rowMapper);
    }


    @Override
    public Crud_Entity update_Crud_Entity(String typeBean,Crud_Entity Entity) {
        String sql = "UPDATE usuarios_crud SET name = ?, email = ? WHERE id = ?";
        jdbcTemplate.update(sql, Entity.getName(), Entity.getEmail(), Entity.getId());
        return Entity;
    }

    @Override
    public void delete_Crud_Entity_ById(String typeBean,Long id) {
        String sql = "DELETE FROM usuarios_crud WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, Crud_Entity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity'");
    }

    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JPA_SP'");
    }
}
