import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Simple player class. Stores name and hand.
 *  * @author Robin Laws
 *  * @version CP2561 Term Project
 */
public class Player implements Serializable {

    private final String name;
    private final LinkedList<Card> hand;
    public Card currentCard;

    /**
     * Create the Player with a name
     * @param name player name
     */
    public Player(String name) {
        this.name = name;
        hand = new LinkedList<>();
    }

    /**
     * Method getCurrentCard will show the card the player is currently
     * playing.
     * @return players current card
     */
    public Card getCurrentCard() {
        return currentCard;
    }

    /**
     * Method setCurrentCard will set the card to the current
     * card.
     * @param currentCard current card the player is playing.
     */
    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    /**
     * Get the player's name
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the players hand
     * @return players hand
     */
    public LinkedList<Card> getHand() {
        return hand;
    }

    /**
     * Print the players hand to a printStream
     * @param printStream print stream of player hand
     */
    public void printHand(PrintStream printStream){
        this.hand.forEach((c)->printStream.print(c.toString() + " "));
    }
}