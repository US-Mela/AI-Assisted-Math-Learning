package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Level;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface LevelRepository extends MongoRepository<Level, UUID> {
    Level findByName(String levelTitle);
}
