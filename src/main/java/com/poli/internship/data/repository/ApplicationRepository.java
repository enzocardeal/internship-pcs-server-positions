package com.poli.internship.data.repository;

import com.poli.internship.data.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByPositionIdAndCurriculumId(Long positionId, Long curriculumId);
}
