package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import searcher.Searcher;

public class BasicSearcher {
	public static void main(String[] args) {

		BufferedWriter writer = null;

		Searcher searcher=new Searcher();

		CmdLineParser parser = new CmdLineParser(searcher);

		try {

			parser.parseArgument(args);
			boolean existingIndex=true;

			try {
				writer = Files.newBufferedWriter(searcher.getOutputFile().toPath(), Charset.defaultCharset(), StandardOpenOption.CREATE);
			} catch (IOException e2) {
				System.out.println("cannot write output file!");
				System.exit(-1);
			}


			if(!searcher.getRootDir().exists()){
				System.out.println("newsgroups directory does not exist!");
				System.exit(-1);
			}

			if(!searcher.getTopicDirectory().exists()){
				System.out.println("topic directory does not exist!");
				System.exit(-1);
			}

			if(searcher.getIndexDir().exists()){
				File[] dirs = searcher.getIndexDir().listFiles();

				if(dirs.length==0)
				{
					existingIndex=false;
				}
			}
			else{
				existingIndex=false;
			}

			if (!existingIndex) {
				System.out.printf("%-20s", "Indexing...");
				try {
					searcher.createIndex();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.printf("%-10s", "finished\n\r");
			}



			for(int i=1;i<=20;i++)
			{
				Query query=searcher.parseTopic("topic"+i);
				if(query!=null)
				{
					ScoreDoc[] hits=null;
					try {
						hits = searcher.searchSimilarDocuments(query);
					} catch (IOException e) {

						e.printStackTrace();
					}

					System.out.println("\nResult list:");
					int counter = 0;
					for (ScoreDoc hit : hits) {
						counter++;
						Document hitDoc=null;

						try {
							hitDoc = searcher.getIsearcher().doc(hit.doc);
						} catch (IOException e) {
							e.printStackTrace();
						}

						System.out.println(String.format("%-8s Q0 %-30s  %3d  %1.8f %s", "topic"+i, hitDoc.get("name"), counter, hit.score, searcher.getRunName()));

						String line = "topic" + i + " Q0 " + hitDoc.get("name") + " " + counter + " " + hit.score + " " + searcher.getRunName() + "\n";

						writer.write(line, 0, line.length());
						
					}
				} 
				else 
				{
					System.out.println("\nNo such Topicname!");
				}


			}
			
			System.out.println("\n\nExplanations:");
			
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser queryParser = new QueryParser("newstext", analyzer);
			Query q = queryParser.parse("Olympus Stylus");
			
			ScoreDoc[] hits=null;
			
			try {
				hits = searcher.searchSimilarDocuments(q);
				
				for(int i = 0; i < 2; i++){
					System.out.println(searcher.getExplanation(q, hits[i].doc));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			writer.flush();
			writer.close();

			System.out.println("\nProgram terminated");



		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
