package com.ehgus.qrcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.WINDOW_SERVICE;


public class URLFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public URLFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static URLFragment newInstance(String param1, String param2) {
        URLFragment fragment = new URLFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 기기, 사진, 미디어, 파일 엑세스 권한
            Manifest.permission.READ_EXTERNAL_STORAGE, // 기기, 사진, 미디어, 파일 엑세스 권한
    };
    private static final int MULTIPLE_PERMISSIONS = 101;

    private AlertDialog dialog;
    String TAG = "GenerateQRCode";
    EditText edtValue;
    ImageView qrImage;
    String inputValue, Url;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fabsave, fabshare;
    private EditText URLText;
    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QRCode/";




    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions();
        }
        try{
            File file = new File("data/data/"+ getActivity().getPackageName() +"/files/");
            File[] flist = file.listFiles();
            for(int i = 0 ; i < flist.length ; i++)
            {
                String fname = flist[i].getName();
                if(fname.equals("temp.png"))
                {
                    flist[i].delete();
                }
            }
        }catch(Exception e){}

        qrImage = (ImageView) getView().findViewById(R.id.QR_Image);
        edtValue = (EditText) getView().findViewById(R.id.edt_value);
        Button Button = (Button) getView().findViewById(R.id.Button);
        URLText = (EditText) getView().findViewById(R.id.urlText);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fabsave = (FloatingActionButton) getView().findViewById(R.id.fabsave);
        fabshare = (FloatingActionButton) getView().findViewById(R.id.fabshare);

        fab.setOnClickListener(this);
        fabsave.setOnClickListener(this);
        fabshare.setOnClickListener(this);



        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Url = URLText.getText().toString();
                    if(Url.contains("http://") || Url.contains("https://")) {
                        inputValue = Url;
                    }else {
                        inputValue = "https://" + Url;
                    }

                if (Url.equals("")) {
                    Snackbar.make(v, "빈 칸 없이 입력해주세요.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                    if (inputValue.length() > 0) {
                        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                        Display display = manager.getDefaultDisplay();
                        Point point = new Point();
                        display.getSize(point);
                        int width = point.x;
                        int height = point.y;
                        int smallerDimension = width < height ? width : height;
                        smallerDimension = smallerDimension * 3 / 4;

                        qrgEncoder = new QRGEncoder(
                                inputValue, null,
                                QRGContents.Type.TEXT,
                                smallerDimension);
                        try {
                            bitmap = qrgEncoder.encodeAsBitmap();
                            qrImage.setImageBitmap(bitmap);
                            qrImage.setVisibility(View.VISIBLE);
                        } catch (WriterException e) {
                            Log.v(TAG, e.toString());
                        }
                    } else {
                        edtValue.setError("Required");
                    }
                }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_url, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
//                Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fabsave:
                anim();
                //save bitmap image
                boolean save;
                String result;
                try {
                    save = QRGSaver.save(savePath, "URLQRcode", bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
//                    Log.d(TAG, savePath+SSIDText.getText().toString().trim()+".jpg");
                } catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(v, "QR코드를 먼저 생성해주세요.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                break;
            case R.id.fabshare:
                anim();
                //shareImage();
                try {
                    FileOutputStream fos = getActivity().openFileOutput("temp.png" , 0);
                    File file = new File(getActivity().getFilesDir(), "/temp.png");
                    Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".fileprovider", file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100 , fos);
                    fos.flush();
                    fos.close();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "공유 하기"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(v, "QR코드를 먼저 생성해주세요.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                break;
        }
    }
    public void anim() {

        if (isFabOpen) {
            fabsave.startAnimation(fab_close);
            fabshare.startAnimation(fab_close);
            fabsave.setClickable(false);
            fabshare.setClickable(false);
            isFabOpen = false;
        } else {
            fabsave.startAnimation(fab_open);
            fabshare.startAnimation(fab_open);
            fabsave.setClickable(true);
            fabshare.setClickable(true);
            isFabOpen = true;
        }
    }

    //ImageView로 부터 이미지를 bitmap으로 변환한다
//    private Bitmap getBitmapFromView(View view) {
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        Drawable bgDrawable =view.getBackground();
//        if (bgDrawable!=null) {
//            //has background drawable, then draw it on the canvas
//            bgDrawable.draw(canvas);
//        }   else{
//            //does not have background drawable, then draw white background on the canvas
//            canvas.drawColor(Color.WHITE);
//        }
//        view.draw(canvas);
//        return returnedBitmap;
//    }




    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast_PermissionDeny();
                            }
                        }
                    }
                } else {
                    showToast_PermissionDeny();
                }
                return;
            }
        }

    }

    private void showToast_PermissionDeny() {
        Toast.makeText(getActivity(), "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        return;
    }

    }