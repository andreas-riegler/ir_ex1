package searcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import searcher.index.AbstractIndex;
import searcher.parser.DocumentParser;

public class Searcher {
	
	private int tflowerBound,tfupperBound;
	private String stopWordList,stemmer;
	private AbstractIndex index;
	private ExecutorService thPool;
	public Searcher(AbstractIndex index, String stopWordListPath, String stemmer,int tflowerBound, int tfupperBound)
	{
		this.index=index;
		this.stopWordList=stopWordListPath;
		this.stemmer=stemmer;
		this.tflowerBound=tflowerBound;
		this.tfupperBound=tfupperBound;
		thPool=Executors.newFixedThreadPool(5);
	}
	
	public void parseDocuments(File rootDir) {
        File[] dirs = rootDir.listFiles();
   
        for (File dir : dirs) {
        	if(dir.isDirectory()){
        		for (File file : dir.listFiles()) {
        			if(dir.isFile())
        			{
        				thPool.execute(new DocumentParser(tflowerBound,tfupperBound,index, stemmer,stopWordList, file));
        			}
        		}
        	}
        }

    }

}
