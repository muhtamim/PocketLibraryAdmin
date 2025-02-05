package leadinguniversity.pocketlibraryadmin.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import leadinguniversity.pocketlibraryadmin.R;
import leadinguniversity.pocketlibraryadmin.Util.ActivityLancher;
import leadinguniversity.pocketlibraryadmin.Util.Constants;
import leadinguniversity.pocketlibraryadmin.Util.Utilities;
import leadinguniversity.pocketlibraryadmin.data.Publisher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class EditPublisherActivity extends Fragment {


    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.nameET)
    EditText NameET;
    @BindView(R.id.addressET)
    EditText AddressET;
    @BindView(R.id.savepub_changesB)
    Button saveB;
    @BindView(R.id.regprogress)
    ProgressBar progressBar;

    private StorageReference mStorageRef;

    private String mCurrentPhotoPath;
    private File imageFile;
    private Uri selectedImageUri;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;



    public EditPublisherActivity() {
    }


    // TODO: Rename and change types and number of parameters
    public static EditPublisherActivity with(Publisher publisher) {
        EditPublisherActivity fragment = new EditPublisherActivity();
        Bundle args = new Bundle();
        args.putSerializable(ActivityLancher.publisher_KEY, publisher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            publisher = (Publisher) getArguments().getSerializable(ActivityLancher.publisher_KEY);
//            mCurrentPhotoPath = savedInstanceState.getString("Path");
            //     imageFile = (File) savedInstanceState.getSerializable("File");
            //   fillpublisherData();
        }

    }

    private void fillpublisherData() {
        NameET.setText(publisher.getName());
        AddressET.setText(publisher.getAddress());
        Glide.with(this).load(publisher.getImageUrl()).into(ivProfile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_publisher, container, false);
        ButterKnife.bind(this, view);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fillpublisherData();
        return view;
    }


    private Publisher publisher;

    @OnClick(R.id.savepub_changesB)
    void updateBookData() {
        Utilities.showLoadingDialog(getContext(), Color.WHITE);
        String address = AddressET.getText().toString();
        String name = NameET.getText().toString();
        publisher.setAddress(address);
        publisher.setName(name);
        if (imageFile != null)
            uploadUserProfileImage(publisher.getId());
        else
            addNewPublisher();

    }


    private void uploadUserProfileImage(String id) {
        //Todo Compress Image File
        Uri photoURI = FileProvider.getUriForFile(getContext(),
                "leadinguniversity.pocketlibraryadmin.Fileprovider", imageFile);

        StorageReference profileImagesRef = mStorageRef.child
                (Constants.PROFILE_IMAGES_FOLDER + id + ".jpg");

        profileImagesRef.putFile(photoURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String imageUrl = taskSnapshot.getDownloadUrl().toString();
                        publisher.setImageUrl(imageUrl);
                        addNewPublisher();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        publisher.setImageUrl("");
                        addNewPublisher();
                    }
                });
    }

    private void addNewPublisher() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.REF_PUBLISHER);
        myRef.child(publisher.getId()).setValue(publisher).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        saveB.setVisibility(View.VISIBLE);
                        ActivityLancher.openBooksActivity(getContext());

                    }
                });
    }


    @OnClick(R.id.iv_profile)
    void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose)
                .setCancelable(true)
                .setItems(new String[]{getString(R.string.gallery), getString(R.string.camera)},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    //intent to open any media
                                    Intent i = new Intent(Intent.ACTION_PICK);
                                    i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, GALLERY_REQUEST);
                                } else {
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                        File photoFile = null;
                                        try {
                                            photoFile = createImageFile();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        if (photoFile != null) {
                                            Uri photoURI = FileProvider.getUriForFile(getContext(),
                                                    "leadinguniversity.pocketlibraryadmin.Fileprovider", photoFile);
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                        }
                                    }
                                }
                            }
                        }).show();


    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivProfile.getWidth();
        int targetH = ivProfile.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if (targetH != 0 && targetW != 0
                && photoW != 0 && photoH != 0) {
            // Determine how much to scale down the image
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivProfile.setImageBitmap(bitmap);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Path", mCurrentPhotoPath);
        outState.putSerializable("File", imageFile);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mCurrentPhotoPath = savedInstanceState.getString("Path");
//        imageFile = (File) savedInstanceState.getSerializable("File");
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                readFileFromSelectedURI();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            setPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readFileFromSelectedURI();
        } else {
            Toast.makeText(getContext(), R.string.cannot_read_image, Toast.LENGTH_SHORT).show();
        }
    }

    // continue select
    private void readFileFromSelectedURI() {
        Cursor cursor = getContext().getContentResolver().query(selectedImageUri,
                new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(0);
            cursor.close();
            imageFile = new File(imagePath);
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            ivProfile.setImageBitmap(image);
        }
    }
}
