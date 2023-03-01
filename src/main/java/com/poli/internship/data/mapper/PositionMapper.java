package com.poli.internship.data.mapper;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.domain.models.PositionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    PositionModel positionEntityToModel(PositionEntity entity);
}
