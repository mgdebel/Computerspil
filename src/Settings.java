import java.io.IOException;
import java.io.PrintWriter;

/**
 * Settings represents the current settings at a given time.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 */
public class Settings {

	private boolean[] activePlayers;
	private int minLoss, maxLoss, gameSpeed, tollSize, robRisk;
	
	/**
	 * Instantiates a new Settings object based on its values.
	 * @param activePlayers Which players are active. Must be an array of length 3, where each index corresponds to a given player (0 = random, 1 = greedy, 2 = smart).
	 * @param tollSize The toll size (in %).
	 * @param robRisk The risk of robbery (in %).
	 * @param minLoss The minimum loss when robbed (in €).
	 * @param maxLoss The maximum loss when robbed (in €).
	 * @param gameSpeed The game speed. Must be in the interval [0,4], where 0 = stop, and 4 = sonic.
	 */
	public Settings(boolean[] activePlayers, int tollSize, int robRisk, int minLoss, int maxLoss, int gameSpeed){
		this.activePlayers = activePlayers;
		this.tollSize = tollSize;
		this.robRisk = robRisk;
		this.minLoss = minLoss;
		this.maxLoss = maxLoss;
		this.gameSpeed = gameSpeed;
	}
	
	/**
	 * Performs a deep copy of the specified Settings object.
	 * @param s The Settings to copy.
	 */
	public Settings(Settings s){
		this();
		for(int i=0; i<3; i++)
			this.activePlayers[i] = s.activePlayers[i];
		
		this.tollSize = s.tollSize;
		this.robRisk  = s.robRisk;
		this.minLoss  = s.minLoss;
		this.maxLoss  = s.maxLoss;
		this.gameSpeed = s.gameSpeed;
	}
	
	/**
	 * Instantiates a Settings object with the default values.
	 */
	public Settings(){
		this(new boolean[]{true, true, true}, 20, 20, 10, 50, 2);
	}
	
	/**
	 * Instantiates a Settings object based on a list of strings.
	 * The first line is the active players, the second line is tollSize, and so on.
	 * @param str An array of string representations of the settings.
	 * @throws SettingsException
	 */
	public Settings(String[] str) throws SettingsException{
		this(collectStrings(str));
	}
	
	private static String collectStrings(String[] str){
		StringBuilder sb = new StringBuilder();
		
		for(String s : str)
			sb.append(s+"\r\n");
		
		return sb.toString();
	}
	
	/**
	 * Instantiates a Settings object based on its string representation.
	 * @param s The string representation of this Settings object.
	 * @throws SettingsException
	 */
	public Settings(String s) throws SettingsException{
		this();
		String[] lines = s.replace("\r", "").split("\n");
		
		if(lines.length!=6)
			throw new SettingsException("Expected a total of 6 lines, but received "+lines.length+".");
		
		for(int i=0; i<lines.length; i++){
			String line = lines[i];
			switch(i){
				case 0:
					if(line.length()!=3)
						throw new SettingsException("Expected the first line to have only 3 characters, but received "+line.length()+".");
					char[] chars = line.toCharArray();
					for(int j=0; j<3; j++)
						setActive(j, chars[j]=='1');
					break;
				case 1:
					setTollToBePaid(Integer.parseInt(line));
					break;
				case 2:
					setRisk(Integer.parseInt(line));
					break;
				case 3:
					minLoss = Integer.parseInt(line);
					break;
				case 4:
					maxLoss = Integer.parseInt(line);
					break;
				case 5:
					setGameSpeed(Integer.parseInt(line));
					break;
			}
		}
	}
	
	private void save() {
		try{
			PrintWriter out = new PrintWriter("settings.dat");
			out.print(this);
			out.close();
		} catch(IOException e){
			System.out.println("Unable to save settings: "+e.getMessage());
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<3; i++)
			sb.append(activePlayers[i] ? "1" : "0");
		
		sb.append("\r\n"+tollSize+"\r\n");
		sb.append(robRisk+"\r\n");
		sb.append(minLoss+"\r\n");
		sb.append(maxLoss+"\r\n");
		sb.append(gameSpeed+"\r\n");
		
		return sb.toString();
	}
	
	/**
	 * Determines whether or not a given AI player is currently active.
	 * @param player The AI Player to test (0 = random, 1 = greedy, 2 = smart).
	 * @return A boolean representing whether or not the given player is active.
	 */
	public boolean isActive(int player){
		return activePlayers[player];
	}
	
	/**
	 * Sets whether or not a given AI Player is active.
	 * @param player The AI Player to change activity of (0 = random, 1 = greedy, 2 = smart). 
	 * @param active Whether or not this player should be active.
	 */
	public void setActive(int player, boolean active){
		activePlayers[player] = active;
		save();
	}
	
	/**
	 * Gets the current toll size (in % as an integer).
	 * @return
	 */
	public int getTollToBePaid(){
		return tollSize;
	}
	
	/**
	 * Sets the current toll size (in %).
	 * @param tollSize
	 */
	public void setTollToBePaid(int tollSize){
		this.tollSize = tollSize;
		save();
	}

	/**
	 * Gets the current risk of being robbed (in %).
	 * @return
	 */
	public int getRisk(){
		return robRisk;
	}
	
	/**
	 * Sets the current risk of being robbed (in %).
	 * @param robRisk
	 */
	public void setRisk(int robRisk){
		this.robRisk = robRisk;
		save();
	}
	
	/**
	 * Gets the minimum robbery amount (can't be robbed for anything less than this).
	 * @return The minimum robbery amount in €.
	 */
	public int getMinRobbery(){
		return minLoss;
	}
	
	/**
	 * Changes the bounds for robbery.
	 * @param min The new minimum robbery amount (in €).
	 * @param max The new maximum robbery amount (in €).
	 */
	public void setMinMaxRobbery(int min, int max){
		this.minLoss = min;
		this.maxLoss = max;
		save();
	}
	
	/**
	 * Gets the maximum robbery amount (can't be robbed for anything more than this).
	 * @return The maximum robbery amount in €.
	 */
	public int getMaxRobbery(){
		return maxLoss;
	}
	
	/**
	 * Gets the current game speed:
	 *  0: STOP
	 *  1: SLOW
	 *  2: MED
	 *  3: FAST
	 *  4: SONIC
	 * @return An integer in the interval [0,4] representing the current game speed.
	 */
	public int getGameSpeed(){
		return gameSpeed;
	}
	
	/**
	 * Sets the current game speed:
	 *  0: STOP
	 *  1: SLOW
	 *  2: MED
	 *  3: FAST
	 *  4: SONIC
	 * @param gameSpeed An integer in the interval [0,4] representing the current game speed.
	 */
	public void setGameSpeed(int gameSpeed){
		this.gameSpeed = gameSpeed;
		save();
	}
}

class SettingsException extends Exception {

	private static final long serialVersionUID = 354443936938324L;
	
	public SettingsException(String s){
		super(s);
	}
}