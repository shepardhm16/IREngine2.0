////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

package engine.ranking;

// paire of docID and it's score for ranking (sort)
public class Pair {
	public int docID;
	double score;
	
	public Pair(int id, double s) {
		docID=id;
		score=s;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double s) {
		score = s;
	}
}
