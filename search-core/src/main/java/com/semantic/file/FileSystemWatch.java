/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.file;

import com.semantic.file.event.FileSystemChangeListener;
import com.semantic.swing.tree.querybuilder.QueryRefreshAction;
import com.semantic.util.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

/**
 * isof http://en.wikipedia.org/wiki/Lsof
 *
 * http://vmccontroller.codeplex.com/SourceControl/changeset/view/47386#195318
 * http://etutorials.org/Networking/network+security+hacks/Chapter+2.+Windows+Host+Security/Hack+22+Get+a+List+of+Open+Files+and+Their+Owning+Processes/
 * http://technet.microsoft.com/en-us/sysinternals/bb896655
 *
 * This API is not designed for indexing a hard drive. Most file system
 * implementations have native support for file change notification — the Watch
 * Service API takes advantage of this where available. But when a file system
 * does not support this mechanism, the Watch Service will poll the file system,
 * waiting for events.
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileSystemWatch implements Runnable, ActionListener {

    protected static final Logger log = Logger.getLogger(FileSystemWatch.class.getName());
    /* */
    private static final int TYPE_CREATE = 0;
    private static final int TYPE_DELETED = 1;
    private static final int TYPE_MODIFIED = 2;
    /* */
    private EventListenerList listeners;
    /* */
    private boolean running;
    private Thread runner;
    /* */
    private WatchService watcher;
    private final Map<Path, WatchKey> watchKeys = new HashMap<>();
    private final Map<WatchKey, Path> pathMapping = new HashMap<>();
    /* update timer for the query */
    private final Timer _updateTimer = new Timer(5000, new ActionListener() {

        private final Action refresh = new QueryRefreshAction();

        @Override
        public void actionPerformed(ActionEvent e) {
            refresh.actionPerformed(e);
        }
    });
    /* shot away on the event dispatch thread */
//    private Timer timer = new Timer(10000, this);
//    private BlockingQueue<IndexEntry> workQueue = new LinkedBlockingQueue<IndexEntry>();

    public void start() {
        if (runner == null) {
            runner = new Thread(this);
            runner.setName("File System Watch Thread");
            runner.setPriority(Thread.MIN_PRIORITY);
            /* create watchservice */
            FileSystem fileSystem = FileSystems.getDefault();
            try {
                watcher = fileSystem.newWatchService();

                _updateTimer.setInitialDelay(5000);
                _updateTimer.setCoalesce(true);
                _updateTimer.setRepeats(false);

                log.info("FileSystem watch service started!");
//            timer.setRepeats(true);
            } catch (IOException ex) {
                log.log(Level.INFO, "can not start watch service.", ex);
            }
        }
        running = true;
        runner.start();
    }

    public void stop() {
        if (runner != null) {
            running = false;
            while (runner.isAlive()) {
            }
            FileUtil.quiteClose(watcher);
        }
    }

    public void addFileSystemChangeListener(FileSystemChangeListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(FileSystemChangeListener.class, l);
    }

    public void removeFileSystemChangeListener(FileSystemChangeListener l) {
        if (listeners != null) {
            listeners.remove(FileSystemChangeListener.class, l);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        IndexEntry entry = workQueue.poll();
//        while (entry != null) {
//            fireChanged(entry.state, entry.file);
//            entry = workQueue.poll();
//        }
    }

    protected void fireChanged(int type, File file) {
        if (listeners != null) {
            _updateTimer.restart();

            for (FileSystemChangeListener l : listeners.getListeners(FileSystemChangeListener.class)) {
                if (!file.isDirectory()) {
                    switch (type) {
                        case TYPE_CREATE:
                            l.entryCreated(file);
                            break;
                        case TYPE_DELETED:
                            l.entryDeleted(file);
                            break;
                        case TYPE_MODIFIED:
                            l.entryModified(file);
                            break;
                    }
                }
            }
        }
    }

    private void register(Path path) {
        if (watcher != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {            
            if (!watchKeys.containsKey(path)) {
                try {
                    WatchKey key = path.register(
                            watcher,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                    watchKeys.put(path, key);
                    pathMapping.put(key, path);
                    log.info(String.format("installed watchservice for [%s]", path));
                } catch (IOException ex) {
                    log.warning(String.format("can not install watchservice for [%s]", path));
                }
            }
        } else {
            log.info("watchservice not running!");
        }
    }

    public void registerAll(File file) {
        if (file.isDirectory()) {
            try {
                Path path = file.toPath();
                /* register directory and sub-directories */
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        register(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                log.log(Level.INFO, "Can't register directory.", ex);
            }
        }
    }

    public void unregisterService(File file) {
        Path path = file.toPath();
        if (watcher != null && watchKeys.containsKey(path)) {
            WatchKey key = watchKeys.remove(path);
            pathMapping.remove(key);
            key.cancel();
            log.info(String.format("removed from watchservice [%s]", file));
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
//                if (workQueue.isEmpty()) {
//                    timer.stop();
                WatchKey watchKey = watcher.take();
                if (watchKey != null) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent event : events) {
                        /* handle path */
                        if (event.context() instanceof Path) {
                            /* construct real path */
                            File file = new File(String.format("%s%s%s",
                                    pathMapping.get(watchKey),
                                    File.separatorChar,
                                    event.context()));
                            /* */
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                                    workQueue.put(new IndexEntry(file, TYPE_CREATE));
                                fireChanged(TYPE_CREATE, file);
                            }
                            if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                                    workQueue.put(new IndexEntry(file, TYPE_DELETED));
                                fireChanged(TYPE_DELETED, file);
                            }
                            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
//                                    workQueue.put(new IndexEntry(file, TYPE_MODIFIED));
                                fireChanged(TYPE_MODIFIED, file);
                            }
                        }
                    }
                    watchKey.reset();
                }
//                } else if (!timer.isRunning()) {
                    /* collect the results, because after event filesize == 0 */
//                    timer.start();
//                }
            }
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, null, e);
        }
        running = false;
    }

    class IndexEntry {

        File file;
        int state;

        public IndexEntry(File file, int state) {
            this.file = file;
            this.state = state;
        }
    }
}
