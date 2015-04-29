package searcher.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class DocumentIndex implements Callable<Document>{

	private File file;
	public DocumentIndex(File file) {
		this.file=file;
	}
	@Override
	public Document call() throws Exception {
		return parseDocument(file);
	}
	
	private Document parseDocument(File file) {

		FileInputStream input = null;
		Document doc = new Document();

		try {

			input = new FileInputStream(file);

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		String newstext="";
		try {

			String line=reader.readLine();

			while(line!=null)
			{
				newstext+=line;
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		doc.add(new Field("newstext", newstext, TextField.TYPE_STORED));
		doc.add(new Field("name", file.getParentFile().getName()+"/"+file.getName(), TextField.TYPE_STORED));
		return doc;
	}

	
}
