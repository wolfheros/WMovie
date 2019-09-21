package wolfheros.life.home.tools.Main;


import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;


/**
 *  此类是专门用来下载网络数据
 *  完成于 2018-5-28 23:28:50
 * */
public class DownloadFiles {

    private static final String HOME_PAGE = "http://www.dytt8.net/";
    private static final String TAG ="DownLoad Link:";
    private static final String DOWNLOAD_CODE_HOME = "co_content8";
    private static final int MAIN_CODE = 0;
    private static final int SEARCH_CODE = 1;
    private Elements homeElements;
    private String xunleiDownloadLink;
    private static DownloadFiles sDownloadFiles;
    private Elements nameElements;

    private Element photoAndTextElement;
    private SQLiteDataBaseHelper mSQLiteDataBaseHelper;
    private String searchuri;

    public DownloadFiles(){
    }

    public DownloadFiles(@NonNull SQLiteDataBaseHelper sqLiteDataBaseHelper) {
        super();
        getHomeElements(HOME_PAGE);
        mSQLiteDataBaseHelper = sqLiteDataBaseHelper;
    }

    // 抽取的解析主页获取所需 Elements 方法。
    public void getHomeElements(String uri){
          searchuri = uri;
        try {
            Document document = Jsoup.connect(uri).get();
            // 获取每个电影的连接
            Elements classElements = document.getElementsByClass(DOWNLOAD_CODE_HOME);
            homeElements = classElements.get(0).getElementsByTag("a");
            nameElements = document.getElementsByClass("title_all");
        } catch (IOException ioe) {
            Log.e(TAG ,"Cant  get HomePage Elements");
            ioe.printStackTrace();
        }
    }

