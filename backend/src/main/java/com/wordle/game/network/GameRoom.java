package com.wordle.game.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game Room Management
 * Áp dụng kiến thức: Concurrent Programming, Game State Management
 */
public class GameRoom {
    private static final Logger logger = LoggerFactory.getLogger(GameRoom.class);
    
    private final String roomId;
    private final Set<PlayerConnection> players;
    private final Map<String, Object> gameState;
    private volatile boolean gameInProgress;
    private String currentWord;
    private int currentRound;
    private long roundStartTime;
    
    public GameRoom(String roomId) {
        this.roomId = roomId;
        this.players = ConcurrentHashMap.newKeySet();
        this.gameState = new ConcurrentHashMap<>();
        this.gameInProgress = false;
        this.currentRound = 0;
    }
    
    public void addPlayer(PlayerConnection player) {
        players.add(player);
        logger.info("Player added to room {}. Total players: {}", roomId, players.size());
        
        // Auto start game if enough players
        if (players.size() >= 2 && !gameInProgress) {
            startNewRound();
        }
    }
    
    public void removePlayer(PlayerConnection player) {
        players.remove(player);
        logger.info("Player removed from room {}. Total players: {}", roomId, players.size());
        
        // Stop game if not enough players
        if (players.size() < 2 && gameInProgress) {
            stopGame();
        }
    }
    
    public Set<PlayerConnection> getPlayers() {
        return new HashSet<>(players);
    }
    
    public boolean isEmpty() {
        return players.isEmpty();
    }
    
    public void processGameMove(PlayerConnection player, GameMessage message) {
        if (!gameInProgress) {
            return;
        }
        
        // Process word submission
        String submittedWord = (String) message.getData();
        if (submittedWord != null && submittedWord.length() == 5) {
            // Validate and score the word
            Map<String, Object> result = validateWord(submittedWord);
            
            // Update game state
            updatePlayerScore(player.getPlayerId(), result);
            
            // Check if round should end
            checkRoundCompletion();
        }
    }
    
    private void startNewRound() {
        currentRound++;
        gameInProgress = true;
        roundStartTime = System.currentTimeMillis();
        
        // Generate random word for this round
        currentWord = generateRandomWord();
        
        logger.info("Started round {} in room {} with word: {}", currentRound, roomId, currentWord);
        
        // Initialize player scores for this round
        for (PlayerConnection player : players) {
            gameState.put(player.getPlayerId() + "_attempts", 0);
            gameState.put(player.getPlayerId() + "_completed", false);
        }
    }
    
    private void stopGame() {
        gameInProgress = false;
        currentRound = 0;
        gameState.clear();
        logger.info("Game stopped in room {}", roomId);
    }
    
