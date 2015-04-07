package searcher.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import searcher.model.Document;

public abstract class AbstractIndex {

	protected Map<String, Integer> index;
	
	public AbstractIndex(){
		index = new HashMap<String, Integer>();
	}
	
	public void addDocument(Document document){
		addTerms(document.getTerms());
		
		//...
	}
	
	public abstract void addTerms(ArrayList<String> terms);
}
