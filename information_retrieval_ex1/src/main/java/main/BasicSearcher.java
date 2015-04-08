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
                searcher.parseDocuments();
        } catch (CmdLineException e) {
          
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }
}
