package com.poli.internship.data.datasource;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.mapper.PositionMapper;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.PositionInput;
import static com.poli.internship.domain.models.PositionModel.Position;

import java.util.List;


@Service
public class PositionDataSource {
    @Autowired
    public PositionRepository repository;

    public Position getPositionById(String id) {
        PositionEntity positionEntity = repository.findById(Long.parseLong(id));
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }

    public List<Position> getAllPositions(){
        Iterable<PositionEntity> positionEntities = repository.findAll();
        return PositionMapper.INSTANCE.positionEntitiesToModels(positionEntities);
    }

    public Boolean deletePosition(String id){
        if(repository.existsById(Long.parseLong(id))){
            repository.deleteById(Long.parseLong(id));
            return true;
        }
        return false;
    }

    public Position createPosition(PositionInput positionInput){
        PositionEntity positionEntity = repository.save(
                new PositionEntity(
                        positionInput.positionName(),
                        positionInput.company(),
                        positionInput.role(),
                        positionInput.startsAt(),positionInput.endsAt()
                )
        );
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }

    public Position updatePosition(PositionInput positionInput, Long id){
        PositionEntity positionEntity = repository.save(
                new PositionEntity(
                        id,
                        positionInput.positionName(),
                        positionInput.company(),
                        positionInput.role(),
                        positionInput.startsAt(),positionInput.endsAt()
                )
        );
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }
}
