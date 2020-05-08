package com.arcansecurity.skeerel.sample.app.controllers;

import com.arcansecurity.skeerel.Skeerel;
import com.arcansecurity.skeerel.exception.SkeerelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Florian Pradines
 */
@RestController
public class WebsiteRestController {

    private final Skeerel skeerel;

    public WebsiteRestController(@Value("${website_id}") String websiteId,
                                 @Value("${website_secret}") String websiteSecret) {
        skeerel = new Skeerel(UUID.fromString(websiteId), websiteSecret);
    }

    @GetMapping(value="/website")
    public String get() throws SkeerelException {
        return skeerel.getWebsiteDetails().toString();
    }
}
