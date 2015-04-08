package searcher.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import searcher.index.AbstractIndex;
import searcher.model.Document;
import searcher.model.TermProperties;

public class DocumentParser implements Runnable{


	private AbstractIndex index;
	private File file,stopWordList;
	private List<String> stopWords;
	private String stemmer="porter";
	
	public DocumentParser(
			AbstractIndex index, String stemmer,File stopWordList, File file) {
		this.index=index;
		this.stopWordList=stopWordList;
		this.file=file;
		
		stopWords = new ArrayList<String>();
		
		if(stemmer!=null)
		{
			this.stemmer=stemmer;
		}
	}

	@Override
	public void run() {
		if(stopWordList!=null)
        {
			stopWords=parseDocument(stopWordList);
        }
		Document doc=new Document();
		doc.setDocumentId(file.getParentFile().getName()+"/"+file.getName());
		index.addDocument(doc,normalizeToken(parseDocument(file)));
		
		
	}

	
	private ArrayList<String> parseDocument(File file) {
		FileInputStream input = null;
		ArrayList<String> tokenList=new ArrayList<String>();
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		try {
			String line=reader.readLine();
			while(line!=null)
			{
				String[]token=line.split(" ");
				tokenList.addAll(Arrays.asList(token));
				line=reader.readLine();
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return tokenList;
	}
	
	private ArrayList<String> normalizeToken(ArrayList<String> parseDocument) {
		ArrayList<String> terms=new ArrayList<String>();
		
		for (String token : parseDocument) {

	            token = token.trim();
	            token = token.toLowerCase(Locale.ENGLISH);
	            token = token.replaceAll("^[^a-zA-Z0-9]+", "");
	            token = token.replaceAll("[^a-zA-Z0-9]+$", "");
	            if(!stopWords.contains(token))
	            {
	            	
	            }
	            switch(stemmer)
	            {
	            	case "porter":
	            	{
	            		Stemmer ps = new Stemmer();
	    	            ps.add(token.toCharArray(), token.length());
	    	            ps.stem();
	    	            token = ps.toString();
	            	}
	            	case "lovins":
	            	{
	            		LovinsStemmer ls=new LovinsStemmer();
	            		token=ls.stem(token);
	            	}
	            	case "lancaster":
	            	{
	            		LancasterStemmer las=new LancasterStemmer();
	            		token=las.stem(token);
	            		
	            	}
	            }
	            

	            if (!token.isEmpty()) {
	                terms.add(token);
	            }

	        }
		return terms;
	}
	
	public void weightDocTerms()
	{
		double docCount=index.getIndex().size();
		for(Document doc:index.getDocuments())
		{
			for(Map.Entry<String,TermProperties> termEntry: doc.getDocumentIndex().entrySet())
			{
				int df=index.getIndex().get(termEntry.getKey());
				double idf=Math.log10(docCount/df);
				double tf=1+Math.log10(termEntry.getValue().getTermFrequency());
				double weight=idf*tf;
				termEntry.getValue().setWeighting(weight);
			}
		}
	}
	public void deriveDocumentVectorLengths()
	{
		for(Document doc:index.getDocuments())
		{
			double vectorLength=0;
			for(Map.Entry<String,TermProperties> termEntry: doc.getDocumentIndex().entrySet())
			{
				vectorLength+=Math.pow(termEntry.getValue().getWeighting(),2);
			}
			doc.setVectorLength(Math.sqrt(vectorLength));
		}
	}


}
