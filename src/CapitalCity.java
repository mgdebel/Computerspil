public class CapitalCity extends BorderCity{
    public CapitalCity(String name, int value, Country country){
        super(name,value,country);
    }
    /**
     * a specific player arrives in a capital city, paying a toll if they come from a different country and pays
     * with their new amount of money for goods in the capital and receives a bonus
     * @param p the player arriving in the city
     * @return bonus minus the toll minus the expenses for goods in the capital, the toll is a percentage of the players money before getting the bonus
     * the expenses for goods is a random number between 0 and their money after paying the toll
     */
    public int arrive(Player p){
        //Calls the super methods arrive for paying toll
        int newBonus = super.arrive(p);
        //Get the money after paying toll
        int money = p.getMoney();
        //Calculates the spent money being between 0 and the new money
        int spentMoney = getCountry().getGame().getRandom().nextInt(money + 1);
        //Sets the players money and changes the value of the city
        p.setMoney(money - spentMoney);
        changeValue(spentMoney);
        return newBonus - spentMoney;
    }
}
