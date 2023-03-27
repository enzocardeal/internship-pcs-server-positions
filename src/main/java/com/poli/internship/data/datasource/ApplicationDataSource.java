package com.poli.internship.data.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.data.entity.ApplicationEntity;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.mapper.ApplicationMapper;
import com.poli.internship.data.messaging.PubsubOutboundGateway;
import com.poli.internship.data.repository.ApplicationRepository;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.CurriculumAuthorizationActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.poli.internship.domain.models.ApplicationModel.Application;
import static com.poli.internship.domain.models.ApplicationModel.ApplicationMessage;

@Service
public class ApplicationDataSource {
    @Autowired
    public ApplicationRepository applicationRepository;
    @Autowired
    public PositionRepository positionRepository;
    @Autowired
    private PubsubOutboundGateway messagingGateway;

    public Application createApplication(String positionId, String userId){
        if(
           positionRepository.existsById(Long.parseLong(positionId)) &&
           applicationRepository.findByPositionIdAndUserId(Long.parseLong(positionId), Long.parseLong(userId)).size() == 0
        )
        {
            PositionEntity positionEntity = positionRepository.findById(Long.parseLong(positionId));
            ApplicationEntity applicationEntity = applicationRepository.save(
                    new ApplicationEntity(positionEntity, Long.parseLong(userId))
            );
            try{
                ApplicationMessage applicationMessage = new ApplicationMessage(
                        CurriculumAuthorizationActionType.GRANT,
                        Long.toString( positionEntity.getUserId()),
                        userId
                        );

                String message = (new ObjectMapper()).writeValueAsString(applicationMessage);
                messagingGateway.sendToPubsub(message);
            } catch (Exception e){
                applicationRepository.delete(applicationEntity);

                throw new CustomError("Application creation failed.", ErrorType.INTERNAL_ERROR);
            }
            return ApplicationMapper.INSTANCE.aplicationEntityToModel(applicationEntity);
        }
        return null;
    }

    public Boolean deleteApplication(String id){
        ApplicationEntity applicationEntity = applicationRepository.findById(Long.parseLong(id));
        if(applicationEntity != null){
            String companyId = applicationEntity.getPosition().getUserId().toString();
            applicationRepository.deleteById(Long.parseLong(id));

            try{
                ApplicationMessage applicationMessage = new ApplicationMessage(
                        CurriculumAuthorizationActionType.REVOKE,
                        companyId,
                        Long.toString(applicationEntity.getUserId())
                );

                String message = (new ObjectMapper()).writeValueAsString(applicationMessage);
                messagingGateway.sendToPubsub(message);
            } catch (Exception e){
                applicationRepository.save(applicationEntity);
                throw new CustomError("Application deletion failed.", ErrorType.INTERNAL_ERROR);
            }
            return true;
        }
        return false;
    }

    public Application getApplicationById(String id){
        ApplicationEntity applicationEntity = applicationRepository.findById(Long.parseLong(id));
        return ApplicationMapper.INSTANCE.aplicationEntityToModel(applicationEntity);
    }

    public List<Application> getAllApplicationsByUserId(String userId){
        List<ApplicationEntity> applicationEntities = applicationRepository.findByUserId(Long.parseLong(userId));
        return ApplicationMapper.INSTANCE.applicationEntitiesToModels(applicationEntities);
    }
}
