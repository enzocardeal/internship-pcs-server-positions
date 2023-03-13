package com.poli.internship.data.mapper;

import com.poli.internship.data.entity.ApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.poli.internship.domain.models.ApplicationModel.Application;

@Mapper
public interface ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    Application aplicationEntityToModel(ApplicationEntity entity);

    List<Application> applicationEntitiesToModels( List<ApplicationEntity> entities);
}
