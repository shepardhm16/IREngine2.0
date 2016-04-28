/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import utility.FileNames;

/**
 *
 * @author Holden
 */
public class FileContentsWriter {
    static BufferedWriter[] writer;
    public static void writeContentsFile(String tag, String cls, String text) {
        for(int i=0; i<FileNames.DIRS.length;i++) {
            if(FileNames.DIRS[i].equalsIgnoreCase(tag) ||
                 FileNames.DIRS[i].equalsIgnoreCase(cls)) {
                    try {
                        writer[i].write(text);
                    } catch (IOException e) {}
                    break;
                }
        }
        try {
            if(tag.equalsIgnoreCase("body"))
                writer[0].write(text);
        } catch (IOException e) {}
    }
    
    public static void init(int idx) {
        try {
            if(writer != null) 
            for(int i=0; i<FileNames.DIRS.length;i++) 
                writer[i].close();
            writer = new BufferedWriter[FileNames.DIRS.length];

            for(int i=0; i<FileNames.DIRS.length;i++) 
                writer[i] = new BufferedWriter(new FileWriter("./data/contents/"+FileNames.DIRS[i] +"/doc-"+idx));
        } catch(Exception e) {}
    }

}