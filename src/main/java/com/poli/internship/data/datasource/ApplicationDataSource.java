package com.poli.internship.data.datasource;

import com.poli.internship.data.entity.ApplicationEntity;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.mapper.ApplicationMapper;
import com.poli.internship.data.repository.ApplicationRepository;
import com.poli.internship.data.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.poli.internship.domain.models.ApplicationModel.Application;

@Service
public class ApplicationDataSource {
    @Autowired
    public ApplicationRepository applicationRepository;
    @Autowired
    public PositionRepository positionRepository;

    public Application createApplication(String positionId, String curriculumId){
        if(positionRepository.existsById(Long.parseLong(positionId)) && applicationRepository.findByPositionIdAndCurriculumId(Long.parseLong(positionId), Long.parseLong(curriculumId)).size() == 0){
            PositionEntity positionEntity = positionRepository.findById(Long.parseLong(positionId));
            ApplicationEntity applicationEntity = applicationRepository.save(
                    new ApplicationEntity(positionEntity, Long.parseLong(curriculumId))
            );
            return ApplicationMapper.INSTANCE.aplicationEntityToModel(applicationEntity);
        }
        return null;
    }

    public Boolean deleteApplication(String id){
        if(applicationRepository.existsById(Long.parseLong(id))){
            applicationRepository.deleteById(Long.parseLong(id));
            return true;
        }
        return false;
    }

    public Application getApplicationById(String id){
        ApplicationEntity applicationEntity = applicationRepository.findById(Long.parseLong(id));
        return ApplicationMapper.INSTANCE.aplicationEntityToModel(applicationEntity);
    }

    public List<Application> getAllApplicationsByCurriculumId(String curriculumId){
        List<ApplicationEntity> applicationEntities = applicationRepository.findByCurriculumId(Long.parseLong(curriculumId));
        return ApplicationMapper.INSTANCE.applicationEntitiesToModels(applicationEntities);
    }
}
