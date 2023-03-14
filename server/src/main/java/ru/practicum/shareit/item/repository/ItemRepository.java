package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByRequestId(Long requestId);

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE i.available = true " +
            "AND (lower(i.name) LIKE lower(CONCAT('%',?1,'%')) " +
            "OR lower(i.description) LIKE lower(CONCAT('%',?1,'%')))")
    List<Item> findItemsByNameAndDescriptionAndAvailable(String text, Pageable pageable);
}
