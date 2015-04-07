package searcher.index;

import java.util.ArrayList;
import java.util.Map;

import searcher.model.Document;

public class BiwordIndex extends AbstractIndex{

	public BiwordIndex() {
		super();
	}

	@Override
	public void addTerms(Document document, ArrayList<String> terms) {

		String [] termsArray = (String []) terms.toArray();
		
		Map<String, Integer> documentIndex = document.getDocumentIndex();

		for(int i = 0; i < terms.size() - 1; i++){

			String biword = termsArray[i] + " " + termsArray[i + 1];

			if(index.containsKey(biword)){
				index.put(biword, index.get(biword) + 1);
			}
			else{
				index.put(biword, 1);
			}
			
			if(documentIndex.containsKey(biword)){
				documentIndex.put(biword, documentIndex.get(biword) + 1);
			}
			else{
				documentIndex.put(biword, 1);
			}
			
		}
	}
}
