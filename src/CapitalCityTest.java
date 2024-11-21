import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the various methods and variables of CapitalCity
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

public class CapitalCityTest {
    private Game game;
    private Country country1, country2;
    private CapitalCity cityA, cityB, cityC;
    private GUIPlayer player1, player2;

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
        cityA = new CapitalCity("City A", 80, country1);
        cityB = new CapitalCity("City B", 80, country2);
        cityC = new CapitalCity("City C", 0, country2);
        //Create players
        player1 = new GUIPlayer(new Position(cityA,cityB,1));
        player2 = new GUIPlayer(new Position(cityA,cityB,1));
        game.setGUIPlayer(player1);
    }

    /**
     * Test arriving in a capital city
     */
    @Test
    public void arrive(){
        for(int seed = 0; seed < 100; seed++) { // Try 100 different seeds
            for (int i = 0; i < 100000; i++) { // Call method 100.000 times
                //Resets every time
                player1.setMoney(100);
                player2.setMoney(100);
                cityA.reset();
                cityB.reset();
                //Initializes variables
                int arrivingValueNewCountry = cityB.arrive(player1);
                int player1Money = player1.getMoney();
                int arrivingValueSameCountry = cityA.arrive(player2);
                int player2Money = player2.getMoney();

                //Checks arriving in another country
                //The Toll will always be subtracted, afterward it removes an interval between [0,80] (80 being after 100-20), therefore it could be any number between [0,80]
                assertTrue(player1Money >= 0);
                assertTrue(player1Money <= 80);
                //The arriving value would in the "worst case" the toll being 20, be the bonus being 0, the spending in the capital city being 80 therefore -100
                assertTrue(arrivingValueNewCountry >= -100);
                //The arriving value would in the "best case" the toll being 20, be the bonus being 80, the spending in the capital city being 0, therefore 80
                assertTrue(arrivingValueNewCountry <= 80);

                //Checks arriving in same country
                //The Toll will not be subtracted, afterward it removes an interval between [0,100], therefore it could be any number between [0,100]
                assertTrue(player2Money >= 0);
                assertTrue(player2Money <= 100);
                //The arriving value would in the "worst case" be the bonus being 0, the spending in the capital city being 100 therefore -100
                assertTrue(arrivingValueSameCountry >= -100);
                //The arriving value would in the "best case" be the bonus being 80, the spending in the capital city being 0, therefore 80
                assertTrue(arrivingValueSameCountry <= 80);
            }
        }
        player1.setMoney(100);
        player2.setMoney(0);
        cityA.reset();
        cityB.reset();
        //Both players arrive in another country in a border city
        cityB.arrive(player1);
        //Checks that the bonus - toll - spending in this case should be 0, since bonus = 0 and toll = 0 and spending = 0
        assertEquals(0, cityC.arrive(player2));
        //Checks that the spending is based off current money, therefore it should always be 0, if there is no money to spend
        assertEquals(0,player2.getMoney());
    }
}