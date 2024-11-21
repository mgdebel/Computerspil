/**
 * A class representing a country in the game that has a risk of being robbed.
 * If the player is robbed, the player loses money instead of gaining money.
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */
public class MafiaCountry extends Country {

    public MafiaCountry(String name) {
        super(name);
    }

    /**
     * Returns a bonus value based on the risk of the country.
     * If the random number is less than the risk, the player is robbed.
     * Otherwise, the player receives a bonus based on the value of the city.
     * @param value the value to calculate the bonus from
     * @return the bonus value
     */
    @Override
    public int bonus(int value) {
        if (getGame().getRandom().nextInt(100) < getGame().getSettings().getRisk()) {
            return -getGame().getLoss();
        }
        return super.bonus(value);
    }
}
