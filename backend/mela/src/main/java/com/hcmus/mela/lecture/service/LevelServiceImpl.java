package com.hcmus.mela.lecture.service;

import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.lecture.dto.response.GetLevelsResponse;
import com.hcmus.mela.lecture.mapper.LevelMapper;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    public GetLevelsResponse getLevelsResponse() {
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            return new GetLevelsResponse(
                    generalMessageAccessor.getMessage(null, "get_levels_empty"),
                    0,
                    Collections.emptyList()
            );
        }

        return new GetLevelsResponse(
                generalMessageAccessor.getMessage(null, "get_levels_success"),
                levels.size(),
                levels.stream()
                        .map(LevelMapper.INSTANCE::levelToLevelDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Level findLevelByLevelId(UUID id) {
        return levelRepository.findById(id).orElse(null);
    }

    @Override
    public Level findLevelByLevelTitle(String title) {
        return levelRepository.findByName(title);
    }
}
