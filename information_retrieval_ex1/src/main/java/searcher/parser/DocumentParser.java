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
import java.util.Locale;

import searcher.index.AbstractIndex;
import searcher.model.Document;

public class DocumentParser implements Runnable{

	private int tflowerBound,tfupperBound;
	private String stopWordList;
	private AbstractIndex index;
	private File file;
	private ArrayList<String> stopWords;
	private String stemmer="porter";
	
	public DocumentParser(int tflowerBound, int tfupperBound,
			AbstractIndex index, String stemmer,String stopWordList, File file) {
		this.index=index;
		this.stopWordList=stopWordList;
		this.tflowerBound=tflowerBound;
		this.tfupperBound=tfupperBound;
		this.file=file;
		
		if(stemmer!=null)
		{
			this.stemmer=stemmer;
		}
	}

	@Override
	public void run() {
		if(stopWordList!=null)
        {
			stopWords=parseDocument(file);
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


}
