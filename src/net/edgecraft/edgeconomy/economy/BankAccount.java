package net.edgecraft.edgeconomy.economy;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecore.user.User;

public class BankAccount {
	
	private int id;
	private int ownerID;
	private double balance;
	private double lowestBalance;
	private double highestBalance;
	private double credit;
	private double paidCredit;
	private boolean closed;
	private String reason;
	private double payday;
	
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected BankAccount() { /* ... */ }
	
	protected BankAccount(int id, int ownerID, double balance, double lowestBalance, double highestBalance, double credit, double paidCredit, boolean closed, String reason, double payday) {
		setID(id);
		setOwnerID(ownerID);
		setBalance(balance);
		setLowestBalance(lowestBalance);
		setHighestBalance(highestBalance);
		setCredit(credit);
		setPaidCredit(paidCredit);
		setClosedStatus(closed);
		setReason(reason);
		setPayday(payday);
	}
	
	/**
	 * Returns the accounts' id
	 * @return Integer
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the account owners' id
	 * @return Integer
	 */
	public int getOwnerID() {
		return ownerID;
	}
	
	/**
	 * Returns the accounts' balance
	 * @return Double
	 */
	public double getBalance() {
		return balance;
	}
	
	/**
	 * Returns the accounts' lowest balance
	 * @return Double
	 */
	public double getLowestBalance() {
		return lowestBalance;
	}
	
	/**
	 * Returns the accounts' highest balance
	 * @return Double
	 */
	public double getHighestBalance() {
		return highestBalance;
	}
	
	/**
	 * Returns the accounts' credit (if existing)
	 * @return Double
	 */
	public double getCredit() {
		return credit + (credit / 100 * Economy.getCreditFee());
	}
	
	/**
	 * Returns the accounts' raw credit (if existing)
	 * @return Double
	 */
	public double getRawCredit() {
		return credit;
	}
	
	/**
	 * Returns the amount of paid credit
	 * @return Double
	 */
	public double getPaidCredit() {
		return paidCredit;
	}
	
	/**
	 * Returns if the account's closed or not
	 * @return true/false
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * Returns the reason if the account is closed
	 * @return String
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Returns the accounts' payday
	 * @return Double
	 */
	public double getPayday() {
		return payday;
	}
	
	/**
	 * Returns the account owners' user instance
	 * @return User
	 */
	public User getUser() {
		return EdgeCoreAPI.userAPI().getUser(ownerID);
	}
	
	/**
	 * Returns the EconomyPlayer connected with this account
	 * @return EconomyPlayer
	 */
	public EconomyPlayer getEconomyPlayer() {
		return EdgeConomy.getEconomy().getEconomyPlayer(ownerID);
	}
	
	/**
	 * Checks if the account is able to receive welfare
	 * @return true/false
	 */
	public boolean hasWelfare() {
		return getEconomyPlayer().hasWelfare();
	}
	
	/**
	 * Returns the account owners' name
	 * @return String
	 */
	public String getOwner() {
		return getUser().getName();
	}
	
	/**
	 * Sets the accounts' id
	 * @param id
	 */
	protected void setID(int id) {
		if (id >= 0)
			this.id = id;
	}
	
	/**
	 * Sets the accounts' owner id
	 * @param ownerID
	 */
	protected void setOwnerID(int ownerID) {
		if (ownerID >= 0)
			this.ownerID = ownerID;
	}
	
	/**
	 * Sets the accounts' balance
	 * @param balance
	 */
	protected void setBalance(double balance) {
		this.balance = balance;
	}
	
	/**
	 * Sets the accounts' lowest balance
	 * @param lowestBalance
	 */
	protected void setLowestBalance(double lowestBalance) {
		this.lowestBalance = lowestBalance;
	}
	
	/**
	 * Sets the accounts' highest balance
	 * @param highestBalance
	 */
	protected void setHighestBalance(double highestBalance) {
		this.highestBalance = highestBalance;
	}
	
	/**
	 * Sets the accounts' credit
	 * @param credit
	 */
	protected void setCredit(double credit) {
		if (credit >= 0.0D)
			this.credit = credit;
	}
	
