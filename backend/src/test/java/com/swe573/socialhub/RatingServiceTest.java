package com.swe573.socialhub;

import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.service.RatingService;
import com.swe573.socialhub.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.service = new RatingService(ratingRepository, serviceRepository, userRepository, approvalRepository);
    }
}
