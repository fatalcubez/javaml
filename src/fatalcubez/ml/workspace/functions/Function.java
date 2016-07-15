package fatalcubez.ml.workspace.functions;


public enum Function {

	EYE("eye"){
		@Override
		public IFunction getFunctionInstance() {
			return new EyeFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	ZEROS("zeros") {
		@Override
		public IFunction getFunctionInstance() {
			return new ZerosFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	ONES("ones"){
		@Override
		public IFunction getFunctionInstance() {
			return new OnesFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	SUM("sum"){
		@Override
		public IFunction getFunctionInstance() {
			return new SumFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	MEAN("mean"){
		@Override
		public IFunction getFunctionInstance() {
			return new MeanFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	SIZE("size"){
		@Override
		public IFunction getFunctionInstance() {
			return new SizeFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 2;
		}
	},
	SIN("sin"){
		@Override
		public IFunction getFunctionInstance() {
			return new SinFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	COS("cos"){
		@Override
		public IFunction getFunctionInstance() {
			return new CosFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	TAN("tan"){
		@Override
		public IFunction getFunctionInstance() {
			return new TanFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	EXP("exp"){
		@Override
		public IFunction getFunctionInstance() {
			return new ExpFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	},
	LOG("log"){
		@Override
		public IFunction getFunctionInstance() {
			return new LogFunction();
		}

		@Override
		public int getMaxOutputs() {
			return 1;
		}
	};
	
	private String consoleName;
	
	private Function(String consoleName){
		this.consoleName = consoleName;
	}
	
	public String getConsoleName(){
		return consoleName;
	}
	
	public static Function getFunction(String functionName){
		for(Function f : Function.values()){
			if(functionName.equals(f.getConsoleName())) return f;
		}
		return null;
	}
	
	public abstract IFunction getFunctionInstance();
	public abstract int getMaxOutputs();

}
