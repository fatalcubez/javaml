package fatalcubez.ml.workspace.commands;

public enum Command {

	LOAD("load"){
		
	},
	SAVE("save"){
		
	};
	
	private String consoleName;
	
	private Command(String consoleName){
		this.consoleName = consoleName;
	}
	
	public String getConsoleName(){
		return consoleName;
	}
	
	public static Command getCommand(String commandName){
		for(Command c : Command.values()){
			if(commandName.equals(c.getConsoleName())) return c;
		}
		return null;
	}
	
	
}
