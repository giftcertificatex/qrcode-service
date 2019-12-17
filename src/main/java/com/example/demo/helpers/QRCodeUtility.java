package com.example.demo.helpers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtility {
	
	public static BufferedImage getQRCode(String content, int qrCodeSize) throws WriterException {
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		
		BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
		
		int matrixWidth = bitMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);

		Color mainColor = new Color(51, 102, 153);
		graphics.setColor(mainColor);
		 
		//Write Bit Matrix as image
		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (bitMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return image;
	}
	
	public static BufferedImage getQRCodeWithLogo(String content, int qrCodeSize, InputStream logoImg) throws WriterException, IOException {
		
		BufferedImage image = getQRCode(content, qrCodeSize);
		
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		
		BufferedImage logo = ImageIO.read(logoImg);
		double scale = calcScaleRate(image, logo);
		logo = getScaledImage( logo,
				(int)( logo.getWidth() * scale),
				(int)( logo.getHeight() * scale) );
		graphics.drawImage( logo,
				image.getWidth()/2 - logo.getWidth()/2,
				image.getHeight()/2 - logo.getHeight()/2,
				image.getWidth()/2 + logo.getWidth()/2,
				image.getHeight()/2 + logo.getHeight()/2,
				0, 0, logo.getWidth(), logo.getHeight(), null);
		
		return image;
	}
	
	private static double calcScaleRate(BufferedImage image, BufferedImage logo) {
		double scaleHeight = 1;
		double scaleWidth = 1;

		int imgHeight = image.getHeight();
		int imgWidth = image.getHeight();
		
		int logoHeight = logo.getHeight();
		int logoWidth = logo.getHeight();
		
		//Should not exceed 30%
		if((imgHeight / logoHeight) < 3) {
			scaleHeight = 1 / ( logoHeight / (imgHeight * 0.3));
		}
		
		if((imgWidth / logoWidth) < 3) {
			scaleWidth = 1 / ( logoWidth / (imgWidth * 0.3));
		}
		
		return scaleHeight < scaleWidth ? scaleHeight : scaleWidth;
	}

	private static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
		int imageWidth  = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double)width/imageWidth;
		double scaleY = (double)height/imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp( scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter( image, new BufferedImage(width, height, image.getType()));
	}
	
	private static boolean isQRCodeCorrect(String content, BufferedImage image){
		boolean result = false;
		Result qrResult = decode(image);
		if (qrResult != null && content != null && content.equals(qrResult.getText())){
			result = true;
		}		
		return result;
	}

	private static Result decode(BufferedImage image){
		if (image == null) {
			return null;
		}
		try {
			LuminanceSource source = new BufferedImageLuminanceSource(image);	      
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));	      
			Result result = new MultiFormatReader().decode(bitmap, Collections.EMPTY_MAP);	      
			return result;
		} catch (NotFoundException nfe) {
			return null;
		}
	}
	
	public static byte[] getImageBytes(BufferedImage originalImage, String imgExtension) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, imgExtension, baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

}
