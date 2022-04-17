import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * Client class represents the player. Recieves hand from server and will send one card for each round.
 * Recieves card back with winning status to check if they were a winner.
 */
public class Client {

    public static void main(String[] args) throws IOException {

        //Game Server location
        String hostName = "localhost";
        int portNumber = 4401;

        //Connect to the game server
        try (
                Socket gameSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(gameSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

                OutputStream outputStream = gameSocket.getOutputStream();
                ObjectInputStream objectIn = new ObjectInputStream(gameSocket.getInputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(gameSocket.getOutputStream());
                InputStream inputStream = gameSocket.getInputStream();

        ) {
            int numOfWins = 1;
            int roundNumber = 1;
            Card playedCard;
            Player player;
            System.out.println("Welcome to One Hundreds!");
            player = (Player) objectIn.readObject();
            System.out.println("Welcome " + player.getName() + "\n Here is your hand: ");
            for (Card card : player.getHand()) {
                if (card.isWild()) {
                    System.out.print(" WILD");
                } else {
                    System.out.print(" " + card.getValue());
                }
            }
            while(true) {
                if (player.getHand().size() > 0) {
                    playedCard = player.getHand().get(0);
                    objectOut.writeObject(playedCard);
                    System.out.println("\nROUND " + roundNumber);
                    System.out.println("You have played : " + playedCard.getValue());
                    player.getHand().remove(0);
                } else {
                    Card endCard = new Card(1000);
                    objectOut.writeObject(endCard);
                    System.out.println("NO MORE CARDS");
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
            System.out.println(numOfWins);
            System.out.println(numOfWins);
            if (numOfWins == (in.read())){
                System.out.println("Congratulations! You are the winner with " + numOfWins);
            }else{
                System.out.println("Sorry, better luck next time!");
            }

            System.out.println("Thanks for playing!");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

