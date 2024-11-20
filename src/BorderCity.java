public class BorderCity extends City{
    public BorderCity(String name, int value, Country country){
        super(name,value,country);

    }

    /**
     * a specific player arrives in a city, paying a toll if they come from a different country
     * and receives a bonus
     * @param p the player arriving in the city
     * @return bonus minus the toll, the toll is a percentage of the players money before getting the bonus
     */
    public int arrive(Player p){
        //Initialize toll to 0 for calculating return value
        int tollToBePaid = 0;
        //Initialize variables
        int playerMoney = p.getMoney();
        Country fromCountry = p.getFromCountry();
        //If the country the player is coming from is not the current country then pay toll
        if(!fromCountry.equals(getCountry())){
            //Divide the integer [0-100] by 100 to get percentage
            double tollToBePaidInPct = (double)getCountry().getGame().getSettings().getTollToBePaid()/100;
            //Multiply the toll with the money before the bonus is added
            tollToBePaid = (int) Math.floor(tollToBePaidInPct * (double)playerMoney);
            p.setMoney(playerMoney - tollToBePaid);
            //Increase the value of the city with the toll
            changeValue(tollToBePaid);
        }
        //Calculate bonus
        int bonus = super.arrive();
        //Returns the bonus - toll
        return bonus - tollToBePaid;
    }
}