    private Map<String, Object> validateWord(String word) {
        Map<String, Object> result = new HashMap<>();
        
        if (currentWord == null) {
            result.put("valid", false);
            return result;
        }
        
        // Check each letter
        char[] wordChars = word.toUpperCase().toCharArray();
        char[] targetChars = currentWord.toUpperCase().toCharArray();
        String[] colors = new String[5];
        
        // First pass: exact matches
        boolean[] used = new boolean[5];
        for (int i = 0; i < 5; i++) {
            if (wordChars[i] == targetChars[i]) {
                colors[i] = "green";
                used[i] = true;
            }
        }
        
        // Second pass: wrong position
        for (int i = 0; i < 5; i++) {
            if (colors[i] == null) {
                boolean found = false;
                for (int j = 0; j < 5; j++) {
                    if (!used[j] && wordChars[i] == targetChars[j]) {
                        colors[i] = "yellow";
                        used[j] = true;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    colors[i] = "gray";
                }
            }
        }
        
        result.put("valid", true);
        result.put("colors", colors);
        result.put("isCorrect", word.equalsIgnoreCase(currentWord));
        
        return result;
    }
    
    private void updatePlayerScore(String playerId, Map<String, Object> result) {
        int attempts = (int) gameState.getOrDefault(playerId + "_attempts", 0);
        attempts++;
        gameState.put(playerId + "_attempts", attempts);
        
        if ((boolean) result.get("isCorrect")) {
            gameState.put(playerId + "_completed", true);
            gameState.put(playerId + "_completionTime", System.currentTimeMillis() - roundStartTime);
        }
    }
    
    private void checkRoundCompletion() {
        boolean allCompleted = true;
        long maxTime = 300000; // 5 minutes
        
        for (PlayerConnection player : players) {
            boolean completed = (boolean) gameState.getOrDefault(player.getPlayerId() + "_completed", false);
            if (!completed) {
                allCompleted = false;
                break;
            }
        }
        
        // End round if all completed or time expired
        if (allCompleted || (System.currentTimeMillis() - roundStartTime) > maxTime) {
            endCurrentRound();
        }
    }
    
    private void endCurrentRound() {
        gameInProgress = false;
        
        // Calculate scores and prepare results
        Map<String, Object> roundResults = new HashMap<>();
        roundResults.put("roundNumber", currentRound);
        roundResults.put("correctWord", currentWord);
        roundResults.put("playerResults", gameState);
        
        logger.info("Round {} ended in room {}", currentRound, roomId);
        
        // Start next round after delay
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (players.size() >= 2) {
                    startNewRound();
                }
            }
        }, 5000); // 5 second delay
    }
    
    private String generateRandomWord() {
        String[] words = {
            "ABOUT", "ABOVE", "ABUSE", "ACTOR", "ACUTE", "ADMIT", "ADOPT", "ADULT", "AFTER", "AGAIN",
            "AGENT", "AGREE", "AHEAD", "ALARM", "ALBUM", "ALERT", "ALIEN", "ALIGN", "ALIKE", "ALIVE",
            "ALLOW", "ALONE", "ALONG", "ALTER", "ANGEL", "ANGER", "ANGLE", "ANGRY", "APART", "APPLE",
            "APPLY", "ARENA", "ARGUE", "ARISE", "ARRAY", "ARROW", "ASIDE", "ASSET", "ATLAS", "AUDIO",
            "AUDIT", "AVOID", "AWAKE", "AWARD", "AWARE", "BADLY", "BAKER", "BANDS", "BASIC", "BATCH",
            "BEACH", "BEGAN", "BEGIN", "BEING", "BELOW", "BENCH", "BILLY", "BIRTH", "BLACK", "BLAME",
            "BLANK", "BLIND", "BLOCK", "BLOOD", "BOARD", "BOOST", "BOOTH", "BOUND", "BRAIN", "BRAND",
            "BRASS", "BRAVE", "BREAD", "BREAK", "BREED", "BRIEF", "BRING", "BROAD", "BROKE", "BROWN",
            "BUILD", "BUILT", "BUYER", "CABLE", "CARRY", "CATCH", "CAUSE", "CHAIN", "CHAIR", "CHAOS",
            "CHARM", "CHART", "CHASE", "CHEAP", "CHECK", "CHEST", "CHILD", "CHINA", "CHOSE", "CIVIL",
            "CLAIM", "CLASS", "CLEAN", "CLEAR", "CLICK", "CLIMB", "CLOCK", "CLOSE", "CLOUD", "COACH",
            "COAST", "COULD", "COUNT", "COURT", "COVER", "CRASH", "CRAZY", "CREAM", "CRIME", "CROSS",
            "CROWD", "CROWN", "CRUDE", "CURVE", "CYCLE", "DAILY", "DANCE", "DATED", "DEALT", "DEATH",
            "DEBUT", "DELAY", "DEPTH", "DOING", "DOUBT", "DOZEN", "DRAFT", "DRAMA", "DRANK", "DRAWN",
            "DREAM", "DRESS", "DRILL", "DRINK", "DRIVE", "DROVE", "DYING", "EAGER", "EARLY", "EARTH",
            "EIGHT", "ELITE", "EMPTY", "ENEMY", "ENJOY", "ENTER", "ENTRY", "EQUAL", "ERROR", "EVENT",
            "EVERY", "EXACT", "EXIST", "EXTRA", "FAITH", "FALSE", "FAULT", "FIBER", "FIELD", "FIFTH",
            "FIFTY", "FIGHT", "FINAL", "FIRST", "FIXED", "FLASH", "FLEET", "FLOOR", "FLUID", "FOCUS",
            "FORCE", "FORTH", "FORTY", "FORUM", "FOUND", "FRAME", "FRANK", "FRAUD", "FRESH", "FRONT",
            "FRUIT", "FULLY", "FUNNY", "GIANT", "GIVEN", "GLASS", "GLOBE", "GOING", "GRACE", "GRADE",
            "GRAND", "GRANT", "GRASS", "GRAVE", "GREAT", "GREEN", "GROSS", "GROUP", "GROWN", "GUARD",
            "GUESS", "GUEST", "GUIDE", "HAPPY", "HARRY", "HEART", "HEAVY", "HENCE", "HENRY", "HORSE",
            "HOTEL", "HOUSE", "HUMAN", "IDEAL", "IMAGE", "INDEX", "INNER", "INPUT", "ISSUE", "JAPAN",
            "JIMMY", "JOINT", "JONES", "JUDGE", "KNOWN", "LABEL", "LARGE", "LASER", "LATER", "LAUGH",
            "LAYER", "LEARN", "LEASE", "LEAST", "LEAVE", "LEGAL", "LEVEL", "LEWIS", "LIGHT", "LIMIT",
            "LINKS", "LIVES", "LOCAL", "LOOSE", "LOWER", "LUCKY", "LUNCH", "LYING", "MAGIC", "MAJOR",
            "MAKER", "MARCH", "MARIA", "MATCH", "MAYBE", "MAYOR", "MEANT", "MEDIA", "METAL", "MIGHT",
            "MINOR", "MINUS", "MIXED", "MODEL", "MONEY", "MONTH", "MORAL", "MOTOR", "MOUNT", "MOUSE",
            "MOUTH", "MOVED", "MOVIE", "MUSIC", "NEEDS", "NEVER", "NEWLY", "NIGHT", "NOISE", "NORTH",
            "NOTED", "NOVEL", "NURSE", "OCCUR", "OCEAN", "OFFER", "OFTEN", "ORDER", "OTHER", "OUGHT",
            "PAINT", "PANEL", "PAPER", "PARTY", "PEACE", "PETER", "PHASE", "PHONE", "PHOTO", "PIANO",
            "PIECE", "PILOT", "PITCH", "PLACE", "PLAIN", "PLANE", "PLANT", "PLATE", "POINT", "POUND",
            "POWER", "PRESS", "PRICE", "PRIDE", "Prime", "PRINT", "PRIOR", "PRIZE", "PROOF", "PROUD",
            "PROVE", "QUEEN", "QUICK", "QUIET", "QUITE", "RADIO", "RAISE", "RANGE", "RAPID", "RATIO",
            "REACH", "READY", "REALM", "REBEL", "REFER", "RELAX", "RELAY", "REPLY", "RIGHT", "RIVAL",
            "RIVER", "ROBIN", "ROGER", "ROMAN", "ROUGH", "ROUND", "ROUTE", "ROYAL", "RURAL", "SCALE",
            "SCENE", "SCOPE", "SCORE", "SENSE", "SERVE", "SEVEN", "SHALL", "SHAPE", "SHARE", "SHARP",
            "SHEET", "SHELF", "SHELL", "SHIFT", "SHINE", "SHIRT", "SHOCK", "SHOOT", "SHORT", "SHOWN",
            "SIGHT", "SILLY", "SINCE", "SIXTH", "SIXTY", "SIZED", "SKILL", "SLEEP", "SLIDE", "SMALL",
            "SMART", "SMILE", "SMITH", "SMOKE", "SNAKE", "SNOW", "SOLID", "SOLVE", "SORRY", "SOUND",
            "SOUTH", "SPACE", "SPARE", "SPEAK", "SPEED", "SPEND", "SPENT", "SPLIT", "SPOKE", "SPORT",
            "STAFF", "STAGE", "STAKE", "STAND", "START", "STATE", "STEAM", "STEEL", "STICK", "STILL",
            "STOCK", "STONE", "STOOD", "STORE", "STORM", "STORY", "STRIP", "STUCK", "STUDY", "STUFF",
            "STYLE", "SUGAR", "SUITE", "SUPER", "SWEET", "TABLE", "TAKEN", "TASTE", "TAXES", "TEACH",
            "TEETH", "TERRY", "TEXAS", "THANK", "THEFT", "THEIR", "THEME", "THERE", "THESE", "THICK",
            "THING", "THINK", "THIRD", "THOSE", "THREE", "THREW", "THROW", "THUMB", "TIGER", "TIGHT",
            "TIMER", "TIRED", "TITLE", "TODAY", "TOPIC", "TOTAL", "TOUCH", "TOUGH", "TOWER", "TRACK",
            "TRADE", "TRAIN", "TREAT", "TREND", "TRIAL", "TRIBE", "TRICK", "TRIED", "TRIES", "TRUCK",
            "TRULY", "TRUNK", "TRUST", "TRUTH", "TWICE", "TWIST", "TYLER", "UNCLE", "UNDER", "UNDUE",
            "UNION", "UNITY", "UNTIL", "UPPER", "UPSET", "URBAN", "USAGE", "USUAL", "VALID", "VALUE",
            "VIDEO", "VIRUS", "VISIT", "VITAL", "VOCAL", "VOICE", "WASTE", "WATCH", "WATER", "WHEEL",
            "WHERE", "WHICH", "WHILE", "WHITE", "WHOLE", "WHOSE", "WOMAN", "WOMEN", "WORLD", "WORRY",
            "WORSE", "WORST", "WORTH", "WOULD", "WRITE", "WRONG", "WROTE", "YOUNG", "YOUTH"
        };
        
        Random random = new Random();
        return words[random.nextInt(words.length)];
    }
}
