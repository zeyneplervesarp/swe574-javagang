package com.swe573.socialhub;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.enums.FlagType;
import com.swe573.socialhub.repository.FlagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FlagTest {
    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;

    @MockBean
    private FlagRepository flagRepository;

    class MockPrincipal implements Principal {

        public MockPrincipal(String name) {
            this.name = name;
        }

        final String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean implies(Subject subject) {
            return Principal.super.implies(subject);
        }
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(service);
    }
/*
    @Test
    public void FlagUser_shouldThrowErrorIfAlreadyFlagged() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");


        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(testUser.getId(), testUser2.getId(), FlagType.user)).thenReturn(Optional.empty());

        Flag flag = new Flag(FlagType.user, testUser.getId(), testUser2.getId());
        flag.setId(1L);
        Mockito.when(flagRepository.save(Mockito.any(Flag.class))).thenReturn(flag);

        service.flagUser(mockUser, testUser2.getId());

        assertThrows(IllegalArgumentException.class, () -> service.flagUser(mockUser, testUser2.getId()));
    }
*/
    @Test
    public void FlagUser_shouldReturnFlag() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");

        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(testUser.getId(), testUser2.getId(), FlagType.user)).thenReturn(Optional.empty());

        Flag flag = new Flag(FlagType.user, testUser.getId(), testUser2.getId());
        flag.setId(1L);
        Mockito.when(flagRepository.save(Mockito.any(Flag.class))).thenReturn(flag);

        Flag result = service.flagUser(mockUser, testUser2.getId());
        assertEquals(testUser.getId(), result.getFlaggingUser());
        assertEquals(testUser2.getId(), result.getFlaggedEntity());
    }


}