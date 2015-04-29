package searcher.model;

public class TermProperties {

	private int termFrequency;
	private double weighting;

	public TermProperties(){
		this.termFrequency = 1;
	}

	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	public double getWeighting() {
		return weighting;
	}
	public void setWeighting(double weighting) {
		this.weighting = weighting;
	}

	public void incrementTermFrequency(){
		this.termFrequency++;
	}

}
