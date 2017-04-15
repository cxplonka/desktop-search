///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.semantic.lucene.akka;
//
//import akka.actor.UntypedActor;
//import static com.semantic.lucene.handler.ImageLuceneFileHandler.FIELD_EXIF_MAKE;
//import static com.semantic.lucene.handler.ImageLuceneFileHandler.FIELD_EXIF_MODEL;
//import static com.semantic.lucene.handler.ImageLuceneFileHandler.FIELD_USER_COMMENT;
//import static com.semantic.lucene.handler.LuceneFileHandler.FIELD_CONTENT;
//import static com.semantic.lucene.handler.LuceneFileHandler.FIELD_FILE_NAME;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.TextField;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.Term;
//
///**
// *
// * @author Christian Plonka (cplonka81@gmail.com)
// */
//final class IndexWriterActor extends UntypedActor {
//
//    private final IndexWriter indexWriter;
//
//    public IndexWriterActor(IndexWriter indexWriter) {
//        super();
//        this.indexWriter = indexWriter;
//    }
//
//    @Override
//    public void onReceive(Object o) throws Exception {
//        if (o instanceof IndexedMsg) {
//            IndexedMsg msg = (IndexedMsg) o;
//            /* create/ update */
//            if (msg.document != null) {
//                /* unique docment id */
//                String id = msg.document.get(FIELD_FILE_NAME.getField());
//                indexWriter.updateDocument(new Term(FIELD_FILE_NAME.getField(), id),
//                        handleDocument(msg.document));
//            } else {
//                /* delete */
//            }
//        } else {
//            unhandled(o);
//        }
//    }
//
//    Document handleDocument(Document doc) {
//        /* add a all fields field for keyword search */
//        StringBuilder buffer = new StringBuilder();
//        /* filename */
//        if (doc.getField(FIELD_FILE_NAME.getField()) != null) {
//            buffer.append(doc.get(FIELD_FILE_NAME.getField()));
//        }
//        /* camera make model */
//        if (doc.getField(FIELD_EXIF_MAKE.getField()) != null) {
//            buffer.append(' ').append(doc.get(FIELD_EXIF_MAKE.getField()));
//        }
//        /* camera model */
//        if (doc.getField(FIELD_EXIF_MODEL.getField()) != null) {
//            buffer.append(' ').append(doc.get(FIELD_EXIF_MODEL.getField()));
//        }
//        /* user comment */
//        if (doc.getField(FIELD_USER_COMMENT.getField()) != null) {
//            buffer.append(' ').append(doc.get(FIELD_USER_COMMENT.getField()));
//        }
//        /* analyze content */
//        doc.add(new TextField(FIELD_CONTENT.getField(), buffer.toString(), Field.Store.YES));
//        return doc;
//    }
//}