package searcher.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import searcher.model.Document;
import searcher.model.TermProperties;

public abstract class AbstractIndex {

	//term and document frequency
	protected Map<String, Integer> index;
	protected List<Document> documents;
	
	protected int termFrequencyLowerBound;
	protected int termFrequencyUpperBound;
	
	int i = 0;

	public AbstractIndex(int termFrequencyLowerBound, int termFrequencyUpperBound){
		index = new HashMap<String, Integer>();
		documents = new ArrayList<Document>();
		
		this.termFrequencyLowerBound = termFrequencyLowerBound;
		this.termFrequencyUpperBound = termFrequencyUpperBound;
	}

	
	public int getTermFrequencyLowerBound() {
		return termFrequencyLowerBound;
	}

	public void setTermFrequencyLowerBound(int termFrequencyLowerBound) {
		this.termFrequencyLowerBound = termFrequencyLowerBound;
	}

	public int getTermFrequencyUpperBound() {
		return termFrequencyUpperBound;
	}

	public void setTermFrequencyUpperBound(int termFrequencyUpperBound) {
		this.termFrequencyUpperBound = termFrequencyUpperBound;
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

		Map<String, TermProperties> documentIndex = document.getDocumentIndex();

		if(documentIndex.containsKey(term)){
			TermProperties termProp = documentIndex.get(term);
			termProp.incrementTermFrequency();
		}
		else{
			documentIndex.put(term, new TermProperties());
		}
	}

}
