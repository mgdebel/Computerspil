import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the various methods and variables of BorderCity
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */

public class BorderCityTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB, cityC;

    /**
     * Set up the test environment
     */
    @BeforeEach
    public void setUp() {
        // Create game object
        game = new Game(0);
        // Create country
        country1 = new Country("Country 1");
        country1.setGame(game);
        country2 = new Country("Country 2");
        country2.setGame(game);
        // Create cities
        cityA = new BorderCity("City A", 80, country1);
        cityB = new City("City B", 60, country2);
        cityC = new BorderCity("City C", 60, country2);

    }

    /**
     * Test arrive method when player arrives from a different country
     */
    @Test
    public void arriveFromOtherCountry() {
        for (int seed = 0; seed < 1000; seed++) {
            Player player = new GUIPlayer(new Position(cityB, cityA, 0), 250);
            game.getRandom().setSeed(seed);
            int bonus = country1.bonus(80);
            int toll = 250 / 5;
            game.getRandom().setSeed(seed); // Assert calculation matches method call
            assertEquals(bonus - toll, cityA.arrive(player));
            assertEquals(80 - bonus + toll, cityA.getValue());
            cityA.reset();
        }
    }

    /**
     * Test arrive method when player arrives from same country
     */
    @Test
    public void arriveFromSameCountry() {
        for (int seed = 0; seed < 1000; seed++) {
            Player player = new GUIPlayer(new Position(cityB, cityC, 0), 250);
            game.getRandom().setSeed(seed);
            int bonus = country2.bonus(60);
            game.getRandom().setSeed(seed); // Assert calculation matches method call
            assertEquals(bonus, cityC.arrive(player));
            assertEquals(60 - bonus, cityC.getValue());
            cityC.reset();
        }
    }
}