package com.lumastyle.eshop.service;

/**
 * Simple service for sending notification emails.
 */
public interface EmailService {

    /**
     * Send a plain text email message.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param text    message body
     */
    void sendPaymentConfirmation(String to, String subject, String text);
}
