package searcher;

import java.io.File;
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

	@Option(name="-tflb", aliases="--tflowerbound",usage="sets the term frequency lowerbound")
	private int tflowerBound=0;

	@Option(name="-tfub", aliases="--tfupperbound",usage="sets the term frequency upperbound")
	private int tfupperBound=Integer.MAX_VALUE;

	@Option(name="-swl", aliases="--stopwordlist",usage="sets the path to the stopword list")
	private File stopWordList;

	@Option(name="-stem", aliases="--stemmer",usage="sets the preferred stemmer (porter, lovins, lancaster)")
	private Stemmer stemmer=Stemmer.PORTER;

	@Option(name="-idx", aliases="--index",usage="sets the preferred index (bagofwords, biword)")
	private IndexType indexTyp=IndexType.BAGOFWORDS;

	@Option(name="-nd", aliases="--newsdir",usage="sets the path to the newsgroups root directory",required=true)
	private File rootDir;
	
	@Option(name="-rn", aliases="--runname",usage="sets the name of the run",required=true)
	private String runName;
	
	@Option(name="-td", aliases="--topicdir",usage="sets the path to the topic directory",required=true)
	private File topicDirectory;


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

	public int getTflowerBound() {
		return tflowerBound;
	}

	public int getTfupperBound() {
		return tfupperBound;
	}

	public File getStopWordList() {
		return stopWordList;
	}

	public File getRootDir() {
		return rootDir;
	}

	public File getTopicDirectory() {
		return topicDirectory;
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

	public Document parseTopic(String topicName){

		File topicFile=new File(topicDirectory+"\\"+topicName);
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
		
		//Check Query-Termfrquency Bounds
		index.checkQueryTermFrequencyBounds(queryDoc);
		
		//Derive Query-Vectorlength
		deriveQueryVectorLength(queryDoc);
		
		for(Document doc:index.getDocuments())
		{
			//Derive numerator of Cosinusformula
			double matchedTermWeights=0;
			for(Map.Entry<String,TermProperties> termEntry: queryDoc.getDocumentIndex().entrySet())
			{
				TermProperties termProp=doc.getDocumentIndex().get(termEntry.getKey());
				if(termProp!=null)
				{
					matchedTermWeights+=termProp.getWeighting()*termEntry.getValue().getWeighting();
				}
			}
			
			//Apply Cosinusfurmula
			double similarity=matchedTermWeights/(queryDoc.getVectorLength()*doc.getVectorLength());
			
			resultMap.put(doc,similarity);
		}
		
		//Instantiate a Comperator and sort Documents respectively to the similarity
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
	public void deriveQueryVectorLength(Document queryDoc)
	{
		double vectorLength = 0;
		for(Map.Entry<String,TermProperties> termEntry: queryDoc.getDocumentIndex().entrySet())
		{
			vectorLength+=Math.pow(termEntry.getValue().getWeighting(),2);
		}
		queryDoc.setVectorLength(Math.sqrt(vectorLength));
	}

	

}
class ValueComparator implements Comparator<Document> {

    Map<Document, Double> base;
    public ValueComparator(Map<Document, Double> base) {
        this.base = base;
    }
   
    public int compare(Document a, Document b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}
