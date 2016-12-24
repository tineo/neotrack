package com.makinap.tineo.neotrack;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makinap.tineo.neotrack.adapter.TrackRecyclerViewAdapter;
import com.makinap.tineo.neotrack.data.DataDef;
import com.makinap.tineo.neotrack.data.Tienda;
import com.makinap.tineo.neotrack.data.TrackingData;
import com.makinap.tineo.neotrack.model.Photo;
import com.makinap.tineo.neotrack.model.Track;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Realm mRealm;
    private RealmConfiguration mRealmConfig;
    private EditText mText;
    private RealmRecyclerView mNotes;


    private AutoCompleteTextView acTienda;

    private ImageView ivImage;
    private ImageView ivImage2;
    private ImageView ivImage3;
    private ImageView ivImage4;

    private String ivImage_full;
    private String ivImage2_full;
    private String ivImage3_full;
    private String ivImage4_full;


    private ImageButton imageButton;

    private ScaleAnimation scaleAnimation;

    private static final int CONTENT_REQUEST = 1337;
    private File output = null;
    private File output1 = null;

    //GPS
    private TrackGPS gps;
    private FallbackLocationTracker locationTracker;
    double longitude;
    double latitude;


    private static final int TAKE_PICTURE = 651;
    private Uri imageUri;

    int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 15623;
    private static final int IMAGE_REQUEST_CODE = 1;

    static Uri capturedImageUri = null;
    static Uri ivUri1 = null;
    static Uri ivUri2 = null;
    static Uri ivUri3 = null;
    static Uri ivUri4 = null;

    private TrackingData dbhelper;
    private SimpleCursorAdapter mAdapter;


    private final int CAMERA_RESULT = 1;

    private final String Tag = getClass().getName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private int codeSeleted = 0;
    private ImageButton editTienda;

    private String m_Text = "";

    private static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();
        //mText = (EditText) findViewById(R.id.et_text);
        //mNotes = (RealmRecyclerView) findViewById(R.id.rv_notes);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbhelper = TrackingData.getInstancia(getApplicationContext());

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        editTienda = (ImageButton) findViewById(R.id.ib_tienda);

        acTienda = (AutoCompleteTextView)
                findViewById(R.id.ac_tienda);
        /*acTienda.setAdapter(adapter);
        acTienda.setThreshold(1);*/
        //initializeDescription();
        //acTienda.setCompletionHint("meow");
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] { DataDef.Tienda.KEY_TIENDA_NAME },
                new int[] {android.R.id.text1},
                0);
        acTienda.setAdapter(mAdapter);

        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return dbhelper.getCursor(str);
            } });

        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(DataDef.Tienda.KEY_TIENDA_NAME);

                return cur.getString(index);
            }});


        acTienda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

                int codeIndex = cursor.getColumnIndexOrThrow(DataDef.Tienda.KEY_TIENDA_CODE);
                int nameIndex = cursor.getColumnIndexOrThrow(DataDef.Tienda.KEY_TIENDA_NAME);
                int stateIndex = cursor.getColumnIndexOrThrow(DataDef.Tienda.KEY_TIENDA_STATE);
                int code = cursor.getInt(codeIndex);
                String name = cursor.getString(nameIndex);
                int state = cursor.getInt(stateIndex);

                Tienda tienda =  new Tienda();
                tienda.setIdTienda(code);
                tienda.setName(name);
                tienda.setState(state);
                codeSeleted = tienda.getIdTienda();

                //Toast.makeText(getApplicationContext(), "item selected: "+ tienda.getIdTienda(), Toast.LENGTH_LONG).show();

                acTienda.setEnabled(false);
                editTienda.setVisibility(View.VISIBLE);

            }
        });

        editTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acTienda.setEnabled(true);
                codeSeleted = 0;
                editTienda.setVisibility(View.GONE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // 権限がない場合はリクエスト
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                checkNumber();
            }
        }else{
            checkNumber();
        }


        scaleAnimation = new ScaleAnimation(1, 2, 1, 2);
        scaleAnimation.setDuration(1502);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage2 = (ImageView) findViewById(R.id.ivImage2);
        ivImage3 = (ImageView) findViewById(R.id.ivImage3);
        ivImage4 = (ImageView) findViewById(R.id.ivImage4);

        setVisibilityImageView(ivImage);
        setVisibilityImageView(ivImage2);
        setVisibilityImageView(ivImage3);
        setVisibilityImageView(ivImage4);

        imageButton = (ImageButton) findViewById(R.id.imageButton2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(ivUri1, "image/*");
                startActivity(intent);
            }
        });
        ivImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(ivUri2, "image/*");
                startActivity(intent);
            }
        });

        ivImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(ivUri3, "image/*");
                startActivity(intent);
            }
        });

        ivImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(ivUri4, "image/*");
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText mCode = (TextInputEditText) findViewById(R.id.et_code);
                String codigo = mCode.getText().toString();

                if(codeSeleted == 0) {
                    Toast.makeText(getApplicationContext(), " Tienda sin seleccionar ", Toast.LENGTH_SHORT).show();
                }else if(codigo.equals("")){
                    Toast.makeText(getApplicationContext(), " Codigo vacio ", Toast.LENGTH_SHORT).show();
                }else if(ivImage.getVisibility() == View.GONE){
                    Toast.makeText(getApplicationContext(), " Adjunta al menos una imagen ", Toast.LENGTH_SHORT).show();
                }else{
                    insertTrack();
                    finish();
                }

            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (MY_PERMISSIONS_REQUEST_READ_PHONE_STATE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可された
                //showLineNumber();
                checkNumber();
            } else {
                // 拒否された
                //Toast.makeText(this, "拒否された", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void checkNumber() {

        //Number
        /*final TelephonyManager t = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        m_Text = t.getLine1Number();
        RealmResults<Configuration> config = mRealm.where(Configuration.class).equalTo("tag", "numero").findAll();
        Log.e("m_Text", " ->"+m_Text);
        Log.e("config.size()", " ->"+config.size());
        if (config.size()>0) Log.e(".get(0).getValue()", " ->"+config.get(0).getValue());
        if(config.size()<1) {
            Log.e("m_Text", " ->"+m_Text);
            if (validatePhoneNumber(m_Text)) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Configuration config = new Configuration();
                        config.setTag("numero");
                        config.setValue(m_Text);

                        realm.copyToRealm(config);
                    }
                });
            }else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ingresa tu numero");

                final EditText input = new EditText(this);

                int maxLength = 9;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);


                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                input.setFilters(fArray);
                builder.setView(input);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(input.getText().toString().equals("") ){
                            Toast.makeText(getApplicationContext(), " Numero vacio ", Toast.LENGTH_LONG).show();
                            checkNumber();
                            return;
                        }else if(input.getText().toString().length() < 9){
                            Toast.makeText(getApplicationContext(), " Numero de 9 digitos ", Toast.LENGTH_LONG).show();

                            checkNumber();
                            return;
                        }
                        m_Text = input.getText().toString();
                        m_Text = "***" + m_Text;


                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Configuration config = new Configuration();
                                config.setTag("numero");
                                config.setValue(m_Text);

                                realm.copyToRealm(config);
                            }
                        });
                    }
                });

                builder.setCancelable(false);

                builder.show();


            }

        }else{
            m_Text = config.get(0).getValue();
        }*/
    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed","true");
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon_alert)
                .setTitle(R.string.info)
                .setMessage(R.string.exit_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }else */if (id == R.id.action_update_tiendas){
            Toast.makeText(getApplicationContext(), "Obteniendo lista tiendas ... ", Toast.LENGTH_LONG).show();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://lg.neoprojects.com.pe/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();



            TrackService service = retrofit.create(TrackService.class);

            Call<List<Tienda>> tiendaCall = service.listTiendas( "0.0.1");



            tiendaCall.enqueue(new Callback<List<Tienda>>() {
                @Override
                public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                    ArrayList<Tienda> listado = (ArrayList<Tienda>) response.body();
                    dbhelper.truncateTienda();
                    int nt =0;
                    for (Tienda tienda : listado) {
                        Log.d("Tienda ins: ", tienda.getIdTienda() +" *** "+ tienda.getName());
                        nt++;


                        dbhelper.insertTienda(tienda);
                    }
                    //Toast.makeText(getApplicationContext(), "Tiendas insertadas: "+nt, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Tiendas encontradas: "+dbhelper.getTiendas().getCount(), Toast.LENGTH_LONG).show();

                    ;
                }

                @Override
                public void onFailure(Call<List<Tienda>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "No se pudo obtener tiendas ", Toast.LENGTH_LONG).show();

                }
            });



            return true;
        }/*else if (id == R.id.action_check_config){

            Config config = dbhelper.getConfigbyTag("tiendas_version");
            if(config != null){
                //Toast.makeText(getApplicationContext(), "tiendas_version: "+config.getValue(), Toast.LENGTH_LONG).show();

            }else{
                //Toast.makeText(getApplicationContext(), "No se pudo config tiendas ", Toast.LENGTH_LONG).show();
                Config c = new Config();
                c.setTag("tiendas_version");
                c.setValue("1");
                dbhelper.insertConfig(c);
            }
        }*/else if(id == R.id.action_about){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.about_message);
            builder.setIcon(R.drawable.barcode_tiny_icon);
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else if (id == R.id.action_go_list){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onUserLeaveHint(){
        Log.e("onUserLeaveHint","true");
        new android.app.AlertDialog.Builder(this).setMessage(getString(R.string.exit_msg))
                .setTitle(getString(R.string.info))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }*/



    public void scanCode(View view) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Escaneando el codigo...");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        //integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        integrator.initiateScan();

    }

    private void cameraIntent() {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
        List<ResolveInfo> resolvedIntentActivities = getBaseContext().
                getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            //Toast.makeText(getApplicationContext(), "pkg: " + packageName, Toast.LENGTH_LONG).show();
            getBaseContext().grantUriPermission(packageName, Uri.parse("content://com.makinap.tineo/"), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        File path = new File(getBaseContext().getFilesDir(), ".");
        if (!path.exists()) path.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "IMG_" + timeStamp + ".jpg";
        File image = new File(path, filename);




        capturedImageUri = FileProvider.getUriForFile(MainActivity.this,
                "com.makinap.tineo" ,
                image);




        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(intent, CAMERA_RESULT);
    }


    public void setVisibilityImageView(ImageView ivImage) {
        if (ivImage.getDrawable() == null) {
            ivImage.setVisibility(View.GONE);
        } else {
            ivImage.setVisibility(View.VISIBLE);
        }
    }

    /*private void uploadFile(Uri fileUri) {
        // create upload service client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://neotrack.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TrackService service = retrofit.create(TrackService.class);


        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        //File file = FileUtils.getFile(this, fileUri);
        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Log.i(Tag, "Receive the camera result");
        //Toast.makeText(getBaseContext(),"q01: "+(resultCode == RESULT_OK), Toast.LENGTH_LONG).show();
        //Toast.makeText(getBaseContext(),"q02: "+(requestCode == CAMERA_RESULT), Toast.LENGTH_LONG).show();

        if (
                resultCode == RESULT_OK &&
                        requestCode == CAMERA_RESULT) {

           /* Compress */

            try {

                File path = new File(getBaseContext().getFilesDir(), ".");
                if (!path.exists()) path.mkdirs();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = "IMG_" + timeStamp + ".jpg";
                File fileimage = new File(path, filename);
                FileOutputStream fOut = new FileOutputStream(fileimage);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), capturedImageUri);


                //Toast.makeText(getBaseContext(),"width: "+bitmap.getWidth(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(),"kb 1: "+(bitmap.getByteCount()/1024), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(),"mb 1: "+(bitmap.getByteCount()/1024/1024), Toast.LENGTH_LONG).show();


                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);

                fOut.flush();
                fOut.close();

                //Toast.makeText(getBaseContext(),"kb 2: "+(bitmap.getByteCount()/1024), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(),"mb 2: "+(bitmap.getByteCount()/1024/1024), Toast.LENGTH_LONG).show();
                //String path3 = MediaStore.Images.Media.insertImage(this.getContentResolver(), decoded, "Titulo", null);

                File _fileimage = new File(path, "_"+filename);
                InputStream inputStream = getContentResolver().openInputStream(capturedImageUri);
                OutputStream outputStream = new FileOutputStream(_fileimage);
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                //Log.e("getPathfromUri()", ImageUtil.getPath(this,capturedImageUri));
                //Log.e("fi.getAbsolutePath()", fileimage.getAbsolutePath());

                copyExif(_fileimage.getAbsolutePath(),fileimage.getAbsolutePath());

                capturedImageUri = FileProvider.getUriForFile(MainActivity.this,
                        "com.makinap.tineo" ,
                        fileimage);

            } catch (IOException e) {
                System.out.println(capturedImageUri.toString());
                e.printStackTrace();
            }

        /* Compress */

            assignImageGlide(capturedImageUri);

            //Glide.with(getApplicationContext()).load(capturedImageUri).into(ivImage);

        } else {

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("MainActivity", "Escaneo cancelado");
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();

                    TextInputEditText mCode = (TextInputEditText) findViewById(R.id.et_code);
                    Random r = new Random();
                    int Low = 1000;
                    int High = 100000;
                    int Result = r.nextInt(High-Low) + Low;
                    mCode.setText(String.valueOf(Result));

                } else {
                    TextInputEditText mCode = (TextInputEditText) findViewById(R.id.et_code);
                    String scode = result.getContents();
                    //Log.d("VALUE CODE", scode);


                    //if(scode.isEmpty()) scode = "1233515";
                    if (scode.matches("\\d+")) {

                        Long code = Long.parseLong(scode);
                        mCode.setText(code.toString());

                        Log.d("MainActivity", "Scanned");
                        Toast.makeText(this, "Codigo obtenido: " + result.getContents(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "'" + result.getContents() + "' no es un código numérico", Toast.LENGTH_LONG).show();
                    }
                    //code += 123;


                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }


    }

    private void assignImage(Bitmap mBitmap, String fullname) {
        if (ivImage.getDrawable() == null) {
            ivImage_full = fullname;
            //Toast.makeText(this, " ivImage_full: " + ivImage_full, Toast.LENGTH_SHORT).show();
            //Glide.with(this).load().into(ivImage);
            //Toast.makeText(this, " ivImage_full: " + ivImage_full, Toast.LENGTH_SHORT).show();
            //ivImage.setImageBitmap(mBitmap);
            setVisibilityImageView(ivImage);
        } else if (ivImage2.getDrawable() == null) {
            ivImage2_full = fullname;
            ivImage2.setImageBitmap(mBitmap);
            setVisibilityImageView(ivImage2);
        } else if (ivImage3.getDrawable() == null) {
            ivImage3_full = fullname;
            ivImage3.setImageBitmap(mBitmap);
            setVisibilityImageView(ivImage3);
        } else if (ivImage4.getDrawable() == null) {
            ivImage4_full = fullname;
            ivImage4.setImageBitmap(mBitmap);
            setVisibilityImageView(ivImage4);
            imageButton.setVisibility(View.GONE);
        } else {
            imageButton.setVisibility(View.GONE);
        }
    }

    private void assignImageGlide(Uri res) {
        if (ivImage.getDrawable() == null) {
            //Toast.makeText(this, " ivImage_full: " + res.toString(), Toast.LENGTH_SHORT).show();
            Glide.with(this).load(res).into(ivImage);
            ivImage.setVisibility(View.VISIBLE);
            ivUri1 = res;
            ivImage_full = res.toString();
        } else if (ivImage2.getDrawable() == null) {
            Glide.with(this).load(res).into(ivImage2);
            ivImage2.setVisibility(View.VISIBLE);
            ivUri2 = res;
            ivImage2_full = res.toString();
        } else if (ivImage3.getDrawable() == null) {
            Glide.with(this).load(res).into(ivImage3);
            ivImage3.setVisibility(View.VISIBLE);
            ivUri3 = res;
            ivImage3_full = res.toString();
        } else if (ivImage4.getDrawable() == null) {
            Glide.with(this).load(res).into(ivImage4);
            ivImage4.setVisibility(View.VISIBLE);
            ivUri4 = res;
            ivImage4_full = res.toString();
            imageButton.setVisibility(View.GONE);
        } else {
            imageButton.setVisibility(View.GONE);
        }
    }

    @Override

    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();

        //imageView1 = null;

    }

    private File exportFile(File src, File dst) throws IOException {

        //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File expFile = new File(dst.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }

    public File resizeBitmap(File file) throws IOException {
        double MAX_WIDTH = 1280, scale;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);


        scale = MAX_WIDTH / bitmap.getWidth();
        Log.e(" **** Escala ", " Scala: " + MAX_WIDTH);
        Log.e(" **** Escala ", " Scala: " + bitmap.getWidth());
        Log.e(" **** Escala ", " Scala: " + scale);

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), true);

        File out = new File(file.getAbsolutePath());
        OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
        resized.compress(Bitmap.CompressFormat.JPEG, 85, os);
        os.close();

        return out;
    }


    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        //File file = FileUtils.getFile(this, fileUri);
        //File file = FileUtils.getFile(fileUri);

        File file = new File(ivImage_full);
        // 为file建立RequestBody实例
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.makinap.tineo.neotrack/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.makinap.tineo.neotrack/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }





    private void loadTrack() {
        RealmResults<Track> notes = mRealm.where(Track.class).findAllSorted("dtime", Sort.ASCENDING);
        TrackRecyclerViewAdapter noteAdapter = new TrackRecyclerViewAdapter(getBaseContext(), notes);
        mNotes.setAdapter(noteAdapter);
    }

    private void insertTrack() {



        //GPS
        gps = new TrackGPS(MainActivity.this);
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            //Toast.makeText(getApplicationContext(),"GPS\nLongitude:"+Double.toString(longitude)+"\nLatitude:"+Double.toString(latitude),Toast.LENGTH_SHORT).show();
        } else {
            gps.showSettingsAlert();
        }

        final String codigo;
        String direccion;
        final String obs;
        final String lat = String.valueOf(latitude);
        final String lng = String.valueOf(longitude);
        final String num = m_Text;
        final String usr = "demo";

        final ArrayList<byte[]> imagenes = new ArrayList<>();
        final ArrayList<String> uris = new ArrayList<>();


            Log.e("View.GONE:", " -> "+View.GONE);
            Log.e("View.VISIBLE:", " -> "+View.VISIBLE);
            Log.e("View.INVISIBLE:", " -> "+View.INVISIBLE);

            Log.e(".getVisibility(1):", "->"+ivImage.getVisibility());
            if (ivImage.getVisibility() != View.GONE) {
                //Bitmap bmp = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                Bitmap bmp = ((GlideBitmapDrawable) ivImage.getDrawable().getCurrent()).getBitmap();
                Log.e("ivImage_full",ivImage_full);
                uris.add(ivImage_full);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imagenes.add(byteArray);
            }
            Log.e(".getVisibility(2):", "->"+ivImage2.getVisibility());
            if (ivImage2.getVisibility() != View.GONE) {
                //Bitmap bmp = ((BitmapDrawable) ivImage2.getDrawable()).getBitmap();
                Bitmap bmp = ((GlideBitmapDrawable) ivImage2.getDrawable().getCurrent()).getBitmap();
                uris.add(ivImage2_full);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imagenes.add(byteArray);
            }
            Log.e(".getVisibility(3):", "->"+ivImage3.getVisibility());
            if (ivImage3.getVisibility() != View.GONE) {
                //Bitmap bmp = ((BitmapDrawable) ivImage3.getDrawable()).getBitmap();
                Bitmap bmp = ((GlideBitmapDrawable) ivImage3.getDrawable().getCurrent()).getBitmap();
                uris.add(ivImage3_full);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imagenes.add(byteArray);
            }
            Log.e(".getVisibility(4):", "->"+ivImage4.getVisibility());
            if (ivImage4.getVisibility() != View.GONE) {
                //Bitmap bmp = ((BitmapDrawable) ivImage4.getDrawable()).getBitmap();
                Bitmap bmp = ((GlideBitmapDrawable) ivImage4.getDrawable().getCurrent()).getBitmap();
                uris.add(ivImage4_full);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imagenes.add(byteArray);
            }

        Log.e("imagenes : "," -> "+imagenes.size());

        TextInputEditText mCode = (TextInputEditText) findViewById(R.id.et_code);
        codigo = mCode.getText().toString();

        TextInputEditText mDir = (TextInputEditText) findViewById(R.id.et_direccion);
        direccion = mDir.getText().toString();

        TextInputEditText mObs = (TextInputEditText) findViewById(R.id.et_observacion);
        obs = mObs.getText().toString();

        /*if(codeSeleted == 0){
            Toast.makeText(getApplicationContext(), "Sin selecionar ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Seleccionado: "+ codeSeleted, Toast.LENGTH_SHORT).show();
        }*/
        final boolean isChecked = ((CheckBox) findViewById(R.id.state_baja)).isChecked();



        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Track track = new Track();
                track.setCode(Long.parseLong(codigo));
                track.setLat(Double.parseDouble(lat));
                track.setLng(Double.parseDouble(lng));
                track.setObs(obs);
                track.setIdTienda(codeSeleted);
                track.setNum(num);
                track.setUser(usr);
                track.setFlag((isChecked?"Baja":"Alta"));
                track.setDtime(new Date());

                //mRealm.copyToRealm(track);

                //realm.beginTransaction();
                Track track1 = realm.copyToRealm(track);
                String id = track1.getId();
                Log.e("NEW track: ","->" +id);
                int i = 0;
                for (byte[] bytes : imagenes) {

                    Log.e("ADD photo: ","->" +id);
                    Photo photo =  new Photo();
                    photo.setTrackId(id);
                    photo.setUri(uris.get(i));
                    photo.setImage(bytes);
                    realm.copyToRealm(photo);
                    i++;
                }




                //realm.commitTransaction();

                //Toast.makeText(getApplicationContext(), "Lastid: "+ id, Toast.LENGTH_SHORT).show();



                /*Note note = new Note();
                note.setText(noteText);
                mRealm.copyToRealm(note);*/
            }
        });
    }

    public static void copyExif(String oldPath, String newPath) throws IOException
    {
        ExifInterface oldExif = new ExifInterface(oldPath);

        String[] attributes = new String[]
                {
                        ExifInterface.TAG_APERTURE,
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_DATETIME_DIGITIZED,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_DATESTAMP,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_ISO,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_SUBSEC_TIME,
                        ExifInterface.TAG_SUBSEC_TIME_DIG,
                        ExifInterface.TAG_SUBSEC_TIME_ORIG,
                        ExifInterface.TAG_WHITE_BALANCE
                };

        ExifInterface newExif = new ExifInterface(newPath);
        for (int i = 0; i < attributes.length; i++)
        {
            String value = oldExif.getAttribute(attributes[i]);
            if (value != null)
                newExif.setAttribute(attributes[i], value);
        }
        newExif.saveAttributes();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if(phoneNo == null) return false;

        else if (phoneNo.matches("\\d{9}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }







}