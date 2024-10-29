import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;
/**
 * Models a smart player which uses a depth-first search to determine the optimal path.
 * @author Nikolaj Ignatieff Schwartzbach. 
 * @version August 2019.
 */
public class SmartPlayer extends Player {

    /**
     * Creates a new SmartPlayer with the specified position.
     * @param pos   Position of this player.
     */
    public SmartPlayer(Position pos) {
        super(pos);
    }

    @Override
    public boolean step() {
        if(super.step()) {
            City city = getPosition().getTo();
            setPosition(getCountry().readyToTravel(city, maximizeValue(city, getCountry().getGame().getStepsLeft())));
            return true;
        } 
        return false;
    }

    /**
     * Determine the next city to travel to.
     * Calculates the 'maxDepth'-steps optimal route (DFS) and returns
     * the next city of the path.
     * @param c The current city.
     * @param n The number of steps to look forward (is capped at 'maxDepth').
     */
    private City maximizeValue(City c, int n) {
        HashMap<City, Integer> visited = new HashMap<City, Integer>();
        visited.put(c, 1);
        PlayerPath best = maximizeValue(visited, c, n);
        if(best.isEmpty()) {
            return c;
        }
        return best.getRoad().getTo();
    }

    /**
     * Determine the next city to travel to.
     * Calculates the 'maxDepth'-steps optimal route (DFS) and returns
     * the next city of the path.
     * @param visits    The number of times each city has been visited.
     * @param c         The current city.
     * @param i         The number of steps to look forward (is capped at 'maxDepth').
     */
    private PlayerPath maximizeValue(HashMap<City, Integer> visits, City c, int i) {
        /* Max number of steps to look forward (determined experimentally).
           If you have a fast computer, you can increase this number.*/
        int maxDepth = 200;
        
        // Number of steps to look forward
        int n = Math.min(maxDepth, i);
        
        // Initialize best path.
        PlayerPath p = new PlayerPath(this);
        
        // Consider each road from current city.
        for(Road r : c.getCountry().getRoads(c)) {
            
            // Only consider roads we can reach in due time.
            if(r.getLength() <= n) {
                
                // Prepare recursive call. Update 'visits'.
                HashMap<City, Integer> newVisits = new HashMap<City, Integer>(visits);
                City to = r.getTo();
                int v = 0;
                if(newVisits.containsKey(to)) {
                    v = newVisits.get(to);
                }
                newVisits.put(to, ++v);

                // Call procedure recursively.
                PlayerPath subPlayerPath = maximizeValue(newVisits, to, n - r.getLength());
                subPlayerPath.addRoad(r, v);
                double newValue = subPlayerPath.getValue();
                
                // If the new path is better, substitute it.
                if(subPlayerPath.compareTo(p) == 1) {
                    p = subPlayerPath;
                }
            }
        }

        // Return the best path.
        return p;
    }

    @Override
    public String getName() {
        return "Smart Player";
    }

    @Override
    public Color getColor() {
        return new Color(255,225,33);
    }
}

/**
 * Models a possible path for the smart player.
 * Is used by the searching algorithm for SmartPlayer to model the paths it can take.
 * @author Nikolaj I. Schwartzbach
 * @version August 2019
 */
class PlayerPath {

    private Stack<Road> edges;              // Ordered list of roads to choose.
    private int length;                     // The length of the path (in steps).
    private double value;                   // The expected difference in player value after path.
    private double impulsiveness = 1.10;    // Propensity to prefer shorter paths (i = 1.0 corresponds to no preference in length).
    private SmartPlayer source;             // Reference to the player object.

    /**
     * Initialize a new path with given player.
     * @param source    Reference to the smart player.
     */
    public PlayerPath(SmartPlayer source) {
        this.source = source;
        edges = new Stack<Road>();
        length = 0;
        value = 0;
    }

    public int compareTo(PlayerPath p) {
        if(value > p.value) { return 1; }
        if(value < p.value) { return -1; }
        if(length < p.length) { return 1; }
        if(length > p.length) { return -1; }
        return getRoad().compareTo(p.edges.peek());
    }

    /**
     * Return the first road in this path.
     * @return The first road in this path.
     */
    public Road getRoad() {
        return edges.peek();
    }

    /**
     * Return the length of this path (in steps).
     * @return The number of steps to walk this path.
     */
    public int getLength() {
        return length;
    }

    /**
     * Return the expected difference in player value after path.
     * @return Expected difference in player value after path.
     */
    public double getValue() {
        return value;
    }

    /**
     * Determines if this path is empty or not.
     * @return True, if this path is empty; false otherwise.
     */
    public boolean isEmpty() {
        return edges.isEmpty();
    }

    @Override
    public String toString() {
        if(edges.isEmpty()){ return "[]"; }
        StringBuilder sb = new StringBuilder("[");
        for(Road r : edges) {
            sb.append(r + ", ");
        }
        String s = sb.toString();
        return s.substring(0,s.length()-2)+"]";
    }

    /**
     * Adds a new road to the path in question.
     * @param r The road object to add.
     * @param penalty The accumulated penalty (see valueFrom).
     */
    public void addRoad(Road r, int penalty) {
        if(!edges.isEmpty()) {
            Road top = getRoad();
            if(!top.getFrom().equals(r.getTo())) {
                throw new RuntimeException("Invalid road. You tried to add road to "+r.getTo()+", but the next city is "+top.getFrom());
            }
        }
        else {
            value += valueFrom(r, penalty);
        }
        edges.add(r);
        length += r.getLength();
        value += valueTo(r, penalty);
    }

    /**
     * Computes the value of the current path after inserting the possible road 'r', being in position 'from'.
     * longer paths are penalized by O(impulsiveness^length).
     * 
     * Takes into account CapitalCity and MafiaCountry implementations using the
     * expectation of each random variable.
     * 
     * @param r The possible road to add.
     * @param penalty The length of the path in question; 
     * @return The expected value of the new path in question.
     */
    public double valueFrom(Road r, int penalty) {
        Settings s = r.getFrom().getCountry().getGame().getSettings();
        double v = r.getFrom().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));
        return v;
    }

    /**
     * Computes the value of the current path after inserting the possible road 'r', being in position 'to'.
     * longer paths are penalized by O(impulsiveness^length).
     * 
     * Takes into account BorderCity, CapitalCity and MafiaCountry implementations using the
     * expectation of each random variable.
     * 
     * @param r The possible road to add.
     * @param penalty The length of the path in question; 
     * @return The expected value of the new path in question.
     */
    public double valueTo(Road r, int penalty) {
        Settings s = r.getFrom().getCountry().getGame().getSettings();
        double v = r.getTo().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));
        return v;
    }
}