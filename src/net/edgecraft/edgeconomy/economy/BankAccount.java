package net.edgecraft.edgeconomy.economy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.Habitat;
import net.edgecraft.edgecuboid.shop.ShopHandler;

public class BankAccount {
	
	private int id;
	private UUID owner;
	
	private double balance;
	private double lowestBalance;
	private double highestBalance;
	private double credit;
	private double paidCredit;
	private double payday;
	
	private boolean closed;
	private String reason;
	
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected BankAccount() { /* ... */ }
	
	protected BankAccount(int id, UUID owner, double balance, double lowestBalance, double highestBalance, double credit, double paidCredit, double payday, boolean closed, String reason) {
		
		setId(id);
		setOwner(owner);
		
		setBalance(balance);
		setLowestBalance(lowestBalance);
		setHighestBalance(highestBalance);
		setCredit(credit);
		setPaidCredit(paidCredit);
		setPayday(payday);
		
		setClosedStatus(closed);
		setReason(reason);
	}
	
	/**
	 * Returns the account id
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the owner as an UUID
	 * @return
	 */
	public UUID getOwner() {
		return owner;
	}
	
	/**
	 * Returns an instance of the user class of the owner
	 * @return User
	 */
	public User getUser() {
		return EdgeCoreAPI.userAPI().getUser(getOwner());
	}
	
	/**
	 * Returns the @link(EconomyPlayer) of the account owner
	 * @return EconomyPlayer
	 */
	public EconomyPlayer getEconomyPlayer() {
		return Economy.getInstance().getEconomyPlayer(getOwner());
	}

	/**
	 * Returns the balance
	 * @return Double
	 */
	public double getBalance() {
		return balance;
	}
	
	/**
	 * Returns the lowest balance this account ever had
	 * @return Double
	 */
	public double getLowestBalance() {
		return lowestBalance;
	}

	/**
	 * Returns the highest balance this account ever had
	 * @return Double
	 */
	public double getHighestBalance() {
		return highestBalance;
	}

	/**
	 * Returns the credit, including all taxes and fees
	 * @return Double
	 */
	public double getCredit() {
		return credit + (credit / 100 * Economy.getCreditFee());
	}
	
	/**
	 * Returns the raw credit
	 * @return
	 */
	public double getRawCredit() {
		return credit;
	}
	
	/**
	 * Returns the paid credit
	 * @return Double
	 */
	public double getPaidCredit() {
		return paidCredit;
	}
	
	/**
	 * Returns the payday
	 * @return Double
	 */
	public double getPayday() {
		return payday;
	}
	
	/**
	 * Returns the state taxes the account owner has to pay
	 * @return Integer
	 */
	public int getStateTax() {
		return (int) Math.round(getBalance() / 100 * Economy.getStateTax());
	}
	
	/**
	 * Returns the credit tax the account owner has to pay (if the account has got a credit)
	 * @return Double
	 */
	public double getCreditTax() {
		return getCredit() / 100 * 0.2;
	}
	
	/**
	 * Returns the tax of the account owner's vehicle
	 * @return Double
	 */
	public double getVehicleTax() {
		return 0.0D;
	}
	
	/**
	 * Returns the taxes of all properties the account owner owns
	 * @return Double
	 */
	public double getPropertyTax() {
		if (EdgeCuboidAPI.cuboidAPI().getHabitatByOwner(getUser().getName()) == null)
			return 0.0D;
		
		List<Double> propertieTaxes = new ArrayList<>();
		double fullTax = 0.0D;
		
		for (Habitat h : EdgeCuboidAPI.cuboidAPI().getHabitats().values()) {
			if (h.getOwner().equals(getUser().getName()))
				propertieTaxes.add(h.getTaxes());
		}
		
		for (double d : propertieTaxes) {
			fullTax += d;
		}
		
		double shopTax = ShopHandler.getInstance().getShop(getUser().getName()) == null ? 0 : ShopHandler.getInstance().getShop(getUser().getName()).getTaxes();
		
		return fullTax + shopTax;
	}

	/**
	 * Checks whether the account is closed or not
	 * @return true/false
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Returns the reason why the account is locked
	 * @return String
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Checks if the account has welfare or not
	 * @return true/false
	 */
	public boolean hasWelfare() {
		return getEconomyPlayer().hasWelfare();
	}
	
	/**
	 * Updates the given Object obj in the given String var in the database
	 * @param var
	 * @param obj
	 * @throws Exception
	 */
	private void update(String var, Object obj) throws Exception {
		if (var != null && obj != null) {
			this.db.prepareStatement("UPDATE " + Economy.accountTable + " SET " + var + " = '" + obj.toString() + "' WHERE id = '" + this.id + "';").executeUpdate();
		}
	}
	
