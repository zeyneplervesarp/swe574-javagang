package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Badge;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.BadgeDto;
import com.swe573.socialhub.enums.BadgeType;
import com.swe573.socialhub.repository.BadgeRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository repository;


    public List<BadgeDto> getAllBadges() {
        try {
            var entities = repository.findAll();
            var list = entities.stream().map(tag -> mapToDto(tag)).collect(Collectors.toUnmodifiableList());
            return list;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("There was a problem trying to get badges: " + e.getMessage());
        }
    }

    public Long save(BadgeType type, User badgeOwner) {
        try {
            var entity = mapToEntity(type,badgeOwner);
            var savedEntity = repository.save(entity);
            return savedEntity.getId();
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save badge to db");
        }
    }


    private BadgeDto mapToDto(Badge badge) {
        return new BadgeDto(badge.getId(), badge.getBadgeType());
    }


    private Badge mapToEntity(BadgeType type, User owner) {
        return new Badge(owner, type);
    }
}
