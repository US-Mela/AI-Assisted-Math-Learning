package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;

import java.util.List;
import java.util.UUID;

public interface TopicService {

    GetTopicsResponse getTopicsResponse();

    List<TopicDto> getTopics();

    TopicDto getTopicById(UUID topicId);
}
