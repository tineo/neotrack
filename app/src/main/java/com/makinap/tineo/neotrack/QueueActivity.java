package com.makinap.tineo.neotrack;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.makinap.tineo.neotrack.adapter.NoteRecyclerViewAdapter;
import com.makinap.tineo.neotrack.adapter.TrackRecyclerViewAdapter;
import com.makinap.tineo.neotrack.model.Note;
import com.makinap.tineo.neotrack.model.Track;
import com.makinap.tineo.neotrack.services.QueueService;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class QueueActivity extends AppCompatActivity {

    private ProgressReceiver receiver;
    private IntentFilter filter;
    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Realm mRealm;
    private RealmConfiguration mRealmConfig;
    private EditText mText;
    private RealmRecyclerView mNotes;

    private FloatingActionButton fab;
    private ProgressBar pbarProgreso;

    private Intent msgIntent;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        filter = new IntentFilter(ProgressReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ProgressReceiver();
        registerReceiver(receiver, filter);

        msgIntent = new Intent(this, QueueService.class);
        msgIntent.putExtra(QueueService.ACTION_PROGRESO, "mensaje");
        startService(msgIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //pbarProgreso = (ProgressBar)findViewById(R.id.pbarProgreso);

        mRealmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded() // FIXME: 24/10/16
                .build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();
        //mText = (EditText) findViewById(R.id.et_text);
        mNotes = (RealmRecyclerView) findViewById(R.id.rv_notes);
        //mNotes.getRecycleView().addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(mText.getText().length() > 0){
                    insertNote(mText.getText().toString());
                    mText.setText("");
                */
                    /*Snackbar.make(view, "Note added!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                /*}*/

                //Toast.makeText(getApplicationContext(), " Go queue", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //finish();

            }
        });

        loadTracks();



    }

    private void loadNotes() {
        RealmResults<Note> notes = mRealm.where(Note.class).findAllSorted("date", Sort.ASCENDING);
        NoteRecyclerViewAdapter noteAdapter = new NoteRecyclerViewAdapter(getBaseContext(), notes);
        mNotes.setAdapter(noteAdapter);
    }

    private void loadTracks() {
        RealmResults<Track> notes = mRealm.where(Track.class).findAllSorted("dtime", Sort.ASCENDING);
        TrackRecyclerViewAdapter noteAdapter = new TrackRecyclerViewAdapter(getBaseContext(), notes);

        mNotes.setAdapter(noteAdapter);
    }

    private void insertNote(final String noteText) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Note note = new Note();
                note.setText(noteText);
                mRealm.copyToRealm(note);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        //stopService(msgIntent);
        notificationManager.cancelAll();

        mRealm.close();
        super.onDestroy();
         // Remember to close Realm when done.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_queue, menu);
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(this, " Enviando lecturas ... " , Toast.LENGTH_LONG).show();
            RealmResults<Track> notes = mRealm.where(Track.class).findAllSorted("dtime", Sort.ASCENDING);

            /*notes.get(0).getId();
            mRealm.beginTransaction();
            notes.deleteFromRealm(0);
            mRealm.commitTransaction();
            */

            Log.e("onOptionsItemSelected","start");
            /*IntentFilter filter = new IntentFilter();
            filter.addAction(QueueService.ACTION_PROGRESO);
            filter.addAction(QueueService.ACTION_FIN);
            ProgressReceiver rcv = new ProgressReceiver();
            registerReceiver(rcv, filter);*/


            if(isNetworkAvailable()){
                startService(msgIntent);
            }else{

                Snackbar.make(fab, "Sin conexion", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            //msgIntent.putExtra(QueueService.G, strInputMsg);

            Log.e("onOptionsItemSelected","finish");

            //pbarProgreso.setProgress(55);

            /*mNotes.getRecycleView().getChildAt(0)
                    .setBackgroundColor(getResources()
                            .getColor(R.color.common_action_bar_splitter));
            */



            //TrackRecyclerViewAdapter noteAdapter = new TrackRecyclerViewAdapter(getBaseContext(), notes);
            //mNotes.setAdapter(noteAdapter);
            return true;
        }

        if(id == R.id.action_about){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.about_message);
            builder.setIcon(R.drawable.barcode_tiny_icon);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        Log.e("onResume","onResume");
        startService(msgIntent);
        super.onResume();

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class ProgressReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "com.makinap.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("ProgressReceiver","onReceive");
            mNotes = (RealmRecyclerView) findViewById(R.id.rv_notes);
            ProgressBar pb = (ProgressBar) mNotes.getRecycleView().getChildAt(0).findViewById(R.id.pb);
            pb.setIndeterminate(true);
            pb.setVisibility(View.VISIBLE);
            mNotes.getRecycleView()
                    .getChildAt(0)
                    .findViewById(R.id.holder_container)
                    .setClickable(false);
            String text = intent.getStringExtra(QueueService.ACTION_FIN);
            Log.e("getStringExtra",text);

        }
    }
}


