package searcher.index;

import java.util.ArrayList;
import java.util.Map;

import searcher.model.Document;

public class BiwordIndex extends AbstractIndex{

	public BiwordIndex(int termFrequencyLowerBound, int termFrequencyUpperBound) {
		super(termFrequencyLowerBound, termFrequencyUpperBound);
	}

	@Override
	public void addTerms(Document document, ArrayList<String> terms) {

		String [] termsArray = (String []) terms.toArray(new String [terms.size()]);

		for(int i = 0; i < terms.size() - 1; i++){

			String biword = termsArray[i] + " " + termsArray[i + 1];

			if(!document.getDocumentIndex().containsKey(biword)){
				putIndexTerm(biword);
			}
			
			putDocumentIndexTerm(document, biword);
			
		}
	}

	@Override
	public void addQueryTerms(Document document, ArrayList<String> terms) {
		String [] termsArray = (String []) terms.toArray(new String [terms.size()]);

		for(int i = 0; i < terms.size() - 1; i++){

			String biword = termsArray[i] + " " + termsArray[i + 1];
			
			putDocumentIndexTerm(document, biword);
			
		}
		
	}
}
