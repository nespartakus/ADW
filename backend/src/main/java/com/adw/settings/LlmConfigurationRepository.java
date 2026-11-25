package com.adw.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LlmConfigurationRepository extends JpaRepository<LlmConfiguration, UUID> {

    Optional<LlmConfiguration> findFirstByEnabledTrueOrderByUpdatedAtDesc();

    Optional<LlmConfiguration> findFirstByOrderByUpdatedAtDesc();
}
