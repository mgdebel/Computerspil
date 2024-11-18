import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of Country
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
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
        country1.addCity(cityA);
        assertEquals(1, country1.getCities().size());
        assertEquals(cityA, country1.getCity("City A"));
        country1.addCity(cityB);
        assertEquals(2, country1.getCities().size());
        assertEquals(cityB, country1.getCity("City B"));
        assertEquals(0, country2.getCities().size());
        assertNotEquals(cityA, country2.getCity("City A"));
        assertNull(country2.getCity("City A"));
    }

    @Test
    public void position() {
        country1.addCity(cityA);
        country1.addCity(cityB);
        country1.addCity(cityC);
        Position positionA = country1.position(cityA);
        assertEquals(0,positionA.getDistance());
        assertEquals(0,positionA.getTotal());
        positionA.move();
        assertEquals(0,positionA.getDistance());
        assertEquals(0,positionA.getTotal());
        positionA.turnAround();
        assertEquals(0,positionA.getDistance());
        assertEquals(0,positionA.getTotal());
        Position positionB = country2.position(cityB);
    }

    @Test
    public void readyToTravel() {
        country1.addCity(cityA);
        country1.addCity(cityB);
        country2.addCity(cityC);
        country1.addRoads(cityA,cityB,5);
        Position position1 = country1.readyToTravel(cityC,cityA);
        Position position2 = country1.readyToTravel(cityA,cityA);
        Position position3 = country1.readyToTravel(cityA,cityB);
        assertEquals(0,position1.getDistance());
        assertEquals(0,position2.getDistance());
        assertEquals(5,position3.getDistance());
        assertEquals(cityC,position1.getFrom());
        assertEquals(cityC,position1.getTo());
        assertEquals(cityA,position2.getFrom());
        assertEquals(cityA,position2.getTo());
        assertEquals(cityA,position3.getFrom());
        assertEquals(cityB,position3.getTo());
        position3.turnAround();
        assertEquals(0,position3.getDistance());
    }

    @Test
    public void addRoads() {
        assertEquals(0,country1.getRoads(cityA).size());
        country1.addCity(cityA);
        country1.addCity(cityB);
        country1.addCity(cityC);
        City cityD = new City("City D", 100, country2);
        country2.addCity(cityD);
        country1.addRoads(cityA,cityB,4);
        country1.addRoads(cityA,cityB,-1);
        country1.addRoads(cityA,cityD,5);
        assertEquals(0,country1.getRoads(cityC).size());
        assertEquals(0,country2.getRoads(cityD).size());
        assertEquals(2,country1.getRoads(cityA).size());
        country1.addRoads(cityA,cityC,1);
        assertEquals(3,country1.getRoads(cityA).size());
        assertEquals(1,country1.getRoads(cityC).size());
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
        assertEquals("Country 1",country1.toString());
        assertEquals("Country 2",country2.toString());
    }
}