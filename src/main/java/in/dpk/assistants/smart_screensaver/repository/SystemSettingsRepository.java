package in.dpk.assistants.smart_screensaver.repository;

import in.dpk.assistants.smart_screensaver.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    
    Optional<SystemSettings> findBySettingKey(String settingKey);
    
    List<SystemSettings> findByCategory(String category);
    
    List<SystemSettings> findByEnabled(boolean enabled);
    
    @Query("SELECT s FROM SystemSettings s WHERE s.category = :category AND s.enabled = true")
    List<SystemSettings> findEnabledByCategory(@Param("category") String category);
    
    boolean existsBySettingKey(String settingKey);
    
    @Query("SELECT s.settingValue FROM SystemSettings s WHERE s.settingKey = :key AND s.enabled = true")
    Optional<String> findValueByKey(@Param("key") String key);
} 