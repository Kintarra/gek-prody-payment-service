package com.prody.payment.service.app.controller;

import com.prody.payment.service.app.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Payment payment_1 = new Payment(1L, 1.11);
    private final Payment payment_2 = new Payment(2, 2.22);
    private final Payment payment_3 = new Payment(3L, 3.33);
    private final Payment payment_4 = new Payment(4L, 4.44);
    private final Payment payment_5 = new Payment(5L, 5.55);

    private final Map<Long, Payment> paymentMap = Map.of(
        payment_1.getId(), payment_1,
        payment_2.getId(), payment_2,
        payment_3.getId(), payment_3,
        payment_4.getId(), payment_4,
        payment_5.getId(), payment_5
    );

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentMap.get(id);
    }

    @GetMapping("/all")
    public List<Payment> getPayments() {
        return new ArrayList<>(paymentMap.values());
    }
}
