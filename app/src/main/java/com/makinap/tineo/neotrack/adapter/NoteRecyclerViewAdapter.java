package com.makinap.tineo.neotrack.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makinap.tineo.neotrack.R;
import com.makinap.tineo.neotrack.model.Note;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by tineo on 24/10/16.
 */

public class NoteRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
        Note, NoteRecyclerViewAdapter.ViewHolder> {

    public NoteRecyclerViewAdapter(
            Context context,
            RealmResults<Note> realmResults) {
        super(context, realmResults, true, true);
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
        final Note note = realmResults.get(position);
        viewHolder.mText.setText(note.getText());
        viewHolder.mDate.setText(note.getDate().toString());
    }
}