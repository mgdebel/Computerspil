import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of MafiaCountry
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */

public class MafiaCountryTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB, cityC;

    @BeforeEach
    public void setUp() {
        // Create game object
        game = new Game(0);
        // Create country
        country1 = new MafiaCountry("Country 1");
        country1.setGame(game);
        country2 = new Country("Country 2");
        country2.setGame(game);
        // Create cities
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country1);
        cityC = new City("City C", 40, country2);
    }

    /**
     * Tests the bonus method of MafiaCountry.
     */
    @Test
    public void bonus() {
        for(int seed = 0; seed < 1000; seed++) {
            game.getRandom().setSeed(seed);
            Set<Integer> bonuses = new HashSet<>();
            Set<Integer> robValues = new HashSet<>();
            int sum = 0;
            int robSum = 0;
            int robs = 0;

            for(int i = 0; i < 100000; i++) {
                int bonus = country1.bonus(80);
                if (bonus >= 0) {
                    bonuses.add(bonus);
                    sum += bonus;
                    // Bonus lies in a valid range
                    assertTrue(bonus <= 80);
                } else {
                    robs++;
                    robValues.add(-bonus);
                    robSum -= bonus;

                    // Rob value lies in a valid range
                    assertTrue(bonus <= -10);
                    assertTrue(bonus >= -50);
                }
            }
            // Average bonus lies in a valid range
            assertTrue(sum / 80000 >= 39);
            assertTrue(sum / 80000 <= 41);

            // All bonus values are hit
            for (int i = 0; i <= 80; i++){
                assertTrue(bonuses.contains(i));
            }

            // Average number of robs lies in a valid range
            assertTrue(robs > 19000);
            assertTrue(robs < 21000);

            // Average rob value lies in a valid range
            assertTrue(robSum / robs >= 29);
            assertTrue(robSum / robs <= 31);

            // All rob values are hit
            for (int i = 10; i <= 50; i++){
                assertTrue(robValues.contains(i));
            }
        }
    }
}