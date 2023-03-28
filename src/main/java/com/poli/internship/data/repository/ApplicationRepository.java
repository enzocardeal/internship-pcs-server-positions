package com.poli.internship.data.repository;

import com.poli.internship.data.entity.ApplicationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
    ApplicationEntity findById(long id);
    List<ApplicationEntity> findByPositionIdAndUserId(Long positionId, Long userId);
    List<ApplicationEntity> findByUserId(Long userId);
}
