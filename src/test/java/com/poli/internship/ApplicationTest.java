package com.poli.internship;

import com.poli.internship.api.context.JWTService;
import com.poli.internship.data.datasource.ApplicationDataSource;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.ApplicationRepository;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.UserType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.poli.internship.domain.models.ApplicationModel.Application;
import static com.poli.internship.domain.models.AuthTokenPayloadModel.AuthTokenPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
public class ApplicationTest {
    @Autowired
    private HttpGraphQlTester tester;
    @Autowired
    JWTService jwtService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private ApplicationDataSource applicationDataSource;
    private HttpGraphQlTester testerWithAuth;
    private AuthTokenPayload tokenPayload;
    private String authToken;
    private String userId = "123";
    private String companyUserId = "321";

    @BeforeEach
    public void beforeEach() {
        tokenPayload = new AuthTokenPayload(
                userId,
                "enzo@teste.com",
                UserType.STUDENT,
                3600);
        authToken = this.jwtService.createAuthorizationToken(tokenPayload);
        testerWithAuth = this.tester.mutate().header("Authorization", authToken).build();
    }
    @AfterEach
    public void afterEach(){
        this.applicationRepository.deleteAll();
        this.positionRepository.deleteAll();
    }

    @Test
    public void createApplication(){
        PositionEntity positionEntity = this.positionRepository.save(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );

        String id = positionEntity.getId().toString();

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("positionId", id);

        Application application = this.testerWithAuth.documentName("createApplication")
                .variable("input", input)
                .execute()
                .path("createApplication")
                .entity(Application.class)
                .get();

        assertThat(application.position()).isNotNull();
        assertThat(application.userId()).isEqualTo(userId);
        assertThat(application.position().positionName()).isEqualTo(positionEntity.getPositionName());
    }

    @Test
    public void deleteApplication(){
        PositionEntity positionEntity = createElementOnDb();
        String positionId = positionEntity.getId().toString();

        Application application = this.applicationDataSource.createApplication(positionId, userId);

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", application.id());

        Boolean deleted = this.testerWithAuth.documentName("deleteApplication")
                .variable("input", input)
                .execute()
                .path("deleteApplication")
                .entity(Boolean.class)
                .get();

        assertTrue(deleted);
    }

    @Test
    public void getAllApplications(){
        tokenPayload = new AuthTokenPayload(
                userId,
                "enzo@teste.com",
                UserType.COMPANY,
                3600);
        authToken = this.jwtService.createAuthorizationToken(tokenPayload);
        testerWithAuth = this.tester.mutate().header("Authorization", authToken).build();

        List<PositionEntity> positionEntities = new ArrayList<PositionEntity>();
        positionEntities.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );
        positionEntities.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );
        positionEntities.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );
        this.positionRepository.saveAll(positionEntities);

        List<Application> applications = new ArrayList<Application>();
        applications.add(
                this.applicationDataSource.createApplication(Long.toString(positionEntities.get(0).getId()), userId)
        );
        applications.add(
                this.applicationDataSource.createApplication(Long.toString(positionEntities.get(1).getId()), userId)
        );
        applications.add(
                this.applicationDataSource.createApplication(Long.toString(positionEntities.get(2).getId()), userId)
        );

        List<HashMap> returnedApplications = this.testerWithAuth.documentName("getAllApplications")
                .execute()
                .path("getAllApplications")
                 .entity(List.class)
                .get();

        assertThat(returnedApplications.size()).isEqualTo(3);
        assertThat(!((HashMap)returnedApplications.get(0).get("position")).isEmpty());
        assertThat(!((HashMap)returnedApplications.get(1).get("position")).isEmpty());
        assertThat(!((HashMap)returnedApplications.get(2).get("position")).isEmpty());
        assertThat((String)returnedApplications.get(0).get("userId")).isEqualTo(userId);
        assertThat((String)returnedApplications.get(1).get("userId")).isEqualTo(userId);
        assertThat((String)returnedApplications.get(2).get("userId")).isEqualTo(userId);
    }

    @Test
    public void getApplicationById(){
        PositionEntity positionEntity = createElementOnDb();
        String id = positionEntity.getId().toString();

        Application application = this.applicationDataSource.createApplication(id, userId);
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", application.id());

        Application applicationReturned = this.testerWithAuth.documentName("getApplicationById")
                .variable("input", input)
                .execute()
                .path("getApplicationById")
                .entity(Application.class)
                .get();

        assertThat(applicationReturned.id()).isEqualTo(application.id());
        assertThat(applicationReturned.userId()).isEqualTo(application.userId());
    }
    private PositionEntity createElementOnDb(){
        PositionEntity positionEntity = this.positionRepository.save(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );

        return positionEntity;
    }
}

