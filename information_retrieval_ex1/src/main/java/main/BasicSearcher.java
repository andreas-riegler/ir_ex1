package main;

import java.io.File;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import searcher.Searcher;
import searcher.model.Document;

public class BasicSearcher {
	public static void main(String[] args) {
		
		Searcher searcher=new Searcher();
		Scanner scanner = new Scanner(System.in);
		
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
                System.out.println("Deriving Vector");
                
                searcher.deriveDocumentVectorLengths();
                
                System.out.println("Deriving Vector finished");
                
                System.out.println("Topic to search for(Quit with q):");
                
                String input = scanner.nextLine();
                while(!input.equals("q"))
                {
                	Document queryDoc=searcher.parseTopic(input);
                	
                	if (queryDoc != null) {
                		TreeMap<Document, Double> resultMap = searcher.searchSimilarDocuments(queryDoc);

                		System.out.println("Result list:");
                		int counter = 0;
                		for (Map.Entry<Document, Double> resultEntry : resultMap.entrySet()) {
                			counter++;

                			String output = queryDoc.getDocumentId() + " Q0 "
								+ resultEntry.getKey().getDocumentId() + " "
								+ counter + " " + resultEntry.getValue() + " "
								+ searcher.getRunName();
                			System.out.println(output);
                			if (counter == 100) {
                				break;
                			}
                		}
                	} 
                	else 
                	{
                		System.out.println("No such Topicname!");
                	}
					System.out.println("Topic to search for(Quit with q):");
        			input = scanner.nextLine();
                }
                System.out.println("Program terminated");
                
        } catch (CmdLineException e) {
          
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }
}
