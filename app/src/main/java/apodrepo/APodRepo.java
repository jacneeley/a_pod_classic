package apodrepo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicdemoapp.AudioModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utilities.AlbumArtMap;

public class APodRepo implements IAPodRepo{
    HashMap<String, String> artMap = AlbumArtMap.getArtMap();
    public APodRepo() {}

    @Override
    public void getAllAlbumsArt(Context context) {
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
        };

        try{
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            if(cursor != null){
                while(cursor.moveToNext()){
                    String path = cursor.getString(1);
                    String albumName = cursor.getString(2);
                    String artistName = cursor.getString(3);

                    if(path != null){
                        artMap.put(artistName + "_" + albumName, path);
                    }
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllAlbumsArt: " + e.getMessage());
        }
    }

    public HashMap<String, String> retrieveArt(){
        return this.artMap;
    }

    @Override
    public void getAlbumArtByAlbumId(Context context, String albumId){
        try{
            String[] projection = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM_ART,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST
            };

            String where = MediaStore.Audio.Albums._ID + "=?";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection,
                    where,
                    new String[] { albumId },
                    null
            );

            if(cursor != null){
                while(cursor.moveToNext()){
                    String path = cursor.getString(1);
                    String albumName = cursor.getString(2);
                    String artist = cursor.getString(3);
                    if(path != null){
                        artMap.put(artist + "_" + albumName, path);
                    }
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAlbumArtByAlbumId: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<AudioModel> getAllMusic(Context context) {
        ArrayList<AudioModel> songsList = new ArrayList<>();
        try{
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID
            };

            //access tracks
            String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.TITLE + " ASC");

            while(cursor.moveToNext()){
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String duration = cursor.getString(4);
                String albumId = cursor.getString(5);

                if (new File(path).exists()) {
                    songsList.add(new AudioModel(path, title, artist, album, albumId, duration));
                }
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllMusic: " + e.getMessage());
        }

        return songsList;
    }

    @Override
    public ArrayList<ArrayList<String>> getAllAlbums(Context context) {
        ArrayList<ArrayList<String>> albumsList = new ArrayList<>();
        try{
            String[] projection = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST
            };

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Albums.ALBUM + " ASC"
            );

            while(cursor.moveToNext()){
                if(!cursor.getString(0).isEmpty()){
                    String albumName = cursor.getString(1);
                    String artist = cursor.getString(2);
                    ArrayList<String> album = new ArrayList<>(Arrays.asList(artist, albumName));
                    albumsList.add(album);
                }
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllAlbums: " + e.getMessage());
        }

        return albumsList;
    }

    @Override
    public ArrayList<String> getAllArtists(Context context) {
        ArrayList<String> artistList = new ArrayList<>();
        try{
            String[] projection = {
                    MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST
            };

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Artists.ARTIST + " ASC"
            );

            while(cursor.moveToNext()){
                if(!cursor.getString(0).isEmpty()){
                    String artist = cursor.getString(1);
                    artistList.add(artist);
                }
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllArtists: " + e.getMessage());
        }

        return artistList;
    }

    @Override
    public ArrayList<AudioModel> getSongsByAlbumName(Context context, String albumName) {
        ArrayList<AudioModel> songsList = new ArrayList<>();
        try{
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID
            };

            String where = MediaStore.Audio.Media.ALBUM + "=?";
            String[] whereVal = { albumName };
            String orderBy = MediaStore.Audio.Media.TRACK;

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    where,
                    whereVal,
                    orderBy
            );

            while(cursor.moveToNext()){
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String duration = cursor.getString(4);
                String albumId = cursor.getString(5);
                if (new File(path).exists()) {
                    songsList.add(new AudioModel(path, title, artist, album, albumId, duration));
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getSongsByAlbumName: " + e.getMessage());
        }

        return songsList;
    }

    @Override
    public ArrayList<ArrayList<String>> getAlbumsByArtist(Context context, String artistName) {
        ArrayList<String> albumContainer = new ArrayList<>();
        ArrayList<ArrayList<String>> albumsList = new ArrayList<>();
        try{
            String[] projection = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
            };

            String where = MediaStore.Audio.Albums.ARTIST + "=?";
            String[] whereVal = { artistName };
            String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    where,
                    whereVal,
                    sortOrder
            );

            while(cursor.moveToNext()){
                if(!cursor.getString(0).isEmpty()){
                    String albumId = cursor.getString(0);
                    String albumName = cursor.getString(1);
                    ArrayList<String> album = new ArrayList<>(Arrays.asList(artistName, albumName));
                    if(!albumContainer.contains(albumName)){
                        albumContainer.add(albumName);
                        albumsList.add(album);
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAlbumsByArtist: " + e.getMessage());
        }
        return albumsList;
    }

    @Override
    public ArrayList<AudioModel> getSongsByArtistName(Context context , String artistName){
        ArrayList<AudioModel> songsList = new ArrayList<AudioModel>();
        try{
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID
            };

            String where = MediaStore.Audio.Media.ARTIST + "=?";
            String[] whereVal = { artistName };
            String orderBy = MediaStore.Audio.Media.ALBUM + " ASC";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    where,
                    whereVal,
                    orderBy
            );

            while(cursor.moveToNext()){
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String duration = cursor.getString(4);
                String albumId = cursor.getString(5);
                if (new File(path).exists()) {
                    songsList.add(new AudioModel(path, title, artist, album, albumId, duration));
                }
            }
            cursor.close();
        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getSongsByAlbumName: " + e.getMessage());
        }

        return songsList;
    }

    private void queryBuilder(String listType, ArrayList<?> arrayList, String[] projection, String whereCondition,
                              String[] whereVal, String orderBy) {
        //TODO: make a builder for cursor to reduce code in the repo class.
    }


}
