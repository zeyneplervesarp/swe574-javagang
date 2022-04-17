package com.swe573.socialhub.controller;

import com.ibm.common.activitystreams.IO;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.service.ActivityStreamService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.util.Set;

@RestController
public class ActivityStreamsController {

    private final ActivityStreamService service;

    private final IO streamIO = IO.makeDefault();

    public ActivityStreamsController(ActivityStreamService service) {
        this.service = service;
    }

    @GetMapping("/feed")
    public void getStream(OutputStream out) {

        var results = service.fetchFeed(Set.of(
                FeedEvent.EVENT_JOIN_REQUESTED,
                FeedEvent.EVENT_CREATED,
                FeedEvent.EVENT_JOIN_APPROVED,
                FeedEvent.SERVICE_CREATED,
                FeedEvent.SERVICE_JOIN_REQUESTED,
                FeedEvent.SERVICE_JOIN_APPROVED,
                FeedEvent.USER_LOGIN_FAILED,
                FeedEvent.USER_LOGIN_SUCCESSFUL
        ), new TimestampBasedPagination(null, null, 20, null));

        streamIO.write(results, out);
    }
}
