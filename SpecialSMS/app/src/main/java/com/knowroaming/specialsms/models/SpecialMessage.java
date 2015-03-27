package com.knowroaming.specialsms.models;

import java.io.Serializable;

/**
 * Created by Neal on 3/25/2015.
 */
public class SpecialMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String timestamp = null;
    private String sender;
    private String message;
    private String encodedMessage;

    public SpecialMessage(String sender, String message, String encodedMessage) {
        this.message = message;
        this.sender = sender;
        this.encodedMessage = encodedMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
