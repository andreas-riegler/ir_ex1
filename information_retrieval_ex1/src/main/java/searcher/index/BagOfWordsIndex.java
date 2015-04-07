package searcher.index;

import java.util.ArrayList;

public class BagOfWordsIndex extends AbstractIndex{


	public BagOfWordsIndex() {
		super();
	}

	@Override
	public void addTerms(ArrayList<String> terms) {

		for(String term : terms){

			if(index.containsKey(term)){
				index.put(term, index.get(term) + 1);
			}
			else{
				index.put(term, 1);
			}
		}
	}
}
