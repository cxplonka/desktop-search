/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.action;

import com.l2fprod.common.util.OS;
import com.semantic.ApplicationContext;
import com.semantic.crawler.filesystem.LuceneIndexWriteTask;
import com.semantic.lucene.IndexManager;
import com.semantic.swing.MainFrame;
import com.semantic.util.DateUtil;
import com.semantic.util.FileUtil;
import com.semantic.util.Files;
import com.semantic.util.VisitorPattern;
import com.semantic.util.image.ImageUtil;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RenameJPGAction extends AbstractAction {

    private JFileChooser chooser;

    public RenameJPGAction() {
        super("Rename JPG Files (Creation Date)");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        File directory = null;
        /* native dialog for mac, not so much problems */
        if (OS.isMacOSX()) {
            MainFrame frame = ApplicationContext.instance().get(ApplicationContext.MAIN_VIEW);
            FileDialog fileDialog = new FileDialog(frame, "Select Directory", FileDialog.LOAD);
            fileDialog.setVisible(true);
            /* construct directory path */
            if (fileDialog.getDirectory() != null) {
                directory = new File(fileDialog.getDirectory() + fileDialog.getFile());
            }
        } else {
            if (chooser == null) {
                chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            int ret = chooser.showDialog((Component) ae.getSource(), "Select Directory");
            if (ret == JFileChooser.APPROVE_OPTION) {
                /* add to the ontology model node */
                directory = chooser.getSelectedFile();
            }
        }
        /* */
        if (directory != null) {
            recursiveRename(directory);
        }
    }

    private void recursiveRename(final File directory) {
        setEnabled(false);

        IndexManager lucene = ApplicationContext.instance().get(
                IndexManager.LUCENE_MANAGER);
        /* push to task service */
        lucene.getTaskService().submit(new Runnable() {

            @Override
            public void run() {
                Files.walkTree(directory, new RenameVisitor());
                setEnabled(true);
            }
        });
    }

    static class RenameVisitor implements VisitorPattern<File> {

        static final Logger log = Logger.getLogger(RenameVisitor.class.getName());
        static final String pattern = "yyyy_MM_dd_HH_mm";
        static final SimpleDateFormat FORMAT = new SimpleDateFormat(pattern);

        private String exists(File base, Date date, String ext, int idx) {
            String file = String.format("%s.%s", FORMAT.format(date), ext);
            if (idx != 0) {
                file = String.format("%s_%s.%s", FORMAT.format(date), idx, ext);
            }
            if (new File(base, file).exists()) {
                return exists(base, date, ext, ++idx);
            }
            return file;
        }

        @Override
        public boolean visit(File node) {
            if (node.isFile()) {
                String ext = FileUtil.getFileExtension(node);
                if (ext.equals("jpg") || ext.equals("jpeg")) {
                    IImageMetadata metadata;
                    try {
                        metadata = Sanselan.getMetadata(node);
                        /* user comment tag */
                        TiffField field = ImageUtil.readExifField(metadata,
                                ExifTagConstants.EXIF_TAG_CREATE_DATE);
                        if (field != null) {
                            Date date = DateUtil.parseEXIFFormat(field.getStringValue());
                            /* else unknown format */
                            if (date != null) {
                                /* check if we already renamed the file with our pattern */
                                if (node.getName().length() >= pattern.length()) {                                    
                                    /* try to compile */
                                    String ptr = node.getName().substring(0, pattern.length());                                    
                                    try {
                                        FORMAT.parse(ptr);
                                        //dont not need to rename
                                        return true;
                                    } catch (ParseException e) {
                                        //ignore
                                    }
                                }
                                File nFile = new File(node.getParentFile(),
                                        exists(node.getParentFile(), date, ext, 0));
                                //
                                node.renameTo(nFile);
                                nFile.setLastModified(date.getTime());
                                //
                                log.log(Level.INFO, "Successful changed date format: {0}", node.getAbsolutePath());
                            }
                        }
                    } catch (Exception ex) {
                        log.log(Level.INFO, "can not change date.", ex);
                    }
                }
            }
            return true;
        }
    }
}
