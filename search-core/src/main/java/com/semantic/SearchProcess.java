/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic;

import igmas.process.JVMProcess;
import igmas.process.util.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SearchProcess extends JVMProcess {
    /* */
    private final List<File> _classPath = new ArrayList<File>();
    private final Map<String, String> _properties = new HashMap<String, String>();
    private File _splashScreen;

    public SearchProcess() {
        super();
        initProcess();
    }

    private void initProcess() {
        _classPath.add(new File("./search-core.jar"));
        _classPath.add(new File("./lib/*"));
        _classPath.add(new File("./plugin/*"));
        _classPath.add(new File("./plugin/lib/*"));
    }

    @Override
    public String command() {
        /* look if we have an bundled jvm and prefer that */
        File bundled = Util.findBundledJRE();
        if (bundled != null && bundled.exists()) {
            return bundled.getAbsolutePath();
        }
        return super.command();
    }

    @Override
    public List<File> getClassPath() {
        return _classPath;
    }

    @Override
    public File getSplash() {
        return _splashScreen;
    }

    @Override
    public Map<String, String> getProperties() {
        return _properties;
    }

    @Override
    public String getMainClass() {
        return "com.semantic.ApplicationContext";
    }

    @Override
    public int getXms() {
        return 32;
    }

    @Override
    public int getXmx() {
        return 1024;
    }

    public static void main(String[] arg) throws IOException {
        /* default startup parameter */
        JVMProcess process = new SearchProcess();
        /* search for jvm configuration */
        File jvm = new File(new File(ApplicationContext.ISEARCH_HOME), "jvm.conf");
        /* load settings */
        if (jvm.exists()) {
            try {
                process = Util.read(jvm);
            } catch (JAXBException ex) {
                ex.printStackTrace();
            }
        }
        /* build the jvm with the configuration */
        ProcessBuilder builder = new ProcessBuilder(process.buildCommandLine());        
        builder.directory(process.workingDirectory());
        /* start process and exit current jvm */
        builder.start();
        /* exit us!? */
        System.exit(0);
    }
}