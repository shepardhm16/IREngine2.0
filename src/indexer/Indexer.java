////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

// MUST CHANGE THIS FILE FOR IT TO WORK; OTHERWISE, IT WILL TAKE A WEEK TO RUN

package indexer;

// We need to create filename-docID pair (0=0, 1=1, 10=2, 100=3, ..)
/* 
read all filenames in the directory; 
for (i = 0, to ..) {
    string str  = docname[i];
    docidx = i;
    filenumber < 0;
*/

// Second Problem:
/*
http://staging.vmi.edu/contents/aspx.id...
and
http://www.vmi.edu/contents/aspx.id...

If they have the same ID, then remove one of them, since they are the same document

Also, for indexing, we may want to use active/selected menu's and description for indexing
*/

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
import static java.util.Arrays.sort;
import java.util.StringTokenizer;

import utility.FileNames;
import utility.StringUtility;
import utility.Utility;

public class Indexer {
	
	DataOutputStream[] indexingWriter = null;
	BufferedWriter indexingTestWriter = null; // indexing test file
	BufferedWriter[] docPtrWriter = null;
	BufferedWriter metaFileWriter = null;
	TermTable table = null;
        int termsInDocs[][] = null;
        
	public void init() {
		try {
                    indexingWriter = new DataOutputStream[FileNames.DIRS.length];
                    docPtrWriter = new BufferedWriter[FileNames.DIRS.length];
                    for(int i=0; i<FileNames.DIRS.length;i++) {
                        indexingWriter[i] = 
                                new DataOutputStream(new FileOutputStream(FileNames.INDEXING + "-" + FileNames.DIRS[i]));
                        docPtrWriter[i] = 
                                new BufferedWriter(new FileWriter(FileNames.DOCPTR + "-" + FileNames.DIRS[i]));
                    }
			
			indexingTestWriter = new BufferedWriter(new FileWriter(FileNames.INDEXING + ".test"));
			metaFileWriter = new BufferedWriter(new FileWriter(FileNames.METAFILE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// close all files 
	public void finalize() {
		try {
                    for(int i=0;i<FileNames.DIRS.length;i++) {
			indexingWriter[i].close();
			docPtrWriter[i].close();
                    }
                    indexingTestWriter.close();
                    metaFileWriter.close();
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
     * @param tbl
	 */
	 
	public void build(TermTable tbl) {
		this.table = tbl;
		int docIdx = 0;
		long avgDocLen = 0;
		Stemmer stemmer = new Stemmer();
		String[] terms = table.getTermTable();
		
		ArrayList<String> docs = null;
                
		
		String text = null;
		StringTokenizer st = null;
		String token = null;

		int[][] docptr = new int[FileNames.DIRS.length][];
		
                for(int dirIdx=0; dirIdx<FileNames.DIRS.length;dirIdx++) {
                   
                    String dir = "./data/contents/";
                    try {
			docs = Utility.getAllDocuments(dir + FileNames.DIRS[dirIdx]);
                    } catch (IOException e) {
			System.out.println("Unable to open files." + e.getMessage());
			return;
                    }
                    docptr[dirIdx] = new int[docs.size()];
			docIdx = 0;
			int t[] = null;
			int tf[] = null;
			int tlen = 0;
			int dlen = 0;
			int ptr = 0;
			
			while(docIdx < docs.size()) {
                            ArrayList<Integer> tempTerms = new ArrayList<>();	
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
                                              dlen++; // increase doc length
                                              tempTerms.add(termID);

                                            for(int i=0; i<=tlen;i++) {
                                                    if(t[i] == -1) {
                                                            t[i] = termID;
                                                            tf[i]++;
                                                            tlen++;
                                                            break;
                                                    } else if(t[i] == termID) {
                                                            tf[i]++;
                                                            break;
                                                    }
                                            }
                                    }
                            }
                
                        } catch(FileNotFoundException ex) {
                                System.out.println("Unable to open file '" + docs.get(docIdx) + "'");
                         } catch(IOException ex) {
                                    System.out.println("Error reading file '" + docs.get(docIdx) + "'");                  
                         } 
                         try {
                            writeIndexing(dirIdx, docIdx, dlen, tlen, t, tf, table);  // Write the docID, doc length, term length, term, and term frequency to the table
                         } catch (IOException ioe) {
                             ioe.printStackTrace();
                         }
                            docptr[dirIdx][docIdx] = ptr;  // Sets the pointer in the docptr array
                         ptr += (1+1+1+2*(tlen));  //Increments the pointer in the index based on the term length
                         docIdx++;
                         
                         if(docIdx%100 ==0) System.out.println(docIdx + " processed for " + dirIdx);
                        }
                        
                }
               
		try {
			 writeDocPtr(docptr);
			writemetaFileWriter(docIdx, avgDocLen);
			for(int i=0; i<FileNames.DIRS.length;i++) {
                            indexingWriter[i].close();
                            docPtrWriter[i].close();
                        }
			indexingTestWriter.close();
			metaFileWriter.close();
			System.out.println("***Indexing List Created.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDocPtr(int[][] ptr) throws IOException {

		for(int i=0; i<ptr.length;i++) {
                    for(int j=0; j<ptr[i].length;j++) {
			try {
			docPtrWriter[i].write(ptr[i][j] + "");
			docPtrWriter[i].newLine();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
                    }
		}
	}
        
	public void writemetaFileWriter(int noOfDocs, long avgDocLen)
			throws IOException {

		try {
			metaFileWriter.write(noOfDocs + "");
			metaFileWriter.newLine();
			metaFileWriter.write((float)(avgDocLen/noOfDocs) + "");
			metaFileWriter.newLine();
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

        
	public void writeIndexing(int dirIndex, int docID, int dlen, int tlen, int[] t, int[] tf, TermTable table) throws IOException {

		
		indexingWriter[dirIndex].writeInt(docID);
		indexingWriter[dirIndex].writeInt(dlen);
		indexingWriter[dirIndex].writeInt(tlen);
		for(int i=0;i<tlen;i++) {
			indexingWriter[dirIndex].writeInt(t[i]); 
			indexingWriter[dirIndex].writeInt(tf[i]);
		}

	}


}
