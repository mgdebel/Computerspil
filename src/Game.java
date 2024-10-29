import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
/**
 * A Game object is an instance of NordicTraveller.
 * @author Nikolaj Ignatieff Schwartzbach.
 * @version August 2019.
 */
public class Game {

    private List<Country> countries;       // List of all countries in the game
    private List<Player>  players;         // List of all players.
    private GUIPlayer guiPlayer;           // Reference to the GUI Player
    private Random random;                 // Reference to random generator
    private boolean logging;               // Boolean telling whether the game is being logged
    private int totalSteps = 600;           // Total number of steps
    private int stepsLeft = totalSteps;    // Steps left
    private int seed;                      // Seed of this Game instance (used for Random)
    private Map<City, Point> guiPosition;  // Positions in the GUI for the various cities (in pixels)
    private Settings settings;             // Settings for this Game
    private boolean aborted=false;         // Boolean telling whethe this Game is forcefully aborted

    /**
     * Creates a new Game object with a random seed.
     */
    public Game() {
        this((int)(Math.random() * Integer.MAX_VALUE));
    }

    /**
     * Creates a new Game object with a specified seed.
     * @param seed   The seed value to be used.
     */
    public Game(int seed) {
        
        // Create random
        this.seed = seed;
        random = new Random(seed);

        // Create collections
        countries = new ArrayList<>();
        players   = new ArrayList<>();
        guiPosition = new HashMap<>();

        // Try to load Settings from file, otherwise default to normal settings
        try{
            settings = new Settings(new String(Files.readAllBytes(Paths.get("settings.dat"))));
        } catch(IOException|SettingsException e) {
            settings = new Settings();
        }
        
    }

