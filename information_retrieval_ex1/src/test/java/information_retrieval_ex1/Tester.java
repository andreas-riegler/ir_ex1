package information_retrieval_ex1;

import java.io.File;

import searcher.Searcher;
import searcher.index.*;
import searcher.parser.*;

public class Tester {
	
	public static void main(String[] args) {
		
		AbstractIndex index = new BagOfWordsIndex();
		//PorterStemmer stemmer = new PorterStemmer();
		
		//Searcher searcher = new Searcher(index, null, "porter", 0, 100);
		
		File input = new File("D:/uni/Information Retrieval/20_newsgroups_subset");
		
		System.out.println(input.exists());
		
		//searcher.parseDocuments(input);
		
	}
	
}
