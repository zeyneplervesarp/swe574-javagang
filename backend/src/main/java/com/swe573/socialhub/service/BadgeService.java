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

    public Long save(BadgeType type, User badgeOwner) {
        try {
            var entity = mapToEntity(type,badgeOwner);
            var savedEntity = repository.save(entity);
            return savedEntity.getId();
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save badge to db");
        }
    }


    public BadgeDto mapToDto(Badge badge) {
        return new BadgeDto(badge.getId(), badge.getBadgeType());
    }


    private Badge mapToEntity(BadgeType type, User owner) {
        return new Badge(owner, type);
    }

    public User checkBadges(User user) {
        var userBadges = user.getBadges();
        var participatedServiceCount = user.getServiceApprovalSet().stream().filter(x->x.getApprovalStatus() == ApprovalStatus.APPROVED).count();
        var participatedServiceNewcomerCount = user.getServiceApprovalSet().stream().filter(x->x.getApprovalStatus() == ApprovalStatus.APPROVED && x.getUser().getBadges().stream().anyMatch(y->y.getBadgeType() == BadgeType.newcomer)).count();
        var userReputationPoint = user.getReputationPoint();


        //region newcomer
        // check if user has 10 services, if so remove their newcomer badge

        var userHasNewcomerBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.newcomer);
        var participatedServiceIsMoreThanRequiredBadgeCount = participatedServiceCount >= 10;
        if (userHasNewcomerBadge && participatedServiceIsMoreThanRequiredBadgeCount)
        {
            var badge = userBadges.stream().filter(x->x.getBadgeType() == BadgeType.newcomer).findFirst().get();
            user.removeBadge(badge);
        }
        //endregion

        //region regular
        var userHasRegularBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.newcomer);
        if (!userHasRegularBadge)
        {
            if (participatedServiceCount >= 20)
            {
                var regularBadge = new Badge(user,BadgeType.regular);
                user.addBadge(regularBadge);
            }
        }

        //endregion

        //region mentor
        var userHasMentorBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.mentor);

        if (!userHasMentorBadge)
        {
            if (participatedServiceNewcomerCount >= 5)
            {
                var mentorBadge = new Badge(user,BadgeType.mentor);
                user.addBadge(mentorBadge);
            }
        }

        //endregion

        //region super mentor
        var userHasSuperMentorBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.superMentor);

        if (!userHasSuperMentorBadge)
        {
            if (participatedServiceNewcomerCount >= 10)
            {
                var mentorBadge = new Badge(user,BadgeType.superMentor);
                user.addBadge(mentorBadge);
            }
        }
        //endregion

        //region reputable
        var userHasReputableBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.reputable);

        if (!userHasReputableBadge)
        {
            if (userReputationPoint >= 10)
            {
                var reputableBadge = new Badge(user,BadgeType.reputable);
                user.addBadge(reputableBadge);
            }
        }
        //endregion

        //region reputable
        var userHasWellKnownBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.wellKnown);

        if (!userHasWellKnownBadge)
        {
            if (userReputationPoint >= 10)
            {
                var wellKnownBadge = new Badge(user,BadgeType.wellKnown);
                user.addBadge(wellKnownBadge);
            }
        }
        //endregion

        //region reputable
        var userHasGuruBadge = userBadges.stream().anyMatch(x->x.getBadgeType() == BadgeType.guru);

        if (!userHasGuruBadge)
        {
            if (userReputationPoint >= 10)
            {
                var guruBadge = new Badge(user,BadgeType.guru);
                user.addBadge(guruBadge);
            }
        }
        //endregion

        return user;
    }
}
