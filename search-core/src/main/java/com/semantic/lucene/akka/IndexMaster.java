///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.semantic.lucene.akka;
//
//import akka.actor.Actor;
//import akka.actor.ActorRef;
//import akka.actor.Props;
//import akka.actor.UntypedActor;
//import akka.actor.UntypedActorFactory;
//import akka.routing.RoundRobinRouter;
//import org.apache.lucene.index.IndexWriter;
//
///**
// *
// * @author Christian Plonka (cplonka81@gmail.com)
// */
//class IndexMaster extends UntypedActor {
//
//    private final ActorRef indexingRouter;
//    private final ActorRef indexWriterActor;
//    private final IndexWriter indexWriter;
//
//    public IndexMaster(IndexWriter indexWriter) {
//        super();
//        this.indexWriter = indexWriter;
//        /* parallel the work */
//        indexingRouter = getContext().actorOf(new Props(IndexingActor.class).
//                withRouter(new RoundRobinRouter(4)), "indexingRouter");
//        /* one index writer actor */
//        indexWriterActor = getContext().actorOf(new Props(new UntypedActorFactory() {
//            @Override
//            public Actor create() throws Exception {
//                return new IndexWriterActor(IndexMaster.this.indexWriter);
//            }
//        }));
//    }
//
//    @Override
//    public void onReceive(Object o) throws Exception {
//        if (o instanceof IndexMsg) {
//            /* redirect to indexing router */
//            indexingRouter.tell(o, getSelf());
//        } else if (o instanceof IndexedMsg) {
//            /* redirect to index writer */
//            indexWriterActor.tell(o, getSelf());
//        } else {
//            unhandled(o);
//        }
//    }
//}