package com.swe573.socialhub;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SearchServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private TagRepository tagRepository;

    private final SearchService service = new SearchService(userRepository, tagRepository, serviceRepository);

    @Test
    public void SearchService_canFindByServiceHeader() {
        var sampleEntity = new Service();
        sampleEntity.setHeader("test");
        sampleEntity.setId(1L);

        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase("test"))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getHeader());
        Assertions.assertIterableEquals(expectedResult, result);
    }


}
