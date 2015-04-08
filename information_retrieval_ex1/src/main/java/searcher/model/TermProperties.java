package searcher.model;

public class TermProperties {

	private int termFrequency;
	private int weighting;

	public TermProperties(){
		this.termFrequency = 0;
	}

	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	public int getWeighting() {
		return weighting;
	}
	public void setWeighting(int weighting) {
		this.weighting = weighting;
	}

	public void incrementTermFrequency(){
		this.termFrequency++;
	}

}
