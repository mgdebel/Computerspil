import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the various methods and variables of City
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

public class CityTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB;
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
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country2);
    }

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

    @Test
    public void arrive(){
        for(int seed = 0; seed < 1000; seed++) { // Try different seeds
            game.getRandom().setSeed(seed); // Set seed
            int bonus = country1.bonus(80); // Remember bonus
            game.getRandom().setSeed(seed); // Reset seed
            assertEquals(bonus, cityA.arrive()); // Same bonus
            assertEquals(80-bonus, cityA.getValue());
            cityA.reset();
        }
    }

    @Test
    public void changeValue() {
        int initialValue = cityA.getInitialValue();
        int currentValueB = cityB.getValue();
        cityA.changeValue(10);
        assertEquals(80 + 10, cityA.getValue());
        assertEquals(initialValue, cityA.getInitialValue());
        cityA.changeValue(20);
        assertEquals(80 + 10 + 20, cityA.getValue());
        cityB.changeValue(0);
        assertEquals(currentValueB, cityB.getValue());
        cityB.changeValue(-10);
        assertEquals(currentValueB - 10, cityB.getValue());
    }

    @Test
    public void reset(){
        cityA.changeValue(100);
        cityB.changeValue(50);
        cityA.reset();
        assertEquals(cityA.getInitialValue(),cityA.getValue());
        cityA.changeValue(50);
        assertEquals(80 + 50,cityA.getValue());
        cityA.reset();
        assertEquals(cityA.getInitialValue(),cityA.getValue());
        assertEquals(60 + 50,cityB.getValue());
    }
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