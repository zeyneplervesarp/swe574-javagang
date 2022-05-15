package com.swe573.socialhub.controller;

import com.ibm.common.activitystreams.IO;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.service.ActivityStreamService;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.security.Principal;
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
            @RequestParam(required = false) String filterKey,
            OutputStream out
    ) {
        Set<FeedEvent> eventsToFetchForAdmin;
        if(filterKey == null || filterKey.equals("") || filterKey.equals("undefined")) {
            eventsToFetchForAdmin = Set.of(
                    FeedEvent.EVENT_JOIN_REQUESTED,
                    FeedEvent.EVENT_CREATED,
                    FeedEvent.EVENT_JOIN_APPROVED,
                    FeedEvent.SERVICE_CREATED,
                    FeedEvent.SERVICE_JOIN_REQUESTED,
                    FeedEvent.SERVICE_JOIN_APPROVED,
                    FeedEvent.USER_LOGIN_FAILED,
                    FeedEvent.USER_LOGIN_SUCCESSFUL,
                    FeedEvent.FOLLOW
            );
        } else {
                if(filterKey.equals("service_created")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.SERVICE_CREATED
                    );
                } else if (filterKey.equals("service_join_requested")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.SERVICE_JOIN_REQUESTED
                    );
                } else if (filterKey.equals("service_join_approved")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.SERVICE_JOIN_APPROVED
                    );
                } else if (filterKey.equals("user_login_failed")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.USER_LOGIN_FAILED
                    );
                } else if (filterKey.equals("user_login_successful")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.USER_LOGIN_SUCCESSFUL
                    );
                } else if (filterKey.equals("follow")) {
                    eventsToFetchForAdmin = Set.of(
                            FeedEvent.FOLLOW
                    );
                } else {
                        throw new IllegalArgumentException("Filter key must be one of these: service_created, service_join_requested, service_join_approved, user_login_failed, user_login_successful, folllow.");
                }
        }

        final var results = service.fetchFeedValidated(
                principal,
                eventsToFetchForAdmin,
                ControllerUtils.parseTimestampPagination(gt, lt, size, sort),
                "admin/feed",
                filterKey
        );
        streamIO.write(results, out);
    }
}
