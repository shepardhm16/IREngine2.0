////////////////////////////////////////////////////////////////////////////////
//  Cadet Holden M Shepard                                                    //
//  VMI CIS-443-01  AYSP16                                                    //
//  12 March 2016                                                             //
//  IREngine 2.0                                                              //
////////////////////////////////////////////////////////////////////////////////

package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class VMICrawler2 {
	private  static ArrayList<String> allLinks = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		if(addLinksFromFile("./vmi_link_final.txt") == false) {
			System.out.println("Critical Error: Cannot Open File");
			System.exit(1);
		}

		int i = 0;
		String url = null;
		Document doc = null;
		Elements elements = null;
		String contents = null;

		while( i<allLinks.size()) {
			contents = "";
			url = allLinks.get(i);
			System.out.println("processing " + i + "th url: " + url);
			try {
                            doc = Jsoup.connect(url).timeout(20000).get();  // Maybe increase timeout?
                            /*elements = doc.getAllElements();

                            for (Element e : elements) {
                                    contents += (e.text().trim() + " ");
                            }*/
                            contents = doc.outerHtml();
                           // contents = contents.replaceAll("[^a-zA-Z0-9 ]+"," ");
                            writeContentsToFile(i, contents);
			} catch(Exception e) {
                            System.out.println("Error: " + i + ":" + url + "[" + e.getMessage() + "]");
			}
			
			i++;
                        if(i%100 == 0)
                            System.out.println(i + "th documents collected.)");
		}	
		
		System.out.println("All done! (" + i + " documents collected.)");
	}

        public static void writeToFile(String filename, String content) {
            Writer out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
                out.write(content);
                out.close();
            } 
            catch (Exception e) {
            }
        }

        
	private static boolean addLinksFromFile(String fileName) {
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			/*while((line = bufferedReader.readLine()) != null) {
				allLinks.add(line);
			}*/ //Replaced with the below method
                        
                        int fileidx = 0;
                        while ((line = bufferedReader.readLine()) != null) {
                            allLinks.add(line.trim());
                        }
                        
			// Always close files.
			bufferedReader.close();         
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
			return false;
		} catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
			// Or we could just do this: 
			// ex.printStackTrace();
			return false;
		}

		return true;
	}


	private static void writeContentsToFile(int fileIndex, String contents) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("./docs/doc-"+fileIndex));
			writer.write(contents);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

