package com.wordle.game.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIWordValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIWordValidationService.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> wordCache;
    private final DictionaryService fallbackDictionary;
    
    // Free Dictionary API endpoints
    private static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private static final String WORDNIK_API_URL = "https://api.wordnik.com/v4/word.json/";
    private static final String WORDS_API_URL = "https://wordsapiv1.p.rapidapi.com/words/";
    
    public AIWordValidationService(DictionaryService fallbackDictionary) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.wordCache = new ConcurrentHashMap<>();
        this.fallbackDictionary = fallbackDictionary;
    }
    
    /**
     * Validate word using multiple AI/API sources with fallback
     */
    public boolean validateWordWithAI(String word) {
        if (word == null || word.length() != 5) {
            return false;
        }
        
        String upperWord = word.toUpperCase();
        
        // Check cache first
        if (wordCache.containsKey(upperWord)) {
            return wordCache.get(upperWord);
        }
        
        boolean isValid = false;
        
        try {
            // Try multiple validation methods
            isValid = validateWithDictionaryAPI(upperWord) || 
                     validateWithWordnik(upperWord) ||
                     validateWithWordsAPI(upperWord);
                     
            // If all APIs fail, use fallback dictionary
            if (!isValid) {
                isValid = fallbackDictionary.isValidWord(upperWord);
                logger.info("Using fallback dictionary for word: {}", upperWord);
            }
            
        } catch (Exception e) {
            logger.warn("AI validation failed for word: {}, using fallback", upperWord, e);
            isValid = fallbackDictionary.isValidWord(upperWord);
        }
        
        // Cache the result
        wordCache.put(upperWord, isValid);
        
        return isValid;
    }
    
    /**
     * Validate using Free Dictionary API
     */
    private boolean validateWithDictionaryAPI(String word) {
        try {
            String url = DICTIONARY_API_URL + URLEncoder.encode(word.toLowerCase(), StandardCharsets.UTF_8);
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.contains("No Definitions Found")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return jsonNode.isArray() && jsonNode.size() > 0;
            }
        } catch (Exception e) {
            logger.debug("Dictionary API failed for word: {}", word);
        }
        return false;
    }
    
    /**
     * Validate using Wordnik API (backup method)
     */
    private boolean validateWithWordnik(String word) {
        try {
            String url = WORDNIK_API_URL + URLEncoder.encode(word.toLowerCase(), StandardCharsets.UTF_8) + 
                        "/definitions?limit=1&includeRelated=false&useCanonical=false&includeTags=false";
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.equals("[]")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return jsonNode.isArray() && jsonNode.size() > 0;
            }
        } catch (Exception e) {
            logger.debug("Wordnik API failed for word: {}", word);
        }
        return false;
    }
    
    /**
     * Validate using Words API (another backup)
     */
    private boolean validateWithWordsAPI(String word) {
        try {
            // This would require RapidAPI key, so we'll skip for now
            // Can be implemented with proper API key
            return false;
        } catch (Exception e) {
            logger.debug("Words API failed for word: {}", word);
        }
        return false;
    }
    
    /**
     * Get word definition using AI/API
     */
    public String getWordDefinition(String word) {
        try {
            String url = DICTIONARY_API_URL + URLEncoder.encode(word.toLowerCase(), StandardCharsets.UTF_8);
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.contains("No Definitions Found")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    JsonNode firstEntry = jsonNode.get(0);
                    JsonNode meanings = firstEntry.get("meanings");
                    if (meanings != null && meanings.size() > 0) {
                        JsonNode definitions = meanings.get(0).get("definitions");
                        if (definitions != null && definitions.size() > 0) {
                            return definitions.get(0).get("definition").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get definition for word: {}", word, e);
        }
        
        return "Definition not available";
    }
    
    /**
     * Get word suggestions using AI
     */
    public List<String> getWordSuggestions(String partialWord) {
        List<String> suggestions = new ArrayList<>();
        
        // For now, we'll use our local dictionary for suggestions
        // This can be enhanced with AI-powered suggestion APIs
        Set<String> allWords = getAllKnownWords();
        
        for (String word : allWords) {
            if (word.toLowerCase().startsWith(partialWord.toLowerCase()) && word.length() == 5) {
                suggestions.add(word);
                if (suggestions.size() >= 10) break;
            }
        }
        
        return suggestions;
    }
    
    /**
     * Check if word is commonly used (frequency analysis)
     */
    public boolean isCommonWord(String word) {
        // This could be enhanced with frequency APIs
        // For now, check if it's in our core dictionary
        return fallbackDictionary.isValidWord(word);
    }
    
    /**
     * Get all known words from various sources
     */
    private Set<String> getAllKnownWords() {
        // Return words from fallback dictionary
        // This can be expanded to include cached API results
        return Set.of(
            "APPLE", "HOUSE", "WATER", "PHONE", "HAPPY", "MONEY", "LIGHT", "WORLD", "MUSIC", "FRIEND",
            "SMILE", "BREAD", "CHAIR", "PLANT", "SHIRT", "BEACH", "DREAM", "PARTY", "STORY", "PIZZA",
            "TIGER", "GHOST", "QUEEN", "KNIFE", "CANDY", "CLOUD", "HEART", "STONE", "DANCE", "LEARN",
            "SMART", "BRAVE", "PEACE", "MAGIC", "FRESH", "SWEET", "CLEAN", "QUICK", "SHARP", "YOUNG"
        );
    }
    
    /**
     * Clear word cache
     */
    public void clearCache() {
        wordCache.clear();
        logger.info("Word validation cache cleared");
    }
    
    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", wordCache.size());
        stats.put("cachedWords", new ArrayList<>(wordCache.keySet()));
        return stats;
    }
}
