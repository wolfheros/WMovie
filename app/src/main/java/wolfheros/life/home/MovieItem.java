package wolfheros.life.home;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class MovieItem implements Serializable,Comparable<MovieItem>{
    private String mName;
    private String mUUID;
    private String mDerector;
    private String mActor;
    private String mPoint;
    private String mMovieDetail;
    private String mUri;
    private String mPhotosUri;
    private String mPhotoShortUri;
    private String mMovieDownloadUri;
    private String mImageFileName;
    private String mDate;
    private String mMovieTime;
    private String mLaguage;
    private String mCountry;
    private String mKind;
    private String myFavoMovie;
    private String reDownload;
    private int storeTime;
    private String searchMovie;
    private String searchLongName;
    private String searchWord;

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getSearchLongName() {
        return searchLongName;
    }

    public void setSearchLongName(String searchLongName) {
        this.searchLongName = searchLongName;
    }

    // 设置是否是搜索的电影
    public String getSearchMovie() {
        return searchMovie;
    }

    public void setSearchMovie(String searchMovie) {
        this.searchMovie = searchMovie;
    }

    // 设置储存时间
    public int getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(int storeTime) {
        this.storeTime = storeTime;
    }
    // 设置下载记录
    public String  isReDownload() {
        return reDownload;
    }

    public void setReDownload(String reDownload) {
        this.reDownload = reDownload;
    }
    // 收藏
    public String isMyFavoMovie() {
        return myFavoMovie;
    }

    public void setMyFavoMovie(String myFavoMovie) {
        this.myFavoMovie = myFavoMovie;
    }
    // 迅雷下载地址
    public String getMovieDownloadUri() {
        return mMovieDownloadUri;
    }

    public void setMovieDownloadUri(String movieDownloadUri) {
        mMovieDownloadUri = movieDownloadUri;
    }

    // 电影截图
    public String getPhotoShortUri() {
        return mPhotoShortUri;
    }

    public void setPhotoShortUri(String  photoShortUri) {
        mPhotoShortUri = photoShortUri;
    }

    // 语言
    public String getLaguage() {
        return mLaguage;
    }
    public void setLaguage(String laguage) {
        mLaguage = laguage;
    }
    // 国家
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }
    // 类型
    public String getKind() {
        return mKind;
    }

    public void setKind(String kind) {
        mKind = kind;
    }

    // 片长
    public String getMovieTime() {
        return mMovieTime;
    }

    public void setMovieTime(String movieTime) {
        mMovieTime = movieTime;
    }

    // 上映日期
    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getImageFileName() {
        return mImageFileName;
    }

    // 用String 代替UUID.
    public MovieItem(String string){
        mUUID = string;
        mImageFileName = mUUID;
    }

    // 图片连接地址
    public String getPhotosUri() {
        return mPhotosUri;
    }

    public void setPhotosUri(String photosUri) {
        mPhotosUri = photosUri;
    }

    // 网页详细页地址
    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    // 详细页时候
    public String getMovieDetail() {
        return mMovieDetail;
    }

    public void setMovieDetail(String movieDetail) {
        mMovieDetail = movieDetail;
    }

    // 导演
    public String getDerector() {
        return mDerector;
    }

    public void setDerector(String derector) {
        mDerector = derector;
    }

    //演员
    public String getActor() {
        return mActor;
    }

    public void setActor(String actor) {
        mActor = actor;
    }

    // 分数
    public String getPoint() {
        return mPoint;
    }

    public void setPoint(String point) {
        mPoint = point;
    }

    // 标识符UUID
    public String getUUID() {
        return mUUID;
    }
    public void setUUID(String uuid) {
        mUUID = uuid;
    }

    // 电影名字
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int compareTo(@NonNull MovieItem o) {
        return -(this.getStoreTime() - o.getStoreTime());
    }
}
