package engine.ranking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;

public class IndexingPuller {
	private static ArrayList<Integer> termIndexes = new ArrayList<Integer>();
	private static ArrayList<Integer> postingsPointersStart = new ArrayList<>();
	private static ArrayList<Integer> postingsPointerDistance = new ArrayList<>();
	private static ArrayList<Integer> docNumbers = new ArrayList<>();
	
	public static void main(String[] args) {
		String[] a = {"abetelectron"};
		getPostingsDocs(a);
	}
	
	public static void getPostingsDocs(String[] a) {
		TreeMap<String,Integer> termIndex = new TreeMap<>();
		BufferedReader br = null;
		String line = "";
		for(String str : a)
			termIndex.put(str, null);
		
		//gets indexes of terms and stores in maps
		try {
			br = new BufferedReader(new FileReader("./data/terms"));
			int index = 0; 
			for(String str: a)
				while((line = br.readLine()) != null) {
					if(line.equals(str)) {
						termIndex.put(str, index);
						termIndexes.add(index);
						System.out.println("index: " + index);
					}
					index++;	
				}
		br.close();
		pullPostingPointers();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//gets the start of where docs are held for terms and how many docs for the term(distance)
	private static void pullPostingPointers() {
		DataInputStream dis = null;
		
		try {
			dis = new DataInputStream(new FileInputStream("./postingsPointer"));
			int count = 0;
			int distance = 0;
			for(Integer i : termIndexes) {
				for(int j = 0; j<=i; j++) {
					dis.readInt();
					count = dis.readInt();
					dis.readInt();
					distance = dis.readInt() - count;
				}
				System.out.println("count: " + count);
				System.out.println("distance: " + distance);
				postingsPointersStart.add(count);
				postingsPointerDistance.add(distance);
				count =0;
				distance = 0;
			}
			
			dis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		pullDocNumbers();
		
	}

	//gets the doc numbers that will need to be pulled from the indexing file and stores in docNumbers list
	private static void pullDocNumbers() {
		DataInputStream dis = null;
		
		try {
			dis = new DataInputStream(new FileInputStream("./postings"));
			int docNum = 0;
			for (int x = 0; x < postingsPointersStart.size(); x++)
				for (Integer i : postingsPointersStart) {
					for(int y = 0 ; y<i ; y++)
						dis.readInt();
					for (Integer j : postingsPointerDistance) {
						for (int k = 0; k < j; k++) {
							docNum = dis.readInt();
							System.out.println("docNum: " + docNum);
							docNumbers.add(docNum);
						}
					}
				}
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	//Make method to find out what number is on the line of the doc numbers (line 2 will be 10) 
	//EX: use number 10 to represent doc 2 to find the right location in indexing
	
	
	
	
	//Create another mehtod to go through indexing file and pull out the information needed for BM25
	//Once finding the doc start point, you will have to count the number of ints to read based on 
	// how many terms are in that doc which is told by the third int in the sequence.

}
