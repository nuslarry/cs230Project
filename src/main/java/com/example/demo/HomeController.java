package com.example.demo;

import com.example.demo.exception.StorageException;
import com.example.demo.service.StorageService;


import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller 
public class HomeController {
	@Autowired
    private StorageService storageService;
//	@RequestMapping("home")
//	public ModelAndView home(@RequestParam("name")String myName, HttpSession session) { //can even acept an object 1:19:44
//		ModelAndView mv = new ModelAndView();
//		System.out.println("Hi " + myName);
//		session.setAttribute("name", myName);
//		mv.addObject("name", myName);
//		mv.setViewName("home");
//		return mv; 
//	}
	@RequestMapping("home")
	public ModelAndView home(@RequestParam("user")String user, HttpSession session) { //can even acept an object 1:19:44
		return getUserHome(user);
	}
	
	public ModelAndView getUserHome(String user) {
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
    public String upload(@RequestParam("user")String user, @RequestParam MultipartFile file) {

        storageService.uploadFile(user, file);

        //System.out.println("Success");
        //return "redirect:/success.html";
        return "redirect:/home?user="+user;
    }
    
    @RequestMapping(value = "/download")
    public ModelAndView download(@RequestParam("user")String user, @RequestParam("downloadFileName") String downloadFileName,@RequestParam(value = "downloadSharedUser",required = false) String sharedUser,HttpServletResponse response) {
		if(downloadFileName == null || downloadFileName.isEmpty())
			return null;
		System.out.println(sharedUser);
		if(sharedUser == null)
			storageService.downloadFile(user, downloadFileName, response);
		else
			storageService.downloadFile(sharedUser,downloadFileName,response);
    	return null;
    }

	@RequestMapping(value = "/share")
	public String share(@RequestParam("user")String user, @RequestParam("fileToShare") String fileToShare,@RequestParam("shareWith") String shareWith, HttpServletResponse response) {
		String result = storageService.updateUserInfo(user,fileToShare,shareWith);
		return "redirect:/home?user="+user;
	}
    
    
    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(StorageException e) {
    	e.printStackTrace(); 
        return "redirect:/failure.html";
    }
}
