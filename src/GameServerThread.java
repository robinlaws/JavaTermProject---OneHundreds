import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

/**
 * GameServerThread recieves the card to play from client, sends the card for the round and
 * recieves the results. Returns the card with winning status updated.
 */
public class GameServerThread implements Runnable {

    GameProtocol gameProtocol;
    Player player;
    Socket socket;
    ObjectOutputStream objectOut;
    ObjectInputStream objectIn;
    PrintWriter out;
    BufferedReader in;
    LinkedList<Card> returnedCards;

    /**
     * Constructor for GameServerThread starts the thread and will initialize input and
     * output streams through the socket provided. Player connected to this thread is
     * initialized with this constructor and will talk to player client.
     * @param gameProtocol game protocol for game play
     * @param player client
     * @param socket client socket
     * @throws IOException
     */
    public GameServerThread(GameProtocol gameProtocol, Player player, Socket socket) throws IOException {
        this.gameProtocol = gameProtocol;
        this.player = player;
        this.socket = socket;
        this.objectOut = new ObjectOutputStream(socket.getOutputStream());
        this.objectIn = new ObjectInputStream(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.returnedCards = new LinkedList<Card>();
    }

    /**
     * Method setup will send player object to the client for game play.
     * @throws IOException
     */

    synchronized public void setup() throws IOException {
        objectOut.writeObject(player);
        gameProtocol.getPlayerMap().put(player, 0);
        System.out.println(player.getName() + " is ready.");
    }

    /**
     * Method playRound will take the card played by the client, and print the details.
     * The card is played through game protocol where the gameprotocol will wait for
     * all player cards and then compare. The winning card is returned for comparison.
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    synchronized public Card playRound() throws IOException, ClassNotFoundException {
        Card winningCard = new Card(0);
        Card playedCard;
        playedCard = (Card) objectIn.readObject();
        player.setCurrentCard(playedCard);
        if (playedCard.getValue() != 1000) {
            if (playedCard.isWild()) {
                System.out.println(player.getName() + " WILD (" + playedCard.getValue() + ")");
            }else{
                System.out.println(player.getName() + ": " + playedCard.getValue());
            }
            gameProtocol.setRoundCards(playedCard);
            if (gameProtocol.roundCards.size() == gameProtocol.NUMBER_PLAYERS) {
                winningCard = gameProtocol.processCards();
            }
        } else {
            gameProtocol.setGameStatus(GameProtocol.Status.GAME_OVER);
            winningCard = new Card(1000);
        }
        return winningCard;
    }

    /**
     * Method sendResults will update the player map to track wins, and will
     * send out the players card played with winning status results.
     * @param winningCard
     * @throws IOException
     */
    synchronized public void sendResults(Card winningCard) throws IOException {
        if (winningCard.getValue() == player.getCurrentCard().getValue()) {
            System.out.println(player.getName() + " WINS!");
            player.getCurrentCard().setWinner(true);
            for (Map.Entry<Player, Integer> entry: GameProtocol.playerMap.entrySet()){
                GameProtocol.playerMap.put(player, GameProtocol.playerMap.get(player) + 1);
            }
        }
        objectOut.writeObject(player.currentCard);
    }

    /**
     * Method get winner will display the results of the game through
     * player map and number of wins. It will send the number of wins to the client.
     */
    synchronized public void getWinner() {
        int mostWins = (Collections.max(GameProtocol.playerMap.values()));
        for (Map.Entry<Player, Integer> entry : GameProtocol.playerMap.entrySet()) {
            if (entry.getValue() == mostWins) {
                Player winner = entry.getKey();
                Integer wins = entry.getValue();
                out.println(wins);
                if (winner.getName().equals(player.getName())) {
                    System.out.println("\n" + player.getName() + " IS THE WINNER WITH " + wins + " WINS!");
                }
            }
        }
    }

    /**
     * Thread runnable method run() which calls methods according to status of game.
     * This method will begin the game, and close sockets when the game has ended.
     */
    public void run() {
        while (true) {
            if (gameProtocol.getGameStatus() == GameProtocol.Status.NOT_STARTED) {
                try {
                    setup();
                    gameProtocol.setGameStatus(GameProtocol.Status.PLAY_IN_PROGRESS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (gameProtocol.getGameStatus() == GameProtocol.Status.PLAY_IN_PROGRESS) {
                try {
                    Card winningCard = playRound();
                    sendResults(winningCard);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (gameProtocol.getGameStatus() == GameProtocol.Status.GAME_OVER) {
                System.out.println("\n" + player.getName() + " HAS NO MORE CARDS!");
                break;
            }
        }
        getWinner();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
