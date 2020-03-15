package com.example.demo.service;

import com.example.demo.exception.StorageException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@Service
public class StorageService {

    @Value("${upload.path}")
    private String documentsPath;

	@Value("${metadata.path}")
	private String metadataPath;


    
    
    public void uploadFile(String user,int replications, MultipartFile file) {
        try {
        	String fileName = file.getOriginalFilename();
        	InputStream is = file.getInputStream();
        	String userDocumentsPath = documentsPath + user + "/";
        	HDFSAccess.getInstance().createDirIfNotExist(userDocumentsPath);
            HDFSAccess.getInstance().uploadFile(is, userDocumentsPath + fileName,replications);
        } catch (IOException e) {
        	String msg = String.format("Failed to store file", file.getName());
            throw new StorageException(msg, e);
        }

    }
    public void downloadFile(String user, String fileName, HttpServletResponse response) throws IOException {
    	String userDocumentsPath = documentsPath + user + "/";
    	if(!HDFSAccess.getInstance().exists(userDocumentsPath))return;
		if (fileName.contains(".pdf")) response.setContentType("application/pdf");
    	response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    	response.setHeader("Content-Transfer-Encoding", "binary");
		if(!HDFSAccess.getInstance().exists(userDocumentsPath + fileName))return;
    	try {
    		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
    		InputStream fis = HDFSAccess.getInstance().readFile(userDocumentsPath + fileName);
			IOUtils.copy(fis,bos);
    		bos.close();
    		fis.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }


	public String updateUserInfo(String user, String fileName, String sharedWith) throws IOException {
		String filePath = documentsPath + user+"/"+fileName;
		if(!HDFSAccess.getInstance().exists(filePath))return "Share Failed: File does not exist";
		String userInfoPath = metadataPath + sharedWith + "/metadata.txt";
		HDFSAccess.getInstance().createDirIfNotExist(metadataPath + sharedWith);
		if(!HDFSAccess.getInstance().exists(userInfoPath)){
			try {
				HDFSAccess.getInstance().createFile(userInfoPath, 3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(HDFSAccess.getInstance().readFile(userInfoPath)));
			BufferedWriter writer = new BufferedWriter(new FileWriter(sharedWith+"-tempfile"));
			String line;
			boolean found = false;
			while((line = reader.readLine())!=null) {
				writer.write(line+"\n");
				String[] data = line.split(",");
				if(data[0].equals(fileName) && data[1].equals(user)){
					found = true;
					break;
				}
			}
			reader.close();
			if(!found){
				writer.write(fileName+","+user+"\n");
				writer.close();
				HDFSAccess.getInstance().uploadFile(new FileInputStream(sharedWith+"-tempfile"), userInfoPath,3);
				new File(sharedWith+"-tempfile").delete();
				return "Share Succeed";
			}else{
				writer.close();
				new File(sharedWith+"-tempfile").delete();
				return "Share Failed: File has already shared with user: "+sharedWith;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Share Failed: Unknown Reason";
	}
    
    
    public ArrayList<String[]> getUserDocumentsList(String user) throws IOException {
		ArrayList<String[]> results = new ArrayList<>();
    	if(HDFSAccess.getInstance().exists(documentsPath + user)){
			RemoteIterator<LocatedFileStatus> status = HDFSAccess.getInstance().listFiles(documentsPath + user);
			while(status.hasNext()){
				LocatedFileStatus fs = status.next();
				results.add(new String[]{fs.getPath().getName(),byteToSize(fs.getLen())});
			}
		}
    	return results;
    }

    private String byteToSize(Long numBytes){
    	if(numBytes>=1024){
    		numBytes/=1024;
    		if(numBytes>=1024){
    			numBytes/=1024;
    			if(numBytes>=1024){
    				numBytes/=1024;
    				return numBytes +" GB";
				}else{
    				return numBytes +" MB";
				}
			}else{
    			return numBytes +" KB";
			}
		}else{
    		return numBytes+" Bytes";
		}
	}

	public void deleteFile(String user, String fileName) throws IOException {
		String filePath = documentsPath + user+"/"+fileName;
		HDFSAccess.getInstance().deleteFile(filePath);
	}


	public ArrayList<String[]> getSharedDocumentsList(String user) throws IOException {
		ArrayList<String[]> results = new ArrayList<>();
		if(HDFSAccess.getInstance().exists(metadataPath + user + "/metadata.txt")) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(HDFSAccess.getInstance().readFile(metadataPath + user + "/metadata.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] data = line.split(",");
					if(HDFSAccess.getInstance().exists(documentsPath + data[1] + "/" + data[0])){
						FileStatus fs = HDFSAccess.getInstance().getFileStatus(documentsPath + data[1] + "/" + data[0]);
						results.add(new String[]{data[0], data[1], byteToSize(fs.getLen())});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return results;
	}
} 