import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the various methods and variables of MafiaCountry
 *
 * @author 202406714 Magnus Debel-Hansen og 20240543 Alexander Bak
 */

class MafiaCountryTest {
    private Game game;
    private MafiaCountry country1, country2;
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
        country1 = new MafiaCountry("Sweden");
        country1.setGame(game);
        country2 = new MafiaCountry("Mafia country 2");
        country2.setGame(game);
        // Create cities
        cityA = new BorderCity("City A", 50, country1);
        cityB = new BorderCity("City B", 100, country1);
        cityC = new BorderCity("City C", 80, country2);
        //Create players
        player1 = new GUIPlayer(new Position(cityA,cityB,1));
        player2 = new GUIPlayer(new Position(cityA,cityB,1));
        game.setGUIPlayer(player1);
    }

    /**
     * Test bonus method for MafiaCountry
     */
    @Test
    public void bonus(){
        for(int seed = 0; seed < 100; seed++) { // Try 100 different seeds
            int timesRobbed = 0;
            int sumRobbedAmount = 0;
            ArrayList<Integer> bonuses = new ArrayList<>();
            for (int i = 0; i < 100000; i++) { // Call method 100.000 times
                //Resets every time
                player1.setMoney(100);
                player2.setMoney(100);
                cityA.reset();
                cityB.reset();
                cityC.reset();
                //Initializes variables
                int arrivingValue = cityA.arrive(player1);
                int bonusCityB = cityB.arrive(player1);
                int arrivingDifferentCountry = cityC.arrive(player2);
                if(arrivingValue < 0){
                    timesRobbed ++;
                    //For sumRobbedAmount to be positive it has to minus the negative bonus
                    sumRobbedAmount -= arrivingValue;
                    //Checks that the robbed amount is between the interval [-50,-10] - it is minus, since the robbed amount is negative
                    assertTrue(arrivingValue <= -10);
                    assertTrue(arrivingValue >= -50);
                    bonuses.add(-arrivingValue);
                }else{
                    //Check the bonus is calculated correctly when not robbed, therefore in the interval [0,50] and [0,100]
                    assertTrue(arrivingValue <= 50);
                    assertTrue(bonusCityB <= 100);
                }
                //Checks if arriving in a different country works normally when not getting robbed
                if(arrivingDifferentCountry >= 0){
                    //The normal bonus would be between 0 and the money - toll = 80 therefore in the interval [0,80] - we have checked the first part of the interval, by ensuring it isn't a robbery
                    assertTrue(arrivingDifferentCountry <= 80);
                }
            }
            //Check the average for times getting robbed is about 20%
            assertTrue(((double) timesRobbed) * 100 / ((double)100000) >= 17);
            assertTrue(((double) timesRobbed) * 100 / ((double)100000) <= 23);
            //Check the average for the amount being robbed is about 30
            assertTrue(((double) sumRobbedAmount)/ ((double)timesRobbed) >= 27);
            assertTrue(((double) sumRobbedAmount)/ ((double)timesRobbed) <= 33);
            //Check that the robbed amount can be any number between [10,50]
            for (int i = 10; i <= 50; i++){
                assertTrue(bonuses.contains(i));
            }
        }
    }
}