package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findTagByName(String name);
    @Query("select s from Tag s where lower(s.name) like lower(concat('%', :match, '%'))")
    List<Tag> findByNameLikeIgnoreCase(@Param("match") String stringToMatch);
}