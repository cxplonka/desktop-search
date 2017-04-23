/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.guigarage.jgrid.JGrid;
import com.semantic.ApplicationContext;
import com.semantic.eventbus.GenericEventBus;
import com.semantic.eventbus.GenericEventListener;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.handler.ImageLuceneFileHandler;
import com.semantic.lucene.task.QueryResultEvent;
import static com.semantic.swing.MainFrame.PREF;
import com.semantic.swing.UIDefaults;
import com.semantic.swing.ViewAction;
import com.semantic.swing.facet.SearchPanel;
import com.semantic.swing.layerui.ImageLayerUI;
import com.semantic.swing.list.ListCellRendererDelegate;
import com.semantic.swing.list.ListTransferHandler;
import static com.semantic.swing.preferences.GlobalKeys.*;
import com.semantic.swing.slideshow.ShowSlideShowAction;
import com.semantic.util.image.TextureManager;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.swing.SwingUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.document.Document;
import org.jdesktop.jxlayer.JXLayer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ResultView extends JPanel implements MouseListener, PreferenceChangeListener,
        GenericEventListener<QueryResultEvent> {

    private final ShowSlideShowAction slideAction = new ShowSlideShowAction();
    private final JGrid grid = new ModifiedJGrid();
    private final JList list = new JList();
    private JPanel content;
    private JPanel controlView;
    //
    private JSlider slider;
    private JPanel viewBarPanel;
    private JLabel small;
    private JLabel big;
    private JXLayer overlay;
    private SearchPanel searchPanel;
    private final DefaultDocumentGridCellRenderer defaultGridRenderer = new DefaultDocumentGridCellRenderer();
    //
    private JDialog dialog;
    private final JLabel gridView = new JLabel(new ImageIcon(TextureManager.def().loadImage(
            "grid_view.png")));
    private final JLabel listView = new JLabel(new ImageIcon(TextureManager.def().loadImage(
            "list_view.png")));
    private final JLabel slideView = new JLabel(new ImageIcon(TextureManager.def().loadImage(
            "slideshow.png")));
    //
    private final GridTransferHandler transferHandle;
    private PopupMenuListener popupHandle;
//    private TopTermsResultView topTermsView;

    public ResultView() {
        super(new BorderLayout());
        initOwnComponents();
        //install the transfer handler for drag and drop
        transferHandle = new GridTransferHandler(grid);
        PREF.addPreferenceChangeListener(this);
    }

    public void setLazyList(LazyList<Document> lazyList) {
        grid.setModel(new LazyDocumentListModel(lazyList));
        list.setModel(new LazyDocumentListModel(lazyList));
        slideAction.setModel(lazyList);
        //
        if (popupHandle == null) {
            popupHandle = new PopupMenuListener(this, lazyList);
            grid.getSelectionModel().addListSelectionListener(popupHandle);
            list.getSelectionModel().addListSelectionListener(popupHandle);
            grid.addMouseListener(popupHandle);
            list.addMouseListener(popupHandle);
        }
    }

    public void addViewAction(ViewAction viewAction) {
        viewBarPanel.add(new ViewActionLabel(viewAction));
    }

    private void initOwnComponents() {
        listView.addMouseListener(this);
        gridView.addMouseListener(this);
        slideView.addMouseListener(this);
        //
        list.setCellRenderer(new ListCellRendererDelegate());
        list.setBackground(UIManager.getColor(UIDefaults.BACKGROUND_GRID));
        //
        list.setDragEnabled(true);
        list.setTransferHandler(new ListTransferHandler());
        grid.setUI(new ImageBoxGridUI());
        grid.setBackground(UIManager.getColor(UIDefaults.BACKGROUND_GRID));
        grid.setFixedCellDimension(128);
        /* clear selection action */
        SwingUtils.registerKeyBoardAction(grid, new ClearSelectionAction());
        SwingUtils.registerKeyBoardAction(grid, slideAction);

        //
        defaultGridRenderer.setDrawShadow(PREF.getBoolean(KEY_DRAW_SHADOW, true));
        defaultGridRenderer.setDrawDescription(PREF.getBoolean(KEY_DRAW_DESCRIPTION, true));
        grid.getCellRendererManager().setDefaultRenderer(defaultGridRenderer);
        //
        slider = new JSlider(64, 164, grid.getFixedCellDimension());
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                grid.setFixedCellDimension(((JSlider) e.getSource()).getValue());
            }
        });
        controlView = new JPanel(new BorderLayout());

        viewBarPanel = new JPanel(new GridLayout());
        viewBarPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
        gridView.setBorder(new EmptyBorder(0, 3, 0, 3));
        listView.setBorder(new EmptyBorder(0, 3, 0, 3));

        viewBarPanel.add(gridView);
        viewBarPanel.add(listView);
        viewBarPanel.add(slideView);

        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sliderPanel.add(small = new JLabel(new ImageIcon(TextureManager.def().loadImage("dim_small.png"))));
        sliderPanel.add(slider);
        sliderPanel.add(big = new JLabel(new ImageIcon(TextureManager.def().loadImage("dim_large.png"))));

        controlView.add(viewBarPanel, BorderLayout.WEST);
        controlView.add(sliderPanel, BorderLayout.EAST);

        content = new JPanel(new CardLayout());

        JScrollPane gridScroll = new JScrollPane(grid);
        gridScroll.setBorder(UIManager.getBorder(UIDefaults.BORDER_GRID_VIEW));

        content.add(gridScroll, "gridView");

        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(UIManager.getBorder(UIDefaults.BORDER_GRID_VIEW));

        /* list view */
        JPanel listResult = new JPanel(new BorderLayout());
        listResult.add(listScroll, BorderLayout.CENTER);

        content.add(listResult, "listView");

        /* test */
