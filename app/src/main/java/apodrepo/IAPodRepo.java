package apodrepo;

import android.content.Context;

import com.example.musicdemoapp.AudioModel;

import java.util.ArrayList;

public interface IAPodRepo {
    void getAllAlbumsArt(Context context) throws NullPointerException;
    void getAlbumArtByAlbumId(Context context, String albumId) throws NullPointerException;
    ArrayList<AudioModel> getAllMusic(Context context) throws NullPointerException;
    ArrayList<ArrayList<String>> getAllAlbums(Context context) throws NullPointerException;
    ArrayList<String> getAllArtists(Context context) throws NullPointerException;

    ArrayList<AudioModel> getSongsByAlbumName(Context context, String albumName) throws NullPointerException;
    ArrayList<ArrayList<String>> getAlbumsByArtist(Context context, String artistName) throws NullPointerException;
    ArrayList<AudioModel> getSongsByArtistName(Context context , String artistName) throws NullPointerException;
}
