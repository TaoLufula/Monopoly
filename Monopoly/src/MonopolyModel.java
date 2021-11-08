import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MonopolyModel {
    /**
     * The Monopoly class
     *
     * This class is the brains of the game,
     * it controls all the functions and how the players
     * move about the board.
     *
     * @attribute board type Board, is one instance
     * @attribute players type ArrayList<Player> contains the list
     * players that are currently playing
     * @attribute die type Dice is the die that will be used for the game
     * @attribute playerTurn type int is used to determine whose turn it is
     */
    private final Board board;
    private final List<Player> players;
    private final Dice die;
    private int diceValue;
    private boolean validCommand = false;
    private boolean running = true;
    int playerTurn;
    private MonopolyView view;

    public MonopolyModel() {
        this.board = new Board();
        this.die = new Dice();
        this.players = new ArrayList<>();
        this.playerTurn = 0;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addMonopolyView(MonopolyView mv){
        view = mv;
    }

    /**
     * This method is called if a player goes bankrupt, it first removes all the players ownerships and
     * then removes the player from the game.
     *
     * Created and documented by Matthew Belanger
     */
    public void removePlayer() {
        this.players.get(this.playerTurn).removeProperties();
        this.players.remove(this.playerTurn);
    }

    /**
     * Roll() method rolls the dice
     * @return the random int generated by rolling the die
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public int roll() {
        return this.die.roll();
    }

    /**
     * This method is used to get the player whose turn it is
     * @return the current Player object
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     */
    public Player getPlayer(){
        return this.players.get(playerTurn);
    }

    /**
     * This method returns a string representation of the property that the Player is currently on
     * @return string representation of a Property
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     */
    public String getPropertyInfo(){
        return this.board.getProperty(this.getPlayer().getPosition()).toString();
    }

    /**
     * This method returns the owner (which is a Player object) of the Property the Player is currently on
     * @return Player object
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     */
    public Player getPropertyOwner(){
        return this.board.getProperty(this.getPlayer().getPosition()).getOwner();
    }

    /**
     * This method verifies that the property is available to buy.
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     * Refactored and documented by Nathan MacDiarmid - 101098993
     */
    public boolean checkProperty() {
        if (this.getPropertyOwner() != null) {
            if (this.getPlayer() != this.getPropertyOwner()) {
                payRent();
            }
            return false;
        }

        if (handleEmptyProperties()) {
            return false;
        }

        return true;
    }

    /**
     * This method allows players to buy properties.
     * @param selection a selection from a JOptionPane
     * @return a boolean value whether the property was bought.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public boolean buyProperty(int selection) {
        if (selection == JOptionPane.YES_OPTION) {
            return this.getPlayer().buy(this.board.getProperty(this.getPlayer().getPosition()));
        }
        return true;
    }

    /**
     * This method calls the current Player's rent method and passes the property they are currently on as a parameter
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     */
    public void playerRent(){
        this.getPlayer().rent(this.board.getProperty(this.getPlayer().getPosition()));
    }

    /**
     * This method adds money to the Player the rent is being paid too. This is done by getting the Player who owns the
     * property the current Player is on and then calling the addMoney() method on the Player owning the property.
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     * Refactored by Nathan MacDiarmid - 101098993
     */
    public void payRent(){
        this.board.getProperty(this.getPlayer().getPosition()).getOwner().addMoney(this.board.getProperty(this.getPlayer().getPosition()).getRent());
        this.playerRent();
        System.out.println("You paid $" + this.getRent() + " of rent to " + this.getPropertyOwner().getName());
        System.out.println("You now have $" + this.getPlayer().getMoney());
    }

    /**
     * This method will return the rent amount of the Property the current Player is on
     * @return rent value in the form of an int
     *
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     */
    public int getRent(){
        return this.board.getProperty(this.getPlayer().getPosition()).getRent();
    }

    /**
     * This method is the logic is the logic behind adding players to the list
     * of players.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    /**
     * Handles the empty properties that don't do anything at the moment.
     * Current empties: Go and Free Parking
     * @return boolean value whether a player landed on one of these properties.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public boolean handleEmptyProperties() {
        if (this.board.getProperty(this.getPlayer().getPosition()).equals(this.board.getProperty(0))) {
            return true;
        }
        else return this.board.getProperty(this.getPlayer().getPosition()).equals(this.board.getProperty(17));
    }

    /**
     * Check to see if the current player has run out of money
     * Prints bankruptcy if current player ran out of money
     *
     * Created and documented by Mehedi Mostofa (101154128)
     */
    public boolean checkMoney() {
        if (getPlayer().getMoney() <= 0) {
            this.removePlayer();
            return true;
        }
        return false;
    }

    /**
     * Checks to see if there is only one player left. If yes, the remaining player has won
     * Prints the name of the winner
     *
     * Created and documented by Mehedi Mostofa (101154128)
     */
    public boolean checkPlayer(){
            if(this.players.size() == 1){
                return true;
            }
            return false;
    }

    public void playTurn(int rollValue){
        this.getPlayer().addPosition(rollValue);
        //Increase playerTurn to pass the turn to the next player
        view.checkAvailability();
        if(getPlayer().getMoney() <= 0){
            view.playerEliminated();
            this.removePlayer();
        }
        if(this.players.size() == 1){
            view.playerWin();
        }
        this.playerTurn = (this.playerTurn + 1) % this.players.size();
        view.updateStatus();
    }

    /**
     * This is the method that actually runs the game, it starts by getting names for every player in the game and adding them to the list
     * of players. Next it runs a loop that will loop until only one player remains, this loop starts by getting the player whose turn
     * it is to roll the dice. The value they roll will be added to their current position, they will then move the corresponding amount of
     * spaces on the board landing on a property space. If the property is unowned they will be prompted to buy said property, if another
     * player owns the property they will pay this player the rent of the property. The players turn then ends, the player's money is then checked
     * to see if they are bankrupt, if they are then they are removed from the game. If a player is removed from the game then the list of players
     * is checked to see if only one player remains, if only one player remains then the game is over and this player wins. Finally at the end of the loop
     * the playerTurn counter is increased so that the game knows it is the next players turn.
     *
     * Created and documented by Matthew Belanger - 101144323, Nathan MacDiarmid - 101098993, Tao - 101164153, Mehedi Mostofa - 101154128
     */
    public void play(){

        running = true;

        while(running){
            System.out.println(this.getPlayer().getName() + " it is your turn");

            //Get user input
            Scanner rollInput = new Scanner(System.in);
            System.out.println("Enter 'roll' to roll the Dice, 'info' to show player info or 'quit' to quit the game");
            System.out.print(">>> ");
            String input = rollInput.nextLine();

            //Check user input
            switch (input) {
                case "roll":
                    diceValue = this.roll();
                    System.out.println("You rolled a " + diceValue);
                    this.getPlayer().addPosition(diceValue);

                    System.out.println("You landed on " + getPropertyInfo());

                    // check if property is not owned
                    checkProperty();

                    break;
                case "info":
                    System.out.println(this.getPlayer().toString());
                    System.out.println(this.getPlayer().getProperties());
                    System.out.println("You are currently on " + this.getPropertyInfo());
                    continue;
                case "quit":
                    running = false;
                    continue;
                default:
                    System.out.println("Command not recognized");
                    continue;
            }

            //Increase playerTurn to pass the turn to the next player
            this.playerTurn = (this.playerTurn + 1) % this.players.size();
        }
        checkMoney();
        checkPlayer();
    }

}