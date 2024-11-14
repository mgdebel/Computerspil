import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of Country
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

class CountryTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB, cityC;
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
        cityC = new City("City C", 40, country1);
    }

    @Test
    public void constructor() {
        assertEquals("Country 1", country1.getName());
        assertEquals(0, country1.getCities().size());
        assertEquals("Country 2", country2.getName());
        assertEquals(0, country2.getCities().size());
    }

    @Test
    public void getCities() {
        country1.addCity(cityA);
        country1.addCity(cityB);
        country2.addCity(cityB);
        assertEquals(2, country1.getCities().size());
        assertEquals(1, country2.getCities().size());
    }

    @Test
    public void getCity() {
        country1.addCity(cityA);
        country1.addCity(cityB);
        country2.addCity(cityB);
        assertEquals(cityA, country1.getCity("City A"));
        assertEquals(cityB, country1.getCity("City B"));
        assertNull(country2.getCity("City A"));
        country2.addCity(cityA);
        assertEquals(cityA, country2.getCity("City A"));
    }

    @Test
    public void getRoads() {
        assertEquals(0,country1.getRoads(cityA).size());
        country1.addCity(cityA);
        country1.addCity(cityB);
        country1.addCity(cityC);
        country1.addRoads(cityA,cityB,5);
        assertEquals(1,country1.getRoads(cityA).size());
        country1.addRoads(cityC,cityA,5);
        assertEquals(2,country1.getRoads(cityA).size());
        country1.addRoads(cityC,cityB,2);
        assertEquals(2,country1.getRoads(cityA).size());
        assertEquals(2,country1.getRoads(cityB).size());
        assertEquals(2,country1.getRoads(cityC).size());
    }

    @Test
    public void addCity() {
    }

    @Test
    public void position() {
    }

    @Test
    public void readyToTravel() {
    }

    @Test
    public void addRoads() {
    }

    @Test
    public void setGame() {
    }
    @Test
    public void reset() {
        country1.addCity(cityA);
        country1.addCity(cityB);
        country2.addCity(cityC);
        cityA.arrive(); cityA.arrive(); cityA.arrive();
        cityC.arrive(); cityC.arrive(); cityC.arrive();
        int valueE = cityC.getValue(); // Remember value of cityE
        country1.reset();
        assertEquals(80, cityA.getValue()); // cityA is reset
        assertEquals(60, cityB.getValue()); // cityB is reset
        assertEquals(valueE, cityC.getValue()); // cityE is unchanged
    }

    @Test
    public void bonus() {
        for(int seed = 0; seed < 100; seed++) { // Try 100 different seeds
            game.getRandom().setSeed(seed);
            //...
            for(int i = 0; i < 100000; i++) { // Call method 100.000 times
                int bonus = country1.bonus(80);
                //Test at værdien ligger i det korrekte interval
                //...
            }
            //Test at middelværdien er tæt på det forventede
            //Test at alle de mulige værdier returneres
        }
    }

    @Test
    public void testToString() {
        assertEquals("",country1.toString());
    }
}