package fatalcubez.ml.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import fatalcubez.ml.workspace.ExpressionValue;

public class SaveData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5682477275704692248L;
	private transient HashMap<String, ExpressionValue> saveData;
	
	public SaveData(HashMap<String, ExpressionValue> saveData){
		if(saveData.size() == 0) throw new IllegalArgumentException("SaveData must have a size greater than 0");
		this.saveData.putAll(saveData);
	}

	private void writeObject(ObjectOutputStream oos){
		try {
			oos.defaultWriteObject();
			oos.writeInt(saveData.size());
			for(Entry<String, ExpressionValue> e : saveData.entrySet()){
				String key = e.getKey();
				ExpressionValue value = e.getValue();
				oos.writeObject(key);
				oos.writeObject(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois){
		try {
			ois.defaultReadObject();
			int size = ois.readInt();
			for(int i = 0; i < size; i+=2){
				String key = (String)ois.readObject();
				ExpressionValue value = (ExpressionValue)ois.readObject();
				saveData.put(key, value);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(String key, ExpressionValue value){
		saveData.put(key, value);
	}
	
	public void remove(String key){
		if(saveData.size() == 1 && saveData.containsKey(key)) throw new IllegalArgumentException("Size of SaveData must be greater than 0");
		saveData.remove(key);
	}
	
	public HashMap<String, ExpressionValue> getSaveData(){
		return saveData;
	}
}
