package apodrepo;

import android.content.Context;

import com.example.musicdemoapp.AudioModel;

import java.util.ArrayList;

public interface IAPodRepo {
    void getAllAlbumsArt(Context context);
    void getAlbumArtByAlbumId(Context context, String albumId);
    ArrayList<AudioModel> getAllMusic(Context context);
    ArrayList<ArrayList<String>> getAllAlbums(Context context);
    ArrayList<String> getAllArtists(Context context);

    ArrayList<AudioModel> getSongsByAlbumName(Context context, String albumName);
    ArrayList<ArrayList<String>> getAlbumsByArtist(Context context, String artistName);
    ArrayList<AudioModel> getSongsByArtistName(Context context , String artistName);
}
