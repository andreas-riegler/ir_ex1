package searcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.args4j.Option;

import searcher.index.AbstractIndex;
import searcher.index.BagOfWordsIndex;
import searcher.index.BiwordIndex;
import searcher.parser.DocumentParser;

enum IndexTyp{BAGOFWORDS,BIWORD}
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
	private IndexTyp indexTyp=IndexTyp.BAGOFWORDS;
	 
	 @Option(name="-n",usage="Sets the Path to the Rootdirectory")
	private File rootDir;
	 @Option(name="-l",usage="Sets the Name of the stored Index",forbids="-n")
	private String loadDir;
	 
	private AbstractIndex index;
	private ExecutorService thPool;
	public Searcher()
	{
		
		thPool=Executors.newFixedThreadPool(5);
	}
	
	public void parseDocuments() {
       
		if(indexTyp.equals(IndexTyp.BAGOFWORDS))
		{
			index=new BagOfWordsIndex();
			
		}
		else
		{
			index=new BiwordIndex();
		}
		if (rootDir != null) {
			File[] dirs = rootDir.listFiles();

			for (File dir : dirs) {
				if (dir.isDirectory()) {
					for (File file : dir.listFiles()) {
						if (file.isFile()) {
							thPool.execute(new DocumentParser(tflowerBound,
									tfupperBound, index, stemmer.toString(),
									stopWordList, file));
						}
					}
				}
			}
		}

    }

}
