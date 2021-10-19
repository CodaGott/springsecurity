package com.websecurity.controller;

import com.websecurity.model.Poll;
import com.websecurity.payload.*;
import com.websecurity.repository.PollRepository;
import com.websecurity.repository.UserRepository;
import com.websecurity.repository.VoteRepository;
import com.websecurity.security.CurrentUser;
import com.websecurity.security.UserPrincipal;
import com.websecurity.service.PollService;
import com.websecurity.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/polls")
@Slf4j
public class PollController {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    @GetMapping
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser, @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){
        return pollService.getAllPolls(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest){
        Poll poll = pollService.createPoll(pollRequest);

        URI loaction = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();

        return ResponseEntity.created(loaction)
                .body(new ApiResponse(true, "Poll Creted Successfully"));
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable Long pollId){
        return pollService.getPollById(pollId, currentUser);
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    public PollResponse castVote(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable Long pollId,
                                 @Valid @RequestBody VoteRequest voteRequest){
        return pollService.castVoteAndGetUpdate(pollId, voteRequest, currentUser);
    }
}
