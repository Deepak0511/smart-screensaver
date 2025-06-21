package in.dpk.assistants.smart_screensaver.controller;

import in.dpk.assistants.smart_screensaver.service.ScreensaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/screensaver")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScreensaverController {
    
    private final ScreensaverService screensaverService;
    
    @GetMapping("/content")
    public ResponseEntity<Map<String, Object>> getScreensaverContent() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Smart Screensaver is running!");
    }
} 