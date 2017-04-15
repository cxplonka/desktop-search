/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TextAreaLogHandler extends Handler {

    private JTextArea logTo;

    public TextAreaLogHandler() {
        super();
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        if (logTo != null) {
            /* maybe push it to the edt */
            if (logTo.getLineCount() > 1000) {
                logTo.setText(getFormatter().format(record));
            }
            logTo.append(getFormatter().format(record));
        }
    }

    public void setLogTo(JTextArea logTo) {
        this.logTo = logTo;
        DefaultCaret caret = (DefaultCaret) logTo.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}