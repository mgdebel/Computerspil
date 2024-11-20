import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of Country
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

public class CountryTest {
    private Game game;
    private Country country1, country2;
    private City cityA, cityB, cityC, cityE;
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
        cityC = new City("City C", 40, country1);
        cityE = new City("City E", 50, country2);
        country1.addCity(cityA);
        country1.addCity(cityB);
        country1.addCity(cityC);
        country2.addCity(cityE);
    }

    @Test
    public void constructor() {
        Country country5 = new Country("Country 5");
        assertEquals("Country 5", country5.getName());
        assertEquals(0, country5.getCities().size());
        assertEquals("Country 2", country2.getName());
        assertEquals(1, country2.getCities().size());
    }

    @Test
    public void getCities() {
        assertEquals(3, country1.getCities().size());
        City cityD = new City("City D", 40, country1);
        country1.addCity(cityD);
        assertEquals(4, country1.getCities().size());
        assertEquals(1, country2.getCities().size());
    }

    @Test
    public void getCity() {
        assertEquals(cityA, country1.getCity("City A"));
        assertEquals(cityB, country1.getCity("City B"));
        assertNull(country2.getCity("City A"));
        assertEquals(cityE, country2.getCity("City E"));
    }

    @Test
    public void getRoads() {
        assertEquals(0,country1.getRoads(cityA).size());
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
        City cityD = new City("City D", 40, country1);
        assertEquals(3, country1.getCities().size());
        assertEquals(cityA, country1.getCity("City A"));
        country1.addCity(cityD);
        assertEquals(4, country1.getCities().size());
        assertEquals(cityD, country1.getCity("City D"));
        assertEquals(1, country2.getCities().size());
        assertNotEquals(cityA, country2.getCity("City A"));
        assertNull(country2.getCity("City A"));
    }

    @Test
    public void position() {
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
        country1.addRoads(cityA,cityB,5);
        //Add various start positions
        Position position1 = country1.readyToTravel(cityC,cityA);
        Position position2 = country1.readyToTravel(cityA,cityA);
        Position position3 = country1.readyToTravel(cityA,cityB);
        //Checks that their distances are 0 if there are no roads
        assertEquals(0,position1.getDistance());
        assertEquals(0,position2.getDistance());
        // Check that the readyToTravel returns the same as a position between the 2 cities when there is a road
        Position expectedPosition3 = new Position(cityA,cityB,5);
        assertEquals(expectedPosition3,position3);
        //Without a road check that the readyToTravel sets from and to the from city
        assertEquals(cityC,position1.getFrom());
        assertEquals(cityC,position1.getTo());
        assertEquals(cityA,position2.getFrom());
        assertEquals(cityA,position2.getTo());
        //Checks when there is a road that the from and to correlate to the from and to between cityA and cityB
        assertEquals(cityA,position3.getFrom());
        assertEquals(cityB,position3.getTo());
        //Checks that when there is a road it actually is at the starting position and not in the middle of the road
        position3.turnAround();
        assertEquals(0,position3.getDistance());
        //Checks if readyToTravel starts in country1 and ends in country2
        country1.addRoads(cityA,cityE,4);
        Position position4 = country1.readyToTravel(cityA,cityE);
        assertEquals(4,position4.getDistance());
    }

    @Test
    public void addRoads() {
        City cityD = new City("City D",30,country2);
        country2.addCity(cityD);
        //Adding to same city
        country1.addRoads(cityA, cityA, 10);
        assertEquals(0, country1.getRoads(cityA).size());
        //Roads with 0 length
        country1.addRoads(cityA, cityB, 0);
        assertEquals(0, country1.getRoads(cityA).size());
        //Between cities in same country
        country1.addRoads(cityA, cityB, 1);
        assertEquals(1, country1.getRoads(cityA).size());
        assertEquals(1, country1.getRoads(cityB).size());
        assertEquals(0, country2.getRoads(cityB).size());
        //Between cities in other country
        country1.addRoads(cityD, cityE, 10);
        assertEquals(0, country1.getRoads(cityD).size());
        assertEquals(0, country1.getRoads(cityE).size());
        //Between cities in same country
        country1.addRoads(cityA, cityB, 10);
        assertEquals(2, country1.getRoads(cityA).size());
        assertEquals(2, country1.getRoads(cityB).size());
        //Between city in 2 countries starting in country1
        country1.addRoads(cityA, cityD, 10);
        assertEquals(3, country1.getRoads(cityA).size());
        //Between city in 2 countries ending in country1
        country1.addRoads(cityD, cityA, 10);
        assertEquals(0, country1.getRoads(cityD).size());
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
        cityE.arrive(); cityE.arrive(); cityE.arrive();
        int valueE = cityE.getValue(); // Remember value of cityE
        country1.reset();
        assertEquals(80, cityA.getValue()); // cityA is reset
        assertEquals(60, cityB.getValue()); // cityB is reset
        assertEquals(valueE, cityE.getValue()); // cityE is unchanged
    }

    @Test
    public void bonus() {
        assertEquals(0,country1.bonus(0) );
        assertEquals(0,country1.bonus(-1) );
        for(int seed = 0; seed < 100; seed++) { // Try 100 different seeds
            game.getRandom().setSeed(seed);
            ArrayList<Integer> bonuses = new ArrayList<Integer>();
            int sum = 0;
            for(int i = 0; i < 100000; i++) { // Call method 100.000 times
                int bonus = country1.bonus(80);
                bonuses.add(bonus);
                sum += bonus;
                //Test at værdien ligger i det korrekte interval
                assertTrue(bonus >= 0);
                assertTrue(bonus <= 80);

            }
            for(int i = 0; i < 100000; i++) { // Call method 100.000 times
                int bonus = country1.bonus(1);
                //Test at værdien ligger i det korrekte interval
                assertTrue(bonus >= 0);
                assertTrue(bonus <= 1);

            }
            //Test at middelværdien er tæt på det forventede
            assertTrue(((double) sum) / ((double)100000) >= 35);
            assertTrue(((double) sum) / ((double)100000) <= 45);
            //Test at alle de mulige værdier returneres
            for (int i = 0; i <= 80; i++){
                assertTrue(bonuses.contains(i));
            }
        }
    }

    @Test
    public void testToString() {
        assertEquals("Country 1",country1.toString());
        assertEquals("Country 2",country2.toString());
    }
}