package searcher.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import searcher.index.AbstractIndex;
import searcher.model.Document;

public class DocumentParser implements Runnable{

	private Document document;
	private AbstractIndex index;
	private File file,stopWordList;
	private List<String> stopWords;
	private String stemmer="porter";
	private Boolean isQuery;

	public DocumentParser(AbstractIndex index, String stemmer,File stopWordList, File file) {
		this.index=index;
		this.stopWordList=stopWordList;
		this.file=file;
		this.isQuery=false;
		stopWords = new ArrayList<String>();

		if(stemmer!=null)
		{
			this.stemmer=stemmer;
		}
	}
	public DocumentParser(AbstractIndex index, String stemmer,File stopWordList, File file,boolean isQuery) {
		this.index=index;
		this.stopWordList=stopWordList;
		this.file=file;

		stopWords = new ArrayList<String>();

		if(stemmer!=null)
		{
			this.stemmer=stemmer;
		}
		this.isQuery=isQuery;
	}


	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}


	@Override
	public void run() {

		if(stopWordList!=null)
		{
			stopWords=parseDocument(stopWordList);
		}

		document = new Document();
		

		if(isQuery==false){
			document.setDocumentId(file.getParentFile().getName()+"/"+file.getName());
			index.addDocument(document,normalizeToken(parseDocument(file)));
		}
		else
		{
			document.setDocumentId(file.getName());
			index.addQueryTerms(document,normalizeToken(parseDocument(file)));
		}
		

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
				//Split line in Token
				String[]token = line.split(" ");
				
				//Add token to Array of all previous splitted Documenttoken
				tokenList.addAll(Arrays.asList(token));
				line = reader.readLine();
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return tokenList;
	}

	private ArrayList<String> normalizeToken(ArrayList<String> parseDocument) {
		ArrayList<String> terms=new ArrayList<String>();

		for (String token : parseDocument) {

			//Trim Token
			token = token.trim();
			
			//Lowercase Token
			token = token.toLowerCase(Locale.ENGLISH);
			
			//Replace Specialcharacters at the beginning of the Token
			token = token.replaceAll("^[^a-zA-Z0-9]+", "");
			
			//Replace Specialcharacters at the ending of the Token
			token = token.replaceAll("[^a-zA-Z0-9]+$", "");
			
			//Skip Stopwords
			if(!stopWords.contains(token))
			{

				//Stem Token
				switch (stemmer) {
				//Porterstemmer
				case "porter": {
					Stemmer ps = new Stemmer();
					ps.add(token.toCharArray(), token.length());
					ps.stem();
					token = ps.toString();
				}
				//Lovinsstemmer
				case "lovins": {
					LovinsStemmer ls = new LovinsStemmer();
					token = ls.stem(token);
				}
				//Lancasterstemmer
				case "lancaster": {
					LancasterStemmer las = new LancasterStemmer();
					token = las.stem(token);

				}
				}

				if (!token.isEmpty()) {
					terms.add(token);
				}
			}

		}

		return terms;
	}
}
