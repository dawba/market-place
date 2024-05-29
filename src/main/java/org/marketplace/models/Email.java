package org.marketplace.models;

import org.springframework.mail.SimpleMailMessage;

public class Email extends SimpleMailMessage {
    private String content;

    public Email(String from, String to, String subject, String content) {
        super.setFrom(from);
        super.setTo(to);
        super.setSubject(subject);
        super.setText(content);
        this.content = content;
    }

    @Override
    public String getText() {
        return this.content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        super.setText(content);
    }
}