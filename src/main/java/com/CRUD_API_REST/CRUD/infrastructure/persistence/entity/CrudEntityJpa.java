package com.CRUD_API_REST.CRUD.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "usuarios_crud") 
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_insert_query",
        procedureName = "jbAPI_crud_insert",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_name", type = String.class), 
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email", type = String.class), 
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_id", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_created", type = java.sql.Timestamp.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_list_query",
        procedureName = "jbAPI_crud_list",
        resultClasses = CrudEntityJpa.class
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_find_by_id_query",
        procedureName = "jbAPI_crud_listId",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class)
        },
        resultClasses = CrudEntityJpa.class
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_find_by_name_query",
        procedureName = "jbAPI_crud_list_byName",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_name", type = String.class)
        },
        resultClasses = CrudEntityJpa.class
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_update_query",
        procedureName = "jbAPI_crud_update",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_name", type = String.class), 
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email", type = String.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_delete_physical_query",
        procedureName = "jbAPI_crud_delete_phisical",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name = "jbAPI_crud_delete_logical_query",
        procedureName = "jbAPI_crud_delete_logical",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class)
        }
    ),
})


public class CrudEntityJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created; 
    
    @UpdateTimestamp
    @Column(name = "updated", insertable = false)
    private LocalDateTime updated;
    
    @Column(name = "state", insertable = false, updatable = true)
    private Boolean state;

    public CrudEntityJpa() {

    }
    public CrudEntityJpa(com.CRUD_API_REST.CRUD.domain.model.Crud_Entity domainEntity) {
        this.id = domainEntity.getId(); 
        this.name = domainEntity.getName();
        this.email = domainEntity.getEmail();
        this.created = domainEntity.getCreated();
        this.updated = domainEntity.getUpdated();
        this.state = domainEntity.getState() != null ? domainEntity.getState() : true;
        //if (domainEntity.getState() != null) {
        //    this.state = domainEntity.getState();
        //}
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDateTime getCreated() {
        return created;
    }
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
    public LocalDateTime getUpdated() {
        return updated;
    }
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    public Boolean getState() {
        return state;
    }
    public void setState(Boolean state) {
        this.state = state;
    }

    public com.CRUD_API_REST.CRUD.domain.model.Crud_Entity toDomainEntity() {
        LocalDateTime finalUpdated = this.updated;
        if(this.id != null && this.updated != null && (this.updated.withNano(0).equals(this.created.withNano(0)))) {
            finalUpdated = null;
        }
        return new com.CRUD_API_REST.CRUD.domain.model.Crud_Entity(
            this.id, 
            this.name, 
            this.email, 
            this.created == null ? null : this.created.withNano(0), 
            finalUpdated == null ? null : finalUpdated.withNano(0), // -> this.updated
            this.state
        );
    }
}