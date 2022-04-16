package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.domain.LoginAttempt;
import com.swe573.socialhub.repository.LoginAttemptRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class CreatedQueryableSuccessfulLoginAttemptRepository implements CreatedQueryableRepository<LoginAttempt> {

    private final LoginAttemptRepository loginAttemptRepository;

    public CreatedQueryableSuccessfulLoginAttemptRepository(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public List<LoginAttempt> findAllByCreatedBetween(Date createdGt, Date createdLt, Pageable pageable) {
        return loginAttemptRepository.findAllSuccessfulByCreatedBetween(createdGt, createdLt, pageable);
    }
}
