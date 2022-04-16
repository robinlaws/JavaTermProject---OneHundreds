import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Deck of Cards for use in 100s game
 */
public class CardDeck {

    private ArrayList<Card> cardList;

    /**
     * Simple constructor that creates a blank deck
     */
    public CardDeck() {
        this.cardList = new ArrayList<>();
        this.resetDeck();
    }

    /**
     * Deal a deck to a list of players
     * @param playersList
     */
    public void deal(List<Player> playersList){
        while(this.cardList.size() >= playersList.size()){
            playersList.forEach( p -> p.getHand().add(this.cardList.remove(0)));
        }
    }

    /**
     * Shuffle the deck
     */
    public void shuffle(){
        Collections.shuffle(this.cardList);
    }

    /**
     * Sort the deck
     */
    //TODO remove this - for testing purposes only
    public void sort(){
        this.cardList.sort( (c1,c2) -> Card.compare(c1,c2) );
    }

    /**
     * Print the deck to a printStream
     * @param printStream
     */
    public void print(PrintStream printStream){
        cardList.forEach((c)-> printStream.print(c.toString() + " "));
    }

    /**
     * Get the number of cards remaining
     * @return
     */
    public int cardsRemaining(){
        return this.cardList.size();
    }

    /**
     * Resets the deck with Random wilds
     */
    private void resetDeck(){
        cardList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            cardList.add(new Card(i));
        }

        //4 Random Wilds
        Supplier<Integer> randomSupplier = () -> new Random().nextInt(100); //Draws 0 to 99 which is the index

        for (int i = 0; i < 4; i++) {
            int j = randomSupplier.get();
            if(!cardList.get(j).isWild()){
                cardList.get(j).setWild(true);
            } else {
                i--; //try again
            }
        }
    }

}
