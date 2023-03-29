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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
    @Transactional
    public void createPosition(){
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("userId", companyUserId);
        input.put("company", "BTG Pactual");
        input.put("positionName", "Estágio Quadrimestral");
        input.put("role", "Security Office Intern");
        input.put("startsAt", LocalDate.of(2023, 5, 1));
        input.put("endsAt", LocalDate.of(2023, 8, 30));
        input.put("steps", Arrays.asList("1", "2", "3"));
        input.put("benefits", "VA");
        input.put("area", "IT");
        input.put("location", "Sao Paulo");
        input.put("scholarship", "Computacao");


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
        assertThat(position).hasOnlyFields("id", "userId", "company", "positionName", "role", "startsAt", "endsAt", "steps", "benefits", "area", "location", "scholarship");
        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
        assertThat(positionEntity.getPositionName()).isEqualTo(input.get("positionName"));
    }

    @Test
    @Transactional
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
        assertThat(position).hasOnlyFields("id", "userId", "company", "positionName", "role", "startsAt", "endsAt", "steps", "benefits", "area", "location", "scholarship");
    }

    @Test
    @Transactional
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
    @Transactional
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

//    @Test
//    @Transactional
//    public void updatePosition(){
//        PositionEntity positionEntity = createElementOnDb();
//        String id = positionEntity.getId().toString();
//
//        Map<String, Object> input = new HashMap<String, Object>();
//        input.put("id", id);
//        input.put("userId", companyUserId);
//        input.put("positionName", "Estágio Semestral");
//
//        Position position = this.testerWithAuth.documentName("updatePosition")
//                .variable("input", input)
//                .execute()
//                .path("updatePosition")
//                .entity(Position.class)
//                .get();
//
//        assertThat(position.id()).isNotNull();
//        assertThat(position.userId()).isEqualTo(companyUserId);
//        assertThat(positionEntity.getId()).isEqualTo(Long.parseLong(position.id()));
//        assertThat(position.company()).isEqualTo(positionEntity.getCompany());
//        assertThat(position.role()).isEqualTo(positionEntity.getRole());
//        assertThat(input.get("positionName")).isEqualTo("Estágio Semestral");
//        assertThat(position.startsAt()).isEqualTo(positionEntity.getStartsAt());
//        assertThat(position.endsAt()).isEqualTo(positionEntity.getEndsAt());
//        assertThat(position).hasOnlyFields("id", "userId", "company", "positionName", "role", "startsAt", "endsAt", "steps", "benefits", "area", "location", "scholarship");
//    }

    private PositionEntity createElementOnDb(){
        PositionEntity positionEntity = this.repository.save(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30),
                        Arrays.asList("1", "2", "3"),
                        "VA",
                        "IT",
                        "Sao Paulo",
                        "Computacao"
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
                        LocalDate.of(2023, 8, 30),
                        Arrays.asList("1", "2", "3"),
                        "VA",
                        "IT",
                        "Sao Paulo",
                        "Computacao"
                )
        );
        positionEntitiesToBeSaved.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30),
                        Arrays.asList("1", "2", "3"),
                        "VA",
                        "IT",
                        "Sao Paulo",
                        "Computacao"
                )
        );
        positionEntitiesToBeSaved.add(
                new PositionEntity(
                        Long.parseLong(companyUserId),
                        "Estágio Quadrimestral",
                        "BTG Pactual",
                        "Security Office Intern",
                        LocalDate.of(2023, 5, 1),
                        LocalDate.of(2023, 8, 30),
                        Arrays.asList("1", "2", "3"),
                        "VA",
                        "IT",
                        "Sao Paulo",
                        "Computacao"
                )
        );

        List<PositionEntity> positionEntities = (List<PositionEntity>) this.repository.saveAll(positionEntitiesToBeSaved);
        return positionEntities;
    }
}
