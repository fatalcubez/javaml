package fatalcubez.ml.workspace.operations;


public enum Operation {
	
	ADD("+") {
		@Override
		public IOperation getOperationInstance() {
			return new AddOperation();
		}
	},
	SUBTRACT("-") {
		@Override
		public IOperation getOperationInstance() {
			return new SubtractOperation();
		}
	},
	MULTIPLY("*") {
		@Override
		public IOperation getOperationInstance() {
			return new MultiplyOperation();
		}
	},
	DIVIDE("/") {
		@Override
		public IOperation getOperationInstance() {
			return new DivideOperation();
		}
	},
	POWER("^"){
		@Override
		public IOperation getOperationInstance() {
			return new PowerOperation();
		}
	};
	
	private String consoleName;
	
	private Operation(String consoleName){
		this.consoleName = consoleName;
	}
	
	public String getConsoleName(){
		return consoleName;
	}

	public static Operation getOperation(String operation){
		for(Operation o : Operation.values()){
			if(operation.equals(o.getConsoleName())) return o;
		}
		return null;
	}
	
	public abstract IOperation getOperationInstance();
	
}
