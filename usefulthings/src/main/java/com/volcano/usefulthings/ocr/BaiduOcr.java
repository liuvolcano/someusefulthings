package com.volcano.usefulthings.ocr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.volcano.usefulthings.base.util.HttpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liu-can on 2018/11/24.
 */
@Controller
public class BaiduOcr {

    @Value("${ak}")
    private String ak;

    @Value("${sk}")
    private String sk;

    @RequestMapping("/baiduOcr/test")
    @ResponseBody
    public String test(){
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=";
        String accessToken = "24.a7dff38e28a6f1275977c28915638162.2592000.1563759269.282335-11844761";
        String imgStr = null;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try{
            Map<String,Object> paraMap = new HashMap<>();
            imgStr = Base64Util.encode(readFileByByte("F:\\" + "111.png"));
            paraMap.put(URLEncoder.encode("image", "UTF-8"),URLEncoder.encode(imgStr, "UTF-8"));
            result = HttpUtil.doPost(url+accessToken,paraMap);
//            System.out.println("result-->" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            List<Object> list = (JSONArray)jsonObject.get("words_result");
            if(list != null){
                for(Object obj : list){
                    sb.append(((JSONObject)obj).get("words"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    @RequestMapping("/baiduOcr/getAccessToken")
    @ResponseBody
    public String getAccessTokenFromBaidu() throws Exception{
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONObject jsonObject = JSON.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            throw e;
        }
    }

    private byte[] readFileByByte(String filePath) throws FileNotFoundException,IOException{
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }
                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
                bos.close();
            }
        }
    }
}

class Base64Util {
    private static final char S_BASE64CHAR[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final byte S_DECODETABLE[];

    static {
        S_DECODETABLE = new byte[128];
        for (int i = 0; i < S_DECODETABLE.length; i++)
            S_DECODETABLE[i] = 127;

        for (int i = 0; i < S_BASE64CHAR.length; i++)
            S_DECODETABLE[S_BASE64CHAR[i]] = (byte) i;

    }


    /**
     * @param ibuf
     * @param obuf
     * @param wp
     * @return
     */

    private static int decode0(char ibuf[], byte obuf[], int wp) {
        int outlen = 3;
        if (ibuf[3] == '=')
            outlen = 2;
        if (ibuf[2] == '=')
            outlen = 1;
        int b0 = S_DECODETABLE[ibuf[0]];
        int b1 = S_DECODETABLE[ibuf[1]];
        int b2 = S_DECODETABLE[ibuf[2]];
        int b3 = S_DECODETABLE[ibuf[3]];
        switch (outlen) {
            case 1: // '\001'
                obuf[wp] = (byte) (b0 << 2 & 252 | b1 >> 4 & 3);
                return 1;

            case 2: // '\002'
                obuf[wp++] = (byte) (b0 << 2 & 252 | b1 >> 4 & 3);
                obuf[wp] = (byte) (b1 << 4 & 240 | b2 >> 2 & 15);
                return 2;

            case 3: // '\003'
                obuf[wp++] = (byte) (b0 << 2 & 252 | b1 >> 4 & 3);
                obuf[wp++] = (byte) (b1 << 4 & 240 | b2 >> 2 & 15);
                obuf[wp] = (byte) (b2 << 6 & 192 | b3 & 63);
                return 3;
        }
        throw new RuntimeException("Internal error");
    }


    /**
     * @param data
     * @param off
     * @param len
     * @return
     */

    public static byte[] decode(char data[], int off, int len) {
        char ibuf[] = new char[4];
        int ibufcount = 0;
        byte obuf[] = new byte[(len / 4) * 3 + 3];
        int obufcount = 0;
        for (int i = off; i < off + len; i++) {
            char ch = data[i];
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127))
                continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount == ibuf.length) {
                ibufcount = 0;
                obufcount += decode0(ibuf, obuf, obufcount);
            }
        }

        if (obufcount == obuf.length) {
            return obuf;
        } else {
            byte ret[] = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }
    }


    /**
     * @param data
     * @return
     */

