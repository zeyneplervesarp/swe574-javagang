package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class ApprovedQueryableServiceApprovalRepository implements CreatedQueryableRepository<UserServiceApproval> {

    private final UserServiceApprovalRepository repository;

    public ApprovedQueryableServiceApprovalRepository(UserServiceApprovalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserServiceApproval> findAllByCreatedBetween(Date createdGt, Date createdLt, Pageable pageable) {
        return repository.findAllByApprovedDateBetween(createdGt, createdLt, pageable);
    }
}
