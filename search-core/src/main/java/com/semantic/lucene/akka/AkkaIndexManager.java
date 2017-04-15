///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.semantic.lucene.akka;
//
//import akka.actor.Actor;
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.actor.UntypedActorFactory;
//import com.semantic.file.event.FileSystemChangeListener;
//import com.semantic.lucene.handler.LuceneFileHandler;
//import com.semantic.plugin.Context;
//import com.semantic.plugin.IPlugIn;
//import com.semantic.util.Files;
//import com.semantic.util.VisitorPattern;
//import com.semantic.util.property.PropertyMap;
//import java.io.File;
//
///**
// *
// * @author Christian Plonka (cplonka81@gmail.com)
// */
//public class AkkaIndexManager extends PropertyMap implements FileSystemChangeListener, IPlugIn {
//
//    private ActorSystem _system;
//    private ActorRef _master;
//
//    @Override
//    public void init(Context context) throws Exception {
//        _system = ActorSystem.create("akka-indexer-system");
//        _master = _system.actorOf(new Props(new UntypedActorFactory() {
//            @Override
//            public Actor create() throws Exception {
//                return new IndexMaster(null);
//            }
//        }));
//    }
//
//    @Override
//    public void shutdown(Context context) throws Exception {
//        _system.shutdown();
//    }
//
//    @Override
//    public void entryCreated(File file) {
//        _master.tell(new IndexMsg(LuceneFileHandler.IndexState.CREATE, file), null);
//    }
//
//    @Override
//    public void entryDeleted(File file) {
//        _master.tell(new IndexMsg(LuceneFileHandler.IndexState.DELETE, file), null);
//    }
//
//    @Override
//    public void entryModified(File file) {
//        _master.tell(new IndexMsg(LuceneFileHandler.IndexState.UPDATE, file), null);
//    }
//
//    public static void main(String[] arg) throws Exception {
//        final AkkaIndexManager mng = new AkkaIndexManager();
//        try {
//            mng.init(null);
//            Files.walkTree(new File("C:\\Users\\Christian\\Desktop"), new VisitorPattern<File>() {
//                @Override
//                public boolean visit(File node) {
//                    if (!node.isDirectory()) {
//                        mng.entryCreated(node);
//                    }
//                    return true;
//                }
//            });
//        } finally {
//            mng.shutdown(null);
//        }
//    }
//}