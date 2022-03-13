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
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
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

    private SearchService service;

    private static final int DEFAULT_LIMIT = 20;

    private static final Pageable DEFAULT_PAGE = Pageable.ofSize(DEFAULT_LIMIT);

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.service = new SearchService(userRepository, tagRepository, serviceRepository);

//        Mockito.when(userRepository.findByBioLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
//        Mockito.when(userRepository.findByUsernameLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
//        Mockito.when(tagRepository.findByNameLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
//        Mockito.when(serviceRepository.findByDescriptionLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
//        Mockito.when(serviceRepository.findByLocationLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
//        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
//                .thenReturn(Collections.emptyList());
    }

    @Test
    public void SearchService_canFindByServiceHeader() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(sampleEntity.getHeader(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getHeader(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByServiceDescription() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByDescriptionLikeIgnoreCase(sampleEntity.getDescription(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getDescription(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByServiceLocation() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByLocationLikeIgnoreCase(sampleEntity.getLocation(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE));
        var result = service.search(sampleEntity.getLocation(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserBio() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByBioLikeIgnoreCase(sampleEntity.getBio(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER));
        var result = service.search(sampleEntity.getBio(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserUsername() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByUsernameLikeIgnoreCase(sampleEntity.getUsername(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER));
        var result = service.search(sampleEntity.getUsername(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByTagName() {
        var sampleEntity = sampleTagEntity();

        Mockito.when(tagRepository.findByNameLikeIgnoreCase(sampleEntity.getName(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getName(), "/tags/" + sampleEntity.getId(), SearchMatchType.TAG));
        var result = service.search(sampleEntity.getName(), DEFAULT_LIMIT);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    private Service sampleServiceEntity() {
        var sampleEntity = new Service();
        sampleEntity.setHeader("test");
        sampleEntity.setId(1L);
        sampleEntity.setDescription("description");
        sampleEntity.setLocation("Istanbul");
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
