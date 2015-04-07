package searcher.index;

import java.util.ArrayList;

public class BiwordIndex extends AbstractIndex{

	public BiwordIndex() {
		super();
	}

	@Override
	public void addTerms(ArrayList<String> terms) {

		String [] termsArray = (String []) terms.toArray();

		for(int i = 0; i < terms.size() - 1; i++){

			String biword = termsArray[i] + " " + termsArray[i + 1];

			if(index.containsKey(biword)){
				index.put(biword, index.get(biword) + 1);
			}
			else{
				index.put(biword, 1);
			}		
		}	
	}
}
