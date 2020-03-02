package com.example.demo.service;

import com.example.demo.exception.StorageException;
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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

@Service
public class StorageService {

    @Value("${upload.path}")
    private String documentsPath;
    
    @Value("${info.path}")
    private String infoPath;
    
    
    public void uploadFile(String user, MultipartFile file) {

//        if (file.isEmpty()) {
//            throw new StorageException("Failed to store empty file");
//        }

        try {
        	String fileName = file.getOriginalFilename();
        	InputStream is = file.getInputStream();
        	String userDocumentsPath = documentsPath + user + "//";
        	updateUserInfo(user, fileName);
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
    	if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");
    	String userDocumentsPath = documentsPath + user + "//";
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
    		response.flushBuffer();
    		fis.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    
    public void updateUserInfo(String user, String fileName) {
    	String userInfoPath = infoPath + user + ".txt";
    	File file = new File(userInfoPath);
    	try {
    		file.createNewFile();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(userInfoPath));
			String line = reader.readLine();
			boolean foundDocument = false;
			while (line != null) {
				if (line.equals(fileName))
					foundDocument = true;
				// read next line
				line = reader.readLine();
			}
			if (!foundDocument) {
				FileWriter fr = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fr);
				System.out.println("@@" + fileName);
				bw.write(fileName);
				bw.newLine();
				bw.close();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    private void createDirIfNotExist(String dirPath) {
    	File thrDir = new File(dirPath);
    	if (!thrDir.exists()) thrDir.mkdir();
    }
    
    
    public LinkedList<String> getUserDocumentsList(String user) {
    	LinkedList<String> res = new LinkedList<>();
    	String userInfoPath = infoPath + user + ".txt";
    	File file = new File(userInfoPath);
    	try {
    		file.createNewFile();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(userInfoPath));
			String line = reader.readLine();
			boolean foundDocument = false;
			while (line != null) {
				res.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
    }
} 