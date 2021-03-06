import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dominik on 06.06.2015.
 */
public class StreamCloudEUDownloader extends Downloader {
    private String filename;
    private String streamURL;

    public StreamCloudEUDownloader(String streamcloudURL) {
        try {
            Document streamcloud = Jsoup.connect(streamcloudURL).timeout(0)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0").get();

            Elements inputhidden = streamcloud.select("input[type=hidden]");

            String op = "";
            String usr_login = "";
            String id = "";
            String fname = "";
            String referer = "";
            String hash = "";
            String imhuman = streamcloud.select("input[name=imhuman]").attr("value");

            for (int i = 0; i < inputhidden.size(); i++) {
                if (inputhidden.get(i).attr("name").equals("op"))
                    usr_login = inputhidden.get(i).attr("value");

                if (inputhidden.get(i).attr("name").equals("usr_login"))
                    usr_login = inputhidden.get(i).attr("value");

                if (inputhidden.get(i).attr("name").equals("id"))
                    id = inputhidden.get(i).attr("value");

                if (inputhidden.get(i).attr("name").equals("fname"))
                    fname = inputhidden.get(i).attr("value");

                if (inputhidden.get(i).attr("name").equals("referer"))
                    referer = inputhidden.get(i).attr("value");

                if (inputhidden.get(i).attr("name").equals("hash"))
                    hash = inputhidden.get(i).attr("value");
            }
            op = "download1";
            filename = fname;
            try {
                Thread.sleep(12 * 1000);
            } catch (InterruptedException e) {
            }
            Connection.Response getVideo = Jsoup.connect(streamcloudURL)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
                    .data("op", op, "usr_login", usr_login, "id", id, "fname", fname, "referer",
                            referer, "hash", hash, "imhuman", imhuman)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true).execute();

            Document getStream = getVideo.parse();

            Elements scripts = getStream.select("script");
            for (int i = 0; i < scripts.size(); i++) {
                if (scripts.get(i).outerHtml().contains("file:")) {
                    String[] getFile = scripts.get(i).outerHtml().split(",");
                    for (int j = 0; j < getFile.length; j++) {
                        if (getFile[j].contains("file:")) {
                            streamURL = getFile[j].replace("file:", "").replace("\"", "").trim();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStreamURL() {
        return this.streamURL;
    }

    public String getFilename() {
        return this.filename;
    }

    public void DownloadFile(String dlUrl, String filename, int downloadSize, int i, DefaultTableModel dTableModel) throws Exception {
        try {
            URL url = new URL(dlUrl);
            URLConnection hc = url.openConnection();

            hc.setReadTimeout((100 * 1000));
            hc.setReadTimeout((100 * 1000));
            hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            super.DownloadFile(hc, filename, downloadSize, i, dTableModel);
        } catch (Exception ex) {
            if (ex instanceof java.net.SocketTimeoutException) {
                throw new java.net.SocketTimeoutException("Socket timeout!");
            } else
                System.err.println("Error while parsing download link from StreamCloudDownloader to Engine!");
        }
    }
}
