package com.wordle.game.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.game.service.AIWordValidationService;
import com.wordle.game.service.EnhancedDictionaryService;

@RestController
@RequestMapping("/api/dictionary")
@CrossOrigin(origins = "*")
public class DictionaryController {
    
    @Autowired
    private EnhancedDictionaryService dictionaryService;
    
    @Autowired
    private AIWordValidationService aiValidationService;
    
    @GetMapping("/words")
    public ResponseEntity<Set<String>> getAllWords() {
        Set<String> words = dictionaryService.getAllValidWords();
        return ResponseEntity.ok(words);
    }
    
    @GetMapping("/validate/{word}")
    public ResponseEntity<Boolean> validateWord(@PathVariable String word) {
        boolean isValid = aiValidationService.validateWordWithAI(word);
        return ResponseEntity.ok(isValid);
    }
    
    @GetMapping("/validate-ai/{word}")
    public ResponseEntity<Map<String, Object>> validateWordWithAI(@PathVariable String word) {
        boolean isValid = aiValidationService.validateWordWithAI(word);
        String definition = aiValidationService.getWordDefinition(word);
        boolean isCommon = aiValidationService.isCommonWord(word);
        
        Map<String, Object> result = Map.of(
            "word", word.toUpperCase(),
            "isValid", isValid,
            "definition", definition,
            "isCommon", isCommon
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/suggestions/{partial}")
    public ResponseEntity<List<String>> getWordSuggestions(@PathVariable String partial) {
        List<String> suggestions = aiValidationService.getWordSuggestions(partial);
        return ResponseEntity.ok(suggestions);
    }
    
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        aiValidationService.clearCache();
        return ResponseEntity.ok("Cache cleared successfully");
    }
    
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = aiValidationService.getCacheStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/random")
    public ResponseEntity<String> getRandomWord() {
        String word = dictionaryService.getRandomTargetWord();
        return ResponseEntity.ok(word);
    }
    
    @GetMapping("/random/{count}")
    public ResponseEntity<List<String>> getRandomWords(@PathVariable int count) {
        List<String> words = dictionaryService.getRandomWords(count);
        return ResponseEntity.ok(words);
    }
}