	/**
	 * Switches the account's owner
	 * @param newOwner
	 * @throws Exception
	 */
	public void switchOwner(User newOwner) throws Exception {
		setOwner(newOwner.getUUID());
		update("uuid", newOwner.getUUID().toString());
	}
	
	/**
	 * Updates the balance of the account
	 * @param balance
	 * @throws Exception
	 */
	public void updateBalance(double balance) throws Exception {
		setBalance(balance);
		update("balance", balance);
	}
	
	/**
	 * Updates the lowest amount of money this account ever had
	 * @param lowestBalance
	 * @throws Exception
	 */
	public void updateLowestBalance(double lowestBalance) throws Exception {
		setLowestBalance(lowestBalance);
		update("lowestbalance", lowestBalance);
	}
	
	/**
	 * Updates the highest amount of money this account ever had
	 * @param highestBalance
	 * @throws Exception
	 */
	public void updateHighestBalance(double highestBalance) throws Exception {
		setHighestBalance(highestBalance);
		update("highestbalance", highestBalance);
	}
	
	/**
	 * Updates the credit 
	 * @param credit
	 * @throws Exception
	 */
	public void updateCredit(double credit) throws Exception {
		setCredit(credit);
		update("credit", credit);
	}
	
	/**
	 * Updates the amount of credit the account owner has paid
	 * @param paidCredit
	 * @throws Exception
	 */
	public void updatePaidCredit(double paidCredit) throws Exception {
		setPaidCredit(paidCredit);
		update("paidcredit", paidCredit);
	}
	
	/**
	 * (Un-)locks the account
	 * @param var
	 * @throws Exception
	 */
	public void setClosed(boolean var) throws Exception {
		setClosedStatus(var);
		
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
	public void updatePayday() {
		try {
			
			if (getBalance() <= 0) this.payday = 0;
			
			DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
			df.applyPattern("0.00");
			
			this.payday = Double.valueOf(df.format(getBalance() / 100 * Economy.getPaydayBonus()));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the account id
	 * @param id
	 */
	protected void setId(int id) {
		if (id > 0)
			this.id = id;
	}

	/**
	 * Sets the internal account owner
	 * @param owner
	 */
	protected void setOwner(UUID owner) {
		if (owner != null)
			this.owner = owner;
	}

	/**
	 * Sets the accounts' balance
	 * @param balance
	 * @throws Exception 
	 */
	protected void setBalance(double balance) {
		try {
			
			DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
			df.applyPattern("0.00");
			
			this.balance = Double.valueOf(df.format(balance));
			
			if (getLowestBalance() == 0)
				updateLowestBalance(this.balance);
			else if (balance < getLowestBalance())
				updateLowestBalance(this.balance);
			else if (balance > getHighestBalance())
				updateHighestBalance(this.balance);
			
			updatePayday();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the lowest amount of money the player ever had
	 * @param lowestBalance
	 */
	protected void setLowestBalance(double lowestBalance) {
		this.lowestBalance = lowestBalance;
	}
	
	/**
	 * Sets the highest amount of money the player ever had
	 * @param highestBalance
	 */
	protected void setHighestBalance(double highestBalance) {
		this.highestBalance = highestBalance;
	}

	/**
	 * Sets the credit
	 * @param credit
	 */
	protected void setCredit(double credit) {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		df.applyPattern("0.00");
		
		this.credit = Double.valueOf(df.format(credit));
	}
	
	/**
	 * Sets the amount of paid credit
	 * @param paidCredit
	 */
	protected void setPaidCredit(double paidCredit) {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		df.applyPattern("0.00");
		
		this.paidCredit = Double.valueOf(df.format(paidCredit));
	}
	
	/**
	 * Sets the payday
	 * @param payday
	 */
	protected void setPayday(double payday) {
		if (payday >= 0)
			this.payday = payday;
	}
	
	/**
	 * (Un-)locks the account
	 * @param closed
	 */
	protected void setClosedStatus(boolean closed) {
		this.closed = closed;
	}
	
	/**
	 * Sets the reason why the account is locked
	 * @param reason
	 */
	protected void setReason(String reason) {
		if (reason != null)
			this.reason = reason;
	}
	
	@Override
	public int hashCode() {
		return (int) getUser().getUUID().hashCode() * getUser().getName().hashCode() * getReason().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final BankAccount another = (BankAccount) obj;
		
		if (getUser().equals(another.getUser())) {
			if (getBalance() == another.getBalance()) {
				if (getReason().equals(another.getReason())) {
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "Account {" + getUser().getUUID() + ";" + getId() + ";" + getBalance() + ";" + isClosed();
	}
}
