package searcher.model;

import java.util.Map;

public class Document {

	private String documentId;	
	private Map<String, Integer> documentIndex;
	
	
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