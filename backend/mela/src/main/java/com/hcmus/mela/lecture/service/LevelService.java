package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.response.GetLevelsResponse;
import com.hcmus.mela.lecture.model.Level;

import java.util.UUID;

public interface LevelService {

    GetLevelsResponse getLevelsResponse();

    Level findLevelByLevelId(UUID id);

    Level findLevelByLevelTitle(String title);
}
