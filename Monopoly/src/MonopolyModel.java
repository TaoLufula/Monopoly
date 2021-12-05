import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MonopolyModel{
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
     * @attribute MonopolyView this is the GUI class
     */
    private final Board board;
    private final List<Player> players;
    private final Dice die;
    int playerTurn;
    private MonopolyView view;
    private final static String SAVEBOARDFILE = "saveBoard.xml";
    private final static String SAVEPLAYERSFILE = "savePlayers.xml";
    private final static String OTHERINFOFILE = "otherInfo.xml";

    public MonopolyModel(int boardType) {
        this.board = new Board(boardType);
        this.die = new Dice();
        this.players = new ArrayList<>();
        this.playerTurn = 0;
    }

    public MonopolyModel(Board b, ArrayList<Player> p, int pTurn){
        this.board = b;
        this.players = p;
        this.playerTurn = pTurn;
        this.die = new Dice();
    }

    /**
     * Getter for board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     *  Setter for MonopolyView.
     */
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
     * This method checks if the rolled dice are doubles.
     * @return a boolean expression of whether the dice is doubles
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public boolean isDoubles() {
        return this.die.isDoubles();
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
     * Created and documented by Nathan MacDiarmid - 101098993
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
     * Created and documented by Matthew Belanger - 101144323 and Tao - 101164153
     * Refactored and re-documented by Nathan MacDiarmid - 101098993
     */
    public boolean buyProperty(int selection) {
        if (selection == JOptionPane.YES_OPTION) {
            return this.getPlayer().buy(this.board.getProperty(this.getPlayer().getPosition()));
        }
        return true;
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
        this.getPlayer().rent(this.board.getProperty(this.getPlayer().getPosition()));
    }

    /**
     * This method is the logic behind adding players to the list
     * of players.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    /**
     * This method is the logic behind adding AI's to the list
     * of players.
     *
     * Created and documented by Matthew Belanger - 101144323
     */
    public void addAI(String name) {
        players.add(new AI(name));
    }

    /**
     * Handles the squares that aren't normal properties.
     * Current squares: Go, Free Parking, Jail, Go To Jail
     * @return boolean value whether a player landed on one of these properties.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public boolean handleEmptyProperties() {
        if (this.board.getProperty(this.getPlayer().getPosition()) instanceof Go) {
            return true;
        }
        else if (this.board.getProperty(this.getPlayer().getPosition()) instanceof FreeParking) {
            return true;
        }
        else if (this.board.getProperty(this.getPlayer().getPosition()) instanceof GoToJail) {
            return true;
        }
        else {
            return this.getBoard().getProperty(this.getPlayer().getPosition()) instanceof Jail;
        }
    }

    /**
     * Determines the amount of Railroads a Player owns and calls the updateRent() method
     * from Railroad
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public void setRailroadRent() {
        int railroads;
        for (Player p : players) {
            railroads = p.getAmountofRailroads();
            for (Property pr : p.getPropertiesArray()) {
                if (pr instanceof Railroad) {
                    pr.updateRent(railroads);
                }
            }
        }
    }

    /**
     * Determines the number of Utilities owned by a player, then updates the rent
     * accordingly
     *
     * @param roll int value of dice rolled
     *
     *Created and documented by Tao Lufula - 101164153
     */
    public void setUtilityRent(int roll) {
        int utility;
        for (Player p : players) {
            utility = p.getAmountOfUtilities();
            for (Property pr : p.getPropertiesArray()) {
                if (pr instanceof Utilities) {
                    if (utility == 1) {
                        pr.updateRent(roll*4);
                    }
                    else if (utility == 2) {
                        pr.updateRent(roll*10);
                    }
                }
            }
        }
    }

    /**
     * Calls the goToJail method from the GoToJail class
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public void goToJail() {
        if (this.board.getProperty(this.getPlayer().getPosition()) instanceof GoToJail) {
            ((GoToJail) this.board.getProperty(this.getPlayer().getPosition())).goToJail(this.getPlayer());
        }
    }

    /**
     * Calls the inJail method in the Jail class
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public void inJail() {
        if (this.getPlayer().getJailed()) {
            ((Jail) this.board.getProperty(this.getPlayer().getPosition())).inJail(this.getPlayer(), isDoubles());
        }
    }

    /**
     * Checks if a player is in Jail or landed on GoToJail.
     * Reduces code smells.
     *
     * @return a boolean value whether a player is in or is going to Jail.
     *
     * Created and documented by Nathan MacDiarmid - 101098993
     */
    public boolean checkIfInJail() {
        if (getBoard().getProperty(getPlayer().getPosition()) instanceof GoToJail) {
            return true;
        }
        else return getBoard().getProperty(getPlayer().getPosition()) instanceof Jail && getPlayer().getJailed();

    }

    /**
     * This method handles the logic behind a player turn, it will be called by view and will increment the player position
     * check the property the player is on as well as the players money and if someone has won. Finally it will change the turn
     * to the next player and then update the view.
     * @param rollValue
     *
     * Created and documented by Matthew Belanger - 101144323 , Tao Lufula - 101164153
     *
     */
    public void playTurn(int rollValue){

        do {
            if(this.getPlayer() instanceof AI){
                ((AI) this.getPlayer()).AITurn(this, view);
            }
            else {
                this.getPlayer().addPosition(rollValue);
                this.setUtilityRent(rollValue);
                view.checkAvailability();
                this.goToJail();
            }

            this.inJail();

            this.getPlayer().updatePositionTracker();

            if (getPlayer().getMoney() <= 0) {
                view.playerEliminated();
                this.removePlayer();
            }
            if (this.players.size() == 1) {
                view.playerWin();
            }
            this.playerTurn = (this.playerTurn + 1) % this.players.size();
            view.updateStatus();

        }while(this.getPlayer() instanceof AI);
    }

    /**
     * This method exports the current Monopoly model into multiple xml files, first there is a file to save the board,
     * next is a file to save the players, and finally a file to save the board type and whos turn it is.
     *
     * Created and documented by Matthew Belanger - 101144323
     */
    public void exportToXmlFile(){
        try {
            Writer w = new FileWriter(SAVEBOARDFILE);
            w.write(this.board.toXML(0));
            w.close();
            Writer w2 = new FileWriter(SAVEPLAYERSFILE);
            w2.write("<Players>\n");
            for (Player player : this.players) {
                w2.write(player.toXML(1));
            }
            w2.write("</Players>\n");
            w2.close();
            Writer w3 = new FileWriter(OTHERINFOFILE);
            w3.write("<OtherInfo>\n");
            w3.write("<Turn>"+this.playerTurn+"</Turn>\n");
            w3.write("<BoardType>"+this.board.getBoardType()+"</BoardType>\n");
            w3.write("</OtherInfo>");
            w3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method imports a saved version of monopolyModel from the saved files using the defined SAX handlers to
     * parse the files. First the board file is parsed and a list of all the properties is returned. Next the player
     * file is parsed and a list of all players is returned. Next all of the properties are assigned their current owner.
     * Finally the other info file is parsed and the player turn and board type is returned. Now that we have all the
     * required information a new model is created and returned.
     *
     * @return a MonopolyModel object
     *
     * Created and documented by Matthew Belanger - 101144323
     */
    public static MonopolyModel importFromXmlFile(){

        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            InputStream is = new FileInputStream(SAVEBOARDFILE);
            SAXParser saxParser = factory.newSAXParser();

            BoardSAXHandler handler = new BoardSAXHandler();

            saxParser.parse(is, handler);

            List<Property> properties = handler.getResult();

            InputStream is2 = new FileInputStream(SAVEPLAYERSFILE);
            SAXParser saxParser2 = factory.newSAXParser();

            PlayerSAXHandler handler2 = new PlayerSAXHandler(properties);

            saxParser2.parse(is2, handler2);

            List<Player> players = handler2.getResult();

            for(int i = 0; i < properties.size(); i++){
                for(int j = 0; j < players.size(); j++){
                    for(int k = 0; k < players.get(j).getPropertiesOwned().size(); k++){
                        if(players.get(j).getPropertiesOwned().get(k).getName().equals(properties.get(i).getName())){
                            properties.get(i).setOwner(players.get(j));
                        }
                    }
                }
            }

            InputStream is3 = new FileInputStream(OTHERINFOFILE);
            SAXParser saxParser3 = factory.newSAXParser();

            OtherInfoSAXHandler handler3 = new OtherInfoSAXHandler();

            saxParser3.parse(is3, handler3);

            Board board = new Board(handler3.getBoardType());

            return new MonopolyModel(board, (ArrayList<Player>) players, handler3.getTurn());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}