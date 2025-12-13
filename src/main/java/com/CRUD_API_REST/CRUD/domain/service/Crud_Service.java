package com.CRUD_API_REST.CRUD.domain.service;

import com.CRUD_API_REST.CRUD.domain.model.Crud_Entity;
import com.CRUD_API_REST.CRUD.domain.ports.in.Crud_ServicePort;
import com.CRUD_API_REST.CRUD.domain.ports.out.Crud_RepositoryPort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class Crud_Service implements Crud_ServicePort {
    private final Map<String, Crud_RepositoryPort> crudRepositoryPort;

    public Crud_Service(Map<String, Crud_RepositoryPort> crudRepositoryPort) {
        this.crudRepositoryPort = crudRepositoryPort;
    }

    private Crud_RepositoryPort getRepositoryPort(String typeBean) {
        Crud_RepositoryPort repositoryPort = crudRepositoryPort.get(typeBean);
        if (repositoryPort == null) {
            throw new IllegalArgumentException("No repository found for type: " + typeBean);
        }
        return repositoryPort;
    }

    @Override
    public Crud_Entity save_Crud_Entity(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.save_Crud_Entity(typeBean, entity);
    }

    @Override
    public Crud_Entity save_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.save_Crud_Entity_JDBC_SP(typeBean, entity);
    }

    @Override
    public Crud_Entity save_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.save_Crud_Entity_JPA_SP(typeBean, entity);
    }

    @Override
    public List<Crud_Entity> save_multi_Crud_Entity(String typeBean, List<Crud_Entity> entityList) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.save_multi_Crud_Entity(typeBean, entityList);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityById(typeBean, id);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JDBC_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JDBC_SP_ById(typeBean, id);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_Entity_JPA_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_Entity_JPA_SP_ById(typeBean, id);
    }

    @Override
    public Optional<Crud_Entity> find_Crud_EntityByName(String typeBean, String name) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.find_Crud_EntityByName(typeBean, name);
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity(typeBean);
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JDBC_SP(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity_JDBC_SP(typeBean);
    }

    @Override
    public List<Crud_Entity> findAll_Crud_entity_JPA_SP(String typeBean) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.findAll_Crud_entity_JPA_SP(typeBean);
    }

    @Override
    public Crud_Entity update_Crud_Entity(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity(typeBean, entity);
    }

    @Override
    public Crud_Entity update_Crud_Entity_JDBC_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity_JDBC_SP(typeBean, entity);
    }

    @Override
    public Crud_Entity update_Crud_Entity_JPA_SP(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.update_Crud_Entity_JPA_SP(typeBean, entity);
    }

    @Override
    public void delete_Crud_Entity_phisical_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_ById(typeBean, id);
    }

    @Override
    public void delete_Crud_Entity_phisical_JDBC_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_JDBC_SP_ById(typeBean, id);
    }
    @Override
    public void delete_Crud_Entity_phisical_JPA_SP_ById(String typeBean, Long id) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        repositoryPort.delete_Crud_Entity_phisical_JPA_SP_ById(typeBean, id);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_ById(typeBean, entity);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JDBC_SP_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_JDBC_SP_ById(typeBean, entity);
    }

    @Override
    public Crud_Entity delete_Crud_Entity_logical_JPA_SP_ById(String typeBean, Crud_Entity entity) {
        Crud_RepositoryPort repositoryPort = getRepositoryPort(typeBean);
        return repositoryPort.delete_Crud_Entity_logical_JPA_SP_ById(typeBean, entity);
    }
}