    public static byte[] decode(String data) {
        char ibuf[] = new char[4];
        int ibufcount = 0;
        byte obuf[] = new byte[(data.length() / 4) * 3 + 3];
        int obufcount = 0;
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127))
                continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount == ibuf.length) {
                ibufcount = 0;
                obufcount += decode0(ibuf, obuf, obufcount);
            }
        }

        if (obufcount == obuf.length) {
            return obuf;
        } else {
            byte ret[] = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }
    }


    /**
     * @param data
     * @param off
     * @param len
     * @param ostream
     * @throws IOException
     */

    public static void decode(char data[], int off, int len, OutputStream ostream) throws IOException {
        char ibuf[] = new char[4];
        int ibufcount = 0;
        byte obuf[] = new byte[3];
        for (int i = off; i < off + len; i++) {
            char ch = data[i];
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127))
                continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount == ibuf.length) {
                ibufcount = 0;
                int obufcount = decode0(ibuf, obuf, 0);
                ostream.write(obuf, 0, obufcount);
            }
        }

    }


    /**
     * @param data
     * @param ostream
     * @throws IOException
     */

    public static void decode(String data, OutputStream ostream) throws IOException {
        char ibuf[] = new char[4];
        int ibufcount = 0;
        byte obuf[] = new byte[3];
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127))
                continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount == ibuf.length) {
                ibufcount = 0;
                int obufcount = decode0(ibuf, obuf, 0);
                ostream.write(obuf, 0, obufcount);
            }
        }

    }


    /**
     * @param data
     * @return
     */

    public static String encode(byte data[]) {
        return encode(data, 0, data.length);
    }


    /**
     * @param data
     * @param off
     * @param len
     * @return
     */

    public static String encode(byte data[], int off, int len) {
        if (len <= 0)
            return "";
        char out[] = new char[(len / 3) * 4 + 4];
        int rindex = off;
        int windex = 0;
        int rest;
        for (rest = len - off; rest >= 3; rest -= 3) {
            int i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
            out[windex++] = S_BASE64CHAR[i >> 18];
            out[windex++] = S_BASE64CHAR[i >> 12 & 63];
            out[windex++] = S_BASE64CHAR[i >> 6 & 63];
            out[windex++] = S_BASE64CHAR[i & 63];
            rindex += 3;
        }

        if (rest == 1) {
            int i = data[rindex] & 255;
            out[windex++] = S_BASE64CHAR[i >> 2];
            out[windex++] = S_BASE64CHAR[i << 4 & 63];
            out[windex++] = '=';
            out[windex++] = '=';
        } else if (rest == 2) {
            int i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
            out[windex++] = S_BASE64CHAR[i >> 10];
            out[windex++] = S_BASE64CHAR[i >> 4 & 63];
            out[windex++] = S_BASE64CHAR[i << 2 & 63];
            out[windex++] = '=';
        }
        return new String(out, 0, windex);
    }


    /**
     * @param data
     * @param off
     * @param len
     * @param ostream
     * @throws IOException
     */

    public static void encode(byte data[], int off, int len, OutputStream ostream) throws IOException {
        if (len <= 0)
            return;
        byte out[] = new byte[4];
        int rindex = off;
        int rest;
        for (rest = len - off; rest >= 3; rest -= 3) {
            int i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
            out[0] = (byte) S_BASE64CHAR[i >> 18];
            out[1] = (byte) S_BASE64CHAR[i >> 12 & 63];
            out[2] = (byte) S_BASE64CHAR[i >> 6 & 63];
            out[3] = (byte) S_BASE64CHAR[i & 63];
            ostream.write(out, 0, 4);
            rindex += 3;
        }

        if (rest == 1) {
            int i = data[rindex] & 255;
            out[0] = (byte) S_BASE64CHAR[i >> 2];
            out[1] = (byte) S_BASE64CHAR[i << 4 & 63];
            out[2] = 61;
            out[3] = 61;
            ostream.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
            out[0] = (byte) S_BASE64CHAR[i >> 10];
            out[1] = (byte) S_BASE64CHAR[i >> 4 & 63];
            out[2] = (byte) S_BASE64CHAR[i << 2 & 63];
            out[3] = 61;
            ostream.write(out, 0, 4);
        }
    }


    /**
     * @param data
     * @param off
     * @param len
     * @param writer
     * @throws IOException
     */

    public static void encode(byte data[], int off, int len, Writer writer) throws IOException {
        if (len <= 0)
            return;
        char out[] = new char[4];
        int rindex = off;
        int rest = len - off;
        int output = 0;
        do {
            if (rest < 3)
                break;
            int i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
            out[0] = S_BASE64CHAR[i >> 18];
            out[1] = S_BASE64CHAR[i >> 12 & 63];
            out[2] = S_BASE64CHAR[i >> 6 & 63];
            out[3] = S_BASE64CHAR[i & 63];
            writer.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
            if ((output += 4) % 76 == 0)
                writer.write("\n");
        }
        while (true);
        if (rest == 1) {
            int i = data[rindex] & 255;
            out[0] = S_BASE64CHAR[i >> 2];
            out[1] = S_BASE64CHAR[i << 4 & 63];
            out[2] = '=';
            out[3] = '=';
            writer.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
            out[0] = S_BASE64CHAR[i >> 10];
            out[1] = S_BASE64CHAR[i >> 4 & 63];
            out[2] = S_BASE64CHAR[i << 2 & 63];
            out[3] = '=';
            writer.write(out, 0, 4);
        }
    }
}