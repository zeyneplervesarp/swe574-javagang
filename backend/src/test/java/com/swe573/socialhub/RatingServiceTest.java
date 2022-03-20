package com.swe573.socialhub;

import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.service.RatingService;
import com.swe573.socialhub.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;

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

    private RatingService service;

    private final Principal mockPrincipal = new UserServiceUnitTests.MockPrincipal("test");

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.service = new RatingService(ratingRepository, serviceRepository, userRepository, approvalRepository);
    }

    @Test
    public void RatingService_disallows_InvalidRatings() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addOrUpdateRating(mockPrincipal, 0L, -1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addOrUpdateRating(mockPrincipal, 0L, 6));
    }

}
