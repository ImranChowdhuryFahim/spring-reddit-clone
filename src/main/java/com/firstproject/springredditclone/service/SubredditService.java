package com.firstproject.springredditclone.service;

import com.firstproject.springredditclone.dto.SubredditDto;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.mapper.SubredditMapper;
import com.firstproject.springredditclone.model.Subreddit;
import com.firstproject.springredditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    public SubredditDto save(SubredditDto subredditDto)
    {
      Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
      subredditDto.setId(subreddit.getId());
      return subredditDto;
    }

    public List<SubredditDto> getAll()
    {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(toList());

    }

    public SubredditDto getSubreddit(Long id)
    {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with this "+ id));
        return subredditMapper.mapSubredditToDto(subreddit);
    }




}