import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * GameProtocol is initialized from server and will track the status of the game. This is where the players cards
 * are received for comparison and will return results.
 *  * @author Robin Laws
 *  * @version CP2561 Term Project
 */
public class GameProtocol{
    private Status gameStatus;
    LinkedList<Player> playerList;
    public static Map<Player, Integer> playerMap = new HashMap<>();
    int NUMBER_PLAYERS;
    LinkedList<Card> roundCards;
    Card winningCard = null;
    int roundNumber;

    /**
     * Status of gameplay
     */
    public enum Status {
        NOT_STARTED, PLAY_IN_PROGRESS, GAME_OVER,
    }

    /**
     * Constructor to initialize GameProtocol
     * @param playerList list of players
     * @param NUMBER_PLAYERS number of players
     */
    public GameProtocol(LinkedList<Player> playerList, int NUMBER_PLAYERS) {
        this.playerList = playerList;
        this.gameStatus = Status.NOT_STARTED;
        this.NUMBER_PLAYERS = NUMBER_PLAYERS;
        this.roundCards = new LinkedList<>();
        for (Player p : playerList) {
            playerMap.put(p, 0);
        }
        this.roundNumber = 1;
    }

    /**
     * synchronized method prints round number on server during game
     */
    public void printRoundNumber(){
        System.out.println("ROUND " + roundNumber);
    }

    /**
     * synchronized method getPlayerMap will return the player map with current scores.
     * @return player map
     */
    synchronized public Map<Player, Integer> getPlayerMap() {
        return playerMap;
    }

    /**
     * synchronized method getGameStatus will return the current status of the game.
     * @return game status
     */
    synchronized public Status getGameStatus() {
        return gameStatus;
    }

    /**
     * synchronized method setGameStatus for setting status of game
     * @param gameStatus status of game
     */
    synchronized public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }

    /**
     * synchronized method setRoundCards will add the cards sent from the client to a linkedList for
     * comparison to play the round.
     * @param card players current card.
     */
    synchronized public void setRoundCards(Card card) {
        roundCards.add(card);
        if (roundCards.size() == NUMBER_PLAYERS){
            processCards();
        }
    }

    /**
     * synchronized method gets winner from player map and returns the name of the winner
     * @return winner name
     */
    synchronized public String getWinnerName() {
        String winnerName = "";
        int tieGame = 0;
        int mostWins = (Collections.max(GameProtocol.playerMap.values()));
        for (Map.Entry<Player, Integer> entry : GameProtocol.playerMap.entrySet()) {
            if (entry.getValue() == mostWins) {
                tieGame += 1;
                Player winner = entry.getKey();
                winnerName = winner.getName();
            }
        }
        if (tieGame > 1){
            winnerName = "TIE";
        }
        return winnerName;
    }

    /**
     * synchronized method gets num of wins that won the game
     * @return max value of wins
     */
    synchronized public int getNumOfWins() {
        return (Collections.max(GameProtocol.playerMap.values()));
    }

    /**
     * synchronized method gives winning card for the round.
     * @return winning card
     */
    synchronized public Card getWinningCard() {
        return winningCard;
    }

    /**
     * synchronized method sets card to the end card value to initialize end of the game
     */
    synchronized public void setEndCard() {
        this.winningCard = new Card(1000);
    }

    /**
     * synchronized method sets the round winner to winning card
     * @param card winning card
     */
    synchronized  public void setWinningCard(Card card){
        this.winningCard = card;
    }

    /**
     * Method process cards will run once the game has received all cards.
     * This will compare the two cards and return the winning card.
     */
    public void processCards(){
        printRoundNumber();
        Card winningCard = new Card(0);
            for (Card c : roundCards){
                int result = winningCard.compareTo(c);
                if (result > 0){
                    winningCard = c;
                }
            }
            roundCards.clear();
            setWinningCard(winningCard);
            roundNumber += 1;
    }
}