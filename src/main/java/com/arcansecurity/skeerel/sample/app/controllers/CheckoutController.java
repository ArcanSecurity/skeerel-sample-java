package com.arcansecurity.skeerel.sample.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Florian Pradines
 */
@Controller
public class CheckoutController {

    private final static String CSRF_COOKIE_NAME = "csrf";

    private final static Random RANDOM = new SecureRandom();

    @GetMapping(value="/")
    public String home(@Value("${website_id}") String websiteId,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) {
        Cookie csrfCookie = new Cookie(CSRF_COOKIE_NAME, new BigInteger(130, RANDOM).toString(32));
        csrfCookie.setHttpOnly(true); // important, to avoid reading the cookie in case of XSS vulnerability
        csrfCookie.setMaxAge(600); // 10 minutes

        response.addCookie(csrfCookie);

        model.addAttribute("baseUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());
        model.addAttribute("website_id", websiteId);
        model.addAttribute("state", csrfCookie.getValue());
        return "home";
    }

}