    /**
     * Constructs a new Game object from a file.
     * The file is decoded using UTF-8 and each line must have the following form [CMD] [args...], CMD is one of the following commands
     * (up to ~ where x ~ y is a shorthand or x.equalsIgnoreCase(y)):
     *  - Game: set the game to a new Game with seed args[0].
     *  - Country: create a new Country with name args[0] (creates a MafiaCountry if args[1] ~ "mafia"). 
     *  - City: create a new city with name args[0], value args[1] and country equal to the last created country
     *    (creates a BorderCity if args[2] ~ "border" and a CapitalCity if args[2] ~ "capital").
     *  - Road: create a new road with length args[2] from a city with name ~ args[0] to a city with name ~args[1].
     * All cities must be created after the countries in which they are positioned.
     * Analogously roads must be created after the cities which they connect.
     * Lines beginning with // are ignored. All tabs are replaced by spaces.
     * Any exception thrown is printed to System.out.
     * 
     * @param path   Path of the file to load.
     * @return       Game object (or 'null' if an error is detected).
     * @throws       NumberFormatException   If the values of cities are not ints.
     */
    public static final Game fromFile(String path) {
        Game game = null;
        try {
            String country = null;
            for(String line : Files.readAllLines(Paths.get(path))) {
                while(line.contains("  ") || line.contains("\t")) {
                    line = line.replace("\t"," ").replace("  ", " ");
                }
                String[] args = line.split(" ");
                if(args[0].startsWith("//") || args[0].trim().isEmpty()) {
                    continue;
                }
                switch(args[0].toLowerCase()) {
                    case "background": break;
                    case "game": game = new Game(Integer.parseInt(args[1])); break;
                    case "country": game.addCountry(new Country(args[1])); country = args[1]; break;
                    case "city": game.addCity(args[1], Integer.parseInt(args[2]), country); break;
                    case "road": game.addRoads(args[1], args[2], Integer.parseInt(args[3])); break;
                    case "position": game.putPosition(game.getCity(args[1].trim()),
                        new Point(Integer.parseInt(args[2]), Integer.parseInt(args[3]))); break;
                    default: if(!args[0].contains("//")) System.out.println("unknown: "+args[0]); break;
                }
            }
            
            if(game != null) {
                game.getPlayers().add(new SmartPlayer(game.getRandomStartingPosition()));
                game.getPlayers().add(new GreedyPlayer(game.getRandomStartingPosition()));
                game.getPlayers().add(new RandomPlayer(game.getRandomStartingPosition()));
                GUIPlayer p = new GUIPlayer(game.getRandomStartingPosition());
                game.setGUIPlayer(p);
                game.reset();
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            return game;
        }
    }

    /**
     * Aborts the current game.
     */
    public void abort() {
        aborted = true;
    }

    /**
     * Determines whether or not this Game is ongoing.
     * @return   true and only if this Game is ongoing.
     */
    public boolean ongoing() {
        return !aborted && stepsLeft!=0;
    }
    
    /**
     * Gets the Random objects of this Game (to be used by all objects in this project).
     * @return   Reference to the Random generator of this Game.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Gets the Settings object.
     * @return   Reference to the Settings object of this Game.
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Gets the loss in a robbery.
     * Returns a random integer in the interval [minRobbery, maxRobbery] determined by the Settings object.
     * @return   An integer representing how many euroes the player lost.
     */
    public int getLoss() {
        return settings.getMinRobbery() + random.nextInt(settings.getMaxRobbery() - settings.getMinRobbery() + 1);
    }

    /**
     * Associates a given City object with its GUI position.
     * @param c   City.
     * @param p   Position (as a Point object).
     */
    public void putPosition(City c, Point p) {
        guiPosition.put(c, p);
    }

    /**
     * Gets the GUI position of a given City.
     * @param c   City.
     * @return    Position of the speicifed city (as a Point object).
     */
    public Point getPosition(City c) {
        return guiPosition.get(c);
    }

    /**
     * Returns the position of a random City (by a choosing a random Country and then a random City within that Country).
     * @return   Position of a Random City.
     */
    public Position getRandomStartingPosition() {
        Country country = getRandom(countries);
        return country.position(getRandom(country.getCities()));
    }

    /**
     * Adds a City to a Country .
     * @param city    The name of the city.
     * @param value   The initial value of the city.
     * @param contry  The name of the country.
     */    
    public void addCity(String name, int value, String country) {
        for(Country c : countries) {
            if(c.getName().equals(country.trim())) {
                c.addCity(new City(name, value, c));
                return;
            }
        }
    }

    /**
     * Resets this Game object (by resetting the log, Random object and all countries).
     * Assigns random positions to all players.
     */
    public void reset() {
        seed = random.nextInt(Integer.MAX_VALUE);
        random = new Random(seed);
        stepsLeft = totalSteps;
        aborted = false;
        
        for(Country c : countries) {
            c.reset();
        }
        
        Collections.sort(players);
        for(Player p : players) {
            p.setMoney(0);
            p.setPosition(getRandomStartingPosition());
        }
    }

    /**
     * Returns a random element in a given Set.
     * @param set    The set.
     * @return       A randomly chosen element from the given set.
     */
    private <T> T getRandom(Collection<T> set) {
        int r = random.nextInt(set.size());
        int i = 0;
        for(T t : set)
            if(i++ == r) { return t; }
        return null;
    }

    /**
     * Sets the GUI Player (the one controlled by the GUI). 
     * @param p   The Player object representing the GUI Player.
     */
    public void setGUIPlayer(GUIPlayer p) {
        this.guiPlayer = p;
        players.add(p);
    }

    /**
     * Gets a list of all countries within this Game.
     * @return   List of all countries.
     */
    public List<Country> getCountries() {
        return countries;
    }

    /**
     * Adds a given Country to this Game.
     * @param c   Country to be added.
     */
    public void addCountry(Country c) {
        countries.add(c);
        c.setGame(this);
        Collections.sort(countries, Comparator.comparing(k -> k.getName()));
    }

    /**
     * Gets the GUI Player (the one controlled by the GUI).
     * @return  Reference to the GUI Player.
     */
    public Player getGUIPlayer() {
        return guiPlayer;
    }

    /**
     * Gets a list of all players in this Gamee.
     * @return   List of all players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Finds a city with the specified name (in an arbitrary country).
     * @param name   Name of the city to search for (case sensitive).
     * @return       City object with the speicified named (or 'null').
     */
    public City getCity(String name) {
        City city = null;
        for(Country c : countries) {
            city = c.getCity(name);
            if(city != null) {
                return city;
            }
        }
        return null;
    }

    /**
     * Advances this Game one step.
     * A step consists of moving all players one step on the road they are currently travelling, as well as updating money.
     * If the step finishes the game (getStepsLeft()==1), the Log of this Game is saved to 'last.log'.
     */
    public void step() {
        if(stepsLeft == 0 || aborted) {
            return;
        }
        Collections.sort(players);
        for(Player p : players) {
            if(p.getClass()==RandomPlayer.class && !settings.isActive(0)) { continue; }
            if(p.getClass()==GreedyPlayer.class && !settings.isActive(1)) { continue; }
            if(p.getClass()==SmartPlayer.class && !settings.isActive(2))  { continue; }
            p.step();
            if(p.getMoney() < 0) {
                p.setMoney(0);
            }
        }
        stepsLeft--;
    }

    /**
     * Gets the number of steps remaining in this Game instance.
     * @return An integer representing how many steps this Game object can take before reaching the end.
     */
    public int getStepsLeft() {
        return stepsLeft;
    }

    /**
     * This method is called whenever a City is clicked.
     * Is used mainly by the GUI instance to invoke player commands.
     * It is also used by the LogPlayer to simulate mouse clicks.
     * @param c The city to click.
     */
    public void clickCity(City c) {
        guiPlayer.travelTo(c);
    }

    /**
     * Adds roads between 'a' and 'b' (if they exist) with a given length.
     * Adds a road from a to b, as well as a road from b to a (it is a symmetrical operator).
     * @param a    Name of the first city.
     * @param b    Name of the second city.
     * @param length  Length of the road to to be added..
     */
    public void addRoads(String a, String b, int lenght) {
        addRoads(getCity(a),getCity(b),lenght);
    }

    /**
     * Adds roads between 'a' and 'b' (if they exist) with a given length.
     * Adds a road from a to b, as well as a road from b to a (it is a symmetrical operator).
     * @param a       First City object.
     * @param b       Second City object.
     * @param length  Length of the road to construct.
     */
    public void addRoads(City a, City b, int length) {
        countries.stream().forEach(c -> c.addRoads(a,b,length));
    }

    /**
     * Gets how much time this game had at its initialization.
     * @return An integer representing how many steps this Game had available when it was created.
     */
    public int getTotalSteps() {
        return totalSteps;
    }

    /**
     * Changes the total time left of this Game instance.
     * @param totalSteps The new total time left.
     */
    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
}
