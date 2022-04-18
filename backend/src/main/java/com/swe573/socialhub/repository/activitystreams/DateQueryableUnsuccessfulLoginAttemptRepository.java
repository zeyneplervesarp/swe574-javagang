package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.domain.LoginAttempt;
import com.swe573.socialhub.repository.LoginAttemptRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DateQueryableUnsuccessfulLoginAttemptRepository implements DateQueryableRepository<LoginAttempt> {

    private final LoginAttemptRepository loginAttemptRepository;

    public DateQueryableUnsuccessfulLoginAttemptRepository(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public List<LoginAttempt> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable) {
        return loginAttemptRepository.findAllUnsuccessfulByCreatedBetween(createdGt, createdLt, pageable);
    }
}
