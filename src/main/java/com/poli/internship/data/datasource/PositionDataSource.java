package com.poli.internship.data.datasource;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.mapper.PositionMapper;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PositionDataSource {
    @Autowired
    public PositionRepository repository;

    public PositionModel getPositionById(String id) {
        PositionEntity positionEntity = repository.findById(Long.parseLong(id));
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }

    public Boolean deletePosition(String id){
        if(repository.existsById(Long.parseLong(id))){
            repository.deleteById(Long.parseLong(id));
            return true;
        }
        return false;
    }

    public PositionModel createPosition(String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt){
        PositionEntity positionEntity = repository.save(new PositionEntity(positionName, company, role, startsAt, endsAt));
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }
}
