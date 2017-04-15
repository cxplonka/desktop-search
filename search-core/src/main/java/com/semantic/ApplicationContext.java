/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.l2fprod.common.util.OS;
import com.semantic.file.FileSystemOntologyModelListener;
import com.semantic.file.FileSystemWatch;
import com.semantic.model.ModelStore;
import com.semantic.model.OModel;
import com.semantic.swing.MainFrame;
import static com.semantic.swing.MainFrame.*;
import com.semantic.swing.UIDefaults;
import com.semantic.util.FileUtil;
import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.PropertyKey;
import com.semantic.util.property.PropertyMap;
import com.semantic.util.swing.LoggingEventQueue;
import de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.Introspector;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.apache.lucene.LucenePackage;
import com.semantic.plugin.Context;
import com.semantic.plugin.PlugInManager;
import com.semantic.swing.tree.querybuilder.QueryPipeline;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class ApplicationContext extends PropertyMap implements Context {

    /* */
    protected static final Logger log = Logger.getLogger(ApplicationContext.class.getName());
    /* */
    private static ApplicationContext singleton;
    /* */
    public static final String ISEARCH_HOME = System.getProperty("user.home") + "/.iSearch";
    /* watching for filesystem changes */
    public static final IPropertyKey<FileSystemWatch> FILESYSTEM_MANAGER
            = PropertyKey.create("FILESYSTEM_MANAGER", FileSystemWatch.class, null);
    /* our application view */
    public static final IPropertyKey<MainFrame> MAIN_VIEW
            = PropertyKey.create("MAIN_VIEW", MainFrame.class, null);
    /* our model */
    public static final IPropertyKey<OModel> MODEL
            = PropertyKey.create("ONTOLOGY_MODEL", OModel.class, null);
    /**
     * read only
     */
    public static final IPropertyKey<QueryPipeline> QUERY_MANAGER
            = PropertyKey.readOnly("QUERY_MANAGER", QueryPipeline.class, new QueryPipeline());
    /**
     * read only
     */
    public static final IPropertyKey<PlugInManager> PLUGIN_MANAGER
            = PropertyKey.readOnly("PLUGIN_MANAGER", PlugInManager.class, new PlugInManager());
    /* */
    private FileSystemOntologyModelListener directoryListener;

    static {
        /* some macos enhancements, important: call before AWT! */
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "iMagine");
        System.setProperty("com.apple.macos.smallTabs", "true");
        System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("JButton.buttonType", "textured");
        System.setProperty("JComponent.sizeVariant", "small");
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        /* listen on the event dispatch thread */
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(
                new LoggingEventQueue());
        /* create iSearch home if necessary */
        File home = new File(ISEARCH_HOME);
        if (!home.exists()) {
            home.mkdir();
        }
    }

    public static ApplicationContext instance() {
        if (singleton == null) {
            singleton = new ApplicationContext();
            singleton.initContext();
            singleton.startContext();
        }
        return singleton;
    }

    private ApplicationContext() {
    }

    protected void initContext() {
        /* set introspection path */
        Introspector.setBeanInfoSearchPath(new String[]{"com.semantic.model.beaninfo"});
        /* create file system manager and start */
//        set(FILESYSTEM_MANAGER, new FileSystemWatch());
        /* create view */
        set(MAIN_VIEW, new MainFrame(this));
        get(MAIN_VIEW).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopContext();
                System.exit(0);
            }
        });
    }

    protected void startContext() {
        log.info(LucenePackage.get().toString());
//        get(FILESYSTEM_MANAGER).start();
        /* register new directories in watch service */
//        directoryListener = new FileSystemOntologyModelListener(get(FILESYSTEM_MANAGER));
        try {
            /* start plugin manager */
            get(PLUGIN_MANAGER).init(this);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "can not start plugin manager.", ex);
        }
        /* create/load model */
        set(MODEL, ModelStore.restore(new File(ISEARCH_HOME + "/mysearch.xml")));
//        directoryListener.setModel(get(MODEL));
    }

    protected void stopContext() {
        log.info("shutdown application context!");
        try {
            /* start plugin manager */
            get(PLUGIN_MANAGER).shutdown(this);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "can not shutdown plugin manager.", ex);
        }
    }

    public static void main(String[] arg) throws Exception {
        /* setup global logging properties */
        InputStream config = null;
        try {
            config = ApplicationContext.class.getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(config);
        } catch (Exception e) {
            //take default
        } finally {
            FileUtil.quiteClose(config);
        }
        /* set system look and feel */
        boolean bundleLF = PREF.getBoolean(KEY_BUNDLE_LOOK, !OS.isMacOSX());
        if (bundleLF) {
            UIDefaults.loadDefaults();
            /* */
            UIManager.setLookAndFeel(new SyntheticaSimple2DLookAndFeel() {
                @Override
                protected void loadXMLConfig(String string) throws ParseException {
                    super.loadXMLConfig(string);
                    try {
                        super.loadXMLConfig("/com/semantic/mySynth.xml");
                    } catch (Exception e) {
                        log.log(Level.WARNING, "can not load mySynth.xml definition!", e);
                    }
                }
            });
        } else {
            UIDefaults.loadMacDefaults();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        /* must be set after look and feel - look for other state icon */
        LookAndFeelFactory.installJideExtension();
        /* create context, and display the form on the EDT */
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
                ApplicationContext ctx = ApplicationContext.instance();
                /* swing view */
                ctx.get(MAIN_VIEW).setVisible(true);
//            }
//        });
    }
}
