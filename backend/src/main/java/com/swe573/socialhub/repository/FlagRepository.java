package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.enums.FlagStatus;
import com.swe573.socialhub.enums.FlagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlagRepository extends JpaRepository<Flag, Long> {
    Optional<Flag> findFlagByFlaggingUserAndFlaggedEntityAndType(Long flaggingUserId, Long flaggedEntityId, FlagType type);
    long countByTypeAndFlaggedEntityAndStatus(FlagType type, Long flaggedEntityId, FlagStatus status);

}
