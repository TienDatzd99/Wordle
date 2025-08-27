package com.wordle.game.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class DictionaryService {
    
    private final Set<String> vietnameseWords;
    
    public DictionaryService() {
        this.vietnameseWords = initializeDictionary();
    }
    
    private Set<String> initializeDictionary() {
        Set<String> words = new HashSet<>();
        
        // Thêm một số từ tiếng Việt phổ biến (có thể mở rộng từ file)
        words.addAll(Arrays.asList(
            "APPLE", "HOUSE", "WATER", "PHONE", "HAPPY", "MONEY", "LIGHT", "WORLD", "MUSIC", "FRIEND",
            "SMILE", "BREAD", "CHAIR", "PLANT", "SHIRT", "BEACH", "DREAM", "PARTY", "STORY", "PIZZA",
            "TIGER", "GHOST", "QUEEN", "KNIFE", "CANDY", "CLOUD", "HEART", "STONE", "DANCE", "LEARN",
            "SMART", "BRAVE", "PEACE", "MAGIC", "FRESH", "SWEET", "CLEAN", "QUICK", "SHARP", "YOUNG",
            "BLAZE", "FROST", "STORM", "FLAME", "WINDS", "EARTH", "OCEAN", "MOUNT", "FIELD", "RIVER",
            "XANH", "VANG", "TRANG", "HONG", "TIM", "DEN", "NAU", "XANH", "CAM", "BE",
            "YEU", "THICH", "GHÉT", "BUON", "VUI", "HAO", "KINH", "SỢNG", "GIẬN", "THƯƠNG",
            "CON", "MẸ", "CHA", "ANH", "CHỊ", "EM", "CÔ", "BÁC", "CHÚ", "DÌ"
        ));
        
        return words;
    }
    
    public boolean isValidWord(String word) {
        return vietnameseWords.contains(word.toUpperCase());
    }
    
    public List<String> getRandomWords(int count) {
        List<String> wordList = new ArrayList<>(vietnameseWords);
        Collections.shuffle(wordList);
        return wordList.subList(0, Math.min(count, wordList.size()));
    }
    
    public List<Character> generateRandomLetters(int count) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<Character> letters = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            letters.add(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        
        return letters;
    }
    
    public String getRandomTargetWord() {
        List<String> words = getRandomWords(1);
        return words.isEmpty() ? "HELLO" : words.get(0);
    }
}
