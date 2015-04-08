package searcher.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import searcher.model.Document;

public abstract class AbstractIndex {

	protected Map<String, Integer> index;
	protected List<Document> documents;
	int i = 0;

	public AbstractIndex(){
		index = new HashMap<String, Integer>();
		documents = new ArrayList<Document>();
	}

	
	public Map<String, Integer> getIndex() {
		return index;
	}

	public void setIndex(Map<String, Integer> index) {
		this.index = index;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}


	public void addDocument(Document document, ArrayList<String> terms){
		addTerms(document, terms);
	
		if(!documents.contains(document)){
			documents.add(document);
			System.out.println("doc added " + ++i);
		}
		
	}

	public abstract void addTerms(Document document, ArrayList<String> terms);

	public void putIndexTerm(String term){

		synchronized (index) {

			if(index.containsKey(term)){
				index.put(term, index.get(term) + 1);
			}
			else{
				index.put(term, 1);
			}

		}
	}

	public void putDocumentIndexTerm(Document document, String term) {

		Map<String, Integer> documentIndex = document.getDocumentIndex();

		if(documentIndex.containsKey(term)){
			documentIndex.put(term, documentIndex.get(term) + 1);
		}
		else{
			documentIndex.put(term, 1);
		}
	}

}
