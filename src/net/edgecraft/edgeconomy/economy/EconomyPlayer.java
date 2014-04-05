package net.edgecraft.edgeconomy.economy;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecore.user.User;

public class EconomyPlayer {
	
	private int id;
	private double cash;
	private double totalGiven;
	private double totalReceived;
	private double totalDonated;
	private boolean welfare;
	
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected EconomyPlayer() { /* ... */ }
	
	protected EconomyPlayer(int id, double cash, double totalGiven, double totalReceived, double totalDonated, boolean welfare) {
		setID(id);
		setCash(cash);
		setTotalGiven(totalGiven);
		setTotalReceived(totalReceived);
		setTotalDonated(totalDonated);
		setWelfareStatus(welfare);
	}
	
	/**
	 * Returns the players' id
	 * @return Integer
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the players' cash
	 * @return Double
	 */
	public double getCash() {
		return cash;
	}
	
	/**
	 * Returns the players' amount of total given cash
	 * @return Double
	 */
	public double getTotalGiven() {
		return totalGiven;
	}
	
	/**
	 * Returns the players' amount of total received cash
	 * @return Double
	 */
	public double getTotalReceived() {
		return totalReceived;
	}
	
	/**
	 * Returns the players' amount of total donated cash
	 * @return Double
	 */
	public double getTotalDonated() {
		return totalDonated;
	}
	
	/**
	 * Returns if the player has welfare
	 * @return true/false
	 */
	public boolean hasWelfare() {
		return welfare;
	}
	
	/**
	 * Returns the players' user instance
	 * @return User
	 */
	public User getUser() {
		return EdgeCoreAPI.userAPI().getUser(id);
	}
	
	/**
	 * Returns the players' bank account
	 * @return
	 */
	public BankAccount getAccount() {
		return EdgeConomy.getEconomy().getAccountByOwnerID(id);
	}
	
	/**
	 * Returns the players' name
	 * @return String
	 */
	public String getName() {
		return getUser().getName();
	}
	
	/**
	 * Updates the given Object obj in the given String var in the database
	 * @param var
	 * @param obj
	 * @throws Exception
	 */
	private void update(String var, Object obj) throws Exception {
		if (var != null && obj != null) {
			this.db.prepareStatement("UPDATE " + Economy.ecoPlayerTable + " SET " + var + " = '" + obj.toString() + "' WHERE id = '" + id + "';").executeUpdate();
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
		if (getAccount().getBalance() >= Economy.getMaxWelfareBalance()) return false;
		if (getCash() >= Economy.getMaxWelfareBalance()) return false;
		if ((getCash() + getAccount().getBalance()) >= Economy.getMaxWelfareBalance()) return false;
		
		return true;
	}
	
	/**
	 * Sets the players' id
	 * @param id
	 */
	protected void setID(int id) {
		if (id >= 0)
			this.id = id;
	}
	
	/**
	 * Sets the players' cash
	 * @param cash
	 */
	protected void setCash(double cash) {
		this.cash = cash;
	}
	
	/**
	 * Sets the players' amount of total given cash
	 * @param totalGiven
	 */
	protected void setTotalGiven(double totalGiven) {
		if (totalGiven >= 0) 
			this.totalGiven = totalGiven;
	}
	
	/**
	 * Sets the players' amount of total received cash
	 * @param totalReceived
	 */
	protected void setTotalReceived(double totalReceived) {
		if (totalReceived >= 0)
			this.totalReceived = totalReceived;
	}
	
	/**
	 * Sets the players' amount of total donated cash
	 * @param totalDonated
	 */
	protected void setTotalDonated(double totalDonated) {
		if (totalDonated >= 0)
			this.totalDonated = totalDonated;
	}
	
	/**
	 * Disables/Enables welfare for this player
	 * @param welfare
	 */
	protected void setWelfareStatus(boolean welfare) {
		this.welfare = welfare;
	}
}