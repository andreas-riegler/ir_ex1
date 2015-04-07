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

		Map<String, Integer> documentIndex = document.getDocumentIndex();
		
		for(String term : terms){

			if(index.containsKey(term)){
				index.put(term, index.get(term) + 1);
			}
			else{
				index.put(term, 1);
			}
			
			if(documentIndex.containsKey(term)){
				documentIndex.put(term, documentIndex.get(term) + 1);
			}
			else{
				documentIndex.put(term, 1);
			}
		}
	}
}
