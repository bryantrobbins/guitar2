package edu.umd.cs.guitar.testdata.berkeleylm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import edu.berkeley.nlp.lm.WordIndexer;

public class ArpaObject {
	private List<String> lines;
	private int maxOrder;
	
	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	
	public void addLine(String line){
		if (lines == null){
			lines = new ArrayList<String>();
		}
		
		lines.add(line);
	}
	
	public String getLine(int lineNum){
		String text = null;
		
		try{
			text = lines.get(lineNum); 
		} catch(IndexOutOfBoundsException oob){
			// Let it be null!
			text = null;
		}

		return text;
	}
	
	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public void toArpa(PrintWriter pw){
		for(String ll : this.lines){
			pw.println(ll);
		}
	}
	
	public static ArpaObject fromJson(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, ArpaObject.class);
	}

	public int getMaxOrder() {
		return maxOrder;
	}

	public void setMaxOrder(int maxOrder) {
		this.maxOrder = maxOrder;
	}
}
