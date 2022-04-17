import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Simple player class. Stores name and hand.
 */
public class Player implements Serializable {

    private final String name;
    private LinkedList<Card> hand;
    public Card currentCard;

    /**
     * Create the Player with a name
     * @param name
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
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the players hand
     * @return
     */
    public LinkedList<Card> getHand() {
        return hand;
    }

    /**
     * Delete the hand
     */
    public void deleteHand(){
        this.hand = new LinkedList<>();
    }

    /**
     * Add a card to a hand
     * @param card
     */
    public void addCard(Card card){
        this.hand.add(card);
    }

    /**
     * Draw a card
     * @return Card
     */
    public Card drawCard(){
        return this.hand.removeFirst();
    }

    /**
     * Set the players hand
     * @param hand
     */
    public void setHand(LinkedList<Card> hand) {
        this.hand = hand;
    }

    /**
     * Print the players hand to a printStream
     * @param printStream
     */
    public void printHand(PrintStream printStream){
        this.hand.forEach((c)->printStream.print(c.toString() + " "));
    }
}