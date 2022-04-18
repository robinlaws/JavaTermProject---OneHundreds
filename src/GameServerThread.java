import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * GameServerThread receives the card to play from client, sends the card for the round and
 * receives the results. Returns the card with winning status updated.
 * @author Robin Laws
 * @version CP2561 Term Project
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
    DataOutputStream dataOut;

    /**
     * Constructor for GameServerThread starts the thread and will initialize input and
     * output streams through the socket provided. Player connected to this thread is
     * initialized with this constructor and will talk to player client.
     * @param gameProtocol game protocol for game play
     * @param player client
     * @param socket client socket
     * @throws IOException IO exception
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
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Method setup will send player object to the client for game play.
     * @throws IOException IOException
     */
    synchronized public void setup() throws IOException {
        objectOut.writeObject(player);
        gameProtocol.getPlayerMap().put(player, 0);
        System.out.println(player.getName() + " is ready....\n");
    }

    /**
     * Method playRound will take the card played by the client, and print the details.
     * The card is played through game protocol where the game protocol will wait for
     * all player cards and then compare. The winning card is returned for comparison.
     * @return winning card
     * @throws IOException IOException
     * @throws ClassNotFoundException Exception class not found
     */
    synchronized public Card playRound() throws IOException, ClassNotFoundException, InterruptedException {
        if (thread.getName().equals(gameProtocol.playerList.get(0).getName())){
            gameProtocol.printRoundNumber();
        }else {
            Thread.sleep(1000);
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
                Thread.sleep(1000);}
            else{
                Thread.sleep(500);
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
     * @param winningCard winningCard
     * @throws IOException IOException
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
        Thread.sleep(1000);
        String winnerName = gameProtocol.getWinnerName();
        int numOfWins = gameProtocol.getNumOfWins();
        if (winnerName.equals(player.getName())) {
            System.out.println("\n" + player.getName() + " IS THE WINNER WITH " + numOfWins + " WINS!\n");
            dataOut.writeInt(numOfWins);
        } else if (winnerName.equals("TIE")) {
            System.out.println("TIE GAME!");
            dataOut.writeInt(0);
        } else {
            dataOut.writeInt(numOfWins);
        }
        Thread.sleep(1000);
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