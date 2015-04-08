package searcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.Option;

import searcher.index.AbstractIndex;
import searcher.index.BagOfWordsIndex;
import searcher.index.BiwordIndex;
import searcher.model.Document;
import searcher.parser.DocumentParser;

enum IndexType{BAGOFWORDS,BIWORD}
enum Stemmer{PORTER,LOVINS,LANCASTER}

public class Searcher {

	@Option(name="-tflb",usage="Sets the Termfrequency lowerbound")
	private int tflowerBound=0;

	@Option(name="-tfub",usage="Sets the Termfrequency upperbound")
	private int tfupperBound=Integer.MAX_VALUE;

	@Option(name="-swl",usage="Sets the Path to the Stopwordlist")
	private File stopWordList;

	@Option(name="-stem",usage="Sets the prefered Stemmer(porter,lovins,lancaster)")
	private Stemmer stemmer=Stemmer.PORTER;

	@Option(name="-idx",usage="Sets the prefered Index(bagofwords,biword)")
	private IndexType indexTyp=IndexType.BAGOFWORDS;

	@Option(name="-n",usage="Sets the Path to the Rootdirectory",required=true)
	private File rootDir;

	private File topicFile;

	private AbstractIndex index;
	private ExecutorService thPool;

	public Searcher()
	{
		thPool=Executors.newFixedThreadPool(5);
	}

	public void parseDocuments() {

		if(indexTyp.equals(IndexType.BAGOFWORDS))
		{
			index=new BagOfWordsIndex(tflowerBound,
					tfupperBound);

		}
		else
		{
			index=new BiwordIndex(tflowerBound,
					tfupperBound);
		}

		File[] dirs = rootDir.listFiles();

		for (File dir : dirs) {
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						thPool.execute(new DocumentParser(index, stemmer.toString(),
								stopWordList, file));
					}
				}
			}
		}

		thPool.shutdown();

		try {
			thPool.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}

	}

	public Document parseTopic(){

		if(topicFile.exists()){
			
			DocumentParser docParser = new DocumentParser(null, stemmer.toString(), stopWordList, topicFile);
			docParser.run();
			Document parsedTopic = docParser.getDocument();
			return parsedTopic;
		}
		else{
			return null;
		}
	}

	public void checkTermFrequencyBounds(){
		this.index.checkTermFrequencyBounds();
	}

	public void weightDocTerms(){
		this.index.weightDocTerms();
	}

	public void deriveDocumentVectorLengths(){
		this.index.deriveDocumentVectorLengths();
	}

}
