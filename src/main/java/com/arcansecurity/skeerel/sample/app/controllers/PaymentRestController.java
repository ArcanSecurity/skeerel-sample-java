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
public class PaymentRestController {

    private final Skeerel skeerel;

    public PaymentRestController(@Value("${website_id}") String websiteId,
                                 @Value("${website_secret}") String websiteSecret) {
        skeerel = new Skeerel(UUID.fromString(websiteId), websiteSecret);
    }

    @GetMapping(value="/payment/{paymentId}/capture")
    public String capture(@PathVariable UUID paymentId) throws SkeerelException {
        return String.valueOf(skeerel.capturePayment(paymentId));
    }

    @GetMapping(value="/payment/{paymentId}")
    public String get(@PathVariable UUID paymentId) throws SkeerelException {
        return skeerel.getPayment(paymentId).toString();
    }

    @GetMapping(value="/payment/{paymentId}/refund")
    public String refund(@PathVariable UUID paymentId, @RequestParam("amount") Optional<Long> amount) throws SkeerelException {
        return String.valueOf(skeerel.refundPayment(paymentId, amount.orElse(null)));
    }

    @GetMapping(value="/payment/{paymentId}/reject")
    public String reject(@PathVariable UUID paymentId) throws SkeerelException {
        return String.valueOf(skeerel.rejectPayment(paymentId));
    }

    @GetMapping(value="/payment/list")
    public String get(@RequestParam("live") Optional<Boolean> live,
                      @RequestParam("first") Optional<Integer> first,
                      @RequestParam("limit") Optional<Integer> limit) throws SkeerelException {
        return skeerel.listPayments(live.orElse(null), first.orElse(null), limit.orElse(null)).toString();
    }
}
