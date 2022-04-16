import java.io.Serializable;

/**
 * Class to represent a card between 1 and 100. The card can be wild
 *
 * @author Josh
 */
public class Card implements Comparable<Card>, Serializable {

    private final int value;
    private boolean wild;
    private boolean winner;

    /**
     * Constructor that takes a value only. Value can't change.
     * @param value Card value between 1 and 100
     */
    public Card(int value) {
        this.value = value; //trust the value is correct
        this.wild = false;
    }

    /**
     * Method isWinner will check the card to see if it has won the round.
     * @return boolean true if winner, false if not winner.
     */

    public boolean isWinner() {
        return winner;
    }

    /**
     * Method setWinner will set the boolean to true if the card
     * has won the round.
     * @param winner boolean
     */

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * Get card value
     * @return value
     */
    public int getValue() {
        return value;
    }

    /**
     * Determines if this card is wild
     * @return true if wild
     */
    public boolean isWild() {
        return wild;
    }

    /**
     * Set this card wild
     * @param wild true if wild
     */
    public void setWild(boolean wild) {
        this.wild = wild;
    }

    /**
     * A static method for comparing two cards. Highest card value wins if both are not wild.
     * If one is wild that card wins. If both are wild the LOWEST card wins
     * @param c1 first card
     * @param c2 second card
     * @return Less than 0 if c1 wins, Greater than 0 if c2 wins
     */
    public static int compare(Card c1, Card c2){
        return c1.compareTo(c2);
    }

    /**
     * A method for comparing the card to the card passed in.
     * Highest card value wins if both are not wild.
     * If one is wild that card wins. If both are wild the LOWEST card wins
     * @param card card to compare against
     * @return Less than 0 if this card wins, Greater than 0 if card passed in wins
     */
    @Override
    public int compareTo(Card card) {
        if(this.isWild() && card.isWild()){ //When both are wild take the smaller value
            return (-1 * (card.getValue() - this.value) < 0) ? -1 : 1; //-1 to flip the value
        } else if (this.isWild() && !card.isWild()){
            return -1;
        } else if (!this.isWild() && card.isWild()){
            return 1;
        } else {
            return (card.getValue() - this.value < 0) ? -1 : 1;
        }
    }

    /**
     * Represent a card to a string
     * @return the card value and a " WILD" appended if it's wild
     */
    public String toString(){
        return String.valueOf(this.value) + (this.isWild() ? " WILD" : "");
    }
}
