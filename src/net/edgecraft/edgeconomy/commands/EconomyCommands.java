package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgecore.command.CommandHandler;

public class EconomyCommands extends CommandHandler {

	private static final EconomyCommands instance = new EconomyCommands();
	
	private EconomyCommands() {
		
		super.registerCommand( AccountCommand.getInstance() );
		super.registerCommand( CashCommand.getInstance() );
		super.registerCommand( CreditCommand.getInstance() );
		super.registerCommand( TransferCommand.getInstance() );
		super.registerCommand( WelfareCommand.getInstance() );
		super.registerCommand( PaydayCommand.getInstance() );
		
	}
	
	public static final EconomyCommands getInstance() {
		return instance;
	}
	
}
