import java.awt.Color;
import java.util.*;

/**
 * Models a player which greedily chooses the next city to travel to (looking only one city ahead).
 * @author Nikolaj Ignatieff Schwartzbach. 
 * @version August 2019.
 */
public class GreedyPlayer extends Player {

    /**
     * Creates a new GreedyPlayer with the specified position.
     * @param pos   Position of this player.
     */
    public GreedyPlayer(Position pos) {
        super(pos);
    }

    @Override
    public boolean step() {
        if(super.step()) {
            City city = getPosition().getTo();
            List<Road> roads = new ArrayList<>(getCountry().getRoads(city));
            double best = 0;
            City bestCity = null;
            for(Road road : roads) {
                double value = road.getTo().getValue() / (double) road.getLength();
                if(value > best) {
                    bestCity = road.getTo();
                    best = value;
                }
            }
            if(bestCity != null)
                setPosition(getCountry().readyToTravel(city, bestCity));
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Greedy Player";
    }

    @Override
    public Color getColor() {
        return new Color(178,0,255);
    }
}