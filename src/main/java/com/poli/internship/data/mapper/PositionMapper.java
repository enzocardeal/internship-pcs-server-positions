package com.poli.internship.data.mapper;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.domain.models.PositionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static com.poli.internship.domain.models.PositionModel.Position;

import java.util.List;

@Mapper
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    Position positionEntityToModel(PositionEntity entity);
    List<Position> positionEntitiesToModels(Iterable<PositionEntity> entities);
}
