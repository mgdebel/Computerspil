import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the various methods and variables of BorderCity
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

class BorderCityTest {
    private Game game;
    private Country country1, country2;
    private BorderCity cityA, cityB, cityC;
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
        cityA = new BorderCity("City A", 80, country1);
        cityB = new BorderCity("City B", 50, country2);
        cityC = new BorderCity("City C", 0, country2);
        //Create players
        player1 = new GUIPlayer(new Position(cityA,cityB,1));
        player2 = new GUIPlayer(new Position(cityA,cityB,1));
        game.setGUIPlayer(player1);
    }

    /**
     * Test arriving in a border city
     */
    @Test
    public void arrive(){
        //Gives player1 100 in money and player2 remains at 0
        player1.setMoney(100);
        //Both players arrive in another country in a border city
        cityB.arrive(player1);
        //Checks that the bonus - toll in this case should be 0, since bonus = 0 and toll = 0
        assertEquals(0, cityC.arrive(player2));
        //Checks that the toll is 20% and has been removed from the players money
        assertEquals(80,player1.getMoney());
        //Checks that the toll is percentage based off of money, therefore it should be 0 for a player with no money
        assertEquals(0,player2.getMoney());
        //Checks that player doesn't pay a toll if they stay in the same land
        cityA.arrive(player1);
        assertEquals(80,player1.getMoney());
    }
}