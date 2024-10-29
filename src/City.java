/**
 * Represents a city with a name and a value.
 * The value can be changed and reset.
 * Cities can be compared by their names.
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */
public class City implements Comparable<City> {
    private String name;
    private int value;
    private int initialValue;
    private Country country;

    /**
     * Creates a city with a name and a value.
     * @param name: the name of the city
     * @param value: the value of the city
     */
    public City(String name, int value, Country country) {
        this.name = name;
        this.initialValue = value;
        this.value = value;
        this.country = country;
    }

    /**
     * Arrives at the city and receives a bonus based on the value of the city.
     * The value of the city is reduced by the bonus.
     * @return the bonus value
     */
    public int arrive() {
        int bonus = country.bonus(value);
        value -= bonus;
        return bonus;
    }

    public Country getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void changeValue(int amount) {
        value += amount;
    }

    public void reset() {
        value = initialValue;
    }

    @Override
    public String toString() {
        return name + " (" + value + ")";
    }

    /**
     * Compares two cities by their names.
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(City o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Compares two cities by their names.
     *
     * @param otherObject: the city to compare to
     * @return true if the cities have the same name and country, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        City otherCity = (City) otherObject;
        return name.equals(otherCity.name) && country.equals(otherCity.country);
    }

    /**
     * Generates a hash code for the city based on its name and country.
     *
     * @return the hash code of the city
     */
    @Override
    public int hashCode() {
        return 11 * name.hashCode() + 13 * country.hashCode();
    }
}
