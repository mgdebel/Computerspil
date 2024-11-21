import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of Country
 *
 * @author 202406714 Magnus Debel-Hansen and 20240543 Alexander Bak
 */

public class CountryTest {
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
        cityB = new City("City B", 60, country1);
        cityC = new City("City C", 40, country2);

        country1.addCity(cityA);
        country1.addCity(cityB);
        country2.addCity(cityC);
    }

    @Test
    public void constructor() {
        assertEquals("Country 1", country1.getName());
        assertEquals(2, country1.getCities().size());
        assertEquals("Country 2", country2.getName());
        assertEquals(1, country2.getCities().size());
    }

    @Test
    public void getCities() {

        assertEquals(2, country1.getCities().size());
        assertEquals(1, country2.getCities().size());
    }

    @Test
    public void getCity() {
        assertEquals(cityA, country1.getCity("City A"));
        assertEquals(cityB, country1.getCity("City B"));
        assertNull(country2.getCity("City A"));
        country2.addCity(cityA);
        assertEquals(cityA, country2.getCity("City A"));
    }

    @Test
    public void getRoads() {
        // Test that cityA has no roads initially
        assertEquals(0, country1.getRoads(cityA).size());

        // Test that cityA and cityB contains the correct amount of roads after adding roads
        country1.addRoads(cityA, cityB,5);
        assertEquals(1, country1.getRoads(cityA).size());
        assertEquals(1, country1.getRoads(cityB).size());
        country1.addRoads(cityC, cityA,5);
        assertEquals(2, country1.getRoads(cityA).size());
        country1.addRoads(cityC, cityB,2);
        assertEquals(2, country1.getRoads(cityA).size());
        assertEquals(2, country1.getRoads(cityB).size());

        // Test that cityC has no roads as it is in a different country
        assertEquals(0, country1.getRoads(cityC).size());
    }

    @Test
    public void addCity() {
        // addCity is called twice on country1 in setUp
        assertEquals(2, country1.getCities().size());
        assertEquals(cityA, country1.getCity("City A"));
        assertEquals(cityB, country1.getCity("City B"));

        // addCity is called once on country2 in setUp
        assertEquals(1, country2.getCities().size());
        assertNotEquals(cityA, country2.getCity("City A"));
        assertNull(country2.getCity("City A"));

        // Test that a new country has no cities
        Country country3 = new Country("Country 3");
        assertEquals(0, country3.getCities().size());
    }

    @Test
    public void position() {
        // Test that the method returns a position object with the correct values
        Position positionA = country1.position(cityA);
        assertEquals(0, positionA.getDistance());
        assertEquals(0, positionA.getTotal());
        positionA.move();
        assertEquals(0, positionA.getDistance());
        assertEquals(0, positionA.getTotal());
        positionA.turnAround();
        assertEquals(0, positionA.getDistance());
        assertEquals(0, positionA.getTotal());
    }

    @Test
    public void readyToTravel() {
        // Test when the two cities are connected
        country1.addRoads(cityA,cityB,5);
        assertEquals(new Position(cityA, cityB, 5), country1.readyToTravel(cityA, cityB));

        // Test when the two cities are not connected
        assertEquals(new Position(cityA, cityA, 0), country1.readyToTravel(cityA, cityC));

        // Test when the two cities are the same
        assertEquals(new Position(cityA, cityA, 0), country1.readyToTravel(cityA, cityA));

    }

    @Test
    public void addRoads() {
        // Test that the method does not add a road if from and to are the same
        country1.addRoads(cityA, cityA, 10);
        assertEquals(0, country1.getRoads(cityA).size());

        // Test that the method does not add a road if length is less than or equal to 0
        country1.addRoads(cityA, cityB, 0);
        country1.addRoads(cityA, cityB, -10);
        assertEquals(0, country1.getRoads(cityA).size());

        // Test that the method adds a road between two cities in the same country
        country1.addRoads(cityA, cityB, 1);
        assertEquals(1, country1.getRoads(cityA).size());
        assertEquals(1, country1.getRoads(cityB).size());

        // Test that the method only adds a road to a city if the city is in the country
        country1.addRoads(cityB, cityC, 10);
        assertEquals(1 + 1, country1.getRoads(cityB).size());
        assertEquals(0, country2.getRoads(cityC).size());

        country2.addRoads(cityA, cityB, 10);
        assertEquals(1, country1.getRoads(cityA).size());
    }

    @Test
    public void setGame() {
        Country country3 = new Country("Country 3");
        Game game2 = new Game(1);
        assertNull(country3.getGame());
        country3.setGame(game2);
        assertEquals(game2,country3.getGame());
        assertEquals(game,country1.getGame());
    }
    @Test
    public void reset() {
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
        assertEquals(0,country1.bonus(0) );
        assertEquals(0,country1.bonus(-1) );
        for(int seed = 0; seed < 100; seed++) {
            game.getRandom().setSeed(seed);
            ArrayList<Integer> bonuses = new ArrayList<Integer>();
            int sum = 0;
            for(int i = 0; i < 100000; i++) {
                int bonus = country1.bonus(80);
                bonuses.add(bonus);
                sum += bonus;

                //Test the value is in the correct interval
                assertTrue(bonus >= 0);
                assertTrue(bonus <= 80);

            }
            for(int i = 0; i < 100000; i++) {
                int bonus = country1.bonus(1);

                //Test the value is in the correct interval
                assertTrue(bonus == 0 || bonus == 1);
            }
            //Test the average value is in the correct interval
            assertTrue(((double) sum) / ((double)100000) >= 35);
            assertTrue(((double) sum) / ((double)100000) <= 45);

            //Test that all possible values are generated
            for (int i = 0; i <= 80; i++){
                assertTrue(bonuses.contains(i));
            }
        }
    }

    @Test
    public void testToString() {
        assertEquals("Country 1", country1.toString());
        assertEquals("Country 2", country2.toString());
    }
}