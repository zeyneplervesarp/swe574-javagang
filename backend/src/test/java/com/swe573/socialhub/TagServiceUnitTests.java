package com.swe573.socialhub;


import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.TagService;
import com.swe573.socialhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TagServiceUnitTests {

    @TestConfiguration
    static class TagServiceUnitTestsConfiguration {
        @Bean
        TagService service() {
            return new TagService();
        }
    }

    @Autowired
    private TagService service;

    @MockBean
    private TagRepository repository;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2g1c", "bimbos", "porn"})
    public void IllegalTag_ShouldReturnTrue(String illegalKeyword ) {
        var tag = new Tag(illegalKeyword);
        var result = service.keywordIsIllegal(tag);
        assertTrue(result);
    }



}
