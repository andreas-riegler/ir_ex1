package searcher.parser;

import java.io.File;

import searcher.index.AbstractIndex;

public class DocumentParser implements Runnable{

	private int tflowerBound,tfupperBound;
	private String stopWordList;
	private AbstractIndex index;
	public DocumentParser(int tflowerBound, int tfupperBound,
			AbstractIndex index, String stopWordList, File file) {
		this.index=index;
		this.stopWordList=stopWordList;
		this.tflowerBound=tflowerBound;
		this.tfupperBound=tfupperBound;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
