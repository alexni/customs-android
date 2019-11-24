package ru.renelogist.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.chat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.testfairy.TestFairy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String MESSAGES_CHILD = "messages";
    public static final String CLAIMS_CHILD = "claims";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    static final int REQUEST_TAKE_PHOTO = 101;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    private String mOpValue;
    private String mUsername;
    private String mPhotoUrl;
    private String mFirebaseUUID;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private Set<String> currentPhotoUries = new HashSet<>();
    private SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
    // This button is placed in main activity layout.
    private Button openInputPopupDialogButton = null;

    // Firebase instance variables
    // Below edittext and button are all exist in the popup dialog view.
    private View popupInputDialogView = null;
    // This listview is just under above button.
    //private ListView userDataListView = null;
    // Contains user name data.
    private EditText surNameEditText = null;
    // Contains password data.
    private EditText nameEditText = null;
    // Contains email data.
    private EditText secondNameEditText = null;
    private EditText phoneEditText = null;
    private EditText birthdateEditText = null;
    private EditText passportPrefixEditText = null;
    private EditText passportNumberEditText = null;
    private EditText passportDateEditText = null;
    private EditText trackNumberEditText = null;
    private EditText trailerNumberEditText = null;
    private EditText checkPointEditText = null;
    private EditText payerEditText = null;
    private EditText contractNumberText = null;
    private EditText carrierEditText = null;
    private EditText commentEditText = null;
    // Click this button in popup dialog to save user input data in above.
    private Button saveUserDataButton = null;
    // Click this button to cancel edit user data.
    private Button cancelUserDataButton = null;
    //Click this button to upload some images to claim
    private ImageView mAddPopupImageView;
    private CheckBox chkFirst, chkSecond, chkThird, chkForth, chkFifth, chkSixth;

    private void selectImage() {
        final CharSequence[] items = { "Камера", "Из памяти",
                "Выход" };

        TextView title = new TextView(MainActivity.this);
        title.setText("Добавить документ");
        title.setBackgroundColor(Color.BLACK);
        title.setPadding(10, 15, 15, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);


        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);



        builder.setCustomTitle(title);

        // builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Камера")) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Из памяти")) {
                    dispathRestorePictureIntent();
                } else if (items[item].equals("Выход")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestFairy.begin(this, "SDK-dNBBZ5L7");
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            mFirebaseUUID = mFirebaseUser.getUid();
        }


        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD+"-"+mFirebaseUUID);
        DatabaseReference claimsRef = mFirebaseDatabaseReference.child(CLAIMS_CHILD);

        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else if (friendlyMessage.getImageUrl() != null) {
                    String imageUrl = friendlyMessage.getImageUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.messageImageView.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.messageImageView);
                                        } else {
                                            Log.w(TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.messageImageView.getContext())
                                .load(friendlyMessage.getImageUrl())
                                .into(viewHolder.messageImageView);
                    }
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }


                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getDateTime()!=null) {
                    viewHolder.messengerTimeView.setText(friendlyMessage.getDateTime());
                }
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(MainActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        //end

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl,
                        null /* no image */);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD+"-"+mFirebaseUUID)
                        .push().setValue(friendlyMessage);

                JSONObject postData = new JSONObject();
                try {
                    postData.put("text", mMessageEditText.getText());
                    new SendData().execute("http://renelogist.ru:8080//phone/message", postData.toString(), mFirebaseUser.getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mMessageEditText.setText("");
            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);

        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                //dispatchTakePictureIntent();
                /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);*/
            }
        });



        initMainActivityControls();

        // When click the open input popup dialog button.
        openInputPopupDialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Create a AlertDialog Builder.
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                // Set title, icon, can not cancel properties.
                alertDialogBuilder.setTitle("Создание заявки.");
                //alertDialogBuilder.setIcon(R.drawable.ic_launcher_background);
                alertDialogBuilder.setCancelable(false);

                // Init popup dialog view and it's ui controls.
                initPopupViewControls();

                // Set the inflated layout view object to the AlertDialog builder.
                alertDialogBuilder.setView(popupInputDialogView);


                // Create AlertDialog and show.
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                alertDialog.show();

                mAddPopupImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       selectImage();
                    }
                });

                // When user click the save user data button in the popup dialog.
                saveUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean haveError = false;
                        // Get user data from popup dialog editeext.
                        String surname = surNameEditText.getText().toString();
                        String name = nameEditText.getText().toString();
                        String secondName = secondNameEditText.getText().toString();
                        String phone = phoneEditText.getText().toString();
                        String birthday = birthdateEditText.getText().toString();
                        String passportPrefix = passportPrefixEditText.getText().toString();
                        String passportNumber = passportNumberEditText.getText().toString();
                        String passportDate = passportDateEditText.getText().toString();
                        String operationType = mOpValue; //operationTypeSpinner.getSelectedItem().toString();
                        String trackNumber = trackNumberEditText.getText().toString();
                        String trailerNumber = trailerNumberEditText.getText().toString();
                        String checkPoint = checkPointEditText.getText().toString();
                        String payer = payerEditText.getText().toString();
                        String carrier = carrierEditText.getText().toString();
                        String comment = commentEditText.getText().toString();
                        String contractNumber = contractNumberText.getText().toString();

                        if (surname == null || surname.trim().length() == 0 || surname.length()>64){
                            surNameEditText.setError("Заполните фамилию");
                            Toast.makeText(MainActivity.this, "Ошибка в фамилии", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (name == null || name.trim().length() == 0 || name.length()>64){
                            nameEditText.setError("Заполните имя");
                            Toast.makeText(MainActivity.this, "Ошибка в имени", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (secondName.length()>64){
                            secondNameEditText.setError("Заполните отчество");
                            Toast.makeText(MainActivity.this, "Ошибка в отчестве", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (phone == null || phone.trim().length() == 0 || phone.length()>12){
                            phoneEditText.setError("Заполните телефон");
                            Toast.makeText(MainActivity.this, "Ошибка в телефоне", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (birthday == null || birthday.trim().length() == 0 || birthday.length()>12){
                            birthdateEditText.setError("Заполните дату ДД-ММ-ГГГГ");
                            Toast.makeText(MainActivity.this, "Ошибка в дате рождения", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        Date d = Utils.stringToDate(birthday, DATE_FORMAT);
                        if (d == null){
                            birthdateEditText.setError("Заполните дату ДД-ММ-ГГГГ");
                            Toast.makeText(MainActivity.this, "Ошибка в дате рождения", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        } else {
                            simpleDate = new SimpleDateFormat(DATE_FORMAT);
                            birthday = simpleDate.format(d);
                        }
                        if (passportPrefix == null || passportPrefix.trim().length() == 0 || passportPrefix.length()>12){
                            passportPrefixEditText.setError("Заполните серию Паспорта");
                            Toast.makeText(MainActivity.this, "Ошибка в серии паспорта", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (passportNumber == null || passportNumber.trim().length() == 0 || passportNumber.length()>12){
                            passportNumberEditText.setError("Заполните номер Паспорта");
                            Toast.makeText(MainActivity.this, "Ошибка в номере паспорта", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (passportDate == null || passportDate.trim().length() == 0 || passportDate.length()>12){
                            passportDateEditText.setError("Заполните дату ДД-ММ-ГГГГ");
                            Toast.makeText(MainActivity.this, "Ошибка в дате паспорта", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        } else {
                            d = Utils.stringToDate(birthday, DATE_FORMAT);
                        }
                        if (d == null){
                            passportDateEditText.setError("Заполните дату ДД-ММ-ГГГГ");
                            Toast.makeText(MainActivity.this, "Ошибка в дате паспорта", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        } else {
                            simpleDate = new SimpleDateFormat(DATE_FORMAT);
                            passportDate = simpleDate.format(d);
                        }
                        if (operationType == null || operationType.trim().length() == 0 || operationType.length()>8){
                            operationType = "FIFTH";
                        }
                        operationType = "FIFTH";

                        if (trackNumber == null || trackNumber.trim().length() == 0 || trackNumber.length()>12){
                            trackNumberEditText.setError("Заполните номер машины");
                            Toast.makeText(MainActivity.this, "Ошибка в номере машины", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (trailerNumber.length()>9){
                            trailerNumberEditText.setError("Заполните номер прицепа");
                            Toast.makeText(MainActivity.this, "Ошибка в номере прицепа", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (checkPoint == null || checkPoint.trim().length() == 0 || checkPoint.length()>32){
                            checkPointEditText.setError("Заполните переход");
                            Toast.makeText(MainActivity.this, "Ошибка в таможенном переходе", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (payer == null || payer.trim().length() == 0 || payer.length()>64){
                            payerEditText.setError("Заполните плательщика");
                            Toast.makeText(MainActivity.this, "Ошибка в плательщике", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (contractNumber == null || contractNumber.trim().length() == 0 || contractNumber.length()>64){
                            contractNumberText.setError("Заполните номер договора");
                            Toast.makeText(MainActivity.this, "Щшибка в номере договора", Toast.LENGTH_SHORT);
                        }

                        if (carrier == null || carrier.trim().length() == 0 || carrier.length()>64){
                            carrierEditText.setError("Заполните перевозчика");
                            Toast.makeText(MainActivity.this, "Ошибка в перевозчике", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }

                        if (comment.length()>256){
                            carrierEditText.setError("Комментарий слишком длинный");
                            Toast.makeText(MainActivity.this, "Ошибка в коментарии", Toast.LENGTH_SHORT).show();
                            haveError = true;
                        }


                        if (!haveError) {
                            ClaimRequest claimMessage = new ClaimRequest(surname, name, secondName, phone, birthday,
                                    passportPrefix, passportNumber, passportDate, operationType, trackNumber, trailerNumber,
                                    checkPoint, payer, carrier, comment);
                            mFirebaseDatabaseReference.child(CLAIMS_CHILD)
                                    .push().setValue(claimMessage);

                            JSONObject postData = new JSONObject();
                            String err = null;
                            try {
                                JSONArray operationTypes = new JSONArray();
                                postData.put("surname", surname);
                                postData.put("name", name);
                                postData.put("secondName", secondName);
                                postData.put("phone", phone);
                                postData.put("birthdate", birthday);
                                postData.put("passportPrefix", passportPrefix);
                                postData.put("passportNumber",passportNumber);
                                postData.put("passportDate", passportDate);
                                postData.put("operationType", operationType);
                                postData.put("trackNumber", trackNumber);
                                postData.put("trailerNumber", trailerNumber);
                                postData.put("checkPoint", checkPoint);
                                postData.put("payer", payer);
                                postData.put("carrier", carrier);
                                postData.put("comment", comment);
                                postData.put("contractNumber", contractNumber);
                                if (currentPhotoUries!=null && currentPhotoUries.size()>0)
                                postData.putOpt("documents", new JSONArray(currentPhotoUries));
                                if (chkFirst.isChecked()){
                                    operationTypes.put("FIRST");
                                }
                                if (chkSecond.isChecked()){
                                    operationTypes.put("SECOND");
                                }
                                if (chkThird.isChecked()){
                                    operationTypes.put("THIRD");
                                }
                                if (chkForth.isChecked()){
                                    operationTypes.put("FOURTH");
                                }
                                if (chkFifth.isChecked()){
                                    operationTypes.put("FIFTH");
                                }
                                if (chkSixth.isChecked()){
                                    operationTypes.put("SIXTH");
                                }
                                postData.put("operationTypes", operationTypes);
                                Log.i("JSON", postData.toString());
                                new SendData().execute("http://renelogist.ru:8080/phone/claim", postData.toString(), mFirebaseUser.getUid());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                err = e.getLocalizedMessage();
                            }

                            Toast.makeText(MainActivity.this, (err == null? "Заявка успешно создана" : err), Toast.LENGTH_LONG).show();
                            currentPhotoUries = new HashSet<>();
                            alertDialog.cancel();
                        }
                    }
                });

                cancelUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }


        });
    }

    /* Initialize main activity ui controls ( button and listview ). */
    private void initMainActivityControls() {
        if (openInputPopupDialogButton == null) {
            openInputPopupDialogButton = (Button) findViewById(R.id.button_popup_overlay_input_dialog);
        }


    }

    /* Initialize popup dialog view and ui controls in the popup dialog. */
    private void initPopupViewControls() {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.popup_layout, null);

        // Get user input edittext and button ui controls in the popup dialog.
        surNameEditText = (EditText) popupInputDialogView.findViewById(R.id.surName);
        nameEditText = (EditText) popupInputDialogView.findViewById(R.id.name);
        secondNameEditText = (EditText) popupInputDialogView.findViewById(R.id.secondName);
        phoneEditText = popupInputDialogView.findViewById(R.id.phone);
        birthdateEditText = popupInputDialogView.findViewById(R.id.birthday);
        TextWatcher birthdateEditTextTextWatcher = new DateInputMask(birthdateEditText);

        passportPrefixEditText = popupInputDialogView.findViewById(R.id.passportPrefix);
        passportNumberEditText = popupInputDialogView.findViewById(R.id.passportNumber);
        passportDateEditText = popupInputDialogView.findViewById(R.id.passportDate);
        TextWatcher passportDateEditTextWatcher = new DateInputMask(passportDateEditText);

        trackNumberEditText = popupInputDialogView.findViewById(R.id.trackNumber);
        trailerNumberEditText = popupInputDialogView.findViewById(R.id.trailerNumber);
        checkPointEditText = popupInputDialogView.findViewById(R.id.checkPoint);
        payerEditText = popupInputDialogView.findViewById(R.id.payer);
        contractNumberText = popupInputDialogView.findViewById(R.id.payerContractNumber);
        carrierEditText = popupInputDialogView.findViewById(R.id.carrier);
        commentEditText = popupInputDialogView.findViewById(R.id.comment);

        saveUserDataButton = popupInputDialogView.findViewById(R.id.button_save_user_data);
        mAddPopupImageView = popupInputDialogView.findViewById(R.id.addPopupImageView);
        cancelUserDataButton = popupInputDialogView.findViewById(R.id.button_cancel_user_data);

        chkFirst = popupInputDialogView.findViewById(R.id.chkFirst);
        chkSecond = popupInputDialogView.findViewById(R.id.chkSecond);
        chkThird = popupInputDialogView.findViewById(R.id.chkThird);
        chkForth = popupInputDialogView.findViewById(R.id.chkForth);
        chkFifth = popupInputDialogView.findViewById(R.id.chkFifth);
        chkSixth = popupInputDialogView.findViewById(R.id.chkSixth);

        initEditTextUserDataInPopupDialog();
        // Display values from the main activity list view in user input edittext.

        //birthdateEditText.addTextChangedListener(new DateInputMask(birthdateEditText));
    }

    /* Get current user data from listview and set them in the popup dialog edittext controls. */
    private void initEditTextUserDataInPopupDialog() {
        mFirebaseDatabaseReference.child(CLAIMS_CHILD);
        String result = null;
        try {
           result =  new GetData().execute("http://renelogist.ru:8080/phone/declarant", mFirebaseUser.getUid()).get();
        } catch (ExecutionException e) {
            Log.e("EXEC", e.getMessage());
        } catch (InterruptedException e) {
            Log.e("EXEC", e.getMessage());
        }
        if (result!=null) {
            Log.i("GET", result);
            JSONObject json = null;
            try {
                json = new JSONObject(result);
                Log.i("My App", json.toString());
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
            }

            try {
                if (surNameEditText != null && json.getString("surname") != null) {
                    surNameEditText.setText(json.getString("surname"));
                }
                if (nameEditText != null && json.getString("name")!=null) {
                    nameEditText.setText(json.getString("name"));
                }
                if (secondNameEditText != null && json.getString("patronymic")!=null) {
                    secondNameEditText.setText(json.getString("patronymic"));
                }
                if (birthdateEditText != null && json.getString("birthday")!=null) {
                    birthdateEditText.setText(json.getString("birthday"));
                }
                if (phoneEditText != null && json.getString("phone")!=null) {
                    phoneEditText.setText(json.getString("phone"));
                }
                /*if (passportPrefixEditText != null && json.getString("passport_series")!=null) {
                    passportPrefixEditText.setText(json.getString("passport_series"));
                }
                if (passportNumberEditText != null && json.getString("passport_number")!=null) {
                    passportNumberEditText.setText(json.getString("passport_number"));
                }*/
                if (passportDateEditText != null && json.getString("passport_date")!=null) {
                    passportDateEditText.setText(json.getString("passport_date"));
                }
            } catch (JSONException e) {
                Log.e("GET", e.getMessage());
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode+", intent= "+data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl,
                    LOADING_IMAGE_URL);
            if (saveUserDataButton!=null) saveUserDataButton.setEnabled(false);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD+"-"+mFirebaseUUID).push()
                    .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                String key = databaseReference.getKey();
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance()
                                                .getReference(mFirebaseUser.getUid())
                                                .child(key)
                                                .child(currentPhotoUri.getLastPathSegment());

                                putImageInStorage(storageReference, currentPhotoUri, key);
                            } else {
                                Log.w(TAG, "Unable to write message to database.",
                                        databaseError.toException());
                            }
                            if (saveUserDataButton!=null) saveUserDataButton.setEnabled(true);
                        }
                    });
        }


        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.i(TAG, "Uri: " + uri.toString());

                    FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD+"-"+mFirebaseUUID).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(final StorageReference storageReference, Uri uri, final String key) {

        UploadTask uploadTask = storageReference.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    String downloadUrl = task.getResult().toString();

                    FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, mPhotoUrl, downloadUrl);

                    mFirebaseDatabaseReference.child(MESSAGES_CHILD+"-"+mFirebaseUUID).child(key).setValue(friendlyMessage);
                    currentPhotoUries.add(downloadUrl);
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("text", downloadUrl);
                        new SendData().execute("http://renelogist.ru:8080/phone/message", postData.toString(), mFirebaseUser.getUid());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w(TAG, "Image upload task was not successful.",
                            task.getException());
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("CAMERA", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ru.renelogist.chat",
                        photoFile);
                currentPhotoUri = photoURI;
                Log.i("CAMERA", photoURI.toString());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.putExtra("PATH", photoURI.getPath());

                startActivityForResult(intent, REQUEST_TAKE_PHOTO);

            } else {
                Log.e("CAMERA", "File not created");
            }
        }
    }

    private void dispathRestorePictureIntent(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        TextView messengerTimeView;
        CircleImageView messengerImageView;


        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerTimeView = (TextView) itemView.findViewById(R.id.messengerTimeView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }


}
