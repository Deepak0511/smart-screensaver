package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.repository.UserPreferenceRepository;
import in.dpk.assistants.smart_screensaver.repository.RoutineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.time.LocalTime;

@Service
@Slf4j
public class UserService {
    
    private static final String DATA_FILE = "screensaver_data.json";
    private static final String ROUTINES_FILE = "routines_data.json";
    private static final String DEFAULT_USER_ID = "default";
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private RoutineRepository routineRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public UserService() {
        // Configure ObjectMapper for Java 8 time types
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    // User Preference methods
    public UserPreference getUserPreference() {
        try {
            return userPreferenceRepository.findByUserId(DEFAULT_USER_ID)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error getting user preference from database: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public UserPreference updateUserPreference(UserPreference updatedPreference) {
        try {
            UserPreference savedPreference = userPreferenceRepository.save(updatedPreference);
            log.info("User preferences updated in database: {}", savedPreference.getDisplayName());
            return savedPreference;
        } catch (Exception e) {
            log.error("Error updating user preference in database: {}", e.getMessage(), e);
            return null;
        }
    }
    
    // Routine methods
    public List<Routine> getAllRoutines() {
        try {
            return new ArrayList<>(routineRepository.findAll());
        } catch (Exception e) {
            log.error("Error getting routines from database: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public List<Routine> getEnabledRoutines() {
        try {
            return routineRepository.findByEnabledTrueOrderByPriorityDescWithActions();
        } catch (Exception e) {
            log.error("Error getting enabled routines from database: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public Routine getRoutineById(Long id) {
        try {
            return routineRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error getting routine by ID from database: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public Routine createRoutine(Routine routine) {
        try {
            Routine savedRoutine = routineRepository.save(routine);
            log.info("Routine created in database: {}", savedRoutine.getName());
            return savedRoutine;
        } catch (Exception e) {
            log.error("Error creating routine in database: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public Routine updateRoutine(Long id, Routine updatedRoutine) {
        try {
            if (routineRepository.existsById(id)) {
                updatedRoutine.setId(id);
                Routine savedRoutine = routineRepository.save(updatedRoutine);
                log.info("Routine updated in database: {}", savedRoutine.getName());
                return savedRoutine;
            }
            return null;
        } catch (Exception e) {
            log.error("Error updating routine in database: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public boolean deleteRoutine(Long id) {
        try {
            if (routineRepository.existsById(id)) {
                routineRepository.deleteById(id);
                log.info("Routine deleted from database: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting routine from database: {}", e.getMessage(), e);
            return false;
        }
    }
} 