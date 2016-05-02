package utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class BinaryConvert {
	
	public static void main(String[] args) {
		convertFileToBinary("./docsterms","./postingsPointer");
	}

	private static void convertFileToBinary(String oldPath, String newPath) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		DataOutputStream dos = null;
		
		try {
			br = new BufferedReader(new FileReader(oldPath));
			dos = new DataOutputStream(new FileOutputStream(newPath));
			int i = 0;
			int j = 0;
			while(br.readLine() != null) {
				i++;
				System.out.println(j);
				String[] current = br.readLine().split(" ");
				dos.writeInt(j);
				j++;
//				for(String s: current)
//					try {
//						//dos.writeInt(Integer.parseInt(s));
//					}catch(NumberFormatException e) {
//						System.out.println("SKIP: " + i);
//					}
			}
			
			dos.flush();
			dos.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
