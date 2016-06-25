package fatalcubez.ml.main;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import fatalcubez.ml.workspace.MatrixValue;



public class Main {
	
	public static void main(String[] args){
//		Workspace work = new Workspace(System.in);
		RealMatrix mat = new Array2DRowRealMatrix(new double[][]{{2,3,4},{1,2,3}});
		MatrixValue mV = new MatrixValue(mat);
		System.out.println(mV.getMatrix().getEntry(0, 0));
		mV.getMatrix().setEntry(0, 0, 55.0d);
		System.out.println(mV.getMatrix().getEntry(0, 0));
	}
}
