package in.dpk.assistants.smart_screensaver.controller;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SettingsController {
    
    private final UserService userService;
    
    // User Preferences endpoints
    @GetMapping("/preferences")
    public ResponseEntity<UserPreference> getUserPreferences() {
        return ResponseEntity.ok(userService.getUserPreference());
    }
    
    @PutMapping("/preferences")
    public ResponseEntity<UserPreference> updateUserPreferences(@RequestBody UserPreference userPreference) {
        UserPreference updated = userService.updateUserPreference(userPreference);
        return ResponseEntity.ok(updated);
    }
    
    // Routines endpoints
    @GetMapping("/routines")
    public ResponseEntity<List<Routine>> getAllRoutines() {
        return ResponseEntity.ok(userService.getAllRoutines());
    }
    
    @GetMapping("/routines/{id}")
    public ResponseEntity<Routine> getRoutineById(@PathVariable Long id) {
        Routine routine = userService.getRoutineById(id);
        if (routine != null) {
            return ResponseEntity.ok(routine);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/routines")
    public ResponseEntity<Routine> createRoutine(@RequestBody Routine routine) {
        Routine created = userService.createRoutine(routine);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/routines/{id}")
    public ResponseEntity<Routine> updateRoutine(@PathVariable Long id, @RequestBody Routine routine) {
        Routine updated = userService.updateRoutine(id, routine);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/routines/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        boolean deleted = userService.deleteRoutine(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 