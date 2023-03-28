package com.poli.internship;

import com.poli.internship.api.context.JWTService;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.AuthTokenPayloadModel;
import com.poli.internship.domain.models.UserType;
import org.junit.jupiter.api.*;
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

import static com.poli.internship.domain.models.PositionModel.Position;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
public class PositionTest {
    @Autowired
    private HttpGraphQlTester tester;
    @Autowired
    JWTService jwtService;
    @Autowired
    private PositionRepository repository;
    private HttpGraphQlTester testerWithAuth;
    private AuthTokenPayloadModel.AuthTokenPayload tokenPayload;
    private String authToken;
    private String companyUserId = "321";

    @BeforeEach
    public void beforeEach() {
        tokenPayload = new AuthTokenPayloadModel.AuthTokenPayload(
                companyUserId,
                "enzo@teste.com",
                UserType.COMPANY,
                3600);
        authToken = this.jwtService.createAuthorizationToken(tokenPayload);
        testerWithAuth = this.tester.mutate().header("Authorization", authToken).build();
    }

    @AfterEach
    public void afterEach(){ this.repository.deleteAll();}

    @Test
    public void createPosition(){
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("userId", companyUserId);
        input.put("company", "BTG Pactual");
        input.put("positionName", "Estágio Quadrimestral");
        input.put("role", "Security Office Intern");
        input.put("startsAt", LocalDate.of(2023, 5, 1));
        input.put("endsAt", LocalDate.of(2023, 8, 30));

        Position position = this.testerWithAuth.documentName("createPosition")
                .variable("input", input)
                .execute()
                .path("createPosition")
                .entity(Position.class)
                .get();
        PositionEntity positionEntity = this.repository.findAll().iterator().next();

        assertThat(position.id()).isNotNull();
        assertThat(position.userId()).isEqualTo(companyUserId);
        assertThat(position.company()).isEqualTo(input.get("company"));
        assertThat(position).hasOnlyFields("id", "userId", "company", "positionName", "role", "startsAt", "endsAt");
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
        assertThat(positionEntity.getPositionName()).isEqualTo(input.get("positionName"));
    }

    @Test
    public void getPositionById(){
        PositionEntity positionEntity = createElementOnDb();
        String id = positionEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        Position position = this.testerWithAuth.documentName("getPositionById")
                .variable("input", input)
                .execute()
                .path("getPositionById")
                .entity(Position.class)
                .get();

        assertThat(position.id()).isEqualTo(id);
        assertThat(position.userId()).isEqualTo(companyUserId);
        assertThat(position.positionName()).isEqualTo(positionEntity.getPositionName());
        assertThat(position).hasOnlyFields("id", "userId","company", "positionName", "role", "startsAt", "endsAt");
    }

    @Test
    public void getAllPositionsByIds(){
        List<PositionEntity> positionEntities = createElementsOnDb();
        List<String> ids = new ArrayList<String>();

        for(PositionEntity positionEntity : positionEntities){
            ids.add(Long.toString(positionEntity.getId()));
        }

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("ids", ids);

        List<HashMap> positions= this.testerWithAuth.documentName("getAllPositionsByIds")
                .variable("input", input)
                .execute()
                .path("getAllPositionsByIds")
                .entity(List.class)
                .get();

        this.tester.documentName("getAllPositionsByIds")
                .variable("input", input)
                .execute()
                .path("getAllPositionsByIds")
                .entity(List.class)
                .get();

        assertThat(positions.size()).isEqualTo(3);

        assertThat(positions.get(0).get("userId")).isEqualTo(companyUserId);
        assertThat(positions.get(1).get("userId")).isEqualTo(companyUserId);
        assertThat(positions.get(2).get("userId")).isEqualTo(companyUserId);

        assertThat(positions.get(0).get("id")).isEqualTo(ids.get(0));
        assertThat(positions.get(1).get("id")).isEqualTo(ids.get(1));
        assertThat(positions.get(2).get("id")).isEqualTo(ids.get(2));

        assertThat(positions.get(0).get("positionName")).isEqualTo(positionEntities.get(0).getPositionName());
        assertThat(positions.get(1).get("positionName")).isEqualTo(positionEntities.get(1).getPositionName());
        assertThat(positions.get(2).get("positionName")).isEqualTo(positionEntities.get(2).getPositionName());
    }

    @Test
    public void getAllPositions(){
        List<PositionEntity> positionEntities = createElementsOnDb();

        List<HashMap> positions = this.testerWithAuth.documentName("getAllPositions")
                .execute()
                .path("getAllPositions")
                .entity(List.class)
                .get();

        assertThat(positions.size()).isEqualTo(3);

        assertThat(positions.get(0).get("userId")).isEqualTo(companyUserId);
        assertThat(positions.get(1).get("userId")).isEqualTo(companyUserId);
        assertThat(positions.get(2).get("userId")).isEqualTo(companyUserId);

        assertThat(positions.get(0).get("positionName")).isEqualTo(positionEntities.get(0).getPositionName());
        assertThat(positions.get(1).get("positionName")).isEqualTo(positionEntities.get(0).getPositionName());
        assertThat(positions.get(2).get("positionName")).isEqualTo(positionEntities.get(0).getPositionName());
    }

    @Test
    public void deletePosition(){
        PositionEntity positionEntity = createElementOnDb();
        String id = positionEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        Boolean deleted = this.testerWithAuth.documentName("deletePosition")
                .variable("input", input)
                .execute()
                .path("deletePosition")
                .entity(Boolean.class)
                .get();
        assertTrue(deleted);
    }

    @Test
    public void updatePosition(){
        PositionEntity positionEntity = createElementOnDb();
        String id = positionEntity.getId().toString();

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);
        input.put("userId", companyUserId);
        input.put("positionName", "Estágio Semestral");

        Position position = this.testerWithAuth.documentName("updatePosition")
                .variable("input", input)
                .execute()
                .path("updatePosition")
                .entity(Position.class)
                .get();

        assertThat(position.id()).isNotNull();
        assertThat(position.userId()).isEqualTo(companyUserId);
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
        assertThat(position.company()).isEqualTo(positionEntity.getCompany());
        assertThat(position.role()).isEqualTo(positionEntity.getRole());
        assertThat(input.get("positionName")).isEqualTo("Estágio Semestral");
        assertThat(position.startsAt()).isEqualTo(positionEntity.getStartsAt());
        assertThat(position.endsAt()).isEqualTo(positionEntity.getEndsAt());
        assertThat(position).hasOnlyFields("id", "userId", "company", "positionName", "role", "startsAt", "endsAt");
    }

    private PositionEntity createElementOnDb(){
        PositionEntity positionEntity = this.repository.save(
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

    private List<PositionEntity> createElementsOnDb(){
        List<PositionEntity> positionEntitiesToBeSaved = new ArrayList<PositionEntity>();
        positionEntitiesToBeSaved.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );
        positionEntitiesToBeSaved.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );
        positionEntitiesToBeSaved.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30)
                )
        );

        List<PositionEntity> positionEntities = (List<PositionEntity>) this.repository.saveAll(positionEntitiesToBeSaved);
        return positionEntities;
    }
}
