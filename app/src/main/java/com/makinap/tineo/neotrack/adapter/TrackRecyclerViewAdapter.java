package com.makinap.tineo.neotrack.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makinap.tineo.neotrack.R;
import com.makinap.tineo.neotrack.model.Track;

import java.text.SimpleDateFormat;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by tineo on 24/10/16.
 */

public class TrackRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
        Track, TrackRecyclerViewAdapter.ViewHolder> {

    TextView textView;

    public TrackRecyclerViewAdapter( Context context,  RealmResults<Track> realmResults) {

        //
        super(context, realmResults, true, true);
        View view = View.inflate(context, R.layout.content_queue, null);
        textView = (TextView) view.findViewById(R.id.textView);
    }



    public class ViewHolder extends RealmViewHolder {
        private TextView mText;
        private TextView mDate;

        public ViewHolder(RelativeLayout container) {
            super(container);
            this.mText = (TextView) container.findViewById(R.id.tv_text);
            this.mDate = (TextView) container.findViewById(R.id.tv_date);
        }
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.track_item, viewGroup, false);
        return new ViewHolder((RelativeLayout) v);
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Track note = realmResults.get(position);
        viewHolder.mText.setText(note.getCode().toString());
        String newstring = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(note.getDtime());
        viewHolder.mDate.setText(newstring);
    }

    @Override
    public int getItemCount() {
        /*if (realmResults.size() == 0) {

            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }*/
        return realmResults.size();
    }
}