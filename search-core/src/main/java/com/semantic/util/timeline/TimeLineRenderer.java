/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.timeline;

import com.semantic.util.AffineUtils;
import com.semantic.util.DateUtil;
import com.semantic.util.Range;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TimeLineRenderer extends JPanel {

    private TimeLine _timeLine;
    private static final String[] MONTHS = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEZ"};
    private final AxisRenderer _monthAxis = new AxisMonthRenderer();
    private final AxisRenderer _yearAxis = new AxisYearRenderer();
    private Range _range = new Range();
    private Range _dateRange = new Range();
    private Path2D _shape;
    private Path2D _fillPath;
    private AffineTransform _transform = new AffineTransform();
    private Rectangle2D _fromBounds = new Rectangle2D.Double();
    private Rectangle2D _selectionBounds = new Rectangle2D.Double();
    private TickMarker _startMarker;
    private TickMarker _endMarker;
    private Rectangle2D _marker = new Rectangle2D.Double(-4, -4, 8, 8);
    private Color _markerColor = new Color(255, 0, 0);
    private Color _selectedFillColor = new Color(255, 204, 204, 180);
    private Color _contentFillColor = new Color(176, 196, 222);
    private Point2D _from = new Point2D.Double();
    private Point2D _to = new Point2D.Double();
    private ControlHandler _handle = new ControlHandler();

    public TimeLineRenderer() {
        super();
        /* register handle */
        addMouseListener(_handle);
        addMouseMotionListener(_handle);
        _monthAxis.setFlipAxis(true);
        _monthAxis.addTickMarker(_startMarker = new TickMarker(.4,
                _marker, _markerColor));
        _monthAxis.addTickMarker(_endMarker = new TickMarker(.7,
                _marker, _markerColor));
    }

    public void setTimeLine(TimeLine _timeLine) {
        this._timeLine = _timeLine;
        rebuild();
    }

    public TimeLine getTimeLine() {
        return _timeLine;
    }

    private void rebuild() {
        if (_timeLine != null) {
            _shape = new Path2D.Double();
            int[] freq = _timeLine.getFrequencyReference();
            int size = freq.length;
            int max = -Integer.MAX_VALUE, min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    _shape.moveTo(i, freq[i]);
                } else {
                    _shape.lineTo(i, freq[i]);
                }
                max = Math.max(freq[i], max);
                min = Math.min(freq[i], min);
            }
            /* close path with y = 0 */
            if (size > 0) {
                _shape.lineTo(size - 1, max + min);
                _shape.lineTo(0, max + min);
            }
            _fromBounds.setFrame(_shape.getBounds2D());
        }
        /* calculate date range */
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(_timeLine.fromDate);
        int fromYear = c1.get(Calendar.YEAR);
        _dateRange.setLowerBound(fromYear);
        c1.setTimeInMillis(_timeLine.toDate);
        int toYear = c1.get(Calendar.YEAR);
        _dateRange.setUpperBound(toYear == fromYear ? toYear + 1 : toYear);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (_timeLine != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            /* */
            double guessHeight = _monthAxis.guessDrawHeight() / 2.2;
            /* create transformation between data and view space */
            _selectionBounds.setFrame(0, 0, w, h - guessHeight);
            AffineUtils.createTransformToFitBounds(_fromBounds,
                    _selectionBounds, false, _transform);
            /* render the timeline */
            g2d.setPaint(_contentFillColor);
            Shape transformed = _shape.createTransformedShape(_transform);
            g2d.fill(transformed);
            g2d.setPaint(Color.BLUE);
            g2d.draw(transformed);

            /* fill between the start and end marker */
            _range.setBounds(0, w);
            double fx = _range.invert(_startMarker.getValue());
            double ex = _range.invert(_endMarker.getValue());
            _from.setLocation(fx, _selectionBounds.getY());
            _to.setLocation(ex, _selectionBounds.getHeight());
            _selectionBounds.setFrameFromDiagonal(_from, _to);
            g2d.setPaint(_selectedFillColor);
            g2d.fill(_selectionBounds);
            g2d.setPaint(Color.BLACK);
            g2d.draw(_selectionBounds);

            /* render axis */
            _monthAxis.setLine(0, h - guessHeight, w, h - guessHeight);
            _yearAxis.setLine(0, h - guessHeight, w, h - guessHeight);
            g2d.setPaint(Color.BLACK);
            _yearAxis.paint(g2d);
            _monthAxis.paint(g2d);            
        }
    }

    class AxisMonthRenderer extends AxisRenderer {

        @Override
        public String toLabel(double value) {
            if (_timeLine != null) {
                double v = _dateRange.invert(value);
                int month = (int) Math.round((v - (int) v) * 11);
                if (month == 0 || month >= MONTHS.length) {
                    return null;
                }
                return String.valueOf(MONTHS[month]);
            }
            return null;
        }
    }

    class AxisYearRenderer extends AxisRenderer {

        @Override
        public String toLabel(double value) {
            if (_timeLine != null) {
                double v = _dateRange.invert(value);
                int month = (int) Math.round((v - (int) v) * 11);
                if (month == 0 || month > MONTHS.length) {
                    return String.valueOf((int) v);
                }
            }
            return null;
        }
    }

    class ControlHandler extends MouseAdapter {

        private static final int TRANSLATE_AREA = 0;
        private static final int TRANSLATE_START = 1;
        private static final int TRANSLATE_END = 2;
        private int current = -1;
        private double sx;

        @Override
        public void mousePressed(MouseEvent e) {
            current = -1;
            /* move selection window */
            sx = _range.normalize(e.getX());
            if (_selectionBounds.contains(e.getPoint())) {
                current = TRANSLATE_AREA;
            }
            /* override selection */
            if (_startMarker.contains(e.getPoint())) {
                current = TRANSLATE_START;
            } else if (_endMarker.contains(e.getPoint())) {
                current = TRANSLATE_END;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            current = -1;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            /* current normalized coordinate */
            double nx = _range.normalize(e.getX());
            double dx = nx - sx;
            double ds = _startMarker.getValue() + dx;
            double de = _endMarker.getValue() + dx;
            /* */
            switch (current) {
                case TRANSLATE_AREA:
                    if (ds >= 0 && ds <= 1 && de >= 0 && de <= 1) {
                        _startMarker.setValue(ds);
                        _endMarker.setValue(de);
                        repaint();
                    }
                    break;
                case TRANSLATE_START:
                    if (ds >= 0 && ds <= 1) {
                        _startMarker.setValue(ds);
                        repaint();
                    }
                    break;
                case TRANSLATE_END:
                    if (de >= 0 && de <= 1) {
                        _endMarker.setValue(de);
                        repaint();
                    }
                    break;
            }
            sx = nx;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Cursor cursor = null;
            if (_selectionBounds.contains(e.getPoint())) {
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            }
            /* override selection */
            if (_startMarker.contains(e.getPoint())) {
                cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            } else if (_endMarker.contains(e.getPoint())) {
                cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            }
            /* no selection */
            if (cursor == null) {
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            }
            setCursor(cursor);
        }
    }

    public static void main(String[] arg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());

                TimeLineRenderer renderer = new TimeLineRenderer();

                JPanel content = new JPanel(new BorderLayout());
                int inset = 10;
                content.setBorder(
                        BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(inset, inset, inset, inset),
                        BorderFactory.createLineBorder(Color.BLACK, 0)));
                content.add(renderer, BorderLayout.CENTER);

                frame.add(content);

                TimeLine timeLine = new TimeLine(
                        DateUtil.parseEXIFFormat("2005.01.01 13:00").getTime(),
                        DateUtil.parseEXIFFormat("2008.05.01 13:00").getTime());
                timeLine.random();

                renderer.setTimeLine(timeLine);

                frame.setSize(400, 130);
                frame.setVisible(true);
            }
        });
    }
}