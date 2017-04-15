/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.worldwind.view;

import com.semantic.eventbus.GenericEventBus;
import com.semantic.eventbus.GenericEventListener;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.image.LatField;
import com.semantic.lucene.fields.image.LonField;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.model.OModel;
import com.semantic.model.OntologyNode;
import com.semantic.model.filter.OGeoHashFilter;
import com.semantic.model.filter.OHasGPSFilter;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.image.TextureManager;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwind.util.layertree.LayerTreeModel;
import gov.nasa.worldwind.util.tree.BasicTreeLayout;
import gov.nasa.worldwind.util.tree.TreeLayout;
import gov.nasa.worldwindx.examples.util.HotSpotController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.LongBitSet;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class WorldWindView extends JPanel implements PropertyChangeListener,
        GenericEventListener<QueryResultEvent> {

    private WorldWindowGLCanvas _view;
    private OModel _model;
    private final RenderableLayer _areaLayer = new RenderableLayer();
    private final AnnotationLayer _annotations = new AnnotationLayer();
    private final ShapeAttributes _attr = new BasicShapeAttributes();
    private final OHasGPSFilter _gpsFilter = new OHasGPSFilter();
    private LongBitSet _currentHit;
    private LongBitSet _currentQuery;

    public WorldWindView() {
        super(new BorderLayout());
        initOwnComponents();
    }

    private void initOwnComponents() {
        _attr.setInteriorOpacity(.5);

        Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        _view = new WorldWindowGLCanvas();
        _view.setModel(model);

        LayerList layerList = model.getLayers();
        //        model.getLayers().add(_areaLayer);
//        layerList.add(new GoogleEarthLayer());
//        layerList.add(new VirtualEarthLayer());
//        layerList.add(new GoogleRoadsLayer());
        layerList.add(_annotations);

        ViewControlsLayer vLayer = new ViewControlsLayer();
        ViewControlsSelectListener control = new ViewControlsSelectListener(_view, vLayer);
        _view.addSelectListener(control);
        layerList.add(vLayer);

        RenderableLayer layer = new RenderableLayer();
        LayerTree layerTree = new LayerTree(new LayerTreeModel(layerList)) {
            @Override
            protected TreeLayout createTreeLayout(Offset offset) {
                BasicTreeLayout ret = (BasicTreeLayout) super.createTreeLayout(offset);
//                    ret.getFrame().setMinimized(true);
                return ret;
            }
        };
        layer.addRenderable(layerTree);
        model.getLayers().add(layer);
        /* Add a controller to handle input events on the layer tree. */
        HotSpotController hotSpotController = new HotSpotController(_view);
        /* */
        add(_view, BorderLayout.CENTER);
    }

    public void setModel(OModel _model) {
        if (this._model != null) {
            this._model.removePropertyChangeListener(this);
        }
        this._model = _model;
        if (this._model != null) {
            this._model.addPropertyChangeListener(this);
            _areaLayer.removeAllRenderables();
            initLayer(_model);
        }
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

    private void initLayer(OntologyNode node) {
        if (node instanceof OGeoHashFilter) {
            addNode((OGeoHashFilter) node);
        }
        /* search deeper */
        for (int i = 0; i < node.getNodeCount(); i++) {
            initLayer(node.getChildAt(i));
        }
    }

    private void addNode(OGeoHashFilter node) {
        List<LatLon> line = new ArrayList<LatLon>();
        double lat_sw = node.getSouthWestLat();
        double lon_sw = node.getSouthWestLng();
        double lat_ne = node.getNorthEastLat();
        double lon_ne = node.getNorthEastLng();
        line.add(LatLon.fromDegrees(lat_sw, lon_sw));
        line.add(LatLon.fromDegrees(lat_ne, lon_sw));
        line.add(LatLon.fromDegrees(lat_ne, lon_ne));
        line.add(LatLon.fromDegrees(lat_sw, lon_ne));
        _areaLayer.addRenderable(new SurfacePolygon(_attr, line));
    }

    public WorldWindowGLCanvas getCanvas() {
        return _view;
    }

    @Override
    public void handleEvent(QueryResultEvent event) {
        IndexSearcher searcher = event.getCurrentSearcher();
        int maxDoc = searcher.getIndexReader().maxDoc();
        /* create, recreate cache */
        if (_currentHit == null || _currentHit.length() < maxDoc) {
            _currentHit = new LongBitSet(maxDoc);
            _currentQuery = new LongBitSet(maxDoc);
        }
        /* clear cache */
        _currentHit.clear(0, _currentHit.length());
        _currentQuery.clear(0, _currentQuery.length());
        /* search only in the current query - filter all gps hits :) */
        updateBitSet(_gpsFilter.createQuery(), searcher, _currentHit);
        /* search in current query */
        updateBitSet(event.getQuery(), searcher, _currentQuery);
        /* combine results */
        _currentHit.and(_currentQuery);
        /* create globe annotations - need lod system */
        _annotations.removeAllAnnotations();
        for (int i = 0; i < maxDoc; i++) {
            if (_currentHit.get(i)) {
                try {
                    addDocument(searcher.doc(i));
                } catch (IOException ex) {
                }
            }
        }
    }

    private void addDocument(Document doc) {
        if (doc.get(LatField.NAME) != null) {
            double lat = doc.getField(LatField.NAME).numericValue().doubleValue();
            double lng = doc.getField(LonField.NAME).numericValue().doubleValue();
            /* */
            GlobeAnnotation marker = new GlobeAnnotation("", Position.fromDegrees(lat, lng));
            marker.getAttributes().setDistanceMaxScale(1);
            marker.getAttributes().setLeader(AVKey.SHAPE_NONE);
            marker.getAttributes().setCornerRadius(0);
            marker.getAttributes().setBackgroundColor(new Color(255, 255, 255, 100));
            marker.getAttributes().setAdjustWidthToText(AVKey.SIZE_FIXED);

            /* complete filename */
            String fileName = doc.get(FileNameField.NAME);
            /* try to get thumbnail image from texture cache, if not manager will create
             * asynchron */
            File thumbFile = new File(ThumbnailManager.def().generateThumbName(new File(fileName)));
            BufferedImage im = TextureManager.def().loadImage(thumbFile);

            int inset = 0; // pixels
            marker.getAttributes().setImageSource(im);
            marker.getAttributes().setInsets(new Insets(im.getHeight(null) + inset * 2, inset, inset, inset));
            marker.getAttributes().setImageOffset(new Point(inset, inset));
            marker.getAttributes().setDrawOffset(new Point());
            marker.getAttributes().setImageRepeat(AVKey.REPEAT_NONE);
            marker.getAttributes().setSize(new Dimension(im.getWidth(null) + inset * 2, 0));

            _annotations.addAnnotation(marker);
        }
    }

    private void updateBitSet(Query query, IndexSearcher searcher, final LongBitSet set) {
        try {
            searcher.search(query, new SimpleCollector() {

                private int docBase;

                @Override
                public void collect(int doc) {
                    set.set(doc + docBase);
                }

                @Override
                protected void doSetNextReader(LeafReaderContext context) throws IOException {
                    super.doSetNextReader(context);
                    this.docBase = context.docBase;
                }

                @Override
                public boolean needsScores() {
                    return false;
                }
            });
        } catch (Exception ex) {
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_ADDED)) {
            if (evt.getNewValue() instanceof OGeoHashFilter) {
//                addNode((OGeoHashFilter) evt.getNewValue());
            }
        }
    }
}
