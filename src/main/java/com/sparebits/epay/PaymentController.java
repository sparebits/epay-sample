/*
 * PaymentController.java
 * Created on 9.4.2019 17:36
 */
package com.sparebits.epay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Formatter;

/**
 * @author nneikov
 */
@Controller
public class PaymentController {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";


    @Value("${sample.epay-url:https://demo.epay.bg/}")
    private String ePayUrl;

    @Value("${sample.merchant-id:}")
    private String merchantId;

    @Value("${sample.secret:}")
    private String secret;

    @Autowired
    private HttpServletRequest request;


    @GetMapping("/")
    public String paymentForm(Model model) throws NoSuchAlgorithmException, InvalidKeyException {

        StringBuilder data = new StringBuilder();
        data.append("MIN=").append(merchantId).append('\n');
        data.append("INVOICE=").append((int)Math.floor(Math.random()*10000000)).append('\n');
        data.append("AMOUNT=").append(22.80).append('\n');
        data.append("CURRENCY=").append("BGN").append('\n');
        data.append("EXP_TIME=").append(LocalDateTime.now().plus(15, ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append('\n');
        data.append("DESCR=").append("sample transaction").append('\n');

        String encoded = new String(Base64.getEncoder().encode(data.toString().getBytes()));
        model.addAttribute("encoded", encoded);
		model.addAttribute("checksum", calculateRFC2104HMAC(encoded, secret));
        model.addAttribute("epayUrl", ePayUrl);

        return "payment-form";
    }


    public String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }


    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


    @GetMapping("/payment-success")
    public String success(HttpServletRequest request) {
        return "payment-success";
    }


    @GetMapping("/payment-cancel")
    public String cancel(HttpServletRequest request) {
        return "payment-cancel";
    }

}
