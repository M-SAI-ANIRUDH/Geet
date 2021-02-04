package com.msa.windstorm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.msa.windstorm.MainActivity.musicFiles;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;

    private ArrayList<MusicFiles> locoFiles = new ArrayList<>();
    private ArrayList<MusicFiles> locoFilesso;
    private ArrayList<MusicFiles> locoFilesu;
    private String[] files = new String[musicFiles.size()];
    private String[] locomos = new String[musicFiles.size()];
    private String loco;
    private int flag;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if (!(musicFiles.size() < 1)){
            locoFilesso = musicFiles;

            for (int i = 0 ; i < ( musicFiles.size() - 1 ) ; i++ ){
                flag = 0;
                for (int j = 0 ; j < locoFiles.size() ; j++){
                    if (musicFiles.get(i).getAlbum().equals(locoFiles.get(j).getAlbum())){
                        flag = 1;
                    }
                }
                if (flag == 0){
                    locoFiles.add(musicFiles.get(i));
                }
            }


            albumAdapter = new AlbumAdapter(getContext(), locoFiles);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }
}