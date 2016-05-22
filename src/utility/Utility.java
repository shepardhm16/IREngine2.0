////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

package utility;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;



public class Utility {

	public static ArrayList<String> getAllDocuments(String dir) throws IOException {
		ArrayList<String> docs = new ArrayList<String>();

		Files.walk(Paths.get(dir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				docs.add(filePath.toString());
			}
		});
		return docs;
	}
        
        public static ArrayList<String> getAllDocuments2(String dir) throws IOException {
		ArrayList<String> docs2 = new ArrayList<String>();

		Files.walk(Paths.get(dir)).forEach(filePath -> {
				docs2.add(filePath.toString());
		});
		return docs2;
	}

	public static int search(String[] tree, String key) {  //Add a BS for Int
		int low = 0;
		int high = tree.length - 1;
		int mid;
		int v;
		while (low <= high) {
			mid = (low + high) / 2;
			v = tree[mid].compareTo(key);
			if      (v > 0) high = mid - 1;
			else if (v < 0) low = mid + 1;
			else return mid;
		}
		return -1;
	}

        public static int search(int[] tree, int key) {  //Add a BS for Int
		int low = 0;
		int high = tree.length - 1;
		int mid;
		int v;
		while (low <= high) {
			mid = (low + high) / 2;
			if      (key<tree[mid]) high = mid - 1;
			else if (key > tree[mid]) low = mid + 1;
			else return mid;
		}
		return -1;
	}
        
	public static void createDirectory(String path) {
		File theDir = new File(path);

		// if the directory does not exist, create it
		if (!theDir.exists()) {

			try{
				theDir.mkdir();
			} 
			catch(SecurityException se){
				//handle it
			}        
		}
	}
	
	
	
	
}