//package aiss.main;
//
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayInputStream;
//import java.io.EOFException;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import java.util.zip.ZipOutputStream;
//
//public class ZipManager {
//
//
//	private static final boolean DEBUG = true;
//	List<String> fileList;
//	private static final String OUTPUT_ZIP_FILE = "in.zip";
//	private static final String OUTPUT_FOLDER = new File("").getAbsolutePath() + "/out";
//	private static final String SOURCE_FOLDER = new File("").getAbsolutePath() + "/in";
//
//	ZipManager(){
//		fileList = new ArrayList<String>();
//	}
//
//	public static void main( String[] args )
//	{
//		//ZipManager zip = new ZipManager();
//		//zip.generateFileList(new File(SOURCE_FOLDER));
//		//zip.zipIt(OUTPUT_ZIP_FILE);
//	}
//
//
//	/**
//     * Unzip it
//     * @param zipFile input zip file
//     * @param output zip file output folder
//     */
//    public void unZipIt(String zipFile){
// 
//     byte[] buffer = new byte[1024];
// 
//     try{
// 
//    	//create output directory is not exists
//    	File folder = new File(OUTPUT_FOLDER);
//    	if(!folder.exists()){
//    		folder.mkdir();
//    	}
// 
//    	//get the zip file content
//    	ZipInputStream zis = 
//    		new ZipInputStream(new FileInputStream(zipFile));
//    	//get the zipped file list entry
//    	ZipEntry ze = zis.getNextEntry();
//
//			while(ze!=null){
//
//				String fileName = ze.getName();
//				String outPath = OUTPUT_FOLDER + File.separator + fileName;
//				debug(outPath);
//				File newFile = new File(outPath);
//
//				System.out.println("file unzip : "+ newFile.getAbsoluteFile());
//
//				//create all non exists folders
//				//else you will hit FileNotFoundException for compressed folder
//				new File(newFile.getParent()).mkdirs();
//
//				FileOutputStream fos = new FileOutputStream(newFile);             
//				BufferedOutputStream bf = new BufferedOutputStream(fos);
//				
//				try{
//				zis.read(buffer);
//				}catch (EOFException e){
//					for(int i = 1; i < 1024; i++){
//						System.out.print(buffer[i]);
//					}
//				}
//				bf.write(buffer);
//				
//				
//				
//				
//				ze = zis.getNextEntry();
//			}
//
//			zis.closeEntry();
//			zis.close();
//
//			System.out.println("Done");
//
//		}catch(IOException ex){
//			ex.printStackTrace(); 
//		}
//	}    
//
//	/**
//	 * Zip it
//	 * @param zipFile output ZIP file location
//	 */
//	public void zipIt(String zipFilePath){
//
//		byte[] buffer = new byte[1024];
//
//		try{
//
//			FileOutputStream fos = new FileOutputStream(zipFilePath);
//			ZipOutputStream zos = new ZipOutputStream(fos);
//
//			debug("Output to Zip : " + zipFilePath);
//
//			for(String file : this.fileList){
//
//				debug("File Added : " + file);
//				ZipEntry ze = new ZipEntry(file);
//				zos.putNextEntry(ze);
//
//				FileInputStream in = 
//						new FileInputStream(SOURCE_FOLDER + File.separator + file);
//
//				int len;
//				while ((len = in.read(buffer)) > 0) {
//					zos.write(buffer, 0, len);
//				}
//
//				in.close();
//			}
//
//			zos.closeEntry();
//			//remember close it
//			zos.close();
//
//			debug("Done");
//		}catch(IOException ex){
//			ex.printStackTrace();   
//		}
//	}
//
//	/**
//	 * Traverse a directory and get all files,
//	 * and add the file into fileList  
//	 * @param node file or directory
//	 */
//	public void generateFileList(File node){
//
//		//add file only
//		if(node.isFile()){
//			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
//
//		}
//
//		if(node.isDirectory()){
//			String[] subNote = node.list();
//			for(String filename : subNote){
//				generateFileList(new File(node, filename));
//			}
//		}
//
//	}
//
//	/**
//	 * Format the file path for zip
//	 * @param file file path
//	 * @return Formatted file path
//	 */
//	private String generateZipEntry(String file){
//		return file.substring(SOURCE_FOLDER.length()+1, file.length());
//	}
//
//	private static void debug(String log){
//		if(DEBUG){
//			System.out.println(log);
//		}
//	}
//
//	public void zipInputFolderTo(String zipFilePath) {
//		generateFileList(new File(SOURCE_FOLDER));
//		zipIt(zipFilePath);
//	}
//}