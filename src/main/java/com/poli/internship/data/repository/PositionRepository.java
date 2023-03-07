package com.poli.internship.data.repository;

import com.poli.internship.data.entity.PositionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<PositionEntity, Long> {
    PositionEntity findById(long id);
    void deleteById(long id);
}
