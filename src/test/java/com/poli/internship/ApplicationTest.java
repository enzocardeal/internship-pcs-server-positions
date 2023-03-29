package com.poli.internship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.poli.internship.api.context.JWTService;
import com.poli.internship.data.datasource.ApplicationDataSource;
import com.poli.internship.data.entity.PositionEntity;
import com.poli.internship.data.repository.ApplicationRepository;
import com.poli.internship.data.repository.PositionRepository;
import com.poli.internship.domain.models.CurriculumAuthorizationActionType;
import com.poli.internship.domain.models.UserType;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.poli.internship.InternshipApplication.LOGGER;
import static com.poli.internship.domain.models.ApplicationModel.Application;
import static com.poli.internship.domain.models.AuthTokenPayloadModel.AuthTokenPayload;
import static com.poli.internship.domain.models.ApplicationModel.ApplicationMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTest {
    @Autowired
    private HttpGraphQlTester tester;
    @Autowired
    JWTService jwtService;

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String pubsubEmulatorHostport;
    private ManagedChannel channel;
    private Subscriber subscriber;
    private MessageReceiver receiver;
    private String messageReceived;
    private String messageId;

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
    public void createApplication() throws JsonProcessingException {
        pubsubServiceUp();
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
        try {
            subscriber.startAsync().awaitRunning();
            subscriber.awaitTerminated(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        assertThat(application.position()).isNotNull();
        assertThat(application.userId()).isEqualTo(userId);
        assertThat(application.position().positionName()).isEqualTo(positionEntity.getPositionName());

        assertThat(messageId).isNotNull();
        assertThat(messageReceived).isEqualTo(
                (new ObjectMapper())
                        .writeValueAsString(
                                (
                                        new ApplicationMessage(
                                        CurriculumAuthorizationActionType.GRANT,
                                        Long.toString(positionEntity.getUserId()),
                                        application.userId()
                                        )
                                )
                        )
        );
        messageReceived = null;
        messageId = null;
        pubsubServiceDown();
    }

    @Test
    public void deleteApplication() throws JsonProcessingException {
        pubsubServiceUp();
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

        try {
            subscriber.startAsync().awaitRunning();
            subscriber.awaitTerminated(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        assertTrue(deleted);

        assertThat(messageId).isNotNull();
        assertThat(messageReceived).isEqualTo(
                (new ObjectMapper())
                        .writeValueAsString(
                                (
                                        new ApplicationMessage(
                                                CurriculumAuthorizationActionType.REVOKE,
                                                Long.toString(positionEntity.getUserId()),
                                                application.userId()
                                        )
                                )
                        )
        );
        messageReceived = null;
        messageId = null;
        pubsubServiceDown();
    }

    @Test
    public void getAllApplicationsByCompany(){
        tokenPayload = new AuthTokenPayload(
                companyUserId,
                "rh@btgpactual.com",
                UserType.COMPANY,
                3600);
        authToken = this.jwtService.createAuthorizationToken(tokenPayload);
        testerWithAuth = this.tester.mutate().header("Authorization", authToken).build();

        createApplications();

        List<HashMap> applications = this.testerWithAuth.documentName("getAllApplicationsByCompany")
                .execute()
                .path("getAllApplicationsByCompany")
                 .entity(List.class)
                .get();

        assertThat(applications.size()).isEqualTo(3);
        assertThat(!((HashMap)applications.get(0).get("position")).isEmpty());
        assertThat(!((HashMap)applications.get(1).get("position")).isEmpty());
        assertThat(!((HashMap)applications.get(2).get("position")).isEmpty());
        assertThat((String)applications.get(0).get("userId")).isEqualTo(userId);
        assertThat((String)applications.get(1).get("userId")).isEqualTo(userId);
        assertThat((String)applications.get(2).get("userId")).isEqualTo(userId);
    }

    @Test
    public void getAllApplicationsByStudent(){
        createApplications();

        List<HashMap> applications = this.testerWithAuth.documentName("getAllApplicationsByStudent")
                .execute()
                .path("getAllApplicationsByStudent")
                .entity(List.class)
                .get();

        assertThat(applications.size()).isEqualTo(3);
        assertThat(!((HashMap)applications.get(0).get("position")).isEmpty());
        assertThat(!((HashMap)applications.get(1).get("position")).isEmpty());
        assertThat(!((HashMap)applications.get(2).get("position")).isEmpty());
        assertThat((String)applications.get(0).get("userId")).isEqualTo(userId);
        assertThat((String)applications.get(1).get("userId")).isEqualTo(userId);
        assertThat((String)applications.get(2).get("userId")).isEqualTo(userId);
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

    private void pubsubServiceUp() {
        channel = ManagedChannelBuilder.forTarget(pubsubEmulatorHostport).usePlaintext().build();
        TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        try{
            ProjectSubscriptionName subscriptionName =
                    ProjectSubscriptionName.of("PROJECT_TEST_ID", "curriculum-authorization-sub");
            receiver =
                    (PubsubMessage message, AckReplyConsumer consumer) -> {
                        messageId = message.getMessageId();
                        messageReceived = message.getData().toStringUtf8();
                        consumer.ack();
                    };
            subscriber = Subscriber.newBuilder(subscriptionName, receiver)
                    .setCredentialsProvider(credentialsProvider)
                    .setChannelProvider(channelProvider)
                    .build();

        } catch (Exception e){
            subscriber.stopAsync();
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void pubsubServiceDown(){
        subscriber.stopAsync();
        channel.shutdown();
    }

    private void createApplications(){
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
    }
}

