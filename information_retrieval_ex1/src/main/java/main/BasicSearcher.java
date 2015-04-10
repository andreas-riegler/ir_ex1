package main;

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
                
                System.out.printf("%-20s", "Parsing...");
                searcher.parseDocuments();
                System.out.printf("%-10s", "finished\n\r");
                
                System.out.printf("%-20s", "Checking Bounds...");   
                searcher.checkTermFrequencyBounds();            
                System.out.printf("%-10s", "finished\n\r");
                
                System.out.printf("%-20s", "Weighting...");   
                searcher.weightDocTerms();
                System.out.printf("%-10s", "finished\n\r");
                
                System.out.printf("%-20s", "Deriving Vector...");
                searcher.deriveDocumentVectorLengths();
                System.out.printf("%-10s", "finished\n\r");
                
                System.out.println("Topic to search for (Quit with q):");
                
                String input = scanner.nextLine();
                while(!input.equals("q"))
                {
                	Document queryDoc=searcher.parseTopic(input);
                	
                	if (queryDoc != null) {
                		TreeMap<Document, Double> resultMap = searcher.searchSimilarDocuments(queryDoc);

                		System.out.println("\nResult list:");
                		int counter = 0;
                		for (Map.Entry<Document, Double> resultEntry : resultMap.entrySet()) {
                			counter++;
                			
                			System.out.println(String.format("%-8s Q0 %-30s  %3d  %1.8f %s", queryDoc.getDocumentId(), resultEntry.getKey().getDocumentId(), counter, resultEntry.getValue(), searcher.getRunName()));
                			
                			if (counter == 100) {
                				break;
                			}
                		}
                	} 
                	else 
                	{
                		System.out.println("\nNo such Topicname!");
                	}
					System.out.println("\nTopic to search for(Quit with q):");
        			input = scanner.nextLine();
                }
                System.out.println("\nProgram terminated");
                
                scanner.close();
                
        } catch (CmdLineException e) {
          
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }
}
