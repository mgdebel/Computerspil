import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the various methods and variables of Road
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

public class RoadTest {
    private Game game;
    private Country country1;
    private City cityA, cityB, cityC, cityD;
    private Road road1, road2;

    @BeforeEach
    public void setUp() {
        // Create game object
        game = new Game(0);

        // Create country
        country1 = new Country("Country 1");
        country1.setGame(game);

        // Create cities
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country1);
        cityC = new City("City C", 40, country1);
        cityD = new City("City D", 100, country1);

        // Connect cities to countries
        country1.addCity(cityA);
        country1.addCity(cityB);
        country1.addCity(cityC);
        country1.addCity(cityD);

        // Create roads
        road1 = new Road(cityA, cityB, 4);
        road2 = new Road(cityC, cityD, 2);
    }

    @Test
    public void constructor() {
        // Første vej går fra CityA til CityB og har længde 4
        assertEquals(cityA, road1.getFrom());
        assertEquals(cityB, road1.getTo());
        assertEquals(4, road1.getLength());
        // Anden vej går fra CityC til CityD og har længde 2
        assertEquals(cityC, road2.getFrom());
        assertEquals(cityD, road2.getTo());
        assertEquals(2, road2.getLength());
    }

    @Test
    public void testToString(){
        assertEquals("City A (80) -> City B (60) : 4", road1.toString());
        assertEquals("City C (40) -> City D (100) : 2", road2.toString());
    }
}
