package net.edgecraft.edgeconomy.economy;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;

public class Economy {
	
	public static final String accountTable = "edgeconomy_accounts";
	public static final String ecoPlayerTable = "edgeconomy_eplayer";
	
	// public static final Map<UUID, List<BankAccount>> accounts = new LinkedHashMap<>(); - // Multiple Accounts?
	public static Map<Integer, BankAccount> accounts = new LinkedHashMap<>();
	public static Map<UUID, EconomyPlayer> ePlayers = new LinkedHashMap<>();
	
	private static String state;
	private static String currency;
	
	private static int paydayInterval;
	private static int cashRadius;
	
	private static double maxATMAmount;
	private static double monitoredAmount;
	private static double defaultCashAmount;
	private static double welfareAmount;
	private static double maxWelfareAmount;
	
	private static double transferFee;
	private static double withdrawalFee;
	private static double creditFee;
	private static double paydayBonus;
	private static double stateTax;
	
	private static boolean allowAccounts;
	private static boolean autoCreateAccounts;
	
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	private static final Economy instance = new Economy();
	
	protected Economy() { /* ... */ }
	
	public static final Economy getInstance() {
		return instance;
	}
	
	public final void checkDatabase() {
		try {
			
			db.prepareStatement("CREATE TABLE IF NOT EXISTS " + Economy.accountTable + " (id INTEGER AUTO_INCREMENT, "
							+ "uuid VARCHAR(36) NOT NULL, "
							+ "balance DOUBLE NOT NULL, "
							+ "lowestbalance DOUBLE NOT NULL, "
							+ "highestbalance DOUBLE NOT NULL, "
							+ "credit DOUBLE NOT NULL, "
							+ "paidcredit DOUBLE NOT NULL, "
							+ "closed BOOLEAN NOT NULL DEFAULT 0, "
							+ "reason TEXT NOT NULL, PRIMARY KEY(id));").executeUpdate();
			
			db.prepareStatement("CREATE TABLE IF NOT EXISTS " + Economy.ecoPlayerTable + " (uuid VARCHAR(36) NOT NULL, "
							+ "cash DOUBLE NOT NULL, "
							+ "totalgiven DOUBLE NOT NULL, "
							+ "totalreceived DOUBLE NOT NULL, "
							+ "totaldonated DOUBLE NOT NULL, "
							+ "welfare BOOLEAN NOT NULL DEFAULT 0, PRIMARY KEY(uuid));").executeUpdate();			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<Integer, BankAccount> getAccounts() {
		return accounts;
	}
	
	public int amountOfAccounts() {
		return accounts.size();
	}
	
	public Map<UUID, EconomyPlayer> getEconomyPlayers() {
		return ePlayers;
	}
	
	public int amountOfEconomyPlayers() {
		return ePlayers.size();
	}
	
	public void registerAccount(UUID uuid, double balance, double credit) {
		try {
			
			if (hasAccount(uuid))
				return;
			
			PreparedStatement registerAcc = db.prepareStatement("INSERT INTO " + Economy.accountTable + " (uuid, balance, lowestbalance, highestbalance, credit, paidcredit, closed, reason) "
					+ "VALUES (?, ?, '0', '0', ?, '0', DEFAULT, '');");
			
			registerAcc.setString(1, uuid.toString());
			registerAcc.setDouble(2, balance);
			registerAcc.setDouble(3, credit);
			registerAcc.executeUpdate();
			
			synchronizeAccount(getGreatestId(Economy.accountTable, "id"));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerEconomyPlayer(UUID uuid) {
		try {
			
			if (existsEconomyPlayer(uuid))
				return;
			
			PreparedStatement registerEcoPlayer = db.prepareStatement("INSERT INTO " + Economy.ecoPlayerTable + " (uuid, cash, totalgiven, totalreceived, totaldonated, welfare) "
					+ "VALUES (?, ?, '0', '0', '0', DEFAULT);");
			
			registerEcoPlayer.setString(1, uuid.toString());
			registerEcoPlayer.setDouble(2, getDefaultCashAmount());
			registerEcoPlayer.executeUpdate();
			
			synchronizeEconomyPlayer(uuid);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAccount(int account) {
		if (account <= 0)
			return;
		
		try {
			
			db.prepareStatement("DELETE FROM " + Economy.accountTable + " WHERE id = '" + account + "';").executeUpdate();
			getAccounts().remove(account);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteEconomyPlayer(UUID uuid) {
		if (uuid == null)
			return;
		
		try {
			
			db.prepareStatement("DELETE FROM " + Economy.ecoPlayerTable + " WHERE uuid = '" + uuid.toString() + "';").executeUpdate();
			getEconomyPlayers().remove(uuid);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteEconomyPlayer(String uuid) {
		if (uuid == null)
			return;
		
		deleteEconomyPlayer(UUID.fromString(uuid));
	}
	
	public final boolean insideBankCuboid(Player player) {
		Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
		
		if (cuboid == null)
			return false;
		
		if (cuboid.getCuboidType() == CuboidType.Bank.getTypeID())
			return true;
		else
			return false;
	}
	
	public final boolean insideATMCuboid(Player player) {
		Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
		
		if (cuboid == null)
			return false;
		
		if (cuboid.getCuboidType() == CuboidType.ATM.getTypeID())
			return true;
		else
			return false;
	}
	
	public boolean hasAccount(User user) {
		for (BankAccount acc : getAccounts().values()) {
			if (acc.getUser().equals(user))
				return true;
		}
		
		return false;
	}
	
	public boolean hasAccount(UUID owner) {
		return hasAccount(EdgeCoreAPI.userAPI().getUser(owner));
	}
	
	public boolean existsAccount(int id) {
		return getAccounts().containsKey(id);
	}
	
	public boolean existsAccount(BankAccount acc) {
		return getAccounts().containsValue(acc);
	}
	
	public BankAccount getAccount(int id) {
		return existsAccount(id) ? getAccounts().get(id) : null;
	}
	
	public BankAccount getAccount(User user) {
		for (BankAccount acc : getAccounts().values()) {
			if (acc.getUser().equals(user))
				return acc;
		}
		
		return null;
	}
	
	public BankAccount getAccount(String user) {
		for (BankAccount acc : getAccounts().values()) {
			if (acc.getUser().getName().equals(user))
				return acc;
		}
		
		return null;
	}
	
	public BankAccount getAccount(UUID owner) {
		for (BankAccount acc : getAccounts().values()) {
			if (acc.getOwner().equals(owner))
				return acc;
		}
		
		return null;
	}
	
	public boolean existsEconomyPlayer(UUID uuid) {
		return getEconomyPlayers().containsKey(uuid);
	}
	
	public boolean existsEconomyPlayer(String name) {
		for (EconomyPlayer eco : getEconomyPlayers().values()) {
			if (EdgeCoreAPI.userAPI().getUser(eco.getUser()).getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public EconomyPlayer getEconomyPlayer(UUID uuid) {
		return getEconomyPlayers().get(uuid);
	}
	
	public EconomyPlayer getEconomyPlayer(String name) {
		for (EconomyPlayer eco : getEconomyPlayers().values()) {
			if (EdgeCoreAPI.userAPI().getUser(eco.getUser()).getName().equals(name))
				return eco;
		}
		
		return null;
	}
	
	public EconomyPlayer getEconomyPlayer(BankAccount acc) {
		return acc.getEconomyPlayer();
	}
	
	public int getGreatestId(String table, String column) throws Exception {
		List<Map<String, Object>> tempVar = db.getResults("SELECT COUNT(" + column + ") AS amount FROM " + table + ";");
		int tempID = Integer.parseInt(String.valueOf(tempVar.get(0).get("amount")));
		
		if (tempID <= 0) return 1;
		
		return tempID;
	}
	
	public void synchronizeEconomy(boolean accounts, boolean ecoplayers) {
		try {
			
			if (accounts) {
				for (int i = 1; i <= getGreatestId(Economy.accountTable, "id"); i++)
					synchronizeAccount(i);
			}
			
			if (ecoplayers) {
				for (int i = 1; i <= getGreatestId(Economy.ecoPlayerTable, "uuid"); i++)
					for (User user : EdgeCoreAPI.userAPI().getUsers().values())
						synchronizeEconomyPlayer(user.getUUID());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void synchronizeAccount(int id) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + Economy.accountTable + " WHERE id = '" + id + "';");
			
			for (int i = 0; i < results.size(); i++) {
				
				BankAccount acc = new BankAccount();
				
				for (Map.Entry<String, Object> entry : results.get(i).entrySet()) {
					
					if (entry.getKey().equals("id")) {
						acc.setId(Integer.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("uuid")) {
						acc.setOwner(UUID.fromString(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("balance")) {
						acc.setBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("lowestbalance")) {
						acc.setLowestBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("highestbalance")) {
						acc.setHighestBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("credit")) {
						acc.setCredit(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("paidcredit")) {
						acc.setPaidCredit(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("closed")) {
						boolean b = ((Boolean) entry.getValue()).booleanValue();
						acc.setClosedStatus(b);
						
					} else if(entry.getKey().equals("reason")) {
						acc.setReason(entry.getValue().toString());
						
					}
				}
				
				acc.updatePayday();
				getAccounts().put(acc.getId(), acc);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void synchronizeEconomyPlayer(UUID uuid) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + Economy.ecoPlayerTable + " WHERE uuid = '" + uuid.toString() + "';");
			
			for (int i = 0; i < results.size(); i++) {
				
				EconomyPlayer eco = new EconomyPlayer();
				
				for (Map.Entry<String, Object> entry : results.get(i).entrySet()) {
					
					if (entry.getKey().equals("uuid")) {
						eco.setUser(UUID.fromString(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("cash")) {
						eco.setCash(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("totalgiven")) {
						eco.setTotalGiven(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("totalreceived")) {
						eco.setTotalReceived(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("totaldonated")) {
						eco.setTotalDonated(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("welfare")) {
						boolean b = ((Boolean) entry.getValue()).booleanValue();
						eco.setWelfareStatus(b);
						
					}
				}
				
				Economy.ePlayers.put(eco.getUser(), eco);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getState() {
		return state;
	}

	public static void setState(String state) {
		Economy.state = state;
	}

	public static String getCurrency() {
		return currency;
	}

	public static void setCurrency(String currency) {
		Economy.currency = currency;
	}

	public static int getPaydayInterval() {
		return paydayInterval;
	}

	public static void setPaydayInterval(int paydayInterval) {
		Economy.paydayInterval = paydayInterval;
	}

	public static int getCashRadius() {
		return cashRadius;
	}

	public static void setCashRadius(int cashRadius) {
		Economy.cashRadius = cashRadius;
	}

	public static double getMaxATMAmount() {
		return maxATMAmount;
	}

	public static void setMaxATMAmount(double maxATMAmount) {
		Economy.maxATMAmount = maxATMAmount;
	}

	public static double getMonitoredAmount() {
		return monitoredAmount;
	}

	public static void setMonitoredAmount(double monitoredAmount) {
		Economy.monitoredAmount = monitoredAmount;
	}

	public static double getDefaultCashAmount() {
		return defaultCashAmount;
	}

	public static void setDefaultCashAmount(double defaultCashAmount) {
		Economy.defaultCashAmount = defaultCashAmount;
	}

	public static double getWelfareAmount() {
		return welfareAmount;
	}

	public static void setWelfareAmount(double welfareAmount) {
		Economy.welfareAmount = welfareAmount;
	}

	public static double getMaxWelfareAmount() {
		return maxWelfareAmount;
	}

	public static void setMaxWelfareAmount(double maxWelfareAmount) {
		Economy.maxWelfareAmount = maxWelfareAmount;
	}

	public static double getTransferFee() {
		return transferFee;
	}

	public static void setTransferFee(double transferFee) {
		Economy.transferFee = transferFee;
	}

	public static double getWithdrawalFee() {
		return withdrawalFee;
	}

	public static void setWithdrawalFee(double withdrawalFee) {
		Economy.withdrawalFee = withdrawalFee;
	}

	public static double getCreditFee() {
		return creditFee;
	}

	public static void setCreditFee(double creditFee) {
		Economy.creditFee = creditFee;
	}

	public static double getPaydayBonus() {
		return paydayBonus;
	}

	public static void setPaydayBonus(double paydayBonus) {
		Economy.paydayBonus = paydayBonus;
	}

	public static double getStateTax() {
		return stateTax;
	}

	public static void setStateTax(double stateTax) {
		Economy.stateTax = stateTax;
	}

	public static boolean isAllowAccounts() {
		return allowAccounts;
	}

	public static void setAllowAccounts(boolean allowAccounts) {
		Economy.allowAccounts = allowAccounts;
	}

	public static boolean isAutoCreateAccounts() {
		return autoCreateAccounts;
	}

	public static void setAutoCreateAccounts(boolean autoCreateAccounts) {
		Economy.autoCreateAccounts = autoCreateAccounts;
	}
	
}