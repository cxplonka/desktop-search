package worldwind.contrib.layers.quadkey;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.retrieve.HTTPRetriever;
import gov.nasa.worldwind.retrieve.RetrievalPostProcessor;
import gov.nasa.worldwind.retrieve.Retriever;
import gov.nasa.worldwind.retrieve.URLRetriever;
import gov.nasa.worldwind.util.WWIO;

/**
 * QuadKey tile
 *
 * @author Owner
 *
 */
public class QuadKeyEarthTile {

    // HTTP request read timeout
    private final int READ_TIMEOUT = 8000;
    private String cacheRoot = "Earth/";
    private String tileKey;
    private String tileURL;
    private boolean loading = false;

    /**
     * Constructor
     *
     * @param tileKey tile ID
     * @param tileURL Image url
     * @param cacheRoot WW tile cache location
     */
    public QuadKeyEarthTile(String tileKey, final String tileURL, final String cacheRoot) {
        this.tileKey = tileKey;
        this.tileURL = tileURL;
        this.cacheRoot = cacheRoot;
    }

    /**
     * Asynch download
     */
    public synchronized void download() {
        final String cacheFile = cacheRoot + tileKey;
        final File file = WorldWind.getDataFileStore().newFile(cacheFile);

        if (loading) {
            return;
        }

        loading = true;

        // Use the WW task service to load the URL asynchronously
        WorldWind.getTaskService().addTask(new Runnable() {
            public void run() {
                try {
                    downloadUrl(tileURL, file);
                } catch (Exception e) {
                    file.delete();
                    //logger.error(e);
                } finally {
                    loading = false;
                }
            }
        });

    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public String toString() {
        return tileKey;
    }

    /**
     * HTTP download post processotr
     *
     * @author Owner
     *
     */
    public static class DownloadPostProcessor implements RetrievalPostProcessor {

        File outFile;

        public DownloadPostProcessor(File outFile) {
            this.outFile = outFile;
        }

        public ByteBuffer run(Retriever retriever) {
            if (retriever == null) {
                return null;
            }

            if (!retriever.getState().equals(Retriever.RETRIEVER_STATE_SUCCESSFUL)) {
                return null;
            }

            URLRetriever r = (URLRetriever) retriever;
            ByteBuffer buffer = r.getBuffer();

            if (retriever instanceof HTTPRetriever) {
                HTTPRetriever htr = (HTTPRetriever) retriever;

                if (htr.getResponseCode() != HttpURLConnection.HTTP_OK) {

                    return null;
                }
            }

            if (outFile.exists()) {
                return buffer;
            }

            if (buffer != null) {
                String contentType = r.getContentType();
                if (contentType == null) {
                    return null;
                }

                if (contentType.contains("image")) {
                    // Just save whatever it is to the cache.
                    try {
                        WWIO.saveBuffer(buffer, outFile);
                    } catch (IOException e) {
                    }
                }
            }
            return buffer;
        }
    }

    /**
     * Download remote url
     *
     * @param url
     * @param file
     */
    private void downloadUrl(String url, File file) throws MalformedURLException {
        Retriever retriever = new HTTPRetriever(new URL(url), new DownloadPostProcessor(file));

        retriever.setReadTimeout(READ_TIMEOUT);
        WorldWind.getRetrievalService().runRetriever(retriever);
    }
}
