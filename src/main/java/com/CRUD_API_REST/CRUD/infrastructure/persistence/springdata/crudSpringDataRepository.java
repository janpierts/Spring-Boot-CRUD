package com.CRUD_API_REST.CRUD.infrastructure.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.CRUD_API_REST.CRUD.infrastructure.persistence.entity.CrudEntityJpa;

@Repository
public interface crudSpringDataRepository extends JpaRepository<CrudEntityJpa, Long> {

}