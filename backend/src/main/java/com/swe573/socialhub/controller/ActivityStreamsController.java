package com.swe573.socialhub.controller;

import com.ibm.common.activitystreams.IO;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.service.ActivityStreamService;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Set;

@RestController
public class ActivityStreamsController {

    private final ActivityStreamService service;

    private final IO streamIO = IO.makeDefault();

    public ActivityStreamsController(ActivityStreamService service) {
        this.service = service;
    }

    @GetMapping("/admin/feed")
    public void getAdminStream(
            Principal principal,
            @RequestParam(required = false) Long gt,
            @RequestParam(required = false) Long lt,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            OutputStream out
    ) {
        final var eventsToFetchForAdmin = Set.of(
                FeedEvent.EVENT_JOIN_REQUESTED,
                FeedEvent.EVENT_CREATED,
                FeedEvent.EVENT_JOIN_APPROVED,
                FeedEvent.SERVICE_CREATED,
                FeedEvent.SERVICE_JOIN_REQUESTED,
                FeedEvent.SERVICE_JOIN_APPROVED,
                FeedEvent.USER_LOGIN_FAILED,
                FeedEvent.USER_LOGIN_SUCCESSFUL
        );

        final var sortDirection = sort != null && sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        final var results = service.fetchFeedValidated(
                principal,
                eventsToFetchForAdmin,
                new TimestampBasedPagination(
                        gt != null ? new Date(gt) : null,
                        lt != null ? new Date(lt) : null,
                        size != null ? size : 20,
                        sortDirection
                ),
                "/admin/feed"
        );

        streamIO.write(results, out);
    }
}
