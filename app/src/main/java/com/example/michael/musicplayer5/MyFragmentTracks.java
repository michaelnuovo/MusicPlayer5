package com.example.michael.musicplayer5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MyFragmentTracks extends Fragment {

    /** Fragment Variables **/
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static ListView listView;
    static ArrayList<SongObject> songList;

    static TextView currentTitleView;
    static TextView currentArtistView;

    LinearLayout TitlePanel;

    /** Static Factory Method for Fragment Instantiation **/
    public static final MyFragmentTracks newInstance(ArrayList<SongObject> arrayList)
    {
        MyFragmentTracks f = new MyFragmentTracks();
        Bundle bdl = new Bundle(1);
        bdl.putParcelableArrayList(EXTRA_MESSAGE, arrayList);
        f.setArguments(bdl);
        return f;
    }

    /** On Create Method**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        songList = getArguments().getParcelableArrayList(EXTRA_MESSAGE);

        View rootView = inflater.inflate(R.layout.my_fragment_layout_tracks, container, false);

        listView = (ListView) rootView.findViewById(R.id.fragmentListView);

        MyListAdapterTracks adapter = new MyListAdapterTracks(getActivity(), R.layout.list_view_item, songList);
        listView.setAdapter(adapter);

        Log.v("TAG", "Tracks Fragment");

        /** Change Y direction of Drop Shadow On Play Controls Strip
        float elevation = 200;
        float density = 0.1f;
        LinearLayout shadowEdge = (LinearLayout) rootView.findViewById(R.id.playControls);
        shadowEdge.setBackgroundDrawable(new RoundRectDrawableWithShadow(
                getResources(), Color.BLACK, 0,
                elevation * density, ((elevation + 1) * density) + 1
        ));**/

        /** View initializations **/

        /*******

         STATIC MEDIA PLAYER CLASS LISTENERS

         1) StaticMediaPlayer.setSongCompletionListener()
         2) StaticMediaPlayer.setLoopButtonListener();
         3) StaticMediaPlayer.setShuffleButtonListener();
         4) StaticMediaPlayer.setPlayButtonListener();
         5) StaticMediaPlayer.setSkipForwardsListener();
         6) StaticMediaPlayer.setSkipBackwardsListener();

         ********/

        //Pass play button to static media player
        StaticMediaPlayer.SetButtonsMainActivity(
                (ToggleButton) rootView.findViewById(R.id.playButton),
                songList
        );

        //Set play button listener
        StaticMediaPlayer.setPlayButtonListener();

        //Set play panel title and artist
        currentTitleView = (TextView) rootView.findViewById(R.id.currentTitle);
        currentArtistView = (TextView) rootView.findViewById(R.id.currentArtist);

        //currentTitleView.setText(songObject.title);
        //currentArtistView.setText(songObject.artist);

        TitlePanel = (LinearLayout) rootView.findViewById(R.id.activity_main_track_info);
        TitlePanelClickListener();

        Listeners();

        return rootView;
    }

    public void TitlePanelClickListener(){



        /** Title Listener **/
        TitlePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the play panel activity from this fragment

                Intent intent = new Intent(getActivity(), PlayPanelActivity.class);
                startActivity(intent);

                /** original code from main activity

                // Open the play panel

                Intent intent = new Intent(MainActivity.this, PlayPanelActivity.class);
                //intent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.dont_move);

                 **/

            }
        });
    }

    private void Listeners(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // Open the play panel activity from this fragment

                Intent intent = new Intent(getActivity(), PlayPanelActivity.class);
                startActivity(intent);

                // arg2 is the position of the view which corresponds to a list array index
                // .get() method on this index will return a song object

                if (StaticMediaPlayer.playButton.isChecked() == false) { // If set to play

                    StaticMediaPlayer.playButton.setChecked(true); // set to pause
                }

                SongObject songObject = songList.get(arg2);
                StaticMediaPlayer.TryToPlaySong(songObject);
                if (StaticMediaPlayer.noSongHasBeenPlayedYet == true) {
                    StaticMediaPlayer.noSongHasBeenPlayedYet = false;
                }

                // If list view items is clicked after user has clicked back tracked a number of times
                // than that many items should be deleted from the play history list
                // since a new play list will be branched from that point

                if (StaticMediaPlayer.clickedSkipBackWardsButtonHowManyTimes > 0) {

                    int i;
                    int j = 0;

                    for (i = StaticMediaPlayer.clickedSkipBackWardsButtonHowManyTimes; i > 0; i--) {

                        StaticMediaPlayer.playHistory.remove(StaticMediaPlayer.playHistory.size() - 1); // Remove last element in the play history
                        j += 1;
                    }

                    StaticMediaPlayer.clickedSkipBackWardsButtonHowManyTimes = StaticMediaPlayer.clickedSkipBackWardsButtonHowManyTimes - j;
                }

                if (StaticMediaPlayer.playHistory.size() >= 1) {
                    // Add the clicked song to the play list
                    if (songList.get(arg2) != songList.get(StaticMediaPlayer.playHistory.get(StaticMediaPlayer.playHistory.size() - 1))) { // <---
                        StaticMediaPlayer.playHistory.add(arg2);

                    } else {
                        // do nothing
                    }
                } else {

                    StaticMediaPlayer.playHistory.add(arg2);
                }
            }
        });
    }
}