package com.example.demo;

import com.example.demo.exception.StorageException;
import com.example.demo.service.StorageService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Stopwatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;



@Controller 
public class HomeController {
	@Autowired
    private StorageService storageService;

	@RequestMapping("home")
	public ModelAndView home(@RequestParam("user")String user, HttpSession session) throws IOException { //can even acept an object 1:19:44
		return getUserHome(user);
	}
	
	public ModelAndView getUserHome(String user) throws IOException {
		ModelAndView mv = new ModelAndView();
		ArrayList<String[]> list = storageService.getUserDocumentsList(user);
		mv.addObject("user", user);
		mv.addObject("list", list);
		ArrayList<String[]> sharedList = storageService.getSharedDocumentsList(user);
		mv.addObject("sharedList", sharedList);
		//System.out.println(sharedList.size());
		mv.setViewName("home");
		return mv; 
	}
	

    @RequestMapping(value = "/doUpload", method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    public String upload(@RequestParam("user")String user,@RequestParam("replications") int replications, @RequestParam MultipartFile file) {
		Stopwatch sw = Stopwatch.createStarted();
        storageService.uploadFile(user,replications, file);
        sw.stop();
        System.out.println("Time for uploading = "+sw.elapsed(TimeUnit.MILLISECONDS));
        return "redirect:/home?user="+user;
    }
    
    @RequestMapping(value = "/download")
    public ModelAndView download(@RequestParam("user")String user, @RequestParam("downloadFileName") String downloadFileName,@RequestParam(value = "downloadSharedUser",required = false) String sharedUser,HttpServletResponse response) throws IOException {
		if(downloadFileName == null || downloadFileName.isEmpty())
			return null;
		Stopwatch sw = Stopwatch.createStarted();
		if(sharedUser == null)
			storageService.downloadFile(user, downloadFileName, response);
		else
			storageService.downloadFile(sharedUser,downloadFileName,response);
		sw.stop();
		System.out.println("Time for downloading = "+sw.elapsed(TimeUnit.MILLISECONDS));
    	return null;
    }

	@RequestMapping(value = "/share")
	public String share(@RequestParam("user")String user, @RequestParam("fileToShare") String fileToShare,@RequestParam("shareWith") String shareWith, HttpServletResponse response) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		String result = storageService.updateUserInfo(user,fileToShare,shareWith);
		sw.stop();
		System.out.println("Time for sharing = "+sw.elapsed(TimeUnit.MILLISECONDS));
		return "redirect:/home?user="+user;
	}

	@RequestMapping(value = "/delete")
	public String delete(@RequestParam("user")String user, @RequestParam("fileToDelete") String fileToDelete, HttpServletResponse response) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		storageService.deleteFile(user,fileToDelete);
		sw.stop();
		System.out.println("Time for deleting = "+sw.elapsed(TimeUnit.MILLISECONDS));
		return "redirect:/home?user="+user;
	}
    
    
    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(StorageException e) {
    	e.printStackTrace(); 
        return "redirect:/failure.html";
    }
}
