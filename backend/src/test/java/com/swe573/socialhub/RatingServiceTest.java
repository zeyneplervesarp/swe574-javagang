package com.swe573.socialhub;

import com.swe573.socialhub.domain.Rating;
import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.service.RatingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RatingServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private UserServiceApprovalRepository approvalRepository;

    private RatingService ratingService;

    private final Principal mockPrincipal = new UserServiceTests.MockPrincipal("test");

    private final Long mockSvcId = 0L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        final var mockUser = new User();
        Long mockPrincipalUserId = 0L;
        mockUser.setId(mockPrincipalUserId);
        mockUser.setReputationPoint(5);
        Mockito.when(userRepository.findUserByUsername(mockPrincipal.getName())).thenReturn(Optional.of(mockUser));


        final var mockServiceCreator = new User();
        Long mockServiceCreatorId = 1L;
        mockServiceCreator.setId(mockServiceCreatorId);
        mockServiceCreator.setReputationPoint(5);
        final var mockSvc = new Service();
        mockSvc.setStatus(ServiceStatus.COMPLETED);
        mockSvc.setCreatedUser(mockServiceCreator);
        Mockito.when(serviceRepository.findById(mockSvcId)).thenReturn(Optional.of(mockSvc));
        this.ratingService = new RatingService(ratingRepository, serviceRepository, userRepository, approvalRepository);
    }

    @Test
    public void RatingService_disallows_InvalidRatings() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ratingService.addOrUpdateRating(mockPrincipal, 0L, -1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ratingService.addOrUpdateRating(mockPrincipal, 0L, 6));
    }

    @Test
    public void RatingService_disallows_NonAttendee() {
        final var mockUserId = 1L;
        final var invalidApproval = new UserServiceApproval();
        final var mockUser = new User();
        mockUser.setId(mockUserId);
        invalidApproval.setUser(mockUser);
        Mockito.when(approvalRepository
                .findUserServiceApprovalByService_IdAndApprovalStatus(mockSvcId, ApprovalStatus.APPROVED)).thenReturn(List.of(invalidApproval));

        Assertions.assertThrows(IllegalArgumentException.class, () -> ratingService.addOrUpdateRating(mockPrincipal, 0L, 3));
    }

    @Test
    public void RatingService_persists_validRating() {
        final var mockSvcId = 0L;
        final var mockUserId = 0L;
        final var validApproval = new UserServiceApproval();
        final var mockUser = new User();
        mockUser.setId(mockUserId);
        validApproval.setUser(mockUser);
        Mockito.when(approvalRepository
                .findUserServiceApprovalByService_IdAndApprovalStatus(mockSvcId, ApprovalStatus.APPROVED)).thenReturn(List.of(validApproval));

        final var expected = new Rating();
        expected.setRating(3);
        Mockito.when(ratingRepository.save(Mockito.any())).thenReturn(expected);

        final var result = ratingService.addOrUpdateRating(mockPrincipal, mockSvcId, 3);
        Assertions.assertEquals(3, result.getRating());
    }

    @Test
    public void RatingService_returns_accurateSummaryForService() {
        final var svc = new Service();
        final var r1 = new Rating();
        r1.setRating(5);
        final var r2 = new Rating();
        r2.setRating(0);
        svc.setRatings(Set.of(r1, r2));
        final var result = ratingService.getServiceRatingSummary(svc);
        Assertions.assertEquals(2.5, result.getRatingAverage());
        Assertions.assertEquals(2, result.getRaterCount());
    }

    @Test
    public void RatingService_returns_accurateSummaryForUser() {
        final var svc1 = new Service();
        final var r1 = new Rating();
        r1.setRating(5);
        final var r2 = new Rating();
        r2.setRating(0);
        svc1.setRatings(Set.of(r1, r2));

        final var svc2 = new Service();
        svc2.setRatings(Set.of(r1, r2));

        final var user = new User();
        user.setCreatedServices(Set.of(svc1, svc2));
        final var result = ratingService.getUserRatingSummary(user);
        Assertions.assertEquals(2.5, result.getRatingAverage());
        Assertions.assertEquals(4, result.getRaterCount());
    }

}
