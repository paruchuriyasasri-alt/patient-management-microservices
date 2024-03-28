package com.yasasri.notificationservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PatientEventListener {

    private static final Logger log = LoggerFactory.getLogger(PatientEventListener.class);

    @KafkaListener(topics = "patient-events", groupId = "notification-group")
    public void handlePatientEvent(String message) {
        log.info("Received patient event: {}", message);

        if (message.startsWith("PATIENT_CREATED:")) {
            String patientId = message.split(":")[1];
            sendWelcomeNotification(patientId);
        } else if (message.startsWith("PATIENT_UPDATED:")) {
            String patientId = message.split(":")[1];
            sendUpdateConfirmation(patientId);
        } else if (message.startsWith("PATIENT_DELETED:")) {
            String patientId = message.split(":")[1];
            log.info("Patient {} record removed from system", patientId);
        }
    }

    private void sendWelcomeNotification(String patientId) {
        log.info("Sending welcome notification to patient {}", patientId);
        // In production, this would integrate with an email/SMS service
    }

    private void sendUpdateConfirmation(String patientId) {
        log.info("Sending update confirmation to patient {}", patientId);
    }
}
