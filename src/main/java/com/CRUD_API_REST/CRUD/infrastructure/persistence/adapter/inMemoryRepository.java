package com.CRUD_API_REST.CRUD.infrastructure.persistence.adapter;

import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;
import com.CRUD_API_REST.CRUD.infrastructure.utils.filesProcessor;

import jakarta.transaction.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component("inMemoryRepository")
public class inMemoryRepository implements Crud_RepositoryPort{
    private final List<Crud_Entity> entities = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean,Crud_Entity entity) {
        if (entity.getId() == null) {
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
                return null;
            }
        } else {
            return update_Crud_Entity(typeBean,entity); 
        }
    }

    @Override
    public List<Crud_Entity> save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
        List<Crud_Entity> savedEntities = new ArrayList<>();
        for (Crud_Entity entity : entityList) {
            Crud_Entity savedEntity = save_Crud_Entity(typeBean, entity);
            if (savedEntity != null){
                savedEntities.add(savedEntity);
            }
        }
        return savedEntities;
    }

    @Override
    @Transactional
    public List<Crud_Entity> save_import_Crud_Entity(String typeBean,MultipartFile file) {
        try {
            Function<Row, Crud_Entity> rowMapper = row -> {
                String name = filesProcessor.getCellValueAsString(row.getCell(0)); // Columna 0
                if (name == null || name.trim().isEmpty()) return null;
            
                Crud_Entity entity = new Crud_Entity();
                entity.setName(name.trim());
                // Aquí puedes setear más campos si el Excel los tiene
                return entity;
            };
        
            // 2. Convertir el Excel a Lista de Objetos de Dominio (usando la util genérica)
            List<Crud_Entity> entitiesFromFile = filesProcessor.excelToEntities(file, rowMapper);
        
            if (entitiesFromFile.isEmpty()) {
                throw new RuntimeException("El archivo Excel está vacío o no tiene el formato correcto");
            }
        
            // 3. REUTILIZAR tu método existente que ya tiene la lógica de validación y guardado
            return this.save_multi_Crud_Entity(typeBean, entitiesFromFile);
        
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean,Long id) {
        return entities.stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        return entities.stream()
                .filter(e -> e.getName() != null && e.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        return new ArrayList<>(entities);
    }

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

    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean,Long id) {
        entities.removeIf(e -> e.getId() != null && e.getId().equals(id));
    }
    
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
}
