package com.poli.internship.data.repository;

import com.poli.internship.data.entity.PositionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PositionRepository extends CrudRepository<PositionEntity, Long> {
    PositionEntity findById(long id);
    List<PositionEntity> findAll();
    List<PositionEntity> findAllById(Iterable<Long> ids);
}
