/**
 * A CapitalCity is a BorderCity with the added functionality of taking money spent by the player when they arrive.
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */
public class CapitalCity extends BorderCity {

    public CapitalCity(String name, int value, Country country) {
        super(name, value, country);
    }

    /**
     * Arrives at the capital city and receives a bonus based on the value of the city.
     * The player also pays a toll to the country.
     * The player also spends a random amount of money.
     * @param p: the player arriving at the city
     * @return the sum of toll and money spent subtracted from the bonus value
     */
    @Override
    public int arrive(Player p) {
        int bonus = super.arrive(p);

        int spent = getCountry().getGame().getRandom().nextInt(p.getMoney() + bonus + 1);
        changeValue(spent);

        return bonus - spent;
    }
}
