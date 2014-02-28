package net.edgecraft.edgeconomy.economy;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;

public class Economy {
	
	public final static String accountTable = "edgeconomy_accounts";
	public final static String ecoPlayerTable = "edgeconomy_eplayer";
	
	public static Map<Integer, BankAccount> accounts = new LinkedHashMap<>();
	public static Map<Integer, EconomyPlayer> ePlayer = new LinkedHashMap<>();
	
	private static String state;
	private static String currency;
	
	private static int maxCashDistance;
	private static int maxATMDistance;
	private static int monitoredAmount;
	
	private static double defaultCash;
	private static double defaultWelfare;
	private static double maxWelfareBalance;
	private static double transferFee;
	private static double withdrawalFee;
	private static double creditFee;
	private static double paydayBonus;
	
	private static boolean allowAccounts;
	private static boolean autoCreateAccounts;
	
	private static final Economy instance = new Economy();
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected Economy() { /* ... */ }
	
	public static Economy getInstance() {
		return instance;
	}
	
	public Map<Integer, BankAccount> getAccounts() {
		return Economy.accounts;
	}
	
	public int amountOfAccounts() {
		return Economy.accounts.size();
	}
	
	public Map<Integer, EconomyPlayer> getEconomyPlayer() {
		return Economy.ePlayer;
	}
	
	public int amountOfEconomyPlayer() {
		return Economy.ePlayer.size();
	}
	
