package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Bundle;
import com.mycompany.myapp.domain.Item;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bundle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
}
