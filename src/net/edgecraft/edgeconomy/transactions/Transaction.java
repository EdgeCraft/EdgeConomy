package net.edgecraft.edgeconomy.transactions;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;

public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private BankAccount transactor;
	private BankAccount receiver;
	private double amount;
	private String description;
	private Date date;
	
	protected Transaction() { }
	
	protected Transaction(int id, BankAccount transactor, BankAccount receiver, double amount, String description, Date date) {
		setID(id);
		setTransactor(transactor);
		setReceiver(receiver);
		setAmount(amount);
		setDescription(description);
		setDate(date);
		
		manageTransaction();
	}
	
	private void manageTransaction() {
		try {
			
			double transactorCalculation = transactor.getBalance() - ((transactor.getBalance() / 100 * Economy.getTransferFee()) + getAmount());
			
			transactor.updateBalance(transactorCalculation);
			receiver.updateBalance(receiver.getBalance() + getAmount());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getGist() {
		return EdgeCoreAPI.languageAPI().getColoredMessage(LanguageHandler.getDefaultLanguage(), "transaction_gist")
										.replace("[0]", getDateAsString()).replace("[1]", getID() + "")
										.replace("[2]", getTransactor().getId() + "").replace("[3]", getReceiver().getId() + "")
										.replace("[4]", getDescription()).replace("[5]", getAmount() + "");
	}

	public int getID() {
		return id;
	}

	public BankAccount getTransactor() {
		return transactor;
	}

	public BankAccount getReceiver() {
		return receiver;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

	protected void setID(int id) {
		if (id >= 0)
			this.id = id;
	}

	protected void setTransactor(BankAccount transactor) {
		if (transactor != null)
			this.transactor = transactor;
	}

	protected void setReceiver(BankAccount receiver) {
		if (receiver != null)
			this.receiver = receiver;
	}

	protected void setAmount(double amount) {
		if (amount > 0)
			this.amount = amount;
	}

	protected void setDescription(String description) {
		if (description != null)
			this.description = description;
	}

	protected void setDate(Date date) {
		if (date != null)
			this.date = date;
	}
	
	private String getDateAsString() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(getDate());
	}
	
	@Override
	public int hashCode() {
		return (int) getTransactor().hashCode() * getReceiver().hashCode() * getDescription().hashCode() * getDate().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final Transaction another = (Transaction) obj;
		
		if (getTransactor().equals(another.getTransactor())) {
			if (getReceiver().equals(another.getReceiver())) {
				if (getDescription().equals(another.getDescription())) {
					return true;
				}
			}
		}
		
		return false;
	}
}
