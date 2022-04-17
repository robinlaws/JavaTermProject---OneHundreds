import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * GameProtocol is initialized from server and will track the status of the game. This is where the players cards
 * are recieved for comparison and will return results.
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

    public void printRoundNumber(){
        System.out.println("ROUND " + roundNumber);
    }

    /**
     * getPlayerMap will return the player map with current scores.
     * @return
     */
    synchronized public Map<Player, Integer> getPlayerMap() {
        return playerMap;
    }

    /**
     * getGameStatus will return the current status of the game.
     * @return
     */
    synchronized public Status getGameStatus() {
        return gameStatus;
    }

    /**
     * setGameStatus for setting status of game
     * @param gameStatus status of game
     */
    synchronized public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }

    /**
     * setRoundCards will add the cards sent from the client to a linkedList for
     * comparison to play the round.
     * @param card players current card.
     */

    synchronized public void setRoundCards(Card card) {

        roundCards.add(card);
        if (roundCards.size() == NUMBER_PLAYERS){
            processCards();
        }
    }

    synchronized public Card getWinningCard() {
        return winningCard;
    }

    synchronized public void setWinningCard(Card winningCard) {
        this.winningCard = winningCard;
    }

    /**
     * process cards will run once the game has recieved all cards.
     * This will compare the two cards and return the winning card.
     * @return
     */


    public void processCards(){
        printRoundNumber();
        Card winningCard = new Card(0);
            for (Card c : roundCards){
                int result = winningCard.compareTo(c);
                if (result == 1){
                    winningCard = c;
                }
            }
            roundCards.clear();
            setWinningCard(winningCard);
            roundNumber += 1;
    }
}