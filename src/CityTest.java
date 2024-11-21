import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the various methods and variables of City
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */

public class CityTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB;

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
        country2 = new MafiaCountry("Country 2");
        country2.setGame(game);
        // Create cities
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country2);
    }

    /**
     * Test the constructor
     */
    @Test
    public void constructor() {
        assertEquals("City A", cityA.getName());
        assertEquals(80, cityA.getValue());
        assertEquals(80, cityA.getInitialValue());
        assertEquals(country1, cityA.getCountry());
        assertEquals("City B",cityB.getName());
        assertEquals(60, cityB.getValue());
        assertEquals(60, cityB.getInitialValue());
        assertEquals(country2, cityB.getCountry());
    }

    /**
     * Test the bonus method
     */
    @Test
    public void arrive(){
        for(int seed = 0; seed < 1000; seed++) { // Try different seeds
            game.getRandom().setSeed(seed); // Set seed
            int bonus = country1.bonus(80); // Remember bonus
            assertTrue(bonus >= 0); // Bonus is non-negative
            game.getRandom().setSeed(seed); // Reset seed
            assertEquals(bonus, cityA.arrive()); // Same bonus
            assertEquals(80-bonus, cityA.getValue()); // Value is reduced by bonus
            cityA.reset();

            game.getRandom().setSeed(seed);
            bonus = country2.bonus(60);
            game.getRandom().setSeed(seed);
            assertEquals(bonus, cityB.arrive());
            assertEquals(bonus > 0 ? 60 - bonus : 60, cityB.getValue()); // Only reduce value if bonus is positive
            cityB.reset();
        }

        cityA.changeValue(-80);
        assertEquals(0, cityA.arrive()); // Value returned is 0
        assertEquals(0, cityA.getValue()); // Value of city is 0
    }

    /**
     * Test the changeValue method
     */
    @Test
    public void changeValue() {
        int initialValue = cityA.getInitialValue();
        int currentValueB = cityB.getValue();

        cityA.changeValue(10);
        assertEquals(80 + 10, cityA.getValue());
        assertEquals(initialValue, cityA.getInitialValue());
        cityA.changeValue(20);
        assertEquals(80 + 10 + 20, cityA.getValue()); // Positive change works

        cityB.changeValue(0);
        assertEquals(currentValueB, cityB.getValue()); // Zero change works

        cityB.changeValue(-10);
        assertEquals(currentValueB - 10, cityB.getValue()); // Negative change works
    }

    /**
     * Test the reset method
     */
    @Test
    public void reset(){
        cityB.changeValue(50);

        cityA.changeValue(50);
        assertEquals(80 + 50, cityA.getValue()); // City A is changed
        cityA.reset();
        assertEquals(cityA.getInitialValue(), cityA.getValue()); // City A is reset

        assertEquals(60 + 50, cityB.getValue()); // City B is not reset
    }

    /**
     * Test the toString method
     */
    @Test
    public void testToString() {
        assertEquals("City A (80)", cityA.toString());
        assertEquals("City B (60)", cityB.toString());
        cityA.changeValue(50);
        assertEquals("City A (130)", cityA.toString());
        cityA.reset();
        assertEquals("City A (80)", cityA.toString());
    }
}