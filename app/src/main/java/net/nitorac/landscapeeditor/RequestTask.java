package net.nitorac.landscapeeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Nitorac.
 */
public class RequestTask extends AsyncTask<Void, Void, Integer> {

    private ResultsFragment resFrag;
    private Bitmap bmp;
    private Bitmap resBitmap;

    public RequestTask(ResultsFragment resFrag){
        this.resFrag = resFrag;
    }

    @Override
    protected void onPreExecute(){
        resFrag.validateBtn.setProgress(50);
    }

    @Override
    protected void onPostExecute(final Integer res){
        if(res == 100){
            resBitmap = Bitmap.createScaledBitmap(bmp, resFrag.resView.getMeasuredWidth(), resFrag.resView.getMeasuredHeight(), true);
            resFrag.currentBitmap = resBitmap;
            resFrag.resView.setImageBitmap(resBitmap);
        }
        resFrag.validateBtn.setProgress(res);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                resFrag.validateBtn.setProgress(0);
            }
        }, 5000);
    }

    @Override
    protected Integer doInBackground(Void... v) {
        OkHttpClient httpClient = new OkHttpClient();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.createScaledBitmap(((MainActivity)resFrag.getActivity()).inputImage, 512, 512, false).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String inputEncoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        int randomNumber = new Random().nextInt(1000000000);
        String name = format.format(new Date()) + ", " + System.currentTimeMillis() + "-" + randomNumber;

        if (MainActivity.REQ_URL.isEmpty() || MainActivity.RECEIVE_URL.isEmpty()) {
            try {
                Matcher matchUrl = Pattern.compile("(http://.*)\"").matcher(Jsoup.connect("https://nvlabs.github.io/SPADE/demo.html").followRedirects(false).execute().body());
                String baseUrl = "";
                if (matchUrl.find()) {
                    baseUrl = matchUrl.group(1);
                }
                Connection.Response conn = Jsoup.connect(baseUrl + "/demo.js")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate").ignoreContentType(true).execute();
                if (!conn.contentType().equals("application/javascript")) {
                    Matcher notJsMatcher = Pattern.compile("(http://.*/demo\\.js)").matcher(conn.body());
                    if (notJsMatcher.find()) {
                        conn = Jsoup.connect(notJsMatcher.group(0))
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0")
                                .ignoreContentType(true)
                                .execute();
                    }
                }
                String js = StringEscapeUtils.unescapeJava(conn.body().replaceAll("\\\\x", "\\\\u00"));
                Matcher matcher = Pattern.compile("http://(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5]):443").matcher(js);
                if (matcher.find()) {
                    String found = "";
                    while (matcher.find()) {
                        found = matcher.group(0);
                    }
                    MainActivity.REQ_URL = found + "/nvidia_gaugan_submit_map";
                    MainActivity.RECEIVE_URL = found + "/nvidia_gaugan_receive_image";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        RequestBody formBody = new FormBody.Builder()
                .add("imageBase64", "data:image/png;base64," + inputEncoded)
                .add("name", name)
                .build();
        Request submitReq = new Request.Builder()
                .url(MainActivity.REQ_URL)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        try {
            Response response = httpClient.newCall(submitReq).execute();
            if(!response.isSuccessful() || response.body() == null){
                System.out.println(response.body());
                return -1;
            }

            RequestBody formBody2 = new FormBody.Builder()
                    .add("name", name)
                    .add("style_name", String.valueOf(MainActivity.getInstance().savedStyle))
                    .add("artistic_style_name", "none")
                    .build();
            Request receiveReq = new Request.Builder()
                    .url(MainActivity.RECEIVE_URL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0")
                    .post(formBody2)
                    .build();

            Response finalResp = httpClient.newCall(receiveReq).execute();

            if(!finalResp.isSuccessful() || finalResp.body() == null){
                System.out.println(response.body());
                return -1;
            }
            bmp = BitmapFactory.decodeStream(finalResp.body().byteStream());
            return 100;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
