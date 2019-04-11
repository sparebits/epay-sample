/*
 * PaymentController.java
 * Created on 9.4.2019 17:36
 */
package com.sparebits.epay;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author nneikov
 */
@Controller
public class PaymentController {

	@Value("${sample.epay-url:https://demo.epay.bg/}")
	private String ePayUrl;

	@Value("${sample.merchant-id:}")
	private String merchantId;

	@Value("${sample.secret:}")
	private String secret;

	@GetMapping("/")
	public String paymentForm(Model model) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		model.addAttribute("epayUrl", ePayUrl);
		model.addAttribute("merchantId", merchantId);
		model.addAttribute("expires", LocalDate.now().plus(1, ChronoUnit.DAYS));
		
		String base64 = new String(Base64.getEncoder().encode(secret.getBytes()));
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		digest.update(base64.getBytes("UTF-8"), 0, base64.length());
		String checksum = DatatypeConverter.printHexBinary(digest.digest());
		model.addAttribute("secret", base64);
		model.addAttribute("checksum", checksum);
		return "payment-form";
	}

}
