package searcher.model;

import java.util.HashMap;
import java.util.Map;

public class Document {

	private String documentId;	
	private Map<String, Integer> documentIndex;
	private double vectorLenght;
	
	public Document(){
		this.documentIndex = new HashMap<String, Integer>();
	}
	
	
	
	public double getVectorLenght() {
		return vectorLenght;
	}
	public void setVectorLenght(double vectorLenght) {
		this.vectorLenght = vectorLenght;
	}
	public String getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public Map<String, Integer> getDocumentIndex() {
		return documentIndex;
	}
	
	public void setDocumentIndex(Map<String, Integer> documentIndex) {
		this.documentIndex = documentIndex;
	}
}
