import java.io.*;
import java.net.Socket;


/**
 * Client class represents the player. Receives hand from server and will send one card for each round.
 * Receives card back with winning status to check if they were a winner.
 */
public class Client {

    public static void main(String[] args) throws IOException {

        //Game Server location
        String hostName = "localhost";
        int portNumber = 4401;

        try (
                Socket gameSocket = new Socket(hostName, portNumber);
                DataInputStream in = new DataInputStream(gameSocket.getInputStream());
                ObjectInputStream objectIn = new ObjectInputStream(gameSocket.getInputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(gameSocket.getOutputStream())
        ) {
            int numOfWins = 0;
            int roundNumber = 1;
            Card playedCard;
            Player player;
            System.out.println("Welcome to One Hundreds!");
            player = (Player) objectIn.readObject();
            System.out.println(player.getName() + " here is your hand: ");
            for (Card card : player.getHand()) {
                if (card.isWild()) {
                    System.out.print(" WILD");
                } else {
                    System.out.print(" " + card.getValue());
                }
            }
            System.out.println();
            while (true) {
                if (player.getHand().size() > 0) {
                    playedCard = player.getHand().get(0);
                    objectOut.writeObject(playedCard);
                    System.out.println("\nROUND " + roundNumber);
                    if (playedCard.isWild()) {
                        System.out.println("You have played : WILD (" + playedCard.getValue() + ")");
                    } else {
                        System.out.println("You have played : " + playedCard.getValue());
                    }
                    player.getHand().remove(0);
                } else {
                    Card endCard = new Card(1000);
                    objectOut.writeObject(endCard);
                    System.out.println("\nNO MORE CARDS");
                    break;
                }
                playedCard = (Card) objectIn.readObject();
                if (playedCard.isWinner()) {
                    System.out.println("WINNER!");
                    numOfWins += 1;
                } else {
                    System.out.println("Sorry not a winner!");
                }
                roundNumber += 1;
            }

            int wins = in.readInt();
            if (wins == numOfWins) {
                System.out.println("\nCONGRATULATIONS! You are the winner with " + numOfWins + " wins!");
            } else if (wins == 0) {
                System.out.println("\nTIE GAME! You both had " + numOfWins + " wins!");
            } else {
                System.out.println("\nGAME OVER! Not a winner....Better luck next time.");
            }
            System.out.println("Thanks for playing!");

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

