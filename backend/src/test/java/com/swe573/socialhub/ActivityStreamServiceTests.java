package com.swe573.socialhub;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Activity;
import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.enums.LoginAttemptType;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.LoginAttemptRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.ActivityStreamService;
import com.swe573.socialhub.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ActivityStreamServiceTests {

    @MockBean
    private LoginAttemptRepository loginAttemptRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    private ActivityStreamService service;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.service = new ActivityStreamService(loginAttemptRepository, userRepository, serviceRepository, eventRepository);
    }

    @Test
    public void ActivityStreamService_canFind_SuccessfulLoginAttempts() {
        var loginUser1 = new User();
        loginUser1.setId(0L);
        loginUser1.setUsername("tester");

        var loginUser2 = new User();
        loginUser2.setId(1L);
        loginUser2.setUsername("tester2");

        var successfulLogin1 = new LoginAttempt(312L, loginUser1.getUsername(), LoginAttemptType.SUCCESSFUL, new Date(System.currentTimeMillis() - 3600 * 1000));
        var successfulLogin2 = new LoginAttempt(1123L, loginUser2.getUsername(), LoginAttemptType.SUCCESSFUL, new Date());

        Mockito.when(loginAttemptRepository.findAllSuccessfulByCreatedBetween(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(successfulLogin2, successfulLogin1));

        Mockito.when(userRepository.findAllByUsername(Mockito.any()))
                .thenReturn(List.of(loginUser1, loginUser2));

        var response = service.fetchFeed(Set.of(FeedEvent.USER_LOGIN_SUCCESSFUL), new TimestampBasedPagination(null, null, 20, Sort.Direction.ASC));
        var actorIdList = StreamSupport.stream(response.items().spliterator(), false)
                .map(a -> getActor(a).id())
                .collect(Collectors.toList());
        Assertions.assertEquals(2, actorIdList.size());
        Assertions.assertTrue(actorIdList.contains("0"));
        Assertions.assertTrue(actorIdList.contains("1"));
    }

    @Test
    public void ActivityStreamService_canFind_UnsuccessfulLoginAttempts() {
        var loginUser2 = new User();
        loginUser2.setId(1L);
        loginUser2.setUsername("tester2");

        var wrongUserLogin = new LoginAttempt(312L, "tester", LoginAttemptType.WRONG_USERNAME, new Date(System.currentTimeMillis() - 3600 * 1000));
        var wrongPwLogin = new LoginAttempt(1123L, loginUser2.getUsername(), LoginAttemptType.WRONG_PASSWORD, new Date());

        Mockito.when(loginAttemptRepository.findAllUnsuccessfulByCreatedBetween(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(wrongUserLogin, wrongPwLogin));

        Mockito.when(userRepository.findAllByUsername(Mockito.any()))
                .thenReturn(List.of(loginUser2));

        var response = service.fetchFeed(Set.of(FeedEvent.USER_LOGIN_FAILED), new TimestampBasedPagination(null, null, 20, Sort.Direction.ASC));
        var objectValueTypes = StreamSupport.stream(response.items().spliterator(), false)
                .map(a -> getObject(a).objectTypeString())
                .collect(Collectors.toList());
        Assertions.assertEquals(2, objectValueTypes.size());
        Assertions.assertEquals(objectValueTypes.get(0), "login-attempt");
        Assertions.assertEquals(objectValueTypes.get(1), "user");
    }

    @Test
    public void ActivityStreamService_canFind_CreatedServices() {
        var loginUser1 = new User();
        loginUser1.setId(0L);
        loginUser1.setUsername("tester");

        var loginUser2 = new User();
        loginUser2.setId(1L);
        loginUser2.setUsername("tester2");

        var testSvc1 = Service.createPhysical(0L, "test svc 1", "test desc 1", "ist", null, 10, 10, 0, loginUser1, 0D, 0D, null);
        testSvc1.setCreated(new Date(System.currentTimeMillis() - 3600 * 1000));
        var testSvc2 = Service.createPhysical(1L, "test svc 2", "test desc 2", "ant", null, 10, 10, 0, loginUser2, 0D, 0D, null);
        testSvc2.setCreated(new Date());


        Mockito.when(serviceRepository.findAllByCreatedBetween(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(testSvc1, testSvc2));

        var response = service.fetchFeed(Set.of(FeedEvent.SERVICE_CREATED), new TimestampBasedPagination(null, null, 20, Sort.Direction.ASC));
        var actorIdList = StreamSupport.stream(response.items().spliterator(), false)
                .map(a -> getActor(a).id())
                .collect(Collectors.toList());
        Assertions.assertEquals(2, actorIdList.size());
        Assertions.assertTrue(actorIdList.contains("0"));
        Assertions.assertTrue(actorIdList.contains("1"));
    }

    @Test
    public void ActivityStreamService_canFind_CreatedEvents() {
        var loginUser1 = new User();
        loginUser1.setId(0L);
        loginUser1.setUsername("tester");

        var loginUser2 = new User();
        loginUser2.setId(1L);
        loginUser2.setUsername("tester2");

        var testSvc1 = new Event(0L, "test svc 1", "test desc 1", "ist", null, 10, 10, 0, loginUser1, 0D, 0D, null);
        testSvc1.setCreated(new Date(System.currentTimeMillis() - 3600 * 1000));
        var testSvc2 = new Event(1L, "test svc 2", "test desc 2", "ant", null, 10, 10, 0, loginUser2, 0D, 0D, null);
        testSvc2.setCreated(new Date());


        Mockito.when(eventRepository.findAllByCreatedBetween(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(testSvc1, testSvc2));

        var response = service.fetchFeed(Set.of(FeedEvent.EVENT_CREATED), new TimestampBasedPagination(null, null, 20, Sort.Direction.ASC));
        var actorIdList = StreamSupport.stream(response.items().spliterator(), false)
                .map(a -> getActor(a).id())
                .collect(Collectors.toList());
        Assertions.assertEquals(2, actorIdList.size());
        Assertions.assertTrue(actorIdList.contains("0"));
        Assertions.assertTrue(actorIdList.contains("1"));
    }

    @Test
    public void pagination_returnsNextPage_correctly() {
        final var gt = new Date(System.currentTimeMillis() - 3600 * 1000);
        final var currentPage = new TimestampBasedPagination(gt, null, 20, Sort.Direction.ASC);
        final var lastEntryDate = new Date();

        final var nextPage = currentPage.nextPage(lastEntryDate);

        Assertions.assertEquals(currentPage.getSortDirection(), nextPage.getSortDirection());
        Assertions.assertEquals(currentPage.getSize(), nextPage.getSize());
        Assertions.assertEquals(lastEntryDate, nextPage.getGreaterThan());
        Assertions.assertEquals(currentPage.getLowerThan(), nextPage.getLowerThan());
    }

    private <A extends ASObject> ASObject getObject(A item) {
        return ((ASObject)((Activity) item).object().iterator().next());
    }

    private <A extends ASObject> ASObject getActor(A item) {
        return ((ASObject)((Activity) item).actor().iterator().next());
    }


}
