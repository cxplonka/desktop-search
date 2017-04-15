/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import com.semantic.lucene.handler.LuceneFileHandler;
import com.semantic.model.filter.OCalcFilter;
import com.semantic.model.filter.OFileSizeFilter;
import com.semantic.model.filter.OFixedFileDateFilter;
import com.semantic.model.filter.OHasGPSFilter;
import com.semantic.model.filter.OImageAspectRatioFilter;
import com.semantic.model.filter.OImageResolutionFilter;
import com.semantic.model.filter.OKeywordFilter;
import com.semantic.model.filter.OMimeTypeFilter;
import com.semantic.model.filter.OPowerPointFilter;
import com.semantic.model.filter.OWordFilter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class BasicModel extends OModel {
    
    public BasicModel() {
        super();
        /* create basic model */
        create();
    }
    
    private void create() {
        addNode(new OFileSystem());
        
        OGroup filters = new OGroup("Filters");
        filters.addNode(createFileNode());
        filters.addNode(createImageNode());
        filters.addNode(createDateNode());
        filters.addNode(createOtherNode());
        
        addNode(filters);
    }
    
    private OntologyNode createOtherNode() {
        OGroup gother = new OGroup("Others");
        
        OKeywordFilter kigmas = new OKeywordFilter();
        kigmas.setName("Contains: igmas");        
        kigmas.setKeyWord("igmas");
        
        OKeywordFilter ktrans = new OKeywordFilter();
        ktrans.setName("Contains: transinsight");
        ktrans.setKeyWord("transinsight");
        
        OHasGPSFilter gps = new OHasGPSFilter();
        gps.setName("Is Geo-located");        
        
        gother.addNode(kigmas);
        gother.addNode(ktrans);
        gother.addNode(gps);
        
        return gother;
    }
    
    private OntologyNode createFileNode() {
        OGroup gdoc = new OGroup("Documents");        
        
        OFileSizeFilter small = new OFileSizeFilter();
        small.setName("File Size Small(< 1mb)");
        
        OFileSizeFilter medium = new OFileSizeFilter();
        medium.setName("File Size Medium(1mb - 5mb)");
        medium.setMinSize(1 * 1024 * 1024l);
        medium.setMaxSize(5 * 1024 * 1024l);
        
        OFileSizeFilter big = new OFileSizeFilter();
        big.setName("File Size Big(> 5mb)");
        big.setMinSize(5 * 1024 * 1024l);
        big.setMaxSize(Long.MAX_VALUE);        
        
        OGroup gsize = new OGroup("Size");
        gsize.addNode(small);
        gsize.addNode(medium);
        gsize.addNode(big);
        
        gdoc.addNode(gsize);
        
        OMimeTypeFilter pdf = new OMimeTypeFilter("Portable Document Format (PDF)");
        pdf.setMimeType("application/pdf");
        gdoc.addNode(pdf);
        
        OMimeTypeFilter svg = new OMimeTypeFilter("Scalable Vector Graphics (SVG)");
        svg.setMimeType("image/svg+xml");
        gdoc.addNode(svg);        
        
        gdoc.addNode(new OPowerPointFilter());
        gdoc.addNode(new OWordFilter());
        gdoc.addNode(new OCalcFilter());
        
        OMimeTypeFilter email = new OMimeTypeFilter("E-Mail Messages");
        email.setMimeType("message/rfc822");
        gdoc.addNode(email);
        
        return gdoc;
    }
    
    private OntologyNode createImageNode() {
        OImageResolutionFilter ismall = new OImageResolutionFilter();
        ismall.setName("Pixel - Image Small(<0.5mio)");
        ismall.setMinSize(0);
        ismall.setMaxSize(500000);
        
        OImageResolutionFilter imed = new OImageResolutionFilter();
        imed.setName("Pixel - Image Medium(0.5mio - 2mio)");
        imed.setMinSize(500000);
        imed.setMaxSize(2000000);
        
        OImageResolutionFilter ibig = new OImageResolutionFilter();
        ibig.setName("Pixel - Image Large(2mio - 5mio)");
        ibig.setMinSize(2000000);
        ibig.setMaxSize(5000000);

        /* aspect ratio */
        OImageAspectRatioFilter sq = new OImageAspectRatioFilter();
        sq.setName("Ratio - Square");
        sq.setMinSize(0.95f);
        sq.setMaxSize(1.05f);
        
        OImageAspectRatioFilter sv = new OImageAspectRatioFilter();
        sv.setName("Ratio - Vertical");
        sv.setMinSize(0.0f);
        sv.setMaxSize(0.95f);
        
        OImageAspectRatioFilter sh = new OImageAspectRatioFilter();
        sh.setName("Ratio - Horizontal");
        sh.setMinSize(1.05f);
        sh.setMaxSize(2f);
        
        OImageAspectRatioFilter sp = new OImageAspectRatioFilter();
        sp.setName("Ratio - Panorama");
        sp.setMinSize(2f);
        sp.setMaxSize(5f);
        
        OGroup gimage = new OGroup("Image");
        
        OGroup gsize = new OGroup("Size");
        gsize.addNode(ismall);
        gsize.addNode(imed);
        gsize.addNode(ibig);
        gimage.addNode(gsize);
        
        OGroup gratio = new OGroup("Ratio");
        gratio.addNode(sq);
        gratio.addNode(sv);
        gratio.addNode(sh);
        gratio.addNode(sp);
        gimage.addNode(gratio);
        
        OGroup imageTypes = new OGroup("Image Types");
        for (String type : LuceneFileHandler.convertMimeTypes(
                LuceneFileHandler.IMAGE_MIME_TYPES)) {
            imageTypes.addNode(new OMimeTypeFilter(type));
        }
        gimage.addNode(imageTypes);
        
        return gimage;
    }
    
    private OntologyNode createDateNode() {
        OGroup date = new OGroup("Date");
        /* last month filter */
        date.addNode(new OFixedFileDateFilter(
                OFixedFileDateFilter.DATE.LAST_MONTH));
        /* this year */
        date.addNode(new OFixedFileDateFilter(
                OFixedFileDateFilter.DATE.THIS_YEAR));
        
        return date;
    }
}
