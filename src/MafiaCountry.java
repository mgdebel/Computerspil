public class MafiaCountry extends Country{

    /**
     * Creates a mafia country with a name, where you have a chance to get stolen from every time you visit a city
     *
     * @param name : the name of the country
     */
    public MafiaCountry(String name) {
        super(name);
    }

    /**
     * Calculates a bonus value based on if you get robbed or not based on the risk percentage which in that case gives a negative bonus instead
     * else gives a positive bonus based on a given value
     * @param value the maximum value of the bonus
     * @return a random bonus value between 0 and the given value or a negative value if you get robbed which is based on the risk percentage
     */
    public int bonus(int value){
        //Checks if a random value from [0,100] is less than the risk value [0,100] meaning that it goes into the if-statement equivalent to the getRisk() pct
        if (getGame().getRandom().nextInt(100 + 1) <= getGame().getSettings().getRisk()){
            //returns the positive loss as a negative number
            return -getGame().getLoss();
        }else{
            return super.bonus(value);
        }
    }
}
