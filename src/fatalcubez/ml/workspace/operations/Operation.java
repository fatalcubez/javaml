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
			return new EqualOperation();
		}
	},
	NOT_EQUAL("~="){
		@Override
		public IOperation getOperationInstance() {
			return new NotEqualOperation();
		}
	},
	GREATER_THAN(">"){
		@Override
		public IOperation getOperationInstance() {
			return new GreaterThanOperation();
		}
	},GREATER_THAN_EQUAL(">="){
		@Override
		public IOperation getOperationInstance() {
			return new GreaterThanEqualOperation();
		}
	},LESS_THAN("<"){
		@Override
		public IOperation getOperationInstance() {
			return new LessThanOperation();
		}
	},LESS_THAN_EQUAL("<="){
		@Override
		public IOperation getOperationInstance() {
			return new LessThanEqualOperation();
		}
	},
	AND("&"){
		@Override
		public IOperation getOperationInstance() {
			return new AndOperation();
		}
	},
	OR("|"){
		@Override
		public IOperation getOperationInstance() {
			return new OrOperation();
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
