package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BundleItem;
import com.mycompany.myapp.domain.Item;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BundleItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BundleItemRepository extends JpaRepository<BundleItem, Long> {
    @Query("SELECT i FROM Item i join BundleItem bi on i.id = bi.itemId WHERE bi.bundleId=:bundleId")
    List<Item> findAllBundleItems(Integer bundleId);
}
