package com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.logs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.NewElectric.app11.units.FilesDirectoryUnits;

public class HttpUploadLogs {

    public void httpPost(final String urlstr,final String day,final String uploadFile, final String cabid, final String admid) {

        final Map<String, String> textMap = new HashMap<String, String>();
        textMap.put("cabid", cabid);
        textMap.put("admid", admid);

        String path = FilesDirectoryUnits.INTERNAL_LOG_DIR + day +"/" + uploadFile;
        final Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("Filedata", path);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                String res = "";
                HttpURLConnection conn = null;
                String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
                try {
                    URL url = new URL(urlstr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setChunkedStreamingMode(0);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

                    OutputStream out = new DataOutputStream(conn.getOutputStream());
                    // text
                    if (textMap != null) {
                        StringBuffer strBuf = new StringBuffer();
                        Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry<String, String> entry = iter.next();
                            String inputName = (String) entry.getKey();
                            String inputValue = (String) entry.getValue();
                            if (inputValue == null) {
                                continue;
                            }
                            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                            strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                            strBuf.append(inputValue);
                        }
                        out.write(strBuf.toString().getBytes());
                    }

                    // file
                    if (fileMap != null) {
                        Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry<String, String> entry = iter.next();
                            String inputName = (String) entry.getKey();
                            String inputValue = (String) entry.getValue();
                            if (inputValue == null) {
                                continue;
                            }
                            File file = new File(inputValue);
                            String filename = file.getName();

                            StringBuffer strBuf = new StringBuffer();
                            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                            strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                            strBuf.append("Content-Type:" + "image/jpeg" + "\r\n\r\n");

                            out.write(strBuf.toString().getBytes());

                            DataInputStream in = new DataInputStream(new FileInputStream(file));
                            int bytes = 0;
                            byte[] bufferOut = new byte[1024];
                            long totalCount = file.length() / 1024;
                            long nowCount = 1;
                            while ((bytes = in.read(bufferOut)) != -1) {
                                out.write(bufferOut, 0, bytes);
                                System.out.println("movies：   上传中   总共 - " + totalCount + "   当前 - " + nowCount);
                                nowCount = nowCount + 1;
                            }
                            in.close();
                        }
                    }

                    byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
                    out.write(endData);
                    out.flush();
                    out.close();

                    // 读取返回数据
                    StringBuffer strBuf = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        strBuf.append(line).append("\n");
                    }
                    res = strBuf.toString();
                    System.out.println("movies：   上传成功   " + res.toString());
                    reader.close();
                    reader = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.toString());
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                        conn = null;
                    }
                }
            }
        };
        thread.start();
    }
}
