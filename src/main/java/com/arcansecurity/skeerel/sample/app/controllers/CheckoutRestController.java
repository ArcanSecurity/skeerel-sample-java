package com.arcansecurity.skeerel.sample.app.controllers;

import com.arcansecurity.skeerel.Skeerel;
import com.arcansecurity.skeerel.data.address.Country;
import com.arcansecurity.skeerel.data.delivery.*;
import com.arcansecurity.skeerel.exception.SkeerelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * @author Florian Pradines
 */
@RestController
public class CheckoutRestController {

    private final static String CSRF_COOKIE_NAME = "csrf";

    private final Skeerel skeerel;

    public CheckoutRestController(@Value("${website_id}") String websiteId,
                                  @Value("${website_secret}") String websiteSecret) {
        skeerel = new Skeerel(UUID.fromString(websiteId), websiteSecret);
    }

    @GetMapping(value="/delivery")
    public String getDeliveryMethods(@RequestParam("user") String user,
                                     @RequestParam("zip_code") String zipCode,
                                     @RequestParam("city") String city,
                                     @RequestParam("country") String countryCode) {
        // A standard delivery mode
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setId("standard");
        deliveryMethod.setType(Type.HOME);
        deliveryMethod.setPrimary(true);
        deliveryMethod.setName("Standard shipping");
        deliveryMethod.setDeliveryTextContent("delivery in 3 days");
        deliveryMethod.setPrice(499);



        // But also a pick up mode
        DeliveryMethod deliveryMethodRelay = new DeliveryMethod();
        deliveryMethodRelay.setId("my_relay");
        deliveryMethodRelay.setType(Type.RELAY);
        deliveryMethodRelay.setName("Pick-up & go");
        deliveryMethodRelay.setDeliveryTextContent("Deliver on ");
        deliveryMethodRelay.setPrice(299);

        // Pick up points
        PickUpPoint pickUpPoint1 = new PickUpPoint();
        pickUpPoint1.setId("1");
        pickUpPoint1.setName("Pick-up 1");
        pickUpPoint1.setAddress("Address 1");
        pickUpPoint1.setZipCode(zipCode);
        pickUpPoint1.setCity(city);
        pickUpPoint1.setCountry(Country.fromAlpha2(countryCode));
        pickUpPoint1.setDeliveryTextContent("tomorrow");
        pickUpPoint1.setDeliveryTextColor(Color.GREEN);
        pickUpPoint1.setPrice(399);
        pickUpPoint1.setPriceTextColor(Color.RED);

        PickUpPoint pickUpPoint2 = new PickUpPoint();
        pickUpPoint2.setId("2");
        pickUpPoint2.setPrimary(true);
        pickUpPoint2.setName("Pick-up 2");
        pickUpPoint2.setAddress("address 2");
        pickUpPoint2.setZipCode(zipCode);
        pickUpPoint2.setCity(city);
        pickUpPoint2.setCountry(Country.fromAlpha2(countryCode));

        PickUpPoint pickUpPoint3 = new PickUpPoint();
        pickUpPoint3.setId("3");
        pickUpPoint3.setName("Pick-up 3");
        pickUpPoint3.setAddress("Address 3");
        pickUpPoint3.setZipCode(zipCode);
        pickUpPoint3.setCity(city);
        pickUpPoint3.setCountry(Country.fromAlpha2(countryCode));

        PickUpPoints pickUpPointsRelay = new PickUpPoints();
        pickUpPointsRelay.add(pickUpPoint1);
        pickUpPointsRelay.add(pickUpPoint2);
        pickUpPointsRelay.add(pickUpPoint3);

        deliveryMethodRelay.setPickUpPoints(pickUpPointsRelay);



        // And why not getting the order directly in the store
        DeliveryMethod deliveryMethodCollect = new DeliveryMethod();
        deliveryMethodCollect.setId("store_collect");
        deliveryMethodCollect.setType(Type.COLLECT);
        deliveryMethodCollect.setName("Click & collect");
        deliveryMethodCollect.setDeliveryTextContent("Available in two hours");
        deliveryMethodCollect.setPrice(0);

        // Collect points
        PickUpPoint collectPoint1 = new PickUpPoint();
        collectPoint1.setId("1");
        collectPoint1.setName("Store 1");
        collectPoint1.setAddress("Address 1");
        collectPoint1.setZipCode(zipCode);
        collectPoint1.setCity(city);
        collectPoint1.setCountry(Country.fromAlpha2(countryCode));

        PickUpPoint collectPoint2 = new PickUpPoint();
        collectPoint2.setId("2");
        collectPoint2.setName("Store 2");
        collectPoint2.setAddress("Address 2");
        collectPoint2.setZipCode(zipCode);
        collectPoint2.setCity(city);
        collectPoint2.setCountry(Country.fromAlpha2(countryCode));

        PickUpPoint collectPoint3 = new PickUpPoint();
        collectPoint3.setId("3");
        collectPoint3.setName("Store 3");
        collectPoint3.setAddress("Address 3");
        collectPoint3.setZipCode(zipCode);
        collectPoint3.setCity(city);
        collectPoint3.setCountry(Country.fromAlpha2(countryCode));

        PickUpPoints pickUpPointsCollect = new PickUpPoints();
        pickUpPointsCollect.add(collectPoint1);
        pickUpPointsCollect.add(collectPoint2);
        pickUpPointsCollect.add(collectPoint3);

        deliveryMethodCollect.setPickUpPoints(pickUpPointsCollect);



        // We add everything to the main object
        DeliveryMethods deliveryMethods = new DeliveryMethods();
        deliveryMethods.add(deliveryMethod);
        deliveryMethods.add(deliveryMethodRelay);
        deliveryMethods.add(deliveryMethodCollect);

        return deliveryMethods.toJson().toString();
    }

    @GetMapping(value="/checkout/complete")
    public String complete(@RequestParam("token") String accessToken,
                           @RequestParam("state") String state,
                           @CookieValue(name = CSRF_COOKIE_NAME) String csrfCookieValue,
                           HttpServletResponse response) throws SkeerelException {
        Cookie csrfCookie = new Cookie(CSRF_COOKIE_NAME, null);
        csrfCookie.setHttpOnly(true);
        csrfCookie.setMaxAge(0);
        response.addCookie(csrfCookie);

        if (!Objects.equals(state, csrfCookieValue)) {
            return "Error: state parameter differs from the one stored in cookie";
        }

        return skeerel.getData(accessToken).toString();
    }
}
