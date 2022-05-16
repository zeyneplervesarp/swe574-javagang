package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.enums.FlagStatus;
import com.swe573.socialhub.enums.FlagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlagRepository extends JpaRepository<Flag, Long> {
    Optional<Flag> findFlagByFlaggingUserAndFlaggedEntityAndType(Long flaggingUserId, Long flaggedEntityId, FlagType type);
    long countByTypeAndFlaggedEntityAndStatus(FlagType type, Long flaggedEntityId, FlagStatus status);

    @Modifying
    @Query("UPDATE Flag f SET f.status = :status WHERE f.type = :type AND f.flaggedEntity = :entityId")
    void dismissFlags(@Param("status") FlagStatus status, @Param("type") FlagType type, @Param("entityId") Long id);

    List<Flag> findAllByTypeAndStatus(FlagType type, FlagStatus status);
}
