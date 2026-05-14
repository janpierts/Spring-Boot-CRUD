package com.rj.MONOLIT.ADMIN.CRUD.domain.readmodel;

import java.time.LocalDateTime;

public record Crud_multiReadModel(
    Long id,
    String name,
    String email,
    LocalDateTime created,
    LocalDateTime updated,
    Boolean state,
    Boolean isValid,
    String message) {
}
