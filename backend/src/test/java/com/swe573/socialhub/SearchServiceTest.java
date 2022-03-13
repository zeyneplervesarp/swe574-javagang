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
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(sampleEntity.getHeader()))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getHeader());
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByServiceDescription() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByDescriptionLikeIgnoreCase(sampleEntity.getDescription()))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getHeader());
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserBio() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByBioLikeIgnoreCase(sampleEntity.getBio()))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER));
        var result = service.search(sampleEntity.getBio());
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserUsername() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByUsernameLikeIgnoreCase(sampleEntity.getUsername()))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER));
        var result = service.search(sampleEntity.getBio());
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByTagName() {
        var sampleEntity = sampleTagEntity();

        Mockito.when(tagRepository.findByNameLikeIgnoreCase(sampleEntity.getName()))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getName(), "/tags/" + sampleEntity.getId(), SearchMatchType.TAG));
        var result = service.search(sampleEntity.getName());
        Assertions.assertIterableEquals(expectedResult, result);
    }

    private Service sampleServiceEntity() {
        var sampleEntity = new Service();
        sampleEntity.setHeader("test");
        sampleEntity.setId(1L);
        sampleEntity.setDescription("description");
        return sampleEntity;
    }

    private User sampleUserEntity() {
        var sampleEntity = new User();
        sampleEntity.setBio("sample bio");
        sampleEntity.setId(1L);
        sampleEntity.setUsername("username");
        return sampleEntity;
    }

    private Tag sampleTagEntity() {
        var sampleEntity = new Tag();
        sampleEntity.setName("Tag");
        sampleEntity.setId(1L);
        return sampleEntity;
    }
}
