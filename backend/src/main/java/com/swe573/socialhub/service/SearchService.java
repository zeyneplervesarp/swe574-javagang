package com.swe573.socialhub.service;

import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ServiceRepository serviceRepository;

    public SearchService(UserRepository userRepository, TagRepository tagRepository, ServiceRepository serviceRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<SearchMatchDto> search(String stringToMatch) {
        // TODO: implement
        return Collections.emptyList();
    }
}
