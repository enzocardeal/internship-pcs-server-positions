package com.poli.internship;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.PositionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class PositionTest {
    @Autowired
    private GraphQlTester tester;

    @Autowired
    private PositionRepository repository;

    @BeforeEach
    public void beforeEach(){
        this.repository.deleteAll();
    }

    @Test
    public void createPosition(){
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("company", "BTG Pactual");
        input.put("positionName", "Estágio Quadrimestral");
        input.put("role", "Security Office Intern");
        input.put("startsAt", LocalDate.of(2023, 5, 1));
        input.put("endsAt", LocalDate.of(2023, 8, 30));

        PositionModel position = this.tester.documentName("createPosition")
                .variable("input", input)
                .execute()
                .path("createPosition")
                .entity(PositionModel.class)
                .get();
        PositionEntity positionEntity = this.repository.findAll().iterator().next();

        assertThat(position.getId()).isNotNull();
        assertThat(position.getCompany()).isEqualTo(input.get("company"));
        assertThat(position).hasOnlyFields("id", "company", "positionName", "role");
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.getId()));
        assertThat(positionEntity.getPositionName()).isEqualTo(input.get("positionName"));
    }

    @Test
    public void getPositionById(){
        PositionEntity positionEntity = this.repository.save(new PositionEntity("Estágio Quadrimestral", "BTG Pactual", "Security Office Intern", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 30)));
        String id = positionEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        PositionModel position = this.tester.documentName("getPositionById")
                .variable("input", input)
                .execute()
                .path("getPositionById")
                .entity(PositionModel.class)
                .get();

        assertThat(position.getId()).isEqualTo(id);
        assertThat(position.getPositionName()).isEqualTo(positionEntity.getPositionName());
        assertThat(position).hasOnlyFields("id", "company", "positionName", "role");
    }

    @Test
    public void deletePosition(){
        PositionEntity positionEntity = this.repository.save(new PositionEntity("Estágio Quadrimestral", "BTG Pactual", "Security Office Intern", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 30)));
        String id = positionEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        Boolean deleted = this.tester.documentName("deletePosition")
                .variable("input", input)
                .execute()
                .path("deletePosition")
                .entity(Boolean.class)
                .get();
        assertTrue(deleted);
    }
}