	/**
	 * Sets the accounts' paid credit
	 * @param paidCredit
	 */
	protected void setPaidCredit(double paidCredit) {
		if (paidCredit >= 0.0D) 
			this.paidCredit = paidCredit;
	}
	
	/**
	 * Unlocks/locks the account
	 * @param closed
	 */
	protected void setClosedStatus(boolean closed) {
		this.closed = closed;
	}
	
	/**
	 * Unlocks/locks the account for the given reason
	 * @param closed
	 * @param reason
	 */
	protected void setClosedStatus(boolean closed, String reason) {
		this.closed = closed;
		if (reason != null)
			setReason(reason);
	}
	
	/**
	 * Sets the reason why the account is locked
	 * @param reason
	 */
	protected void setReason(String reason) {
		if (reason != null)
			this.reason = reason;
	}
	
	/**
	 * Sets the payday
	 * @param payday
	 */
	protected void setPayday(double payday) {
		if (payday >= 0.0D)
			this.payday = payday;
	}
	
	/**
	 * Updates the given Object obj in the given String var in the database
	 * @param var
	 * @param obj
	 * @throws Exception
	 */
	private void update(String var, Object obj) throws Exception {
		if (var != null && obj != null) {
			this.db.prepareUpdate("UPDATE " + Economy.accountTable + " SET " + var + " = '" + obj.toString() + "' WHERE id = '" + this.id + "';").executeUpdate();
		}
	}
	
	/**
	 * Switches the accounts' owner
	 * @param newOwner
	 * @throws Exception
	 */
	public void switchOwner(int newOwner) throws Exception {
		setOwnerID(newOwner);
		update("ownerID", newOwner);
	}
	
	/**
	 * Updates the accounts' balance
	 * @param balance
	 * @throws Exception
	 */
	public void updateBalance(double balance) throws Exception {
		setBalance(balance);
		update("balance", balance);
	}
	
	/**
	 * Updates the accounts' lowest balance
	 * @param lowestBalance
	 * @throws Exception
	 */
	public void updateLowestBalance(double lowestBalance) throws Exception {
		setLowestBalance(lowestBalance);
		update("lowestbalance", lowestBalance);
	}
	
	/**
	 * Updates the accounts' highest balance
	 * @param highestBalance
	 * @throws Exception
	 */
	public void updateHighestBalance(double highestBalance) throws Exception {
		setHighestBalance(highestBalance);
		update("highestbalance", highestBalance);
	}
	
	/**
	 * Updates the accounts' credit
	 * @param credit
	 * @throws Exception
	 */
	public void updateCredit(double credit) throws Exception {
		setCredit(credit);
		update("credit", credit);
	}
	
	/**
	 * Updates the accounts' paid credit
	 * @param paidCredit
	 * @throws Exception
	 */
	public void updatePaidCredit(double paidCredit) throws Exception {
		setPaidCredit(paidCredit);
		update("paidcredit", paidCredit);
	}
	
	/**
	 * Unlocks/Locks the account
	 * @param var
	 * @throws Exception
	 */
	public void setClosed(boolean var) throws Exception {
		setClosedStatus(var);
		
		if (!var)
			setReason("");
		
		int _var = var ? 1 : 0;
		update("closed", _var);
	}
	
	/**
	 * Updates the reason why the account is locked
	 * @param reason
	 * @throws Exception
	 */
	public void updateReason(String reason) throws Exception {
		setReason(reason);
		update("reason", reason);
	}
	
	/**
	 * Calculates and updates the accounts' payday
	 * @param payday
	 * @throws Exception
	 */
	public void updatePayday(double payday) throws Exception {
		if (getBalance() <= 0) setPayday(0);
		
		setPayday(getBalance() / 100 * Economy.getPaydayBonus());
		update("payday", getPayday());
	}
	
	@Override
	public int hashCode() {
		return (int) getUser().getName().hashCode() * getReason().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final BankAccount another = (BankAccount) obj;
		
		if (getUser().getName().equals(another.getUser().getName())) {
			if (getReason().equals(another.getReason())) {
				return true;
			}
		}
		
		return false;
	}
}
