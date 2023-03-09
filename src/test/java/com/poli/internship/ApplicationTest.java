package com.poli.internship;

import com.poli.internship.data.datasource.ApplicationDataSource;
import com.poli.internship.data.entity.ApplicationEntity;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.ApplicationRepository;
import com.poli.internship.data.repository.PositionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.poli.internship.domain.models.ApplicationModel.Application;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class ApplicationTest {
    @Autowired
    private GraphQlTester tester;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ApplicationDataSource applicationDataSource;

    @AfterEach
    public void afterEach(){
        this.applicationRepository.deleteAll();
        this.positionRepository.deleteAll();
    }

    @Test
    public void createApplication(){
        PositionEntity positionEntity = this.positionRepository.save(
                new PositionEntity(
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
        input.put("curriculumId", "2");

        Application application = this.tester.documentName("createApplication")
                .variable("input", input)
                .execute()
                .path("createApplication")
                .entity(Application.class)
                .get();

        assertThat(application.position()).isNotNull();
        assertThat(application.curriculumId()).isNotNull();
        assertThat(application.position().positionName()).isEqualTo(positionEntity.getPositionName());
    }

    @Test
    public void deleteApplication(){
        PositionEntity positionEntity = this.positionRepository.save(new PositionEntity("Estágio Quadrimestral", "BTG Pactual", "Security Office Intern", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 30)));
        String positionId = positionEntity.getId().toString();

        Application application = this.applicationDataSource.createApplication(positionId, "2");

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", application.id());

        Boolean deleted = this.tester.documentName("deleteApplication")
                .variable("input", input)
                .execute()
                .path("deleteApplication")
                .entity(Boolean.class)
                .get();

        assertTrue(deleted);
    }
}
