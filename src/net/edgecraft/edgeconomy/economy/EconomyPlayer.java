package net.edgecraft.edgeconomy.economy;

import java.util.UUID;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecore.user.User;

public class EconomyPlayer {
	
	private UUID user;
	
	private double cash;
	private double totalGiven;
	private double totalReceived;
	private double totalDonated;
	
	private boolean welfare;
	
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected EconomyPlayer() { /* ... */ }
	
	protected EconomyPlayer(UUID user, double cash, double totalGiven, double totalReceived, double totalDonated, boolean welfare) {
		
		setUser(user);
		
		setCash(cash);
		setTotalGiven(totalGiven);
		setTotalReceived(totalReceived);
		setTotalDonated(totalDonated);
		
		setWelfareStatus(welfare);
	}
	
	/**
	 * Returns the UUID of the player
	 * @return
	 */
	public UUID getUser() {
		return user;
	}
	
	/**
	 * Returns the user as a class instance
	 * @return User
	 */
	public User getUserInstance() {
		return EdgeCoreAPI.userAPI().getUser(getUser());
	}
	
	/**
	 * Returns the name of the user
	 * @return String
	 */
	public String getName() {
		return getUserInstance().getName();
	}
	
	/**
	 * Returns an account or null, based on the player has one
	 * @return BankAccount or null
	 */
	public BankAccount getAccount() {
		return Economy.getInstance().getAccount(getUser());
	}
	
	/**
	 * Returns the amount of cash the player got
	 * @return Double
	 */
	public double getCash() {
		return cash;
	}
	
	/**
	 * Returns the amount of total given cash
	 * @return Double
	 */
	public double getTotalGiven() {
		return totalGiven;
	}
	
	/**
	 * Returns the amount of total received cash
	 * @return Double
	 */
	public double getTotalReceived() {
		return totalReceived;
	}
	
	/**
	 * Returns the amount of total donated cash
	 * @return Double
	 */
	public double getTotalDonated() {
		return totalDonated;
	}
	
	/**
	 * Checks if the player is able to receive welfare
	 * @return true/false
	 */
	public boolean hasWelfare() {
		return welfare;
	}
	
	/**
	 * Updates the given Object obj in the given String var in the database
	 * @param var
	 * @param obj
	 * @throws Exception
	 */
	private void update(String var, Object obj) throws Exception {
		if (var != null && obj != null) {
			this.db.prepareStatement("UPDATE " + Economy.ecoPlayerTable + " SET " + var + " = '" + obj.toString() + "' WHERE uuid = '" + getUser().toString() + "';").executeUpdate();
		}
	}
	
	/**
	 * Updates the accounts' cash
	 * @param cash
	 * @throws Exception
	 */
	public void updateCash(double cash) throws Exception {
		setCash(cash);
		update("cash", cash);
	}
	
	/**
	 * Updates the accounts' amount of total given cash
	 * @param totalGiven
	 * @throws Exception
	 */
	public void updateTotalGiven(double totalGiven) throws Exception {
		setTotalGiven(totalGiven);
		update("totalgiven", totalGiven);
	}
	
	/**
	 * Updates the accounts' amount of total received cash
	 * @param totalReceived
	 * @throws Exception
	 */
	public void updateTotalReceived(double totalReceived) throws Exception {
		setTotalReceived(totalReceived);
		update("totalreceived", totalReceived);
	}
	
	/**
	 * Updates the accounts' amount of total donated cash
	 * @param totalDonated
	 * @throws Exception
	 */
	public void updateTotalDonated(double totalDonated) throws Exception {
		setTotalDonated(totalDonated);
		update("totaldonated", totalDonated);
	}
	
	/**
	 * Disables/Enables welfare for the player
	 * @param var
	 * @throws Exception
	 */
	public void setWelfare(boolean var) throws Exception {
		if (!checkWelfare()) var = false;
		
		setWelfareStatus(var);
		
		int _var = var ? 1 : 0;
		update("welfare", _var);
	}
	
	/**
	 * Checks if the player's able to receive welfare
	 * @param id
	 */
	public boolean checkWelfare() throws Exception {
		if (getAccount().getBalance() >= Economy.getMaxWelfareAmount()) return false;
		if (getCash() >= Economy.getMaxWelfareAmount()) return false;
		if ((getCash() + getAccount().getBalance()) >= Economy.getMaxWelfareAmount()) return false;
		
		return true;
	}
	
	/**
	 * Sets the internal user
	 * @param user
	 */
	protected void setUser(UUID user) {
		if (user != null)
			this.user = user;
	}
	
	/**
	 * Sets the amount of cash
	 * @param cash
	 */
	protected void setCash(double cash) {
		if (cash >= 0)
			this.cash = cash;
	}
	
	/**
	 * Sets the amount of total given cash
	 * @param totalGiven
	 */
	protected void setTotalGiven(double totalGiven) {
		if (totalGiven >= 0)
			this.totalGiven = totalGiven;
	}
	
	/**
	 * Sets the amount of total received cash
	 * @param totalReceived
	 */
	protected void setTotalReceived(double totalReceived) {
		if (totalReceived >= 0)
			this.totalReceived = totalReceived;
	}
	
	/**
	 * Sets the amount of total donated cash
	 * @param totalDonated
	 */
	protected void setTotalDonated(double totalDonated) {
		if (totalDonated >= 0)
			this.totalDonated = totalDonated;
	}
	
	/**
	 * Sets the welfare
	 * @param welfare
	 */
	protected void setWelfareStatus(boolean welfare) {
		this.welfare = welfare;
	}
	
	@Override
	public int hashCode() {
		return (int) getUser().hashCode() * getUserInstance().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == this) return true;
		if (obj == null) return false;
		if (!getClass().equals(obj.getClass())) return false;
		
		EconomyPlayer another = (EconomyPlayer) obj;
		
		if (another.getUser().equals(getUser())) {
			if (another.getUserInstance().equals(getUserInstance())) {
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "EconomyPlayer {" + getUser().toString() + ";" + getCash() + "}";
	}
}