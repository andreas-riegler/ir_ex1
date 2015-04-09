package searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.Option;

import searcher.index.AbstractIndex;
import searcher.index.BagOfWordsIndex;
import searcher.index.BiwordIndex;
import searcher.model.Document;
import searcher.model.TermProperties;
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
	
	@Option(name="-rn",usage="Sets the Name of the Run",required=true)
	private String runName;


	private AbstractIndex index;
	private ExecutorService thPool;

	public Searcher()
	{
		thPool=Executors.newFixedThreadPool(5);
	}
	

	public String getRunName() {
		return runName;
	}


	public void setRunName(String runName) {
		this.runName = runName;
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

	public Document parseTopic(File topicFile){

		if(topicFile.exists()){
			
			DocumentParser docParser = new DocumentParser(index, stemmer.toString(), stopWordList, topicFile,true);
			docParser.run();
			Document parsedTopic = docParser.getDocument();
			index.weightQueryTerms(parsedTopic);
			return parsedTopic;
		}
		else{
			return null;
		}
	}
	public TreeMap<Document,Double> searchSimilarDocuments(Document queryDoc)
	{
		Map<Document,Double> resultMap=new HashMap<Document,Double>();
		
		double queryVectorLength=queryDoc.getVectorLength();
		for(Document doc:index.getDocuments())
		{
			double matchedTermWeights=0;
			for(Map.Entry<String,TermProperties> termEntry: queryDoc.getDocumentIndex().entrySet())
			{
				matchedTermWeights+=doc.getDocumentIndex().get(termEntry.getKey()).getWeighting()*termEntry.getValue().getWeighting();
			}
			double similarity=matchedTermWeights/(queryVectorLength*doc.getVectorLength());
			resultMap.put(doc,similarity);
		}
		ValueComparator vc =  new ValueComparator(resultMap);
        TreeMap<Document,Double> sortedresultMap = new TreeMap<Document,Double>(vc);
        sortedresultMap.putAll(resultMap);
		return sortedresultMap;
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
class ValueComparator implements Comparator<Document> {

    Map<Document, Double> base;
    public ValueComparator(Map<Document, Double> base) {
        this.base = base;
    }
   
    public int compare(Document a, Document b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else {
            return -1;
        } 
    }
}
