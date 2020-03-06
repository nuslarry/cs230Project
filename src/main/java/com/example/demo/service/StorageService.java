package com.example.demo.service;

import com.example.demo.exception.StorageException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

@Service
public class StorageService {

    @Value("${upload.path}")
    private String documentsPath;

	@Value("${metadata.path}")
	private String metadataPath;


    
    
    public void uploadFile(String user, MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new StorageException("Failed to store empty file");
//        }
        try {
        	String fileName = file.getOriginalFilename();
        	InputStream is = file.getInputStream();
        	String userDocumentsPath = documentsPath + user + "//";
        	//System.out.println(userDocumentsPath);
        	createDirIfNotExist(userDocumentsPath);
            Files.copy(is, Paths.get(userDocumentsPath + fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
        	String msg = String.format("Failed to store file", file.getName());
            throw new StorageException(msg, e);
        }

    }
    public void downloadFile(String user, String fileName, HttpServletResponse response) {
    	String userDocumentsPath = documentsPath + user + "//";
    	if(!new File(userDocumentsPath).exists())return;
		if (fileName.contains(".pdf")) response.setContentType("application/pdf");
    	response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    	response.setHeader("Content-Transfer-Encoding", "binary");
    	try {
    		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
    		FileInputStream fis = new FileInputStream(userDocumentsPath + fileName);
    		int len;
    		byte[] buf = new byte[1024];
    		while ((len = fis.read(buf)) > 0) {
    			bos.write(buf, 0, len);
    		}
    		bos.close();
    		fis.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    
    public String updateUserInfo(String user, String fileName, String sharedWith) {
    	String filePath = documentsPath + user+"//"+fileName;
    	if(!new File(filePath).exists())return "Share Failed: File does not exist";
    	String userInfoPath = metadataPath + sharedWith + "//metadata.txt";
		createDirIfNotExist(metadataPath + sharedWith);
    	File file = new File(userInfoPath);
    	if(!file.exists()){
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(userInfoPath));
			String line;
			boolean found = false;
			while((line = reader.readLine())!=null) {
				String[] data = line.split(",");
				if(data[0].equals(fileName) && data[1].equals(user)){
					found = true;
					break;
				}
			}
			reader.close();
			if(!found){
				BufferedWriter writer = new BufferedWriter(new FileWriter(userInfoPath,true));
				writer.write(fileName+","+user+"\n");
				writer.close();
				return "Share Succeed";
			}else{
				return "Share Failed: File has already shared with user: "+sharedWith;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Share Failed: Unknown Reason";
    }
    private void createDirIfNotExist(String dirPath) {
    	File thrDir = new File(dirPath);
    	if (!thrDir.exists()){
    		createDirIfNotExist(thrDir.getParent());
    		thrDir.mkdir();
		}
    }
    
    
    public ArrayList<String[]> getUserDocumentsList(String user) {
    	File folder = new File(documentsPath + user);
    	File[] files = folder.listFiles();
		ArrayList<String[]> results = new ArrayList<>();
    	if(files!=null){
			for(int i=0;i<files.length;++i){
				results.add(new String[]{files[i].getName(),files[i].length()/1024+" KB"});
			}
		}
    	return results;
    }


	public ArrayList<String[]> getSharedDocumentsList(String user) {
		File metadata = new File(metadataPath + user + "//metadata.txt");
		ArrayList<String[]> results = new ArrayList<>();
		if(metadata.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(metadata));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] data = line.split(",");
					File f = new File(documentsPath + data[1] + "//" + data[0]);
					results.add(new String[]{data[0], data[1], f.length() / 1024 +" KB"});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return results;
	}
} 