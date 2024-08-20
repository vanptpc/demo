package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MediaController {

	@Value("${upload.path}")
	private String pathUploadMedia;

	@GetMapping("/loadImage")
	@ResponseBody
	public byte[] loadImage(@RequestParam("imageName") String imageName, HttpServletResponse response)
			throws IOException {
		response.setContentType("image/jpeg");
		return loadFile(imageName, response);
	}

	@GetMapping("/loadVideo")
	@ResponseBody
	public void loadVideo(@RequestParam("videoName") String videoName, HttpServletResponse response)
			throws IOException {
		response.setContentType("video/mp4");
		File file = new File(pathUploadMedia + File.separator + videoName);
		if (file.exists()) {
			InputStream inputStream = new FileInputStream(file);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
			inputStream.close();
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private byte[] loadFile(String fileName, HttpServletResponse response) throws IOException {
		File file = new File(pathUploadMedia + File.separator + fileName);
		InputStream inputStream = null;
		if (file.exists()) {
			try {
				inputStream = new FileInputStream(file);
				return IOUtils.toByteArray(inputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		return null;
	}

}
