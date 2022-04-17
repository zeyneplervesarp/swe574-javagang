package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.domain.UserEventApproval;
import com.swe573.socialhub.repository.UserEventApprovalRepository;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class ApprovedQueryableEventApprovalRepository implements CreatedQueryableRepository<UserEventApproval> {

    private final UserEventApprovalRepository repository;

    public ApprovedQueryableEventApprovalRepository(UserEventApprovalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserEventApproval> findAllByCreatedBetween(Date createdGt, Date createdLt, Pageable pageable) {
        return repository.findAllByApprovedDateBetween(createdGt, createdLt, pageable);
    }
}
