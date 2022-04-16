package com.swe573.socialhub.service;

import com.ibm.common.activitystreams.Collection;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ActivityStreamService {

    public Collection fetchFeed(Set<FeedEvent> eventTypes, TimestampBasedPagination pagination) {
        return null; // TODO: unimplemented
    }

}
