////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

package engine.ranking;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import engine.SearchEngine;
import indexer.Stemmer;
import indexer.TermTable;
import utility.FileNames;
import utility.Utility;

public class Ranking {
	TermTable termTable;
	int noOfDocs;
	float avgDocLength;
	int[][] docpointer;
	RandomAccessFile[] indexingFile;
	int[][] doctitles;
	int titleWeight = 10;

	public Ranking(TermTable table, int n, float avglen, int[][] dptr, RandomAccessFile[] f){
		termTable = table;
		noOfDocs = n;
		avgDocLength = avglen;
		docpointer = dptr;
		indexingFile = f;

	}

    public Ranking(TermTable termTable, int noOfDocs, int[] docLengths, float avgDocLength, int[] docpointer, RandomAccessFile indexingFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

	public double getScore(int docID, int[] qt, int[] qtf, int weightMode, boolean titleIndexMode, String[] docs) {
		//double weightArr[] = {1, 5, 4, 3, 2, 7, 8, 6};
		double weight = 0.0;
                // apply a weight of 1 for 'all' contents, and increase with each level up; title will be the highest weight, followed by subheader, selected, h1, and so forth until all (the lowest)
		// Could make an array holding the weights corresponding to the DIRS array, so that DIRS[i] mathces up with weight[i]
                // weight is sum of all contents (h1, h2, etc.)
                /*
                all:bm25
                h1: tf/idf * a
                h2: tf/idf or bm11, * b
                title: tf/idf * 10
                */
                
               /* double allw = getBM25Weight(0, docID, qt);
                double allh1 = getTFWeight(1, docID, qt) * 5;
                double allh2 = getTFWeight(2, docID, qt) * 4;
                double allh3 = getTFWeight(3, docID, qt) * 3;
                double allh4 = getTFWeight(4, docID, qt) * 2;
                double allSub = getTFWeight(5, docID, qt) * 7;
                double allTitle = getTFWeight(6, docID, qt) * 8;
                double allSelected = getTFWeight(7, docID, qt) * 6;
                
                weight = allw + allh1 + allh2 + allh3 + allh4 + allSub + allTitle + allSelected;
                */
		/**********************************************************************
		 50 Points.
		 This program builds the indexing file for document titles in real time.
		 However, it's not totally acceptable when we have a huge number of document.
		 (i.e. query processing time will be much over 'instant')
		 Any indexing file should be created before search engine is ready.
		 Modify program so that the title indexing file is built in indexing time.
		 You may move and modify the buildTitleIndexing() function to PostingsBuilder.
		 Or, you can create a java file separately to create indexing files.
		 **********************************************************************/
		//if(titleIndexMode) {
		///	buildTitleIndexing(docs);
		//	weight += getTitleWeight(0, docID, qt);
		//}
		return getBM25Weight(0, docID, qt);//weight;
	}

	private double getTFWeight(int diridx, int docID, int[] qt) {
		double w = 0.0;
		int[][] tf = getTermFrequency(1, docID, qt);

               
		for(int i=1; i<tf.length;i++) {
                    
			w += ((tf[i][1]));
		}
                
		return w;

	}

	private double getIDFWeight(int diridx, int docID, int[] qt) {
		double w = 0.0;
		int[][] tf = getTermFrequency(diridx, docID, qt);
		double idf = 0.0;
		for(int i=1; i<tf.length;i++) {
			idf = (10/termTable.getDocumentFrequency(tf[i][0]));
			w +=idf;
		}

		return w;

	}

	/**********************
	 * 10 points
	 * complete the function to return document weight 
	 * based on TF-IDF scheme
	 */
	private double getTFIDFWeight(int diridx, int docID, int[] qt) {
		double w = 0.0;
		w = getTFWeight(diridx, docID, qt) * getIDFWeight(diridx, docID, qt);
		return w;
	}

	/**********************
	 * 10 points
	 * complete the function to return document weight 
	 * based on BM25 scheme
	 */
	private double getBM25Weight(int diridx, int docID, int[] qt) {
		double w = 0.0;
                double wp1 = 0.0, wp2 = 0.0;
                double k = 1.2;
                double b = 0.75;
                int[][] tf = getTermFrequency(diridx, docID, qt);
                double idf = 0.0;
                
                for (int i = 1; i < tf.length; i++) {
                    idf = (this.noOfDocs/termTable.getDocumentFrequency(tf[i][0]));
                    wp1 = tf[i][1]*(k+1);
                    wp2 = k * ((1-b)+(b*(avgDocLength / tf.length)) + tf[i][1]);
                    w += (wp1/wp2) * idf;
                }
		return w;
	}
	
	/**********************
	 * 10 points
	 * complete the function to return document weight 
	 * based on BM11 scheme
	 */

	private double getBM11Weight(int diridx, int docID, int[] qt) {
            double wp1 = 0.0, wp2 = 0.0;
            double w = 0.0;
            double k = 1.2;
            int[][] tf = getTermFrequency(diridx, docID, qt);
            double idf = 0.0;
            for (int i=1; i < tf.length; i++) {
                idf = (this.noOfDocs/termTable.getDocumentFrequency(tf[i][0]));
                wp1 = tf[i][1]*k+1;
                wp2 = k*(avgDocLength/tf.length)+tf[i][1];
                w += (wp1/wp2) * idf;
            }
            return w;
	}

	private double getBM15Weight(int diridx, int docID, int[] qt) {
		double wp1 =0.0, wp2=0.0;
		double w = 0.0;
		double k = 1.2;
		int[][] tf = getTermFrequency(diridx, docID, qt);
		double idf = 0.0;
		for(int i=1; i<tf.length;i++) {
			idf = (this.noOfDocs/termTable.getDocumentFrequency(tf[i][0]));
			wp1 = tf[i][1]*(k+1);
			wp2 = k + tf[i][1];
			w += (wp1/wp2)* idf;
		}
		return w;
	}
	
	/**********************************************************************
	 * 10 points
	 * This function returns the weight of title just base on Term Frequency
	 * Change it to return the weight based on TF-IDF scheme.
	 **************************************************************/
	private double getTitleWeight(int diridx, int docID, int[]qt) {
		double w = 0.0;
                getTFIDFWeight(diridx, docID, qt);
		for(int i=0; i<doctitles[docID].length;i++) {
			for(int j=0; j<qt.length;j++)
				if(doctitles[docID][i] == qt[j])
					w += this.titleWeight;
		}
		return w;
	}
	private void buildTitleIndexing(String[] docs) {
		StringTokenizer st ;
		String token;
		Stemmer stemmer = new Stemmer();
		ArrayList<Integer> arr;
		doctitles = new int[docs.length][];
		for(int idx=0; idx<docs.length;idx++) {
			st = new StringTokenizer(docs[idx], " ");
			arr = new ArrayList<Integer>();
			while(st.hasMoreTokens()) {
				token = st.nextToken().toLowerCase();
				stemmer.add(token.toCharArray(), token.length());
				stemmer.stem();
				int termID = Utility.search(termTable.getTermTable(), stemmer.toString());
				if(termID != -1 && arr.contains(termID) == false) {
					arr.add(termID);
				}
			}
			doctitles[idx] = new int[arr.size()];
			for(int i=0;i<arr.size();i++)
				doctitles[idx][i] = arr.get(i).intValue();
		}

	}
	private int[][] getTermFrequency(int diridx, int docID, int[] qt) {
		int data[][] = new int [qt.length+1][2];
		
                try {
			long ptr = this.docpointer[diridx][docID];
			int idx = 1; 
			indexingFile[diridx].seek(ptr*Integer.BYTES);
                        //System.out.println ("GetTermFreq:" + docID + ":" + ptr);
			byte[] arr = new byte[3*Integer.BYTES];
			indexingFile[diridx].read(arr);
			IntBuffer intBuf = ByteBuffer.wrap(arr).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
			int dlen = intBuf.get(1);
			data[0][0] = dlen;
			int tlen = intBuf.get(2);
			arr = new byte[tlen*2*Integer.BYTES];
			indexingFile[diridx].read(arr);
			intBuf = ByteBuffer.wrap(arr).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                        while(intBuf.hasRemaining()) {
				int n = intBuf.get();
				int m = intBuf.get();
				for(int i=0; i<qt.length;i++) {
					if(n == qt[i]) {
                                            //System.out.println("[term pairs]" + qt[i] + ":- " + m);
			
						data[idx][0] = n;
						data[idx++][1] = m;
					}
				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
