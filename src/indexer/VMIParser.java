////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

package indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utility.FileNames;


public class VMIParser {
	TermTable termTable = new TermTable();
	PostingsBuilder postings = new PostingsBuilder();
        Indexer indexer = new Indexer();

	public static void main(String[] args) {

		VMIParser parser = new VMIParser();
		String dir = "./data/contents/all"; //might be ./docs/
		parser.buildTermTable(dir);  // When running the parser.buildPostings(dir), comment this line out
                dir = "./docs/";
		//parser.buildPostings(dir); // When running the parser.buildTermTable(dir), comment this line out
                parser.buildIndexing();
		try {
			parser.writeTermTableToFile();
		} catch(Exception e) {
			System.out.println("Cannot Open File to Write!!!");
		}
		
	}
	
	
	public void buildTermTable(String dir) {
		//termTable.build(dir);
            try {
			FileReader fileReader = new FileReader(FileNames.TERMS);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
                        ArrayList<String> table = new ArrayList<String>();
                        String line = null;
			while((line = bufferedReader.readLine()) != null) {
				table.add(line.trim());
			}   

			termTable.initTermTable(table.size());
			for(int i=0; i<table.size();i++)
				termTable.addTerm(table.get(i), i);

			bufferedReader.close();
             } catch(Exception e) {
                 e.printStackTrace();
             }
            System.out.println("TermTable Initialized");
	}
	
	public void buildPostings(String dir) {
                postings.init();
		postings.build(dir, this.termTable);
		postings.finalize();
		
	}
        
        public void buildIndexing() {
            indexer.init();
            indexer.build(this.termTable);
            indexer.finalize();
		
	}
	
	public void writeTermTableToFile() throws IOException {
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(FileNames.TERMS));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(FileNames.DOCFREQ));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(FileNames.POSTINGSPTR));
		
		String[] terms = termTable.getTermTable();
		int[] docFreq = termTable.getDocumentFrequencies();
		int[] postingsPtr = termTable.getPostingsPointers();
		
		
		for(String str: terms) {			
			try {
				writer1.write(str);
				writer1.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		writer1.close();
		for(int i: docFreq) {			
			try {
				writer2.write(i + "");
				writer2.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		writer2.close();
		
		for(int i: postingsPtr) {			
			try {
				writer3.write(i + "");
				
				writer3.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	
		writer3.close();
	}
}