	public void registerAccount(int ownerID, double  balance, double credit) {
		try {
			
			if (hasAccount(ownerID)) return;
			
			int id = generateAccountID();
			
			PreparedStatement registerAccount = db.prepareUpdate("INSERT INTO " + Economy.accountTable + " (id, ownerID, balance, lowestbalance, highestbalance, credit, paidcredit, closed, reason, payday) "
					+ "VALUES (?, ?, ?, '0', '0', ?, '0', DEFAULT, DEFAULT, '0');");
			
			registerAccount.setInt(1, id);
			registerAccount.setInt(2, ownerID);
			registerAccount.setDouble(3, balance);
			registerAccount.setDouble(4, credit);
			registerAccount.executeUpdate();
			
			synchronizeAccount(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerSessionAccount(BankAccount tempAcc) {
		if (tempAcc != null)
			Economy.accounts.put(tempAcc.getID(), tempAcc);
	}
	
	public void registerEconomyPlayer(String name) {
		try {
			
			if (existsEconomyPlayer(name)) return;
			
			int id = generateEconomyPlayerID();
			
			PreparedStatement registerEP = db.prepareUpdate("INSERT INTO " + Economy.ecoPlayerTable + " (id, cash, totalgiven, totalreceived, totaldonated, welfare) VALUES (?, ?, '0', '0', '0', DEFAULT);");
			registerEP.setInt(1, id);
			registerEP.setDouble(2, getDefaultCash());
			registerEP.executeUpdate();
			
			synchronizeEconomyPlayer(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerSessionEconomyPlayer(EconomyPlayer tempEcoPlayer) {
		if (tempEcoPlayer != null)
			Economy.ePlayer.put(tempEcoPlayer.getID(), tempEcoPlayer);
	}
	
	public void deleteAccount(int id) {
		if (id <= 0) return;
		
		try {
			
			PreparedStatement deleteAccount = db.prepareUpdate("DELETE FROM " + Economy.accountTable + " WHERE id = '" + id + "';");
			deleteAccount.executeUpdate();
			
			Economy.accounts.remove(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteEconomyPlayer(int id) {
		if (id <= 0) return;
		
		try {
			
			PreparedStatement deleteEP = db.prepareUpdate("DELETE FROM " + Economy.ecoPlayerTable + " WHERE id = '" + id + "';");
			deleteEP.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int generateAccountID() throws Exception {
		if (amountOfAccounts() <= 0) return 1;
		
		return greatestID(Economy.accountTable) + 1;
	}
	
	public int generateEconomyPlayerID() throws Exception {
		if (amountOfEconomyPlayer() <= 0) return 1;
		
		return greatestID(Economy.ecoPlayerTable) + 1;
	}
	
	public int greatestID(String table) throws Exception {
		List<Map<String, Object>> tempVar = db.getResults("SELECT COUNT(id) AS amount FROM " + table);
		int tempID = Integer.parseInt(String.valueOf(tempVar.get(0).get("amount")));
		
		if (tempID <= 0) return 1;

		return tempID;
	}
	
	public boolean existsAccount(int id) {
		return Economy.accounts.containsKey(id);
	}
	
	public boolean existsAccount(BankAccount acc) {
		if (acc != null)
			return existsAccount(acc.getID());
		
		return false;
	}
	
	public boolean hasAccount(int ownerID) {
		for (BankAccount acc : Economy.accounts.values()) {
			if (acc.getOwnerID() == ownerID) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean existsEconomyPlayer(int id) {
		return Economy.ePlayer.containsKey(id);
	}
	
	public boolean existsEconomyPlayer(EconomyPlayer eco) {
		if (eco != null)
			return existsEconomyPlayer(eco.getID());
		
		return false;
	}
	
	public boolean existsEconomyPlayer(String name) {
		for (EconomyPlayer eco : Economy.ePlayer.values()) {
			if (eco.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public BankAccount getAccount(int id) {
		return Economy.accounts.get(id);
	}
	
	public BankAccount getAccount(String owner) {
		for (BankAccount acc : Economy.accounts.values()) {
			if (acc.getOwner().equals(owner)) {
				return acc;
			}
		}
		
		return null;
	}
	
	public BankAccount getAccountByOwnerID(int ownerID) {
		for (BankAccount acc : Economy.accounts.values()) {
			if (acc.getOwnerID() == ownerID) {
				return acc;
			}
		}
		
		return null;
	}
	
	public EconomyPlayer getEconomyPlayer(int id) {
		return Economy.ePlayer.get(id);
	}
	
	public EconomyPlayer getEconomyPlayer(String name) {
		for (EconomyPlayer eco : Economy.ePlayer.values()) {
			if (eco.getName().equals(name)) {
				return eco;
			}
		}
		
		return null;
	}
	
	public void synchronizeEconomy(boolean accounts, boolean ePlayer) {
		try {
			
			if (accounts) {
				for (int i = 1; i <= greatestID(Economy.accountTable); i++) {
					synchronizeAccount(i);
				}
			}
			
			if (ePlayer) {
				for (int i = 1; i <= greatestID(Economy.ecoPlayerTable); i++) {
					synchronizeEconomyPlayer(i);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void synchronizeAccount(int id) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + Economy.accountTable + " WHERE id = '" + id + "';");
			
			for (int i = 0; i < results.size(); i++) {
				
				BankAccount acc = new BankAccount();
				
				for (Map.Entry<String, Object> entry : results.get(i).entrySet()) {
					
					if (entry.getKey().equals("id")) {
						acc.setID(Integer.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("ownerID")) {
						acc.setOwnerID(Integer.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("balance")) {
						acc.setBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("lowestbalance")) {
						acc.setLowestBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("highestbalance")) {
						acc.setHighestBalance(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("credit")) {
						acc.setCredit(Double.valueOf(entry.getValue().toString()));
						
					} else if(entry.getKey().equals("closed")) {
						boolean b = ((Boolean) entry.getValue()).booleanValue();
						acc.setClosedStatus(b);
						
					} else if(entry.getKey().equals("reason")) {
						acc.setReason(entry.getValue().toString());
						
					} else if(entry.getKey().equals("payday")) {
						acc.setPayday(Double.valueOf(entry.getValue().toString()));
						
					}
				}
				
				Economy.accounts.put(acc.getID(), acc);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void synchronizeEconomyPlayer(int id) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + Economy.ecoPlayerTable + " WHERE id = '" + id + "';");
			
			for (int i = 0; i < results.size(); i++) {
				
				EconomyPlayer eco = new EconomyPlayer();
				
				for (Map.Entry<String, Object> entry : results.get(i).entrySet()) {
					
					if (entry.getKey().equals("id")) {
						eco.setID(Integer.valueOf(entry.getValue().toString()));
						
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
				
				Economy.ePlayer.put(eco.getID(), eco);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getState() {
		return state;
	}

	public static String getCurrency() {
		return currency;
	}

	public static int getMaxCashDistance() {
		return maxCashDistance;
	}

	public static int getMaxATMDistance() {
		return maxATMDistance;
	}

	public static double getDefaultCash() {
		return defaultCash;
	}

	public static double getDefaultWelfare() {
		return defaultWelfare;
	}

	public static double getMaxWelfareBalance() {
		return maxWelfareBalance;
	}

	public static double getTransferFee() {
		return transferFee;
	}

	public static double getWithdrawalFee() {
		return withdrawalFee;
	}

	public static double getCreditFee() {
		return creditFee;
	}

	public static double getPaydayBonus() {
		return paydayBonus;
	}

	public static boolean isAllowAccounts() {
		return allowAccounts;
	}

	public static boolean isAutoCreateAccounts() {
		return autoCreateAccounts;
	}

	public static void setePlayer(Map<Integer, EconomyPlayer> ePlayer) {
		Economy.ePlayer = ePlayer;
	}

	public static void setState(String state) {
		Economy.state = state;
	}

	public static void setCurrency(String currency) {
		Economy.currency = currency;
	}

	public static void setMaxCashDistance(int maxCashDistance) {
		Economy.maxCashDistance = maxCashDistance;
	}

	public static void setMaxATMDistance(int maxATMDistance) {
		Economy.maxATMDistance = maxATMDistance;
	}

	public static void setDefaultCash(double defaultCash) {
		Economy.defaultCash = defaultCash;
	}

	public static void setDefaultWelfare(double defaultWelfare) {
		Economy.defaultWelfare = defaultWelfare;
	}

	public static void setMaxWelfareBalance(double maxWelfareBalance) {
		Economy.maxWelfareBalance = maxWelfareBalance;
	}

	public static void setTransferFee(double transferFee) {
		Economy.transferFee = transferFee;
	}

	public static void setWithdrawalFee(double withdrawalFee) {
		Economy.withdrawalFee = withdrawalFee;
	}

	public static void setCreditFee(double creditFee) {
		Economy.creditFee = creditFee;
	}

	public static void setPaydayBonus(double paydayBonus) {
		Economy.paydayBonus = paydayBonus;
	}

	public static void setAllowAccounts(boolean allowAccounts) {
		Economy.allowAccounts = allowAccounts;
	}

	public static void setAutoCreateAccounts(boolean autoCreateAccounts) {
		Economy.autoCreateAccounts = autoCreateAccounts;
	}
	
	public static int getMonitoredAmount() {
		return monitoredAmount;
	}

	public static void setMonitoredAmount(int monitoredAmount) {
		Economy.monitoredAmount = monitoredAmount;
	}
	
}
