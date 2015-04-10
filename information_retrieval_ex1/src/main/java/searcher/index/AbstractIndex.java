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

	//list of all documents in index
	protected List<Document> documents;

	protected int termFrequencyLowerBound;
	protected int termFrequencyUpperBound;


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

	//adds terms to index and document-index
	public abstract void addTerms(Document document, ArrayList<String> terms);
	
	//adds query terms to document-index
	public abstract void addQueryTerms(Document document, ArrayList<String> terms);

	public void addDocument(Document document, ArrayList<String> terms){

		//add the terms to index and document-index
		addTerms(document, terms);

		//add the document to document list
		synchronized (documents) {
			if(!documents.contains(document)){
				documents.add(document);
			}
		}
	}

	//puts a term into index
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

	//puts a term into a document-index
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

	public void checkTermFrequencyBounds(){

		synchronized (documents) {

			for(Document doc : documents){

				List<String> termsExceedingBounds = new ArrayList<String>();

				//check bounds
				for(Map.Entry<String,TermProperties> termEntry: doc.getDocumentIndex().entrySet()) {
					if(termEntry.getValue().getTermFrequency() > this.termFrequencyUpperBound || termEntry.getValue().getTermFrequency() < this.termFrequencyLowerBound){
						termsExceedingBounds.add(termEntry.getKey());
					}
				}

				for(String term : termsExceedingBounds){

					//remove from document index
					doc.getDocumentIndex().remove(term);

					//decrease documentFrequency
					
					int docFreq = index.get(term);
					
					if(docFreq - 1 <= 0){
						index.remove(term);
					}
					else{
						index.put(term, docFreq - 1);
					}
				}
			}
		}
	}
	public void checkQueryTermFrequencyBounds(Document queryDoc)
	{
		List<String> termsExceedingBounds = new ArrayList<String>();
		
		//check bounds
		for(Map.Entry<String,TermProperties> termEntry: queryDoc.getDocumentIndex().entrySet()) {
			if(termEntry.getValue().getTermFrequency() > this.termFrequencyUpperBound || termEntry.getValue().getTermFrequency() < this.termFrequencyLowerBound){
				termsExceedingBounds.add(termEntry.getKey());
			}
		}

		for(String term : termsExceedingBounds){

			//remove from document index
			queryDoc.getDocumentIndex().remove(term);
		}
	}

	//computes term weighting of all documents
	public void weightDocTerms()
	{
		double docCount = index.size();

		for(Document doc : documents)
		{
			for(Map.Entry<String,TermProperties> termEntry: doc.getDocumentIndex().entrySet())
			{
				termEntry.getValue().setWeighting(deriveWeight(docCount,index.get(termEntry.getKey()),termEntry.getValue().getTermFrequency()));
			}
		}
	}

	//computes term weighting of all query terms
	public void weightQueryTerms(Document queryDoc)
	{
		for(Map.Entry<String,TermProperties> termEntry: queryDoc.getDocumentIndex().entrySet())
		{
			int df;
			if(index.get(termEntry.getKey())!=null)
			{
				df=index.get(termEntry.getKey());
			}
			else
			{
				df=1;
			}

			termEntry.getValue().setWeighting(deriveWeight(index.size(),df,termEntry.getValue().getTermFrequency()));
		}
	}

	//computes weighting
	public double deriveWeight(double docCount,double df,double tf)
	{
		double idf = Math.log10(docCount/df);
		double wtf = 1 + Math.log10(tf);
		return idf * wtf;
	}

	//computes vector lengths of all documents
	public void deriveDocumentVectorLengths()
	{
		for(Document doc : documents)
		{
			double vectorLength = 0;
			for(Map.Entry<String,TermProperties> termEntry: doc.getDocumentIndex().entrySet())
			{
				vectorLength+=Math.pow(termEntry.getValue().getWeighting(),2);
			}
			doc.setVectorLength(Math.sqrt(vectorLength));
		}
	}
}
