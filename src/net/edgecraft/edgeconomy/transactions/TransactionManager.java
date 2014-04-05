package net.edgecraft.edgeconomy.transactions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.edgecraft.edgeconomy.economy.BankAccount;

public class TransactionManager {
	
	private Map<Integer, Transaction> transactions = new LinkedHashMap<>();
	public static int expireAfterDays = 14;
	
	private static final TransactionManager instance = new TransactionManager();
	
	protected TransactionManager() {  }
	
	public static final TransactionManager getInstance() {
		return instance;
	}
	
	public Map<Integer, Transaction> getTransactions() {
		return transactions;
	}
	
	public void addTransaction(BankAccount transactor, BankAccount receiver, double amount, String description) {
		if (transactor == null || receiver == null || amount <= 0 || description == null) return;
		
		addTransaction(new Transaction(generateID(), transactor, receiver, amount, description, Calendar.getInstance().getTime()));
	}
	
	protected void addTransaction(Transaction transaction) {
		if (transaction != null)
			transactions.put(transaction.getID(), transaction);
	}
	
	public void removeTransaction(int id) {
		if (id <= 0) return;
		transactions.remove(id);
	}
	
	public int amountOfTransactions() {
		return transactions.size();
	}
	
	public int generateID() {
		return amountOfTransactions() + 1;
	}
	
	public Transaction getTransaction(int id) {
		return transactions.get(id);
	}
	
	public Transaction getTransaction(Transaction transaction) {
		
		for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()) {
			Transaction searchFor = entry.getValue();
			
			if (searchFor.equals(transaction)) 
				return searchFor;
		}
		
		return null;
	}
	
	public Transaction getTransaction(Date date) {
		
		for (Transaction transaction : getTransactions().values()) {
			if (transaction.getDate().equals(date))
				return transaction;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void syncTransactions() {
		
		File tempFile = new File("plugins/EdgeConomy/transactions.tmp");
		
		try {
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));			
			out.writeObject(transactions);
			out.close();
			
			transactions.clear();
			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));			
			transactions = (HashMap<Integer, Transaction>) in.readObject();			
			in.close();
			
			Date now = Calendar.getInstance().getTime();
			int days = 0;
			
			for (Transaction transaction : transactions.values()) {
				while(transaction.getDate().before(now)) {
					days++;
				}
				
				if (days >= TransactionManager.expireAfterDays)
					removeTransaction(transaction.getID());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists(int id) {
		return transactions.containsKey(id);
	}
	
	public boolean exists(Transaction transaction) {
		return transactions.containsValue(transaction);
	}
}
