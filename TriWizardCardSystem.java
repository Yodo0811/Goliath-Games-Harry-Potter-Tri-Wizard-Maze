import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TriWizardCardSystem {

    // Card types and their effects
    public enum CardEffect {
        MOVE_FORWARD(1, "Move forward {0} spaces"),
        MOVE_BACK(2, "Move back {0} spaces"),
        SKIP_TURN(3, "Skip your next turn"),
        REROLL(4, "Reroll the die and move again"),
        TELEPORT(5, "Teleport to any Draw Card space"),
        SWAP_POSITIONS(6, "Swap positions with another player"),
        PROTECTION(7, "Protection from next bad card"),
        DOUBLE_MOVE(8, "Your next move is doubled"),
        HALF_MOVE(9, "Your next move is halved (round down)"),
        REVERSE_DIRECTION(10, "Reverse your movement direction"),
        FREE_HOME(11, "Move one piece directly to home"),
        BLOCK(12, "Block another player's next move"),
        TIME_REVERSAL(13, "Time reversal - all players return to their previous positions"); // 新增的时间倒流卡

        private final int id;
        private final String description;

        CardEffect(int id, String description) {
            this.id = id;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Card class representing a single game card
    public static class Card {
        private final CardEffect effect;
        private final int value; // Some effects may have associated values
        private final String fullDescription;

        public Card(CardEffect effect, int value) {
            this.effect = effect;
            this.value = value;
            this.fullDescription = effect.getDescription().replace("{0}", String.valueOf(value));
        }

        public CardEffect getEffect() {
            return effect;
        }

        public int getValue() {
            return value;
        }

        public String getFullDescription() {
            return fullDescription;
        }

        @Override
        public String toString() {
            return fullDescription;
        }
    }

    // Draw pile class to manage the deck of cards
    public static class DrawPile {
        private List<Card> cards;
        private List<Card> discardPile;
        private Random random;

        public DrawPile() {
            this.cards = new ArrayList<>();
            this.discardPile = new ArrayList<>();
            this.random = new Random();
            initializeDeck();
        }

        private void initializeDeck() {
            // Create 48 cards with various effects
            // Distribution can be adjusted based on game balance

            // Move forward (1-3 spaces) - 12 cards
            for (int i = 0; i < 6; i++) cards.add(new Card(CardEffect.MOVE_FORWARD, 1));
            for (int i = 0; i < 4; i++) cards.add(new Card(CardEffect.MOVE_FORWARD, 2));
            for (int i = 0; i < 2; i++) cards.add(new Card(CardEffect.MOVE_FORWARD, 3));

            // Move back (1-2 spaces) - 8 cards
            for (int i = 0; i < 5; i++) cards.add(new Card(CardEffect.MOVE_BACK, 1));
            for (int i = 0; i < 3; i++) cards.add(new Card(CardEffect.MOVE_BACK, 2));

            // Skip turn - 5 cards
            for (int i = 0; i < 5; i++) cards.add(new Card(CardEffect.SKIP_TURN, 0));

            // Reroll - 5 cards
            for (int i = 0; i < 5; i++) cards.add(new Card(CardEffect.REROLL, 0));

            // Teleport - 3 cards
            for (int i = 0; i < 3; i++) cards.add(new Card(CardEffect.TELEPORT, 0));

            // Swap positions - 3 cards
            for (int i = 0; i < 3; i++) cards.add(new Card(CardEffect.SWAP_POSITIONS, 0));

            // Protection - 3 cards
            for (int i = 0; i < 3; i++) cards.add(new Card(CardEffect.PROTECTION, 0));

            // Double move - 2 cards
            for (int i = 0; i < 2; i++) cards.add(new Card(CardEffect.DOUBLE_MOVE, 0));

            // Half move - 2 cards
            for (int i = 0; i < 2; i++) cards.add(new Card(CardEffect.HALF_MOVE, 0));

            // Reverse direction - 2 cards
            for (int i = 0; i < 2; i++) cards.add(new Card(CardEffect.REVERSE_DIRECTION, 0));

            // Free home - 1 card
            cards.add(new Card(CardEffect.FREE_HOME, 0));

            // Block - 1 card
            cards.add(new Card(CardEffect.BLOCK, 0));

            // Time reversal - 1 card
            cards.add(new Card(CardEffect.TIME_REVERSAL, 0));

            shuffle();
        }

        public void shuffle() {
            Collections.shuffle(cards);
        }

        public Card draw() {
            if (cards.isEmpty()) {
                // If draw pile is empty, reshuffle discard pile into draw pile
                cards.addAll(discardPile);
                discardPile.clear();
                shuffle();
            }

            if (cards.isEmpty()) {
                // Shouldn't happen if initialized properly
                throw new IllegalStateException("No cards available to draw");
            }

            return cards.remove(0);
        }

        public void discard(Card card) {
            discardPile.add(card);
        }

        public int remainingCards() {
            return cards.size();
        }

        public int discardedCards() {
            return discardPile.size();
        }
    }

    // Card effect executor to handle the game logic when a card is drawn
    public static class CardEffectExecutor {
        private GameState gameState;

        public CardEffectExecutor(GameState gameState) {
            this.gameState = gameState;
        }

        public void executeEffect(Card card, Player player) {
            switch (card.getEffect()) {
                case MOVE_FORWARD:
                    player.move(card.getValue());
                    break;
                case MOVE_BACK:
                    player.move(-card.getValue());
                    break;
                case SKIP_TURN:
                    player.setSkipNextTurn(true);
                    break;
                case REROLL:
                    player.setReroll(true);
                    break;
                case TELEPORT:
                    // Implement teleport logic to any Draw Card space
                    break;
                case SWAP_POSITIONS:
                    // Implement position swap with another player
                    break;
                case PROTECTION:
                    player.setProtected(true);
                    break;
                case DOUBLE_MOVE:
                    player.setNextMoveMultiplier(2);
                    break;
                case HALF_MOVE:
                    player.setNextMoveMultiplier(0.5f);
                    break;
                case REVERSE_DIRECTION:
                    player.reverseDirection();
                    break;
                case FREE_HOME:
                    // Implement moving one piece directly to home
                    break;
                case BLOCK:
                    // Implement blocking another player's next move
                    break;
                case TIME_REVERSAL:
                    gameState.revertAllPlayersToPreviousPositions();
                    break;
            }

            // Log the card effect
            System.out.println(player.getName() + " drew: " + card.getFullDescription());
        }
    }

    // Supporting classes (simplified for this example)
    public static class GameState {
        private Map<Player, List<Integer>> positionHistory = new HashMap<>();

        public void recordPlayerPosition(Player player, int position) {
            if (!positionHistory.containsKey(player)) {
                positionHistory.put(player, new ArrayList<>());
            }
            positionHistory.get(player).add(position);
        }

        public void revertAllPlayersToPreviousPositions() {
            for (Player player : positionHistory.keySet()) {
                List<Integer> history = positionHistory.get(player);
                if (history.size() > 1) { // 确保有上一回合的位置
                    int previousPosition = history.get(history.size() - 2);
                    player.setPosition(previousPosition);
                    // 更新历史记录
                    history.remove(history.size() - 1);
                    System.out.println(player.getName() + " returned to position " + previousPosition);
                }
            }
            System.out.println("Time reversed! All players returned to their previous positions.");
        }
    }

    public static class Player {
        private String name;
        private int position;
        private boolean skipNextTurn;
        private boolean reroll;
        private boolean isProtected;
        private float nextMoveMultiplier;

        public Player(String name) {
            this.name = name;
            this.position = 0;
        }

        public String getName() {
            return name;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void move(int spaces) {
            this.position += spaces;
            System.out.println(name + " moving " + spaces + " spaces to position " + position);
        }

        public void setSkipNextTurn(boolean skip) {
            this.skipNextTurn = skip;
        }

        public void setReroll(boolean reroll) {
            this.reroll = reroll;
        }

        public void setProtected(boolean isProtected) {
            this.isProtected = isProtected;
        }

        public void setNextMoveMultiplier(float multiplier) {
            this.nextMoveMultiplier = multiplier;
        }

        public void reverseDirection() {
            // Implement direction reversal
        }
    }

    // Example usage
    public static void main(String[] args) {
        // Initialize game components
        DrawPile drawPile = new DrawPile();
        GameState gameState = new GameState();
        CardEffectExecutor executor = new CardEffectExecutor(gameState);

        // Create players
        Player harry = new Player("Harry");
        Player cedric = new Player("Cedric");

        // Record initial positions
        gameState.recordPlayerPosition(harry, harry.getPosition());
        gameState.recordPlayerPosition(cedric, cedric.getPosition());

        // Simulate first moves
        harry.move(3);
        cedric.move(2);

        // Record positions after first move
        gameState.recordPlayerPosition(harry, harry.getPosition());
        gameState.recordPlayerPosition(cedric, cedric.getPosition());

        // Simulate a player drawing and executing a time reversal card
        System.out.println("\nBefore time reversal:");
        System.out.println("Harry's position: " + harry.getPosition());
        System.out.println("Cedric's position: " + cedric.getPosition());

        // Create a time reversal card (for demonstration, normally would draw from pile)
        Card timeReversalCard = new Card(CardEffect.TIME_REVERSAL, 0);
        executor.executeEffect(timeReversalCard, harry);

        System.out.println("\nAfter time reversal:");
        System.out.println("Harry's position: " + harry.getPosition());
        System.out.println("Cedric's position: " + cedric.getPosition());

        // After the effect is executed, discard the card
        drawPile.discard(timeReversalCard);

        System.out.println("\nCards remaining in draw pile: " + drawPile.remainingCards());
        System.out.println("Cards in discard pile: " + drawPile.discardedCards());
    }
}