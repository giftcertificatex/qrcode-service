package com.example.demo.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.helpers.QRCodeUtility;
import com.google.zxing.WriterException;


@RestController
public class TestRestController {
	@Autowired
	private ResourceLoader resourceLoader;

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@GetMapping("/")
	public String getIndex() {
		return "Hello! The list of available Get requests: <br> /image <br> /qrcode <br> /qrcode/logo";
	}
	
	@GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getSampleImage() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:9101.jpg");
		InputStream in = resource.getInputStream();
		return IOUtils.toByteArray(in);
	}
	
	@GetMapping(value = "/qrcode", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getQRCode() throws IOException, WriterException {
		BufferedImage img = QRCodeUtility.getQRCode("This is sample QRCode text!", 350);
		byte[] imgBytes = QRCodeUtility.getImageBytes(img, "jpg");
		return imgBytes;
	}
	
	@GetMapping(value = "/qrcode/logo", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getQRCodeWithLogo() throws IOException, WriterException {
		Resource resource = resourceLoader.getResource("classpath:9101.jpg");
		try (InputStream in = resource.getInputStream()) {
		BufferedImage img = QRCodeUtility.getQRCodeWithLogo("This is sample QRCode text!", 350, in);
		byte[] imgBytes = QRCodeUtility.getImageBytes(img, "jpg");
		return imgBytes;
		}
	}
}
