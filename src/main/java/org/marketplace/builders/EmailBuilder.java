package org.marketplace.builders;

import org.marketplace.models.Email;

public class EmailBuilder {
    private String from;
    private String to;
    private String subject;
    private String content;

    public EmailBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public EmailBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public EmailBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public Email build() {
        return new Email(from, to, subject, content);
    }
}
