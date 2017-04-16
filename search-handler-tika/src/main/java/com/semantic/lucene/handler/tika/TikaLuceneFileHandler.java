/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.handler.tika;

import com.semantic.lucene.fields.ContentField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.handler.LuceneFileHandler;
import com.semantic.lucene.handler.LuceneFileHandler.IndexState;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.swing.grid.DefaultDocumentGridCellRenderer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TikaLuceneFileHandler extends LuceneFileHandler {

    /* prepare renderer */
    static {
        /* very slick http://franksouza183.deviantart.com/art/FS-Icons-Ubuntu-288407674 */
        Map<String, String> map = DefaultDocumentGridCellRenderer.MIMETYPE;
        map.put("application/pdf", "128x128/application-pdf.png");
        map.put("application/xml", "128x128/application-xml.png");
        /* http://en.wikipedia.org/wiki/OpenDocument_technical_specification */
        map.put("application/vnd.oasis.opendocument.text", "128x128/opendocument-text.png");
        map.put("application/vnd.oasis.opendocument.spreadsheet", "128x128/opendocument-calc.png");
        map.put("application/vnd.oasis.opendocument.presentation", "128x128/opendocument-impress.png");
        map.put("application/vnd.oasis.opendocument.graphics", "128x128/opendocument-draw.png");
        /* microsoft */
        map.put("application/vnd.oasis.opendocument.text", "128x128/opendocument-text.png");
        map.put("application/vnd.ms-excel", "128x128/opendocument-calc.png");
        map.put("application/vnd.ms-powerpoint", "128x128/opendocument-impress.png");
        map.put("application/vnd.ms-powerpoint.presentation.macroenabled.12", "128x128/opendocument-impress.png");
        map.put("application/vnd.oasis.opendocument.graphics", "128x128/opendocument-draw.png");
        map.put("application/vnd.openxmlformats-officedocument.presentationml.document", "128x128/opendocument-impress.png");
        map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.document", "128x128/opendocument-calc.png");
        map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "128x128/opendocument-text.png");
        map.put("application/vnd.openxmlformats-officedocument.drawingml.document", "128x128/opendocument-draw.png");
        map.put("application/msword", "128x128/opendocument-text.png");
        map.put("application/rtf", "128x128/application-rtf.png");
        map.put("message/rfc822", "128x128/message-rfc822.png");
        map.put("image/svg+xml", "128x128/image-svg+xml.png");
        map.put("application/zip", "128x128/application-zip.png");
        map.put("text/html", "128x128/text-html.png");
        map.put("video/x-msvideo", "128x128/mime-video.png");
        map.put("video/x-ms-asf", "128x128/mime-video.png");
        map.put("image/vnd.dwg", "128x128/image-vnd-dwg.png");
    }

    @Override
    public Document handleDocument(IndexWriter indexWriter, IndexState state, File file) throws Exception {
        Document doc = super.handleDocument(indexWriter, state, file);
        if (doc != null) {
            InputStream stream = null;
            Metadata metadata = new Metadata();
            Parser parser = new AutoDetectParser();
            BodyContentHandler handle = new BodyContentHandler();
            /* */
            try {
                parser.parse(stream = new FileInputStream(file), handle,
                        metadata, new ParseContext());
                /* for term highlight save and tokenized */
                doc.add(new TextField(ContentField.NAME, handle.toString(), Field.Store.YES));
            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            /* meta informations */
            for (String key : metadata.names()) {
                String name = key.toLowerCase();
                String value = metadata.get(key);
                /* */
                if (!value.isEmpty()) {
                    /* remap */
                    if (name.equalsIgnoreCase("content-type")) {
                        doc.removeField(MimeTypeField.NAME);
                        doc.add(new StringField(MimeTypeField.NAME,
                                value.split(";")[0], Store.YES));
                    } else {
                        IFieldProperty field = get(name);
                        if (field != null) {
                            field.add(doc, value);
                        } else {
                            doc.add(new StringField(name, value, Store.YES));
                        }
                    }
                }
            }
        }
        return doc;
    }

    @Override
    public String[] getFileExtensions() {
        /* look at every file */
        return new String[]{"*"};
    }
}
