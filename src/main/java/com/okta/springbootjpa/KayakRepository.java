package com.okta.springbootjpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@RepositoryRestResource
public interface KayakRepository extends CrudRepository<Kayak, Long> {

    @PreAuthorize("hasAuthority('Admin')")
    <S extends Kayak> S save(S entity);

}