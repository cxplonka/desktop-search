/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TrayInfoEvent {

    private String caption;
    private String text;
    private TrayIcon.MessageType messageType;

    public TrayInfoEvent(String caption, String text, MessageType messageType) {
        this.caption = caption;
        this.text = text;
        this.messageType = messageType;
    }

    public String getCaption() {
        return caption;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getText() {
        return text;
    }
}