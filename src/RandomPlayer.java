import java.awt.Color;
import java.util.List;
import java.util.Set;
/**
 * Models a player which randomly chooses the next city to travel to.
 * @author Nikolaj Ignatieff Schwartzbach. 
 * @version August 2019.
 */
public class RandomPlayer extends Player {

    /**
     * Creates a new RandomPlayer with the specified position.
     * @param pos   Position of this player.
     */
    public RandomPlayer(Position pos) {
        super(pos);
    }

    @Override
    public boolean step() {
        if(super.step()) {
            City city = getPosition().getTo();
            Set<Road> roads = getCountry().getRoads(city);
            roads.stream()
                 .skip(getCountry().getGame().getRandom().nextInt(roads.size()))
                 .limit(1)
                 .map(r -> getCountry().readyToTravel(city,r.getTo()))
                 .forEach(this::setPosition);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Random Player";
    }
    
    @Override
    public Color getColor() {
        return new Color(187,183,128);
    }
}