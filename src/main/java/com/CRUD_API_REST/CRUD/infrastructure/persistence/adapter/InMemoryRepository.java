package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("inMemoryRepository")
public class InMemoryRepository implements Crud_RepositoryPort{
    private final List<Crud_Entity> entities = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean,Crud_Entity entity) {
        if (entity.getId() == null) {
            entity.setId(nextId++);
            entities.add(entity);
        } else {
            update_Crud_Entity(typeBean,entity); 
        }
        return entity;
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id) {
        return entities.stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        return new ArrayList<>(entities);
    }

    @Override
    public Crud_Entity update_Crud_Entity(String typeBean,Crud_Entity entity) {
        delete_Crud_Entity_ById(typeBean,entity.getId());
        entities.add(entity);
        return entity;
    }

    @Override
    public void delete_Crud_Entity_ById(String typeBean,Long id) {
        entities.removeIf(e -> e.getId() != null && e.getId().equals(id));
    }

    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JDBC_SP'");
    }

    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save_Crud_Entity_JPA_SP'");
    }
}
