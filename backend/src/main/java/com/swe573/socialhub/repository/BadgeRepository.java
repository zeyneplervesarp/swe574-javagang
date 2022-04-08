package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}