package searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.Option;

import searcher.index.DocumentIndex;



public class Searcher {


	@Option(name="-nd", aliases="--newsdir",usage="sets the path to the newsgroups root directory",required=true)
	private File rootDir;
	
	@Option(name="-rn", aliases="--runname",usage="sets the name of the run",required=true)
	private String runName;
	
	@Option(name="-td", aliases="--topicdir",usage="sets the path to the topic directory",required=true)
	private File topicDirectory;


	private ExecutorService thPool;
	private Analyzer analyzer = new StandardAnalyzer();
	private Directory indexDirectory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;

	public Searcher()
	{
		thPool=Executors.newFixedThreadPool(5);
		Path indexPath = Paths.get("/temp/index");
		try {
			indexDirectory = FSDirectory.open(indexPath);
			ireader = DirectoryReader.open(indexDirectory);
		   
		} catch (IOException e) {
			e.printStackTrace();
		}
		isearcher = new IndexSearcher(ireader);
	}
	

	public IndexSearcher getIsearcher() {
		return isearcher;
	}


	public void setIsearcher(IndexSearcher isearcher) {
		this.isearcher = isearcher;
	}


	public String getRunName() {
		return runName;
	}

	public void setRunName(String runName) {
		this.runName = runName;
	}

	public File getRootDir() {
		return rootDir;
	}

	public File getTopicDirectory() {
		return topicDirectory;
	}
	

	public void createIndex() throws IOException {
		
		

		// Store the index in memory:
		// Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(indexDirectory, config);
		
		Set<Future<Document>> documents=new HashSet<Future<Document>>();

		File[] dirs = rootDir.listFiles();

		for (File dir : dirs) {
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						Callable<Document> callable=new DocumentIndex(file);
						Future<Document> future=thPool.submit(callable);
						documents.add(future);

					}
				}
			}
		}
		
		for(Future<Document> doc: documents){
			try {
				iwriter.addDocument(doc.get());
			} catch (InterruptedException e) {	
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		iwriter.close();
		

		thPool.shutdown();

		try {
			thPool.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}

	}

	public Query parseTopic(String topicName){

		File topicFile=new File(topicDirectory+"\\"+topicName);
		if(topicFile.exists()){
			
			QueryParser parser = new QueryParser("newstext", analyzer);
			
			FileInputStream input = null;
			try {

				input = new FileInputStream(topicFile);

			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String querytext="";
			try {

				String line=reader.readLine();

				while(line!=null)
				{
					querytext+=line;
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		    Query query=null;
			try {
				query = parser.parse(querytext);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return query;
		}
		else{
			return null;
		}
	}
	public  ScoreDoc[] searchSimilarDocuments(Query query) throws IOException
	{
		
		
		
	    ScoreDoc[] hits = isearcher.search(query, 100).scoreDocs;
		
	    return hits;
	}
	

}

