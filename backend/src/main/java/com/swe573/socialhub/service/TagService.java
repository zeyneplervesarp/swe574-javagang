package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.dto.TagDto;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;


    public List<TagDto> findAllTags() {
        var entities = tagRepository.findAll();
        var list = entities.stream().map(tag -> mapToDto(tag)).collect(Collectors.toUnmodifiableList());
        return list;
    }

    public Optional<TagDto> findById(Long id) {

        Optional<Tag> tag = tagRepository.findById(id);

        if (tag.isPresent()) {
            var dto = mapToDto(tag.get());
            return Optional.ofNullable(dto);
        } else {
            throw new IllegalArgumentException("No tags have been found");
        }
    }

    public Long save(TagDto dto) {
        try {
            var entity = mapToEntity(dto);
            var savedEntity = tagRepository.save(entity);
            return savedEntity.getId();
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save tag to db");
        }


    }



    private TagDto mapToDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }


    private Tag mapToEntity(TagDto dto) {
        return new Tag(dto.getName());
    }
}
