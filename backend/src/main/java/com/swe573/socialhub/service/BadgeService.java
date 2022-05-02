package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Badge;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.BadgeDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.BadgeType;
import com.swe573.socialhub.repository.BadgeRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository repository;

    @Autowired
    private UserRepository userRepository;

    public BadgeService(BadgeRepository repository)
    {
        this.repository = repository;
    }


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

    public BadgeDto mapToDto(Badge badge) {
        return new BadgeDto(badge.getId(), badge.getBadgeType());
    }

    public User checkBadgesAfterApproval(User user) {
        //check if user has 10 services, if so remove their newcomer badge
        var userBadges = user.getBadges();
        var participatedServiceCount = user.getServiceApprovalSet().stream().filter(x->x.getApprovalStatus() == ApprovalStatus.APPROVED).count();

        //region newcomer
        var userHasNewcomerBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.newcomer);
        var participatedServiceIsMoreThanRequiredBadgeCount = participatedServiceCount >= 10;
        if (userHasNewcomerBadge && participatedServiceIsMoreThanRequiredBadgeCount)
        {
            var badge = userBadges.stream().filter(x->x.getBadgeType() == BadgeType.newcomer).findFirst().get();
            user.removeBadge(badge);
        }
        //endregion

        //region regular
        var userHasRegularBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.regular);
        if (!userHasRegularBadge)
        {
            if (participatedServiceCount >= 20)
            {
                var regularBadge = new Badge(user,BadgeType.regular);
                user.addBadge(regularBadge);
            }
        }

        //endregion

        return user;
    }
}