    // 这是一个主页面获取方法。
    public List<MovieItem> urlHomePageLink() {

        // 创建一个可以储存队列。
        List<MovieItem> homePageList =  mSQLiteDataBaseHelper.getMovieItems();
        try {
            /*if (homeElements.size() < 2){
               new DownloadFiles().urlHomePageLink();
            }*/
            for (int i = 2; i < homeElements.size(); i += 2) {

                // 获取的电影url
                String link = homeElements.get(i).attr("abs:href");
                // 获取名称
                String name = homeElements.get(i).text();
                // 分割字符串。
                String[] reallyName = name.split("《");
                String[] reallyName1 = reallyName[1].split("》");
                /**
                 *  这里就是把数据加入数据库的地方。
                 * */
                MovieItem movieItem = getMovieItem(link, reallyName1[0] , null , MAIN_CODE);
                if (mSQLiteDataBaseHelper.getMovieItemDB(link) == null) {
                    mSQLiteDataBaseHelper.addMovieItemDB(movieItem);
                    homePageList.add(movieItem);
                }
                Log.i(TAG, "Link :" + link);
            }



        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        return homePageList;
    }

    /**
     * 我只需要把客户点击的添加到数据库中就可以了。
     * */
    public MovieItem searchUrlLink( MovieItem item , int code){
        try {
            for (int i = 0; i < homeElements.size(); i++) {
                // 获取的迅雷url
                String link = homeElements.get(i).attr("abs:href");
                if (link.startsWith("ftp:")){
                    xunleiDownloadLink = link;
                }
            }
            // 获取电影的名称
            String name = getSearchMovieName();
            /**
             *  这里就是把数据加入数据库的地方。
             * */
            Log.i(TAG, "Link :" + searchuri);

            return getMovieItem(searchuri, name ,item ,code);

        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
        return null;
    }

    private String getSearchMovieName(){
        String s1;
        try {
            for (int i = 0 ; i < nameElements.size() ; i++ ){
                s1 = nameElements.get(i).text();
                // 取出的不需要的字段值中最大的为 7 。
                if (s1.length() > 7){
                    if (s1.contains("《")) {
                        // 分割字符串。
                        String[] reallyName = s1.split("《");
                        if (reallyName[1].contains("》")) {
                            String[] reallyName1 = reallyName[1].split("》");
                            return reallyName1[0];
                        }else if (reallyName[1].contains("】")){
                            String[] reallyName1 = reallyName[1].split("】");
                            return reallyName1[0];
                        }else {
                            return s1;
                        }
                    }else {
                        return s1;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    // 抽取出来的获取主页Elements 的方法。

    // 获取Image 和 Text 的连接。获取mElement变量
    private MovieItem getMovieItem(String movieLink , String movieName , MovieItem item , int code) {
        MovieItem movieItem;
        if (item == null){
            // 获取哈希值。
            String uuid = HashValue.hashKeyForDisk(movieLink);
            // 只是添加电影名称和获取的连接。每次都要添加
            movieItem = new MovieItem(uuid);
            movieItem.setSearchMovie("false");

            Date date = new Date();
            Integer integer = (int) date.getTime();
            movieItem.setStoreTime(integer);

        }else {
            movieItem = item;
        }
        // 在这里设置"最爱电影"和"已经下载"电影的值。
        movieItem.setMyFavoMovie("false");
        movieItem.setReDownload("false");
        movieItem.setSearchMovie("false");
        movieItem.setName(movieName);
        // 设置储存时间
        if (movieItem.getStoreTime() == 0) {
            Date date = new Date();
            Integer integer = (int) date.getTime();
            movieItem.setStoreTime(integer);
        }
        // 设置每个对应的电影子连接。
        movieItem.setUri(movieLink);
        // 这个设置图片的方法必须先于设置字体的方法调用。
        setPhotoAndTextElement(movieLink);

        setXunLeiElements();

        // 使用handle 和 loop 循环调用，下载方法后进行替换。
        movieItem.setMovieDownloadUri(xunleiDownloadLink);
        if (code == MAIN_CODE) {
            movieItem = setPhotoUri(movieItem);
        }else if(code == SEARCH_CODE) {
            movieItem = setSearchPhotoUri(movieItem);
        }
        String text = photoAndTextElement.text();
        movieItem = setText(movieItem,text);
        return movieItem;
    }
    /**
     * 这里打开了每一个连接，获取图片和迅雷下载的 Elements 元素
     */
    private void setPhotoAndTextElement (String url) {
        try {
            // 电影的地址
            Document document = Jsoup.connect(url).get();
            photoAndTextElement = document.getElementsByClass(DOWNLOAD_CODE_HOME).get(0);
            // 设置成成员变量为了可以让textSetting 方法也适用。
        } catch (IOException ioe) {
            Log.e(TAG," static mElement got ERROR");
        }
    }

    private void setXunLeiElements(){
        // 获取迅雷连接
        Elements tbodyElements = photoAndTextElement.getElementsByTag("a");
        if (tbodyElements.size() != 0) {
            for (int i = 0; i < tbodyElements.size(); i++) {
                String uri = tbodyElements.get(i).attr("abs:href");
                if (tbodyElements.get(i).attr("abs:href").startsWith("ftp:") && uri != null) {
                    xunleiDownloadLink = uri;
                    Log.i(TAG, "got downloaduri " + tbodyElements.get(i).attr("abs:href"));
                }
            }
        }
    }

    // 返回成员变量 mElement;

    private Element getElement() {
        return photoAndTextElement;
    }
    // 下载图片的方法。

    private MovieItem setPhotoUri(@NonNull MovieItem movieItem) {

            Elements pngs = getElement().getElementsByTag("img");
            if (pngs.size() != 0 ) {
                if (pngs.size()> 1) {
                    // 获取第一个真实的连接。电影海报和电影截图的Uri
                    movieItem.setPhotosUri(patternUrl(pngs.get(0).toString()));
                    movieItem.setPhotoShortUri(patternUrl(pngs.get(1).toString()));
                }else {
                    movieItem.setPhotosUri(patternUrl(pngs.get(0).toString()));
                }
            }
        return movieItem;

    }
    private MovieItem setSearchPhotoUri(@NonNull MovieItem movieItem) {

        Elements pngs = getElement().getElementsByTag("img");
        if (pngs.size() != 0) {
            for (int i= 0; i<pngs.size();i++){
                // 获取第一个真实的连接。电影海报和电影截图的Uri
                String string = pngs.get(i).toString();
                if (string.contains("imgur.com") || string.contains("affenheimtheater.de")){      // 筛选资源
                    if (string.contains("http")){
                        String[] strings = string.split("http");
                        String url = "http" + strings[1].split(".jpg")[0] + ".jpg";
                        movieItem.setPhotosUri(url);
                    }
                }
            }

        }
        return movieItem;

    }
    // 必须 后于ImageDownLoad；方法调用

    private MovieItem setText (MovieItem movieItem, String text) {
        String[] texts = text.split("◎");

        // 设置其他字符串变量
        for (int i = 0; i< texts.length ; i++) {
            String[] subString;
            String stringNoBackspace;
            String[] subString2;
            if (texts[i].startsWith("导　　演")) {
                subString= texts[i].split(" ");
                movieItem.setDerector(subString[0]);

            } if (texts[i].startsWith("主　　演")) {
                subString= texts[i].split(" ");
                movieItem.setActor(subString[0] +" 等");

            } if (texts[i].startsWith("IMDb评分")) {
                if (texts[i].contains("f")) {
                    int index = texts[i].indexOf("f");
                    String iMDB = texts[i].substring(0, index);
                    movieItem.setPoint(" " + iMDB + "分");
                }

            } if (texts[i].startsWith("简　　介") || texts[i].startsWith("简　 　介")){

                if (texts[i].startsWith("简　 　介")){  // 电影龙猫的修改
                    stringNoBackspace = texts[i].replace("简　 　介","");
                    movieItem.setMovieDetail(stringNoBackspace);  // 有些结果页没有"【"
                }else {
                    stringNoBackspace = texts[i].replace("简　　介", "");
                    if (stringNoBackspace.contains("【")) {
                        subString2 = stringNoBackspace.split("【");
                        movieItem.setMovieDetail(subString2[0]);
                    } else {
                        movieItem.setMovieDetail(stringNoBackspace);  // 有些结果页没有"【"
                    }
                }

            } if (texts[i].startsWith("上映日期")||texts[i].startsWith("年 代") || texts[i].startsWith("年代")){
                if (texts[i].contains("/")){
                    subString2 = texts[i].split("/");
                    if (subString2[0].startsWith("年 代")){
                        movieItem.setDate("上映日期 "+subString2[0].replace("年 代",""));
                    }
                    movieItem.setDate(subString2[0]);
                }else {
                    if (texts[i].startsWith("年代")){
                        movieItem.setDate("年　　代  "+texts[i].replace("年代",""));
                    }
                }

            } if (texts[i].startsWith("片　　长")){
                movieItem.setMovieTime(texts[i]);

            } if (texts[i].startsWith("类　　别")||texts[i].startsWith("类 别")){
                subString= texts[i].split(" ");
                movieItem.setKind(subString[0]);

            } if (texts[i].startsWith("语　　言")||texts[i].startsWith("语 言")){
                subString= texts[i].split(" ");
                movieItem.setLaguage(subString[0]);

            } if (texts[i].startsWith("产　　地") || texts[i].startsWith("国　　家")||texts[i].startsWith("国 家") ){
                movieItem.setCountry(texts[i]);
            }
        }
        return movieItem;
    }
    // 打开连接下载数据的方法,获取照片的下载方法。

    public byte[] downloadPhotoData (String string) throws IOException{
            URL imageUrl = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();

        try {

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Service responsed ERROR");
                return null;
            }
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 如果服务器回复的代码为 http_ok 继续
            // 读取数据流。
            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while ((byteRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, byteRead);
            }
            // 关闭数据流。
            outputStream.close();
            return outputStream.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    private String patternUrl(String string) {
        // 网络连接的变量进行匹配。
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) {
            Log.i(TAG,"Url Not Matched");
        }
        return  matcher.group(0);
    }
    // 获取匹配的uri连接的方法。
}