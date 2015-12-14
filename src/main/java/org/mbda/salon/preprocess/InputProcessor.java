package org.mbda.salon.preprocess;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.tools.ant.DirectoryScanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InputProcessor implements InputProcessorInterface {
	private String inputFolderName = null;
	private String fileNamePattern = null;
	private String outputFolderName = null;
	private String lookupFlatFileName = null; 
	private String commentsFlatFileName = null; 
	private FileWriter lookupFileWriter = null;
	private FileWriter commentsFileWriter = null;
	int currentDocumentId = 0;
	int lastFileWriterInitialize=0;
	int FileSerialNumber=0;

	public InputProcessor(String input_folder_name, String file_name_pattern, String output_folder_name){
		inputFolderName = input_folder_name;
		fileNamePattern = file_name_pattern;
		outputFolderName = output_folder_name;
		lookupFlatFileName = outputFolderName + "tableTalkCommentsLookup" + Integer.toString(FileSerialNumber) + ".txt";
		commentsFlatFileName = outputFolderName + "tableTalkComments" + Integer.toString(FileSerialNumber) + ".txt";

	}

	public String[] getInputFiles(){
		System.out.println("Reading files from folder: " + inputFolderName );
		System.out.println("File Pattern: " + fileNamePattern);
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[]{fileNamePattern});
		scanner.setBasedir(inputFolderName);
		scanner.setCaseSensitive(false);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		
		return files;
	}
	
	public void InitializeFileWriters(){
		if (lookupFileWriter != null && commentsFileWriter != null){
			try{
			if ((currentDocumentId-lastFileWriterInitialize)>=500000){
				FileSerialNumber++;
				lookupFileWriter.close();
				commentsFileWriter.close();
				
				lookupFlatFileName = outputFolderName + "tableTalkCommentsLookup" + Integer.toString(FileSerialNumber) + ".txt";
				commentsFlatFileName = outputFolderName + "tableTalkComments" + Integer.toString(FileSerialNumber) + ".txt";
				
				lookupFileWriter = new FileWriter(lookupFlatFileName,true);
				commentsFileWriter = new FileWriter(commentsFlatFileName,true);
				lastFileWriterInitialize = currentDocumentId;
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return;
		}
		try{
			lookupFileWriter = new FileWriter(lookupFlatFileName,true);
			commentsFileWriter = new FileWriter(commentsFlatFileName,true);
			System.out.println("File Writer Encoding: " + commentsFileWriter.getEncoding());
				
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void processInputFile(String fileName){
		String htmlContent= null;
		//ArrayList<String> processedStringList = new ArrayList<String>();
		try {
			String completeFileName = inputFolderName + fileName;
			htmlContent = readFile(completeFileName, Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InitializeFileWriters();
		
		Document doc = Jsoup.parse(htmlContent);
		//Element content = doc.getElementById("div");
		Elements divContentElements = doc.getElementsByClass("mlMsg");
		
		String lookupString=null;
		String commentString=null;
		try{		    
			for (Element cntnt : divContentElements){
				//System.out.println("Number of ChildNodes: " + cntnt.childNodeSize());
				Elements size4Elements = cntnt.getElementsByClass("size4");
				//System.out.println("size4Elements size: " + size4Elements.size());
				if (size4Elements.size() < 2){
					continue;
				}
				Element nameElement = size4Elements.get(0);
				Element commentElement = size4Elements.get(1);
				
				Elements size3Elements = cntnt.getElementsByClass("size3");
				if (size3Elements.size() < 1){
					continue;
				}
				Element timeElement = size3Elements.get(0);
				currentDocumentId++;
				lookupString = Integer.toString(currentDocumentId) + "||"+nameElement.text() + "||" + timeElement.text() + "||" + fileName + "\n";
				lookupFileWriter.write(lookupString);
				commentString = Integer.toString(currentDocumentId) + "||"+commentElement.text().replace('\n', ' ').replace("\r", "") + "\n";
				commentsFileWriter.write(commentString);
				//System.out.println(nameElement.text() + "----------------->" + timeElement.text() + "-------->" + commentElement.text());
				lookupFileWriter.flush();
				commentsFileWriter.flush();
			}
		} catch(Exception e){
			System.out.println("Exception while extracting child nodes: " + fileName);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String readFile(String path, Charset encoding) 
	throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	

}
