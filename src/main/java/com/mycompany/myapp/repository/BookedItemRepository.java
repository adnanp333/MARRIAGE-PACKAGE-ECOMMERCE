package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookedItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookedItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookedItemRepository extends JpaRepository<BookedItem, Long> {}
