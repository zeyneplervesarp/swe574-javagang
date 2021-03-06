package com.swe573.socialhub;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.SearchPrioritizationService;
import com.swe573.socialhub.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private SearchPrioritizationService prioritizationService;

    private SearchService service;

    private static final int DEFAULT_LIMIT = 20;

    private static final Pageable DEFAULT_PAGE = Pageable.ofSize(DEFAULT_LIMIT);

    private final Principal mockPrincipal = new UserServiceTests.MockPrincipal("test");

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(prioritizationService.assignScoresToServices(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyMap());
        Mockito.when(prioritizationService.assignScoresToUsers(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyMap());
        Mockito.when(userRepository.findUserByUsername("test")).thenReturn(Optional.of(new User()));
        this.service = new SearchService(userRepository, tagRepository, serviceRepository, eventRepository, prioritizationService);
    }

    @Test
    public void SearchService_canFindByServiceHeader() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(sampleEntity.getHeader(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE, 0));
        var result = service.search(sampleEntity.getHeader(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByServiceDescription() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByDescriptionLikeIgnoreCase(sampleEntity.getDescription(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE, 0));
        var result = service.search(sampleEntity.getDescription(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByServiceLocation() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByLocationLikeIgnoreCase(sampleEntity.getLocation(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getHeader(), "/service/" + sampleEntity.getId(), SearchMatchType.SERVICE, 0));
        var result = service.search(sampleEntity.getLocation(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserBio() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByBioLikeIgnoreCase(sampleEntity.getBio(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER, 0));
        var result = service.search(sampleEntity.getBio(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByUserUsername() {
        var sampleEntity = sampleUserEntity();

        Mockito.when(userRepository.findByUsernameLikeIgnoreCase(sampleEntity.getUsername(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getUsername(), "/user/" + sampleEntity.getId(), SearchMatchType.USER, 0));
        var result = service.search(sampleEntity.getUsername(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_canFindByTagName() {
        var sampleEntity = sampleTagEntity();

        Mockito.when(tagRepository.findByNameLikeIgnoreCase(sampleEntity.getName(), DEFAULT_PAGE))
                .thenReturn(List.of(sampleEntity));

        var expectedResult = List.of(
                new SearchMatchDto(sampleEntity.getName(), "/tags/" + sampleEntity.getId(), SearchMatchType.TAG, 0));
        var result = service.search(sampleEntity.getName(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertIterableEquals(expectedResult, result);
    }

    @Test
    public void SearchService_disallows_MoreThan50Limit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.search("test", 51, mockPrincipal));
    }

    @Test
    public void SearchService_clips_ifFoundMoreThanRequested() {
        var differentService = new Service();
        differentService.setHeader("abc");
        differentService.setId(0L);
        var sampleList = List.of(sampleServiceEntity(), differentService);
        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(sampleList);

        var result = service.search(sampleList.get(0).getHeader(), sampleList.size() - 1, mockPrincipal);
        Assertions.assertEquals(sampleList.size() - 1, result.size());
    }

    @Test
    public void SearchService_discardsDuplicateItems() {
        var sampleEntity = sampleServiceEntity();

        Mockito.when(serviceRepository.findByHeaderLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(sampleEntity));
        Mockito.when(serviceRepository.findByDescriptionLikeIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(sampleEntity));

        var result = service.search(sampleEntity.getHeader(), DEFAULT_LIMIT, mockPrincipal);
        Assertions.assertEquals(1, result.size());
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
