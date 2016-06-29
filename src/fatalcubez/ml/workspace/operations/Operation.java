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
	},
	EQUAL("=="){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},
	NOT_EQUAL("~="){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},
	GREATER_THAN(">"){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},GREATER_THAN_EQUAL(">="){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},LESS_THAN("<"){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},LESS_THAN_EQUAL("<="){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},
	AND("&"){
		@Override
		public IOperation getOperationInstance() {
			return null;
		}
	},
	OR("|"){
		@Override
		public IOperation getOperationInstance() {
			return null;
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
