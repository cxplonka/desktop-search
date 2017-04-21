/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * MainFrame.java
 *
 * Created on 15.09.2011, 13:54:09
 */
package com.semantic.swing;

import com.semantic.ApplicationContext;
import com.semantic.eventbus.GenericEventBus;
import com.semantic.eventbus.GenericEventListener;
import com.semantic.logging.TextAreaLogHandler;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.model.ModelStore;
import com.semantic.model.OModel;
import com.semantic.swing.action.ExitAction;
import com.semantic.swing.grid.LazyDocumentListModel;
import com.semantic.swing.grid.ResultView;
import com.semantic.swing.grid.ThumbnailDatabaseHandle;
import com.semantic.swing.preferences.GlobalKeys;
import com.semantic.swing.table.LazyDocumentListService;
import com.semantic.swing.tree.SemanticControlPanel;
import com.semantic.swing.tree.highlight.DocumentTreeSelectionHighlighter;
import com.semantic.swing.tree.querybuilder.MergedFieldsTreeQueryBuilder;
import com.semantic.swing.tree.querybuilder.QueryRefreshAction;
import com.semantic.thumbnail.ThumbnailLoadEvent;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.image.TextureManager;
import com.semantic.util.lazy.DefaultLazyList;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.lazy.OnLoadEvent;
import com.semantic.util.swing.DummyPanel;
import com.semantic.util.swing.SwingUtils;
import com.semantic.util.swing.jtree.TreeExpansionState;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.LineBorder;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class MainFrame extends javax.swing.JFrame implements PropertyChangeListener,
        GenericEventListener, WindowListener, GlobalKeys {

    protected static final String GRID_ZOOM_FACTOR = "grid.zoom.factor";
    protected static final String ALL_DIVIDER_LOCATION = "all.divider.location";
    protected static final String RESULT_DIVIDER_LOCATION = "result.divider.location";
    protected static final String FRAME_WIDTH = "frame.width";
    protected static final String FRAME_HEIGHT = "frame.height";
    protected static final String FRAME_X = "frame.x";
    protected static final String FRAME_Y = "frame.y";
    /* */
    protected static final Logger log = Logger.getLogger(MainFrame.class.getName());
    public static final Preferences PREF = Preferences.userNodeForPackage(MainFrame.class);
    /* */
    private TrayIcon trayIcon;
    private PopupMenu trayPopup;
    /* lazy data fetching */
    private static final int pageSize = 20;
    private final LazyDocumentListService lazyService = new LazyDocumentListService();
    private final LazyList<Document> lazyDocuments = new DefaultLazyList<Document>(lazyService, pageSize);
    /* */
    private final QueryRefreshAction refreshAction = new QueryRefreshAction();
    private DummyPanel dummyPanel;
    private DocumentTreeSelectionHighlighter treeHighlighter;
    private TreeExpansionState expansionState;
    private ThumbnailDatabaseHandle thumbnailHandle;

    public MainFrame(ApplicationContext ctx) {
        super();
        initComponents();
        initOwnComponents();
        setTitle("Cirrus");
        initSystemTray();
        ctx.addPropertyChangeListener(this);
    }

    private void initOwnComponents() {
        addWindowListener(this);
        /* http://www.jyloo.com/news/?pubId=1268254738000, only simple 2d scene */
        if (UIManager.getBoolean(UIDefaults.ROOTPANE_SHAPED)) {
            rootPane.setBorder(new LineBorder(new Color(112, 112, 112)));
        }
        /* */
        setSize(800, 600);
        setIconImage(TextureManager.def().loadImage("trayicon.png"));
        /* layout the 2 dividers */
        cSplit.collapseRight();
        treeHighlighter = new DocumentTreeSelectionHighlighter(
                controlPanel.getJTree(), lazyDocuments, documentPropertyView1);
        /* layout grid view */
        resultView.setLazyList(lazyDocuments);
        resultView.getGrid().getSelectionModel().addListSelectionListener(treeHighlighter);
        /* list view */
        resultView.getList().getSelectionModel().addListSelectionListener(treeHighlighter);
        /* add dummy component into the north of the tree */
        dummyPanel = new DummyPanel();
        dummyPanel.setPreferredSize(resultView.getControlView().getPreferredSize());
        documentPropertyView1.getDummyPanel().setPreferredSize(dummyPanel.getPreferredSize());
        controlPanel.add(dummyPanel, BorderLayout.NORTH);
        expansionState = new TreeExpansionState(controlPanel.getJTree());
        thumbnailHandle = new ThumbnailDatabaseHandle(lazyDocuments, ThumbnailManager.def()) {

            @Override
            public void thumbnailLoaded(ThumbnailLoadEvent event) {
                /* push it to the edt */
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        resultView.repaint();
                    }
                });
            }

            @Override
            public void elementLoaded(OnLoadEvent event) {
                super.elementLoaded(event);
                /* push it to the edt */
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        resultView.repaint();
                    }
                });
            }
        };
        /* */
        beforeOpen();
        /* listen for search results */
        GenericEventBus.addEventListener(QueryResultEvent.class, this);
        GenericEventBus.addEventListener(TrayInfoEvent.class, this);
    }

    private void beforeOpen() {
        /* get root logger and inject log view */
        for (Handler handle : Logger.getLogger("").getHandlers()) {
            if (handle instanceof TextAreaLogHandler) {
                ((TextAreaLogHandler) handle).setLogTo(logView);
            }
        }
        /* */
        setLocation(
                PREF.getInt(FRAME_X, 100),
                PREF.getInt(FRAME_Y, 100));
        setSize(PREF.getInt(FRAME_WIDTH, 800),
                PREF.getInt(FRAME_HEIGHT, 600));
    }

    private void initSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            /* tray popup */
            trayPopup = new PopupMenu();
            ExitAction exitAction = new ExitAction();
            MenuItem defaultItem = new MenuItem(exitAction.getValue(Action.NAME).toString());
            defaultItem.addActionListener(exitAction);
            trayPopup.add(defaultItem);
            /* apply trayicon */
            trayIcon = new TrayIcon(TextureManager.def().loadImage("trayicon.png"),
                    "iSearch", trayPopup);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.log(Level.WARNING, "can not init system tray!", e);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        /* new model loaded */
        if (pce.getPropertyName().equals(ApplicationContext.MODEL.getName())
                && pce.getNewValue() != null) {
            OModel model = (OModel) pce.getNewValue();
            /* register our tree-query builder */
            ApplicationContext.QUERY_MANAGER.getDefaultValue().addQueryBuilder(
                    new MergedFieldsTreeQueryBuilder(controlPanel.getJTree()));
            /* selection and model states */
            controlPanel.getJTree().getCheckBoxTreeSelectionModel().addTreeSelectionListener(refreshAction);
            model.addPropertyChangeListener(refreshAction);
            /* */
            controlPanel.getJTree().setModel(model);
            dummyPanel.setModel(model);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        /* register refresh action */
        SwingUtils.registerKeyBoardAction(SwingUtilities.getRootPane(this), refreshAction);
    }

    @Override
    public void removeNotify() {
        /* unregister refresh action */
        SwingUtils.unregisterKeyBoardAction(SwingUtilities.getRootPane(this), refreshAction);
        super.removeNotify();
    }

    public DocumentTreeSelectionHighlighter getTreeHighlighter() {
        return treeHighlighter;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public SemanticControlPanel getSemanticControlPanel1() {
        return controlPanel;
    }

    public ResultView getResultView() {
        return resultView;
    }

    /** This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        content = new javax.swing.JPanel();
        allSplit = new com.semantic.util.swing.LineStyleSplitPane();
        controlPanel = new com.semantic.swing.tree.SemanticControlPanel();
        resultSplit = new com.semantic.util.swing.JSplitPaneHack();
        cSplit = new com.semantic.util.swing.JSplitPaneHack();
        resultView = new com.semantic.swing.grid.ResultView();
        logPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logView = new javax.swing.JTextArea();
        eclipseMemoryBar1 = new com.semantic.util.swing.EclipseMemoryBar();
        documentPropertyView1 = new com.semantic.swing.DocumentPropertyView();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("iMagine");

        content.setLayout(new java.awt.BorderLayout());

        allSplit.setLeftComponent(controlPanel);

        resultSplit.setBorder(null);

        cSplit.setBorder(null);
        cSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        cSplit.setLeftComponent(resultView);

        logPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));

        logView.setColumns(20);
        logView.setEditable(false);
        logView.setRows(5);
        jScrollPane1.setViewportView(logView);

        logPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout eclipseMemoryBar1Layout = new javax.swing.GroupLayout(eclipseMemoryBar1);
        eclipseMemoryBar1.setLayout(eclipseMemoryBar1Layout);
        eclipseMemoryBar1Layout.setHorizontalGroup(
            eclipseMemoryBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        eclipseMemoryBar1Layout.setVerticalGroup(
            eclipseMemoryBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        logPanel.add(eclipseMemoryBar1, java.awt.BorderLayout.SOUTH);

        cSplit.setRightComponent(logPanel);

        resultSplit.setLeftComponent(cSplit);
        resultSplit.setRightComponent(documentPropertyView1);

        allSplit.setRightComponent(resultSplit);

        content.add(allSplit, java.awt.BorderLayout.CENTER);

        getContentPane().add(content, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.semantic.util.swing.LineStyleSplitPane allSplit;
    private com.semantic.util.swing.JSplitPaneHack cSplit;
    private javax.swing.JPanel content;
    private com.semantic.swing.tree.SemanticControlPanel controlPanel;
    private com.semantic.swing.DocumentPropertyView documentPropertyView1;
    private com.semantic.util.swing.EclipseMemoryBar eclipseMemoryBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel logPanel;
    private javax.swing.JTextArea logView;
    private com.semantic.util.swing.JSplitPaneHack resultSplit;
    private com.semantic.swing.grid.ResultView resultView;
    // End of variables declaration//GEN-END:variables
//    SlideShowDialog dialog;

    @Override
    public void handleEvent(Object evt) {
        if (evt instanceof QueryResultEvent) {
            QueryResultEvent event = (QueryResultEvent) evt;
            /* update table model */
            documentPropertyView1.setSelectedDocuments(null);
            /* */
            IndexSearcher currentSearcher = event.getCurrentSearcher();
            /* */
            TopDocs topDocs = event.getTopDocs();
            dummyPanel.setHitText(String.format("%s hits in %s documents", topDocs.totalHits,
                    currentSearcher.getIndexReader().maxDoc()));
            /* clear current request states */
            lazyDocuments.clear();
            /* set new query and searcher */
            lazyService.setCurrentQuery(currentSearcher, event.getQuery());
            /* update grid view */
            LazyDocumentListModel listModel = (LazyDocumentListModel) resultView.getGrid().getModel();
            listModel.fireContentChanged();
            /* */
            listModel = (LazyDocumentListModel) resultView.getList().getModel();
            listModel.fireContentChanged();
            /* */
            resultView.repaint();
        }
        /* somebody want say something */
        if (evt instanceof TrayInfoEvent) {
            TrayInfoEvent event = (TrayInfoEvent) evt;
            if (trayIcon != null) {
                trayIcon.displayMessage(event.getCaption(), event.getText(),
                        event.getMessageType());
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        cSplit.setDividerLocation(getSize().height);
        allSplit.setDividerLocation(
                PREF.getInt(ALL_DIVIDER_LOCATION, 300));
        resultSplit.setDividerLocation(1d);
//        resultSplit.setDividerLocation(
//                PREFERENCES.getInt(RESULT_DIVIDER_LOCATION, getSize().width));
        /* restore tree expansion state */
        expansionState.restore();
        resultView.setZoomFactor(PREF.getInt(GRID_ZOOM_FACTOR, 96));
    }

    @Override
    public void windowClosing(WindowEvent e) {
        /* save tree state */
        expansionState.store();
        PREF.putInt(GRID_ZOOM_FACTOR, resultView.getZoomFactor());
        /* save current state */
        ModelStore.store(
                ApplicationContext.instance().get(ApplicationContext.MODEL),
                new File(ApplicationContext.ISEARCH_HOME + "/mysearch.xml"));
        /* gui states */
        PREF.putInt(ALL_DIVIDER_LOCATION, allSplit.getDividerLocation());
//        PREFERENCES.putInt(RESULT_DIVIDER_LOCATION, resultSplit.getDividerLocation());
        PREF.putInt(FRAME_X, getLocation().x);
        PREF.putInt(FRAME_Y, getLocation().y);
        PREF.putInt(FRAME_WIDTH, getWidth());
        PREF.putInt(FRAME_HEIGHT, getHeight());
        /* flush settings */
        try {
            PREF.flush();
        } catch (BackingStoreException ex) {
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
