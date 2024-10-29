import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
/**
 * Models a Player which is controlled by the user via the GUI.
 * @author Nikolaj Ignatieff Schwartzbach. 
 * @version August 2019.
 */
public abstract class Player implements Comparable<Player> {

    private Position pos;             // Position of this Player
    private int money;                // Amount of money this Player has collected

    /**
     * Creates a new Player object with the specified position and a specified amount of money.
     * @param pos     Position of this player.
     * @param money   Starting balance of this player (amount of money).
     */
    public Player(Position pos, int money) {
        this.pos = pos;
        this.money = money;
    }

    /**
     * Creates a new Player object with the specified position and no money.
     * @param pos  Position of this player.
     */
    public Player(Position pos) {
        this(pos, 0);
    }

    /**
     * Resets this Player so that money = 0.
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * Advances this Player one step.
     */
    public boolean step() {
        if(pos.move() && pos.hasArrived()) {
            money += pos.getTo().arrive();
        }
        return pos.hasArrived();
    }

    /**
     * Gets the amount of money this Player has collected.
     * @return  The money collected by this Player (in €).
     */
    public int getMoney() {
        return money;
    }

    /**
     * Gets the  position of this Player.
     * @return   Position of this Player (in pixels).
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Sets the position of this Player.
     * Only has effect, if the Player has arrived to its destination, or the game has just started.
     * @param newPos   Position to attempt to move this Player to.
     */
    public void setPosition(Position newPos) {
        if(pos.hasArrived() || getCountry().getGame().getStepsLeft() == getCountry().getGame().getTotalSteps())
            pos = newPos;
    }

    /**
     * Gets the Country which the Player is coming from.
     * @return   Country this Player came from.
     */
    public Country getFromCountry() {
        return pos.getFrom().getCountry();
    }

    /**
     * Gets the Country which this Player currently is in.
     * If the Player is in a City, it is the Country of that City.
     * If the Player is travelling along a Road it is the Country of the from City of that Road.
     * @return   Country this Player is in.
     */
    public Country getCountry() {
        if(pos.hasArrived()) {
            return pos.getTo().getCountry();
        }
        return pos.getFrom().getCountry();
    }

    /**
     * Turns this Player around to travel in the opposite direction.
     */
    public void turnAround() {
        pos.turnAround();
    }

    /**
     * Gets the display name of this Player.
     * @return   Display name of this Player.
     */
    public abstract String getName();

    /**
     * Gets the display color of this Player.
     * @return  Display color of this Player (Color object).
     */
    public abstract Color getColor();

    /**
     * Compares this Player to another Player (sorts lexicographically based on display names).
     */
    @Override
    public int compareTo(Player p) {
        return getName().compareTo(p.getName());
    }

    @Override
    public String toString() {
        return getName()+"\t" + getMoney() + "\t" +
        getPosition().getFrom().getName() + "->" + getPosition().getTo().getName();    
    }
}
