/**
 * Road class represents a road between two cities.
 * It has two fields: from and to, which are the cities at the ends of the road,
 * and length, which is the length of the road.
 * The class implements Comparable interface, so that roads can be compared.
 * The comparison is done first by the from city, then by the to city, and finally by the length.
 * The class also overrides equals and hashCode methods.
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */
public class Road implements Comparable<Road> {
    private City from;
    private City to;
    private int length;

    /**
     * Creates a road between two cities with a given length.
     * @param from: the city at the start of the road
     * @param to: the city at the end of the road
     * @param length: the length of the road
     */
    public Road(City from, City to, int length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " : " + length;
    }

    public City getFrom() {
        return from;
    }

    public City getTo() {
        return to;
    }

    public int getLength() {
        return length;
    }

    /**
     * Compares two roads by their from city, then by their to city, and finally by their length.
     * @param o: the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Road o) {
        if (from.equals(o.from)) {
            if (to.equals(o.to)) {
                return length - o.length;
            }
            return to.compareTo(o.to);
        }
        return from.compareTo(o.from);
    }

    /**
     * Compares two roads by their from city, then by their to city, and finally by their length.
     * @param otherObject: the object to be compared.
     * @return true if the roads are equal, false otherwise.
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
        Road other = (Road) otherObject;
        return from.equals(other.from) && to.equals(other.to) && length == other.length;
    }

    /**
     * Returns a hash code value for the road based on the hash codes of the from city, the to city, and the length.
     * @return a hash code value for the road.
     */
    @Override
    public int hashCode() {
        return 11 * from.hashCode() + 13 * to.hashCode() + 17 * length;
    }
}
