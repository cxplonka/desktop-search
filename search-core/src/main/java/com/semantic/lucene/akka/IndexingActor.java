///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.semantic.lucene.akka;
//
//import akka.actor.UntypedActor;
//import com.semantic.lucene.handler.ImageLuceneFileHandler;
//import com.semantic.lucene.handler.LuceneFileHandler;
//import com.semantic.lucene.handler.TextLuceneFileHandler;
//import com.semantic.util.FileUtil;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.lucene.document.Document;
//
///**
// *
// * @author Christian Plonka (cplonka81@gmail.com)
// */
//final class IndexingActor extends UntypedActor {
//
//    private Map<String, LuceneFileHandler> _handles;
//
//    public IndexingActor() {
//        super();
//        registerFileHandle(new TextLuceneFileHandler());
//        registerFileHandle(new ImageLuceneFileHandler());
//    }
//
//    void registerFileHandle(LuceneFileHandler handle) {
//        if (_handles == null) {
//            _handles = new HashMap<String, LuceneFileHandler>();
//        }
//        /* only 1 handle for a extension */
//        for (String ext : handle.getFileExtensions()) {
//            _handles.put(ext, handle);
//        }
//    }
//
//    @Override
//    public void onReceive(Object o) throws Exception {
//        if (o instanceof IndexMsg) {
//            IndexMsg entry = (IndexMsg) o;
//            Document doc = handle(entry);
//            if (doc != null) {
//                getSender().tell(new IndexedMsg(entry.state, doc), getSelf());
//            }
//        } else {
//            unhandled(o);
//        }
//    }
//
//    Document handle(IndexMsg entry) {
//        File file = entry.file;
//        if (!file.isDirectory()) {
//            LuceneFileHandler handle = _handles == null ? null
//                    : _handles.get(FileUtil.getFileExtension(file));
//            /* handle file */
//            if (handle != null) {
//                try {
//                    return handle.handleDocument(null, entry.state, file);
//                } catch (Exception ex) {
//                    //ignore
//                }
//            }
//        }
//        return null;
//    }
//}