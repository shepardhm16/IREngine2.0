package indexer;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import utility.FileNames;
import utility.StringUtility;
import utility.Utility;

public class PostingsBuilder {

	DataOutputStream postingsWriter = null;
	BufferedWriter postingsTestWriter = null; // postings test file
	TermTable table = null;


	// close all files 
	public void init() {
		try {
			postingsWriter = new DataOutputStream(new FileOutputStream(FileNames.POSTINGS));
			postingsTestWriter = new BufferedWriter(new FileWriter(FileNames.POSTINGS + ".test"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void finalize() {
		try {
			postingsWriter.close();
			postingsTestWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*************************************
	 * 10 points
	 * Add comments for this function
	 * find ??? (triple question mark) and add comments 
	 *   
	 */

	public void build(String dir, TermTable tbl) {
		this.table = tbl;
		int termIdx = 0;
		int docIdx = 0;
		int pointer = 0;
		int termsindocs[][] = null;
		long avgDocLen = 0;
		Stemmer stemmer = new Stemmer();
		String[] terms = table.getTermTable();

		ArrayList<String> docs = null;
		ArrayList<String> docTitles = new ArrayList<String>();

		try {
			docs = Utility.getAllDocuments(dir);
		} catch (IOException e) {
			System.out.println("Unable to open files." + e.getMessage());
			return;
		}

		String text = null;
		StringTokenizer st = null;
		String token = null;

		ArrayList<Integer> postings = null;
		int[] docptr = new int[docs.size()];
		String[] docnames =new String[docs.size()];

		for(int i=0; i<docnames.length;i++)
			docnames[i] = docs.get(i);
		try {
			BufferedWriter tw = new BufferedWriter(new FileWriter("urlpair"));
			for(int i=0; i<docnames.length;i++) {
				tw.write(i + "\t" + docnames[i]);
				tw.newLine();
			}
			tw.close();
                        
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		termsindocs = new int[docnames.length][];
		while(termIdx < terms.length) {
			postings = new ArrayList<Integer>();
			docIdx = 0;
			int t[] = null;
			int tf[] = null;
			int tlen = 0;
			int dlen = 0;
			int ptr = 0;

			while(docIdx < docs.size()) {
				if(termIdx ==0) {
					ArrayList<Integer> tempTerms = new ArrayList<Integer>();

					try {
						text = new String(Files.readAllBytes(Paths.get(docs.get(docIdx))), StandardCharsets.UTF_8);

						st = new StringTokenizer(text, " ");
						t = new int[st.countTokens()];
						tf = new int[t.length];
						Arrays.fill(t, -1);
						Arrays.fill(tf, 0);
						tlen = 0;
						dlen = 0;

						while(st.hasMoreTokens()) {
							token = StringUtility.refineToken(stemmer, st.nextToken());

							if(token == null) continue;

							int termID = Utility.search(table.getTermTable(), token);
							if(termID != -1) {
								if(termID==termIdx && postings.contains(docIdx) == false)
									postings.add(docIdx);

								tempTerms.add(termID); // terms in cur doc
								
							}
						}
					} catch(FileNotFoundException ex) {
						System.out.println("Unable to open file '" + docs.get(docIdx) + "'");
					} catch(IOException ex) {
						System.out.println("Error reading file '" + docs.get(docIdx) + "'");                  
					} 

					// ??? why if termIdx == 0 => 

					try {
						termsindocs[docIdx] = new int[tempTerms.size()];
						for(int n = 0; n<termsindocs[docIdx].length;n++)
							termsindocs[docIdx][n] = tempTerms.get(n).intValue();
						Arrays.sort(termsindocs[docIdx]);
						
					} catch(StringIndexOutOfBoundsException e) {
						System.out.println(e.getMessage() + " " + text);
					}



				} else {
					if(Utility.search(termsindocs[docIdx], termIdx) != -1)
						postings.add(docIdx);

				}
				docIdx++;
			}

			

			try {
				writePostings(postings);

			} catch(IOException e){
				e.printStackTrace();
			}

			table.setDocumentFrequency(termIdx, postings.size());
			table.setPostingsPointer(termIdx, pointer);
			pointer += postings.size();


			termIdx++;
			if(termIdx % 10 == 0)
				System.out.println(termIdx + " out of " + terms.length);
		}	

		try {
			postingsWriter.close();
			postingsTestWriter.close();
			System.out.println("***Postings List Created.");
		} catch(Exception e) {
			System.out.println("adfa");
			e.printStackTrace();
		}
	}



	public void writePostings(ArrayList<Integer> postings) throws IOException {

		for(Integer i: postings) {
			try {
				postingsTestWriter.write(i.toString() + " ");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		postingsTestWriter.newLine();


		for(Integer i: postings) {
			postingsWriter.writeInt(i.intValue()); 
		}

	}

	


}
