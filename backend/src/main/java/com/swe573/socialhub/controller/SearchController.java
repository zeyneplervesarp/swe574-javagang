package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<SearchMatchDto> search(@RequestParam("query") String queryString, @RequestParam("limit") Integer limit, Principal principal) {
        try {
            return service.search(queryString, limit, principal);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}
