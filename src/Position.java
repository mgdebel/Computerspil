/**
 * Position class represents a position of a player in a city.
 * It contains the city where the player is coming from, the city where the player is going to,
 * the distance between the two cities, and the total distance between the two cities.
 * The distance is the remaining distance to the destination city.
 * The total distance is the total distance between the two cities.
 * The class provides methods to move the player, turn around the player, check if the player has arrived,
 * and compare two positions.
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */
public class Position {
    private City from;
    private City to;
    private int distance;
    private int total;

    /**
     * Creates a position with a city where the player is coming from, a city where the player is going to,
     * and the distance between the two cities.
     *
     * @param from: the city where the player is coming from
     * @param to: the city where the player is going to
     * @param distance: the distance between the two cities
     */
    public Position(City from, City to, int distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.total = distance;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " : " + distance + "/" + total;
    }

    public City getFrom() {
        return from;
    }

    public City getTo() {
        return to;
    }

    public int getDistance() {
        return distance;
    }

    public int getTotal() {
        return total;
    }

    /**
     * Moves the player one step closer to the destination city.
     * @return true if the player has moved, false otherwise
     */
    public boolean move() {
        if (distance > 0) {
            distance--;
            return true;
        }
        return false;
    }

    /**
     * Turns the player around, so it is coming from the destination city and going to the starting city.
     */
    public void turnAround() {
        City temp = from;
        from = to;
        to = temp;
        distance = total - distance;
    }

    /**
     * Checks if the player has arrived at the destination city.
     * @return true if the player has arrived, false otherwise
     */
    public boolean hasArrived() {
        return distance == 0;
    }

    /**
     * Compares two positions by their from city, then by their to city, and finally by their distance.
     * @param otherObject the object to be compared
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        Position other = (Position) otherObject;
        return from.equals(other.from) && to.equals(other.to) && distance == other.distance && total == other.total;
    }

    /**
     * Returns a hash code value for the position based on the hash codes of the from city,
     * the to city, the distance, and the total distance.
     * @return a hash code value for the position
     */
    @Override
    public int hashCode() {
        return 11 * from.hashCode() + 13 * to.hashCode() + 17 * distance + 19 * total;
    }
}
