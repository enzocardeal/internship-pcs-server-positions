package com.poli.internship;

import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.PositionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.poli.internship.domain.models.PositionModel.Position;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class PositionTest {
    @Autowired
    private GraphQlTester tester;

    @Autowired
    private PositionRepository repository;

    @AfterEach
    public void afterEach(){ this.repository.deleteAll();}

    @Test
    public void createPosition(){
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("company", "BTG Pactual");
        input.put("positionName", "Estágio Quadrimestral");
        input.put("role", "Security Office Intern");
        input.put("startsAt", LocalDate.of(2023, 5, 1));
        input.put("endsAt", LocalDate.of(2023, 8, 30));

        Position position = this.tester.documentName("createPosition")
                .variable("input", input)
                .execute()
                .path("createPosition")
                .entity(Position.class)
                .get();
        PositionEntity positionEntity = this.repository.findAll().iterator().next();

        assertThat(position.id()).isNotNull();
        assertThat(position.company()).isEqualTo(input.get("company"));
        assertThat(position).hasOnlyFields("id", "company", "positionName", "role", "startsAt", "endsAt");
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
        assertThat(positionEntity.getPositionName()).isEqualTo(input.get("positionName"));
    }

    @Test
    public void getPositionById(){
        PositionEntity positionEntity = this.repository.save(new PositionEntity("Estágio Quadrimestral", "BTG Pactual", "Security Office Intern", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 30)));
        String id = positionEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        Position position = this.tester.documentName("getPositionById")
                .variable("input", input)
                .execute()
                .path("getPositionById")
                .entity(Position.class)
                .get();

        assertThat(position.id()).isEqualTo(id);
        assertThat(position.positionName()).isEqualTo(positionEntity.getPositionName());
        assertThat(position).hasOnlyFields("id", "company", "positionName", "role", "startsAt", "endsAt");
    }

    @Test
    public void getAllPositions(){
        List<PositionEntity> positionEntities = new ArrayList<PositionEntity>();
        positionEntities.add(this.repository.save(new PositionEntity(
                "Estágio Quadrimestral",
                "BTG Pactual",
                "Security Office Intern",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 8, 30
                ))));
        positionEntities.add(this.repository.save(new PositionEntity(
                "Estágio Quadrimestral",
                "BTG Pactual",
                "Security Office Intern",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 8, 30
                ))));
        positionEntities.add(this.repository.save(new PositionEntity(
                "Estágio Quadrimestral",
                "BTG Pactual",
                "Security Office Intern",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 8, 30
                ))));

        List<HashMap> positions = this.tester.documentName("getAllPositions")
                .execute()
                .path("getAllPositions")
                .entity(List.class)
                .get();

        assertThat(positions.size()).isEqualTo(3);
        assertThat(positions.get(0).get("positionName")).isEqualTo(positionEntities.get(0).getPositionName());
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

    @Test
    public void updatePosition(){
        PositionEntity positionEntity = this.repository.save(new PositionEntity(
                "Estágio Quadrimestral",
                "BTG Pactual",
                "Security Office Intern",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 8, 30)
            )
        );
        String id = positionEntity.getId().toString();

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);
        input.put("positionName", "Estágio Semestral");

        Position position = this.tester.documentName("updatePosition")
                .variable("input", input)
                .execute()
                .path("updatePosition")
                .entity(Position.class)
                .get();

        assertThat(position.id()).isNotNull();
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
        assertThat(position.company()).isEqualTo(positionEntity.getCompany());
        assertThat(position.role()).isEqualTo(positionEntity.getRole());
        assertThat(input.get("positionName")).isEqualTo("Estágio Semestral");
        assertThat(position.startsAt()).isEqualTo(positionEntity.getStartsAt());
        assertThat(position.endsAt()).isEqualTo(positionEntity.getEndsAt());
        assertThat(position).hasOnlyFields("id", "company", "positionName", "role", "startsAt", "endsAt");
    }
}
