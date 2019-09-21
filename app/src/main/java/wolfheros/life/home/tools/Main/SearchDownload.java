package wolfheros.life.home.tools.Main;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;

public class SearchDownload {

    private static final String DOWNLOAD_ELEMENTS = "co_content8";
    private static final String TAG ="SearchDownload";
    private static final int SEARCH_CODE=1;
    private static final int OTHERS_CODE = 0;
    private static final int SEARCH_FRAGMENT = 3;
    private Elements urlElements;
    private Elements classElements;
    DownloadFiles mDownloadFiles;
    SQLiteDataBaseHelper sqLiteDataBaseHelper;
    Context mContext;
    String mSearcWord;
    public SearchDownload(Context context){
        mContext = context;
    }
    public SearchDownload(SQLiteDataBaseHelper sqlDB , Context context , String searchWord){
        mContext = context;
        sqLiteDataBaseHelper = sqlDB;
        mSearcWord = searchWord;
    }
    /**
     * 自定义连接的接入口,调用者方法一定要 nullPoint 判断。
     *
     * */
    public void getSearchHomeElements(String uri) {
        try {
            Document document = Jsoup.connect(uri).get();
            classElements = document.getElementsByClass(DOWNLOAD_ELEMENTS);
            if (classElements != null) {
                for (int i = 0; i < classElements.size(); i++) {

                    urlElements = classElements.get(i).getElementsByTag("a");
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG,"Got url" + uri);

        }
    }

    /**
     *
     * 获取每一个选项的连接，和每一个连接的名称。接下来的方法是自动打开了，
     * */
    public List<MovieItem> getEachUrl(int code){
        final List<MovieItem> searchList = new ArrayList<>();
        try {
            for (int i = 0 ; i < urlElements.size(); i++){
                // 获取搜索结果的连接
                String link = urlElements.get(i).attr("abs:href");
                /**
                 * 过滤非电影连接
                 * */
                if (code == SEARCH_FRAGMENT){
                    if ( link.startsWith("http://s.ygdy8.com/html/gndy/") ) {
                        Log.i(TAG,"got Url "+link);
                        // 获取搜索结果的标题
                        String name = urlElements.get(i).text();
                        // 获取哈希值。
                        String uuid = HashValue.hashKeyForDisk(link);
                        // 只是添加电影名称和获取的连接。每次都要添加
                        MovieItem movieItem = new MovieItem(uuid);
                        movieItem.setUri(link);

                        mDownloadFiles = new DownloadFiles();
                        mDownloadFiles.getHomeElements(link);
                        completeMovieItem(name, movieItem);
                        movieItem = mDownloadFiles.searchUrlLink(movieItem, SEARCH_CODE);
                        searchList.add(movieItem);
                    }

                }else if (code == OTHERS_CODE){
                    /**
                     * 排除所有的无用连接
                     * */
                    if (!link.startsWith("http://www.dytt8.net/html/gndy/dyzz/list")
                            && !link.startsWith("http://www.ygdy8.net/html/gndy/jddy/list")
                            && !link.startsWith("http://www.dytt8.net/html/gndy/rihan/list")
                            && !link.startsWith("http://www.ygdy8.net/html/gndy/oumei/list")
                            && !link.startsWith("http://www.ygdy8.net/html/gndy/china/list")
                            && !link.startsWith("https://www.dy2018.com/html/gndy/dyzz/index")
                            && !link.startsWith("https://www.dytt8.net/html/gndy/dyzz/list")
                            && !link.startsWith("https://www.ygdy8.net/html/gndy/china/list")
                            && !link.startsWith("https://www.xiaopian.com/html/gndy/rihan/index")
                            && !link.startsWith("https://www.dytt8.net/html/gndy/rihan/list")
                            && !link.equals("https://www.dytt8.net/html/gndy/dyzz/index.html")
                            && !link.equals("http://www.ygdy8.net/html/gndy/dyzz/index.html")
                            && !link.equals("http://www.ygdy8.net/html/gndy/jddy/index.html")
                            && !link.equals("https://www.ygdy8.net/html/gndy/china/index.html")
                            && !link.equals("https://www.ygdy8.net/html/gndy/dyzz/index.html")
                            && !link.equals("https://www.ygdy8.net/html/gndy/jddy/index.html")
                            && !link.equals("https://www.dytt8.net/html/gndy/dyzz/index.html")
                            && !link.equals("https://www.dytt8.net/html/gndy/jddy/index.html")
                            && !link.equals("https://www.xiaopian.com/html/gndy/jddy/")
                            && !link.equals("https://www.xiaopian.com/html/gndy/dyzz/")){
                        Log.i(TAG,"got Url "+link);
                        // 获取哈希值。
                        String uuid = HashValue.hashKeyForDisk(link);
                        // 只是添加电影名称和获取的连接。每次都要添加
                        MovieItem movieItem = new MovieItem(uuid);
                        movieItem.setUri(link);

                        mDownloadFiles =  new DownloadFiles();
                        mDownloadFiles.getHomeElements(link);
                        movieItem = mDownloadFiles.searchUrlLink(movieItem, OTHERS_CODE);
                        if (sqLiteDataBaseHelper.getMovieItemDB(link) == null) {
                            searchList.add(movieItem);
                        }
                    }
                }
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
        return searchList;
    }

    private void completeMovieItem(String name ,MovieItem movieItem ){
        movieItem.setSearchLongName(name);
        movieItem.setSearchWord(mSearcWord);
        movieItem.setSearchMovie("true");
        setOthersElements(movieItem);
        // 将照片储存到内存中。
        addBitmapTo(movieItem);
    }

    private void addBitmapTo(MovieItem movieItem) {
        if (movieItem.getPhotosUri() != null){
            try {
                byte[] bytes = mDownloadFiles.downloadPhotoData(movieItem.getPhotosUri());
                ImageFileStore.getImageFileStore().addImageBitmap(movieItem,1
                        ,bytes,mContext);
            }catch (IOException e ){
                e.printStackTrace();
            }
        }
    }

    //
    public void setOthersElements(MovieItem movieItem){
        Elements elements = classElements.get(0).select("table");
        for (int i=0; i<elements.size(); i++){
            Elements trElements = elements.get(i).select("tr");
            for (int j=0; j<trElements.size(); j++){
                String string = trElements.get(j).text();
                if (string.equals(movieItem.getSearchLongName())){
                    String movieDetail = trElements.get(j+1).text();
                    setText(movieItem , movieDetail);
                }
            }
        }
    }


    private void setText (MovieItem movieItem, String text) {
        String[] texts = text.split("◎");
        // 设置其他字符串变量
        for (String string : texts) {
           if (string.startsWith("年 代")){
               movieItem.setDate("年　　代  "+string.replace("年 代",""));
            }else if (string.startsWith("类 别")){
               movieItem.setKind("类　　别  "+string.replace("类 别",""));
            }else if (string.startsWith("语 言")){
               movieItem.setLaguage("语　　言  "+string.replace("语 言",""));
            }else if (string.startsWith("国 家")){
               movieItem.setCountry("国　　家  "+string.replace("国 家",""));
            }
        }

        if (movieItem.getDate() == null){
            movieItem.setDate("年　　代  无");
        }else if (movieItem.getKind() == null) {
            movieItem.setKind("类　　别  无");
        }else if (movieItem.getLaguage() == null) {
            movieItem.setLaguage("语　　言  无");
        }else if (movieItem.getCountry() == null) {
            movieItem.setCountry("国　　家  无");
        }
    }


}
