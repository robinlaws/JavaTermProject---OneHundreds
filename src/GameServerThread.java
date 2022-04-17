import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * GameServerThread recieves the card to play from client, sends the card for the round and
 * recieves the results. Returns the card with winning status updated.
 */
public class GameServerThread implements Runnable, Serializable {

    GameProtocol gameProtocol;
    Player player;
    Socket socket;
    ObjectOutputStream objectOut;
    ObjectInputStream objectIn;
    PrintWriter out;
    BufferedReader in;
    LinkedList<Card> returnedCards;
    Thread thread;

    /**
     * Constructor for GameServerThread starts the thread and will initialize input and
     * output streams through the socket provided. Player connected to this thread is
     * initialized with this constructor and will talk to player client.
     * @param gameProtocol game protocol for game play
     * @param player lient
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
        this.returnedCards = new LinkedList<>();
        this.thread = Thread.currentThread();
    }

    /**
     * Method setup will send player object to the client for game play.
     * @throws IOException
     */

    synchronized public void setup() throws IOException {
        objectOut.writeObject(player);
        gameProtocol.getPlayerMap().put(player, 0);
        System.out.println(player.getName() + " is ready....\n");
    }

    /**
     * Method playRound will take the card played by the client, and print the details.
     * The card is played through game protocol where the gameprotocol will wait for
     * all player cards and then compare. The winning card is returned for comparison.
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    synchronized public Card playRound() throws IOException, ClassNotFoundException, InterruptedException {
        if (thread.getName().equals(gameProtocol.playerList.get(0).getName())){
            gameProtocol.printRoundNumber();
        }else {
            thread.sleep(1000);
        }
        Card winningCard;
        Card playedCard;
        playedCard = (Card) objectIn.readObject();
        player.setCurrentCard(playedCard);
        if (playedCard.getValue() != 1000) {
            if (gameProtocol.roundNumber == 1){
                if(gameProtocol.playerList.get(0).getName().equals(player.getName())){
                    System.out.println("STARTING GAME");
                }
            }
            if (playedCard.isWild()) {
                System.out.println(player.getName() + " PLAYS WILD (" + playedCard.getValue() + ")");
            }else{
                System.out.println(player.getName() + " PLAYS: " + playedCard.getValue());
            }
            gameProtocol.setRoundCards(playedCard);
            if (this.thread.getName().equals(gameProtocol.playerList.get(0).getName())){
                thread.sleep(1000);}
            else{
                thread.sleep(500);
            }

        } else {
            gameProtocol.setGameStatus(GameProtocol.Status.GAME_OVER);
            gameProtocol.setEndCard();
        }
        winningCard = gameProtocol.getWinningCard();
        return winningCard;
    }

    /**
     * Method sendResults will update the player map to track wins, and will
     * send out the players card played with winning status results.
     * @param winningCard
     * @throws IOException
     */
    synchronized public void sendResults(Card winningCard) throws IOException, InterruptedException {
        if (winningCard.getValue() == player.getCurrentCard().getValue()) {
            System.out.println(player.getName() + " GETS A POINT! \n");
            player.getCurrentCard().setWinner(true);
            GameProtocol.playerMap.put(player, GameProtocol.playerMap.get(player) + 1);

        }
        objectOut.writeObject(player.currentCard);
    }

    /**
     * Method get winner will display the results of the game through
     * player map and number of wins. It will send the number of wins to the client and close the
     * socket.
     */
    synchronized public void getWinner() throws IOException, InterruptedException {
        String winnerName = null;
        int tieGame = 0;
        int mostWins = (Collections.max(GameProtocol.playerMap.values()));
        for (Map.Entry<Player, Integer> entry : GameProtocol.playerMap.entrySet()) {
            if (entry.getValue() == mostWins) {
                tieGame += 1;
                Player winner = entry.getKey();
                Integer wins = entry.getValue();
                winnerName = winner.getName();
                if (winner.getName().equals(this.player.getName())) {
                    System.out.println("\n" + this.player.getName() + " IS THE WINNER WITH " + wins + " WINS!\n");
                }
            }
        }
        if (tieGame > 1){
            System.out.println("TIE GAME!");
        }
        gameProtocol.setWinnerName(winnerName);
        thread.sleep(1000);
        String finalWinner = gameProtocol.getWinnerName();
        out.println(finalWinner);
        thread.sleep(10000);
        System.exit(1);
    }


    /**
     * Thread runnable method which calls methods according to status of game.
     * This method will begin the game, and close sockets when the game has ended.
     */
    public void run() {
        this.thread.setName(player.getName());
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
                    if (winningCard.getValue() != 1000) {
                        sendResults(winningCard);
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (gameProtocol.getGameStatus() == GameProtocol.Status.GAME_OVER) {
                System.out.println("\n" + player.getName() + " HAS NO MORE CARDS!");
                break;
            }
        }
        try {
            getWinner();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}