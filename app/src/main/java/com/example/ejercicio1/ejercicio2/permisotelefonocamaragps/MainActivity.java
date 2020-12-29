package com.example.ejercicio1.ejercicio2.permisotelefonocamaragps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {



    /**
     * lo primero es comprobar la version de android en la que se ejecuta el programa (api 23)
     * SI APP < 23
     *  No necesito permisos
     * SI NO
     *  Comprobar si tengo permisos para el recurso
     *      si es que si lanzo el recurso
     *      y si es que no solocitar los permisos
     *
     * ...........................
     * comprobare si me los has autorizado o no
     *      si me los has autorizado lanzo recurso
     *      y si no aviso de que no puedo seguir
     *
     * @param savedInstanceState
     */

    private EditText txtNumTelefono;
    private ImageButton btnImgLllamada;
    private ImageView imgCamera;
    private Button btnTakeSaveAction;
    private String curretPhotoPath;
    private Button btnOpenGalleryAction;

    //request permision
    private  final int TELEFONO_PERMISION = 100;
    private  final int CAMERA_PERMISION = 101;
    private  final int TAKE_SAVE_PERMISION = 102;
    private  final int OPEN_GALLERY_PERMISION = 103;


    //request camera ACTION
    private  final int CAMERA_ACTION = 1;
    private  final int TAKE_SAVE_ACTION = 2;
    private  final int OPEN_GALLERY_ACTION = 3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNumTelefono = findViewById(R.id.txtNumTelefono);
        btnImgLllamada = findViewById(R.id.btnLlamadaAction);
        imgCamera = findViewById(R.id.imgCameraAction);
        btnTakeSaveAction = findViewById(R.id.btnTakeSaveAction);
        btnOpenGalleryAction = findViewById(R.id.btnOpenGalery);

        btnImgLllamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtNumTelefono.getText().toString().isEmpty()){
                    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                        llamadaAction();
                    }else {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                            llamadaAction();
                        }else {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},TELEFONO_PERMISION);
                        }
                    }
                }

            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                    cameraAction();
                }else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        cameraAction();
                    }else {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISION);
                    }
                }
                
          
            }
        });

        btnTakeSaveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                    takeSaveAction();
                }else{
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                    takeSaveAction();

                    }else {
                        String[] permisos = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(MainActivity.this, permisos,TAKE_SAVE_PERMISION);
                    }
                }
            }
        });

        btnOpenGalleryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                    openGalleryAction();
                }else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        openGalleryAction();
                    }else {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},OPEN_GALLERY_PERMISION);
                    }
                }
            }
        });

    }




    /**
     * metodo que hace la llmada por telefono
     */
    private void llamadaAction(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel: "+txtNumTelefono.getText().toString()));
        startActivity(intent);
    }
    private void cameraAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_ACTION);
        
    }

    /**
     * metodo encargado de crear u fichero para que se guarde ahi la foto
     * 1. dar nombre al ficehro (TimeStamp)
     * 2. Lo concateno con el prefijo de la imagen
     * 3. creo el Fichero Temporal
     * @return
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String  imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        curretPhotoPath = image.getAbsolutePath();
        return image;
    }
    /**
     * metodo encargado de abrir la camara y la foto se guarde un fichero
     * OJO , el fichero se lo tengo que pasar yo por que la camara no puede crear el fichero
     * 1. Crear fichero
     * 2. Llamar a la camara pasádole la URI del fichero
     */
    private void takeSaveAction() {

        try {
            File photoFile = createImageFile();
            Intent intentTakeSave = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoURI = FileProvider.getUriForFile(this,"com.example.ejercicio1.ejercicio2.permisotelefonocamaragps",photoFile);
            intentTakeSave.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            startActivityForResult(intentTakeSave, TAKE_SAVE_ACTION);


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al scribir el fichero", Toast.LENGTH_SHORT).show();
        }

    }
    private void openGalleryAction() {

        Intent openGalery = new Intent(Intent.ACTION_GET_CONTENT);
        openGalery.setType("image/*");
        startActivityForResult(openGalery,OPEN_GALLERY_ACTION);
    }

    /**
     * se activa al cerrar la ventana de peticion de permisos
     * @param requestCode -> tipo de permiso, momentos del permiso
     * @param permissions -> para tener una copia de lso permisos que se ha pedido
     * @param grantResults -> array con los resultados de las peticiones
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == TELEFONO_PERMISION){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){//HAS AUTORIZADO
                llamadaAction();
            }else {
                Toast.makeText(this, "Necesito permisos para llamar", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISION){
            if (permissions.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                cameraAction();
            }else {
                Toast.makeText(this, "Necesito permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == TAKE_SAVE_PERMISION){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED){
                takeSaveAction();
            }else {
                Toast.makeText(this, "Necesito los permisos para seguir", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == OPEN_GALLERY_PERMISION){
            if (permissions.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                openGalleryAction();
            }else {
                Toast.makeText(this, "Necesito permisos para seguir ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION && resultCode == RESULT_OK && data != null){
            Bitmap imgBitMap = (Bitmap) data.getExtras().get("data");
            imgCamera.setImageBitmap(imgBitMap);
        }
        if (requestCode == TAKE_SAVE_ACTION && resultCode == RESULT_OK){
            imgCamera.setImageURI(Uri.parse(curretPhotoPath));
        }
        if (requestCode == OPEN_GALLERY_ACTION && resultCode == RESULT_OK && data!=null){
            imgCamera.setImageURI(data.getData());
        }
    }
}