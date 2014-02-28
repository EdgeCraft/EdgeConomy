package net.edgecraft.edgeconomy;

import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.transactions.TransactionManager;

public class EdgeConomyAPI {
	
	private static final Economy economyAPI = EdgeConomy.getEconomy();
	private static final TransactionManager transactionAPI = EdgeConomy.getTransactions();
	
	private EdgeConomyAPI() { }
	
	/**
	 * Returns the EconomyAPI
	 * @return Economy
	 */
	public static final Economy economyAPI() {
		return economyAPI;
	}
	
	/**
	 * Returns the TransactionAPI
	 * @return TransactionManager
	 */
	public static final TransactionManager transactionAPI() {
		return transactionAPI;
	}
}
