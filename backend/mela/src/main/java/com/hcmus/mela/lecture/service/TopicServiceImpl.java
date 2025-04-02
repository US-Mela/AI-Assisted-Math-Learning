package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.mapper.TopicMapper;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.lecture.repository.TopicRepository;
import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    public GetTopicsResponse getTopicsResponse() {
        GetTopicsResponse response = new GetTopicsResponse();
        List<Topic> topics = topicRepository.findAll();

        if (topics.isEmpty()) {
            response.setMessage(generalMessageAccessor.getMessage(null, "get_topics_empty"));
            response.setTotal(0);
            response.setData(null);
            return response;
        }

        response.setMessage(generalMessageAccessor.getMessage(null, "get_topics_success"));
        response.setTotal(topics.size());
        response.setData(topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).collect(Collectors.toList()));

        return response;
    }

    @Override
    public List<TopicDto> getTopics () {
        List<Topic> topics = topicRepository.findAll();
        if (!topics.isEmpty()) {
            return topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
        } else {
            return null;
        }
    }
}