//        TimeLineRenderer renderer = new TimeLineRenderer();
//        JPanel timeLineConent = new JPanel(new BorderLayout());
//        timeLineConent.setBorder(
//                BorderFactory.createCompoundBorder(
//                BorderFactory.createEmptyBorder(10, 10, 10, 10),
//                BorderFactory.createLineBorder(Color.BLACK, 0)));
//        TimeLine timeLine = new TimeLine(
//                DateUtil.parseEXIFFormat("2008.01.01 13:00").getTime(),
//                DateUtil.parseEXIFFormat("2008.05.01 13:00").getTime());
//        timeLine.random();
//        renderer.setTimeLine(timeLine);
//        renderer.setPreferredSize(new Dimension(200, 50));
//        timeLineConent.add(searchField, BorderLayout.NORTH);
//        timeLineConent.add(renderer, BorderLayout.CENTER);
        JPanel northView = new JPanel();
        northView.setLayout(new BoxLayout(northView, BoxLayout.Y_AXIS));

//        northView.add(topTermsView = new TopTermsResultView());
//        topTermsView.setPreferredSize(new Dimension(200, 130));
        northView.add(controlView);
        /* */
        northView.add(searchPanel = new SearchPanel());

        add(northView, BorderLayout.NORTH);

        add(overlay = new JXLayer(content, new ImageLayerUI()), BorderLayout.CENTER);

        /* inject querybuilder */
//        ApplicationContext.QUERY_MANAGER.getDefaultValue().addQueryBuilder(topTermsView);
        ApplicationContext.QUERY_MANAGER.getDefaultValue().addQueryBuilder(searchPanel);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        GenericEventBus.addEventListener(QueryResultEvent.class, this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        GenericEventBus.removeEventListener(QueryResultEvent.class, this);
    }

    @Override
    public void handleEvent(QueryResultEvent event) {
        searchPanel.handleEvent(event);
    }

    public int getZoomFactor() {
        return slider.getValue();
    }

    public void setZoomFactor(int value) {
        slider.setValue(value);
    }

    public JPanel getControlView() {
        return controlView;
    }

    public ImageLayerUI getImageLayerUI() {
        return (ImageLayerUI) overlay.getUI();
    }

    public JGrid getGrid() {
        return grid;
    }

    public JList getList() {
        return list;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            CardLayout l = (CardLayout) content.getLayout();
            if (e.getSource() == listView) {
                enabled(false);
                l.show(content, "listView");
            } else if (e.getSource() == gridView) {
                enabled(true);
                l.show(content, "gridView");
            } else if (e.getSource() == slideView) {
                slideAction.actionPerformed(null);
            }
        }
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent pce) {
        if (pce.getKey().equals(KEY_DRAW_SHADOW)) {
            defaultGridRenderer.setDrawShadow(PREF.getBoolean(KEY_DRAW_SHADOW,
                    Boolean.valueOf(pce.getNewValue())));
            repaint();
        } else if (pce.getKey().equals(KEY_DRAW_DESCRIPTION)) {
            defaultGridRenderer.setDrawDescription(PREF.getBoolean(KEY_DRAW_DESCRIPTION,
                    Boolean.valueOf(pce.getNewValue())));
            repaint();
        } else if (pce.getKey().equals(KEY_IMAGE_COLOR_CLASSIFICATION)) {
            IndexManager index = ApplicationContext.instance().get(
                    IndexManager.LUCENE_MANAGER);
            ImageLuceneFileHandler handle = index.findHandle(ImageLuceneFileHandler.class);
            handle.setClassifyImage(Boolean.valueOf(pce.getNewValue()));
        }
    }

    private void enabled(boolean value) {
        small.setEnabled(value);
        big.setEnabled(value);
        slider.setEnabled(value);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    class ClearSelectionAction extends AbstractAction {

        public ClearSelectionAction() {
            super("clear_selection_action");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            grid.getSelectionModel().clearSelection();
        }
    }

    class ViewActionLabel extends JLabel implements MouseListener {

        private ViewAction _viewAction;
        private boolean lazy = true;

        public ViewActionLabel(ViewAction viewAction) {
            super();
            setBorder(new EmptyBorder(0, 3, 0, 3));
            this._viewAction = viewAction;
            if (viewAction.getValue(Action.SMALL_ICON) != null) {
                setIcon((Icon) viewAction.getValue(Action.SMALL_ICON));
            } else {
                setText(viewAction.getValue(Action.NAME).toString());
            }
            addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            _viewAction.actionPerformed(null);
            String cardName = _viewAction.getValue(Action.NAME).toString();
            /* already added */
            if (lazy) {
                content.add(_viewAction.getComponent(), cardName);
                lazy = false;
            }
            /* switch to card */
            CardLayout layout = (CardLayout) content.getLayout();
            enabled(false);
            layout.show(content, cardName);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
