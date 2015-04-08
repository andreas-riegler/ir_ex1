package searcher.index;

import java.util.ArrayList;
import java.util.Map;

import searcher.model.Document;

public class BagOfWordsIndex extends AbstractIndex{


	public BagOfWordsIndex() {
		super();
	}

	@Override
	public void addTerms(Document document, ArrayList<String> terms) {
	
		for(String term : terms){

			putIndexTerm(term);			
			putDocumentIndexTerm(document, term);
		}	
	}
}
