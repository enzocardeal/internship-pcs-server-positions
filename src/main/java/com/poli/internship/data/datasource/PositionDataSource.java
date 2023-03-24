package com.poli.internship.data.datasource;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.mapper.PositionMapper;
import com.poli.internship.data.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.PositionInput;
import static com.poli.internship.domain.models.PositionModel.Position;

import java.util.ArrayList;
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
        List<PositionEntity> positionEntities = repository.findAll();
        return PositionMapper.INSTANCE.positionEntitiesToModels(positionEntities);
    }

    public List<Position> getAllPositionsByIds(List<String> ids){
        List<Long> idsAsLong = new ArrayList<Long>();
        for(String id: ids){
            idsAsLong.add(Long.parseLong(id));
        }
        List<PositionEntity> positionEntities = repository.findAllById(idsAsLong);
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
                        Long.parseLong(positionInput.userId()),
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
                        Long.parseLong(positionInput.userId()),
                        positionInput.positionName(),
                        positionInput.company(),
                        positionInput.role(),
                        positionInput.startsAt(),positionInput.endsAt()
                )
        );
        return PositionMapper.INSTANCE.positionEntityToModel(positionEntity);
    }
}
