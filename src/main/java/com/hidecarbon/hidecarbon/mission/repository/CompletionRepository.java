package com.hidecarbon.hidecarbon.mission.repository;

import com.hidecarbon.hidecarbon.mission.model.MissionCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletionRepository extends JpaRepository<MissionCompletion, Long> {
}
