/**
 * A border city is a city that is on the border of a country.
 * When a player arrives at a border city, they receive a bonus based on the value of the city.
 * The player also pays a toll to the country.
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */
public class BorderCity extends City {

    public BorderCity(String name, int value, Country country) {
        super(name, value, country);
    }

    /**
     * Arrives at the border city and receives a bonus based on the value of the city.
     * The value of the city is reduced by the bonus.
     * The player also pays a toll to the country.
     * @param p: the player arriving at the city
     * @return the bonus value subtracted by the toll value
     */
    @Override
    public int arrive(Player p) {
        int bonus = arrive();
        int toll = 0;
        if (!p.getFromCountry().equals(getCountry())) {
            toll = p.getMoney() * p.getCountry().getGame().getSettings().getTollToBePaid() / 100;
            changeValue(toll);
        }

        return bonus - toll;
    }
}
