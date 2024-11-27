import java.util.*;

/**
 * A country with a network of cities and roads.
 * The network is represented as a map from cities to sets of roads.
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */
public class Country {
    private String name;
    private Map<City, Set<Road>> network;
    private Game game;

    /**
     * Creates a country with a name.
     *
     * @param name: the name of the country
     */
    public Country(String name) {
        this.name = name;
        this.network = new TreeMap<>();
    }

    /**
     * Returns the name of the country.
     *
     * @return the name of the country
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Adds a city to the country.
     *
     * @param c the city to add
     */
    public void addCity(City c) {
        network.put(c, new TreeSet<>());
    }

    /**
     * Creates a position where the player is at a city.
     *
     * @param city the city where the player is
     * @return a position where the player is at the city
     */
    public Position position(City city) {
        return new Position(city, city, 0);
    }

    /**
     * Creates a position where the player is ready to travel from one city to another.
     * If the cities are the same, the player is already at the destination.
     * If there is no road between the cities, the from position is returned.
     *
     * @param from the city where the player is coming from
     * @param to   the city where the player is going to
     * @return a position where the player is ready to travel
     */
    public Position readyToTravel(City from, City to) {
        if (from.equals(to)) {
            return position(from);
        }
        Road road = getRoads(from).stream().filter(r -> r.getTo().equals(to)).findFirst().orElse(null);
        if (road == null) {
            return position(from);
        }
        return new Position(from, to, road.getLength());
    }

    /**
     * Adds roads between two cities with a given length.
     *
     * @param a      the first city
     * @param b      the second city
     * @param length the length of the road
     */
    public void addRoads(City a, City b, int length) {
        if (length <= 0) {
            return;
        }
        if (a.equals(b)) {
            return;
        }
        if (network.containsKey(a)) {
            network.get(a).add(new Road(a, b, length));
        }
        if (network.containsKey(b)) {
            network.get(b).add(new Road(b, a, length));
        }
    }

    /**
     * Calculates a bonus value based on a given value.
     *
     * @param value the maximum value of the bonus
     * @return a random bonus value between 0 and the given value
     */
    public int bonus(int value) {
        if (value <= 0) {
            return 0;
        }
        return game.getRandom().nextInt(value + 1);
    }

    /**
     * Returns the game the country is in.
     *
     * @return the game the country is in
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game the country is in.
     *
     * @param game the game the country is in
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Returns the name of the country.
     *
     * @return the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all cities in the country.
     *
     * @return a set of all cities in the country
     */
    public Set<City> getCities() {
        return network.keySet();
    }

    /**
     * Gets a city by its name.
     *
     * @param name: the name of the city
     * @return the city with the given name, or null if no such city exists
     */
    public City getCity(String name) {
        for (City city : network.keySet()) {
            if (city.getName().equals(name)) {
                return city;
            }
        }
        return null;
    }

    /**
     * Resets all cities in the country.
     */
    public void reset() {
        for (City city : network.keySet()) {
            city.reset();
        }
    }

    /**
     * Gets all roads connected to a city.
     *
     * @param city the city to get roads from
     * @return a set of roads connected to the city
     */
    public Set<Road> getRoads(City city) {
        return network.getOrDefault(city, new TreeSet<>());
    }

    /**
     * Checks if two countries are equal.
     *
     * @param otherObject the object to compare to
     * @return true if the countries are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;
        Country other = (Country) otherObject;
        return name.equals(other.name);
    }

    /**
     * Generates a hash code for a country.
     *
     * @return a hash code for the country
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
