package com.hcmus.mela.lecture.service;

import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.mapper.TopicMapper;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.lecture.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    public GetTopicsResponse getTopicsResponse() {
        List<Topic> topics = topicRepository.findAll();

        if (topics.isEmpty()) {
            return new GetTopicsResponse(
                    generalMessageAccessor.getMessage(null, "get_topics_empty"),
                    0,
                    Collections.emptyList()
            );
        }

        return new GetTopicsResponse(
                generalMessageAccessor.getMessage(null, "get_topics_success"),
                topics.size(),
                topics.stream()
                        .map(TopicMapper.INSTANCE::topicToTopicDto)
                        .toList()
        );
    }

    @Override
    public List<TopicDto> getTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.isEmpty()
                ? Collections.emptyList()
                : topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
    }

    @Override
    public TopicDto getTopicById(UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);

        if (topic == null) {
            return null;
        }

        return TopicMapper.INSTANCE.topicToTopicDto(topic);
    }
}
