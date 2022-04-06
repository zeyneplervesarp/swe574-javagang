package com.swe573.socialhub;

import com.swe573.socialhub.domain.Badge;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.BadgeType;
import com.swe573.socialhub.repository.BadgeRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.BadgeService;
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

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BadgeServiceTests {

    @MockBean
    private BadgeRepository repository;

    @MockBean
    private UserRepository userRepository;

    private BadgeService service;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.service = new BadgeService(repository);
    }


    @Test
    public void contextLoads() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void getAllBadges_ReturnsDtoList() {

        var mockUser = new User();
        Long mockPrincipalUserId = 0L;
        mockUser.setId(mockPrincipalUserId);

        var sampleBadge = new Badge(mockUser, BadgeType.superMentor);
        var sampleBadge2 = new Badge(mockUser, BadgeType.mentor);

        Mockito.when(repository.findAll()).thenReturn(List.of(sampleBadge, sampleBadge2));

        var dtoList = service.getAllBadges();

        assertEquals(2, dtoList.stream().count());
    }


    @Test
    public void checkNewcomerBadgeForServiceApproval_RemovesBadge() {
        var mockUser = new User();
        mockUser.setId(0L);
        var newcomerBadge = new Badge();
        newcomerBadge.setBadgeType(BadgeType.newcomer);
        mockUser.setServiceApprovalSet(new HashSet<>());
        mockUser.setBadges(new HashSet<>() {{
            add(newcomerBadge);
        }});
        mockUser.setServiceApprovalSet(new HashSet<>() {{
            for (int i = 0; i < 10; i++) {
                var approval = new UserServiceApproval();
                approval.setApprovalStatus(ApprovalStatus.APPROVED);
                add(approval);
            }
        }});


        var updatedUser = service.checkNewcomerBadge(mockUser);

        assertEquals(0, updatedUser.getBadges().stream().count());
        assertFalse(updatedUser.getBadges().stream().anyMatch(x -> x.getBadgeType() == BadgeType.newcomer));

    }

    @Test
    public void mapToDto_ReturnsDto() {

        var mockUser = new User();
        Long mockPrincipalUserId = 0L;
        mockUser.setId(mockPrincipalUserId);

        var sampleBadge = new Badge(mockUser, BadgeType.superMentor);
        sampleBadge.setId(0L);

        var dto = service.mapToDto(sampleBadge);

        assertEquals(BadgeType.superMentor, dto.getBadgeType());
        assertEquals(0L, dto.getId());
    }
}
