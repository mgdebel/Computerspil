import java.awt.Color;
import java.util.Queue;
import java.util.LinkedList;
/**
 * Write a description of class GUIPlayer here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GUIPlayer extends Player
{
    private Queue<City> choices;      // Citie chosen by the user (with the mouse)
    
    /**
     * Instantiates a new GUI Player with a given position and money.
     */
    public GUIPlayer(Position pos, int money){
        super(pos, money);
        this.choices = new LinkedList<>();
    }
    
    /**
     * Instantiates a new GUI Player with a given position.
     */
    public GUIPlayer(Position pos){
        super(pos);
        this.choices = new LinkedList<>();
    }
    
    @Override
    public Color getColor() {
        return Color.RED;
    }
    
    @Override
    public String getName() {
        return "GUI Player";
    }
    
    /**
     * Adds a City to a queue of pending cities (which the player will attempt to travel to after finishing the current road).
     * This makes the design more reponsive to the choices made by the user.
     * @param city   City to be added as pending.
     */
    public void enqueueCity(City city) {
        choices.add(city);
    }
    
    /**
     * Attemps to travel to the given city.
     * A player can only travel towards the specified city if there is a direct Road and the Player is not already
     * travelling (in which case the City is placed as 'pending').
     * Can also turn around the current position, if the 'from' City is parsed as argument. 
     * @param city   City to attempt to travel to.
     */
    public void travelTo(City city) {
        City playerCity = getPosition().getTo();
        if(getPosition().hasArrived()) {
            for(Road r : getCountry().getRoads(playerCity)) {
                if(r.getTo().equals(city)) {
                    setPosition(getCountry().readyToTravel(playerCity, city));
                    choices = new LinkedList<>();
                }
            }
        }
        else {
            if(city.equals(getPosition().getFrom())) {
                turnAround();
            } 
            else {
                choices.add(city);
            }
        }
    }
    
    @Override
    public boolean step(){
        if(super.step()){
            if(!choices.isEmpty()) {
                travelTo(choices.remove());
            }
            return true;
        }
        return false;
    }
}
