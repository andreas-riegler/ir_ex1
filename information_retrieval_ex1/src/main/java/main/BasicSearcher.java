package main;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import searcher.Searcher;

public class BasicSearcher {
	public static void main(String[] args) {
		
		Searcher searcher=new Searcher();
		CmdLineParser parser = new CmdLineParser(searcher);
        try {
                parser.parseArgument(args);
                
                System.out.println("Parsing");
                
                searcher.parseDocuments();
                
                System.out.println("Parsing finished");
                System.out.println("Checking Bounds");
                
                searcher.checkTermFrequencyBounds();
                
                System.out.println("Checking Bounds finished");
                System.out.println("Weighting");
                
                searcher.weightDocTerms();
                
                System.out.println("Weighting finished");
                System.out.println("Deriving Vector finished");
                
                searcher.deriveDocumentVectorLengths();
                
                System.out.println("Deriving Vector finished");
                
        } catch (CmdLineException e) {
          
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }
}
