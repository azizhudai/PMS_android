package com.karatascompany.pys3318;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karatascompany.pys3318.firebase.FirebaseInstanceIDService;
import com.karatascompany.pys3318.firebase.PushNotifHttp;
import com.karatascompany.pys3318.models.UserModel;
import com.karatascompany.pys3318.models.UserTokenModel;
import com.karatascompany.pys3318.remote.ApiUtils;
import com.karatascompany.pys3318.remote.UserService;
import com.karatascompany.pys3318.session.Session;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private Button kayitBtn;
    private Button buttonIpPop;

    private ProgressDialog progressDialog;

    private EditText editTextIp;
    private Switch switchIp;
    private Button buttonIp;

    public static String IpUrl = "";
    public static int userId = 0;
    UserService userService;
    private Session session;
    private FirebaseAuth mAuth;
  //  FirebaseDatabase mfirebaseDatabase;
    private FirebaseFirestore mFireStore;

    public boolean switchIpIsChecked = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFireStore = FirebaseFirestore.getInstance();
   //     mfirebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);

        setTitle("Kullanıcı Giriş");

        String token_id = FirebaseInstanceId.getInstance().getToken(); // FirebaseInstanceId.getInstance().getToken();//.getToken();
       // Log.i("myytoken = ", token_id);
       // String sendUserId = "22"; String data = "Deneme";
      //  PushNotifHttp not2 =new PushNotifHttp(); //taskName.substring(0,40)
     //   not2.execute(token_id, "Proje Yönetimi",sendUserId+" kişisi tarafından ("+ data+")");


        /////////////
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        kayitBtn = findViewById(R.id.kayitBtn);
        buttonIpPop = findViewById(R.id.buttonIpPop);

        editTextEmail.setText("azizz@gmail.com");
        editTextPassword.setText("123456");

        session = new Session(this);
        mAuth = FirebaseAuth.getInstance();


        buttonIpPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  startActivity(new Intent(MainActivity.this,Pop.class));
                AlertDialog.Builder mBulBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.popwindow, null);

                editTextIp = mView.findViewById(R.id.editTextIp);
                switchIp = mView.findViewById(R.id.switchIp);
                buttonIp = mView.findViewById(R.id.buttonIp);

                mBulBuilder.setView(mView);
                final AlertDialog dialog = mBulBuilder.create();
                dialog.show();

                buttonIp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(editTextIp.getText().toString().isEmpty() && switchIp.isChecked()==false){
                            Toast.makeText(LoginActivity.this,"Lütfen Seçim yapınız!",Toast.LENGTH_SHORT).show();
                        }
                        if(!(editTextIp.getText().toString().isEmpty()) && switchIp.isChecked()){
                            Toast.makeText(LoginActivity.this, "Lütfen Tek Seçim yapınız!", Toast.LENGTH_SHORT).show();

                        }
                        if(!editTextIp.getText().toString().isEmpty() && switchIp.isChecked() == false){
                            dialog.cancel();
                            IpUrl = editTextIp.getText().toString();
                            userService = ApiUtils.getUserService();
                        }
                        if(switchIp.isChecked() && editTextIp.getText().toString().isEmpty()){
                            switchIpIsChecked = true;
                            dialog.cancel();
                            IpUrl = switchIp.getText().toString();
                            userService = ApiUtils.getUserService();
                        }
                        if(switchIp.isChecked() == false){
                            switchIpIsChecked = false;
                        }


                    }
                });


            }

        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();

                    if(validateLogin(email,password)){

                        doLogin(email,password);
                    }
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        kayitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(LoginActivity.this,UserSignUpActivity.class));

              /*  try {
                    if(switchIpIsChecked){
                        Toast.makeText(LoginActivity.this,switchIp.getText().toString(),Toast.LENGTH_SHORT).show();
                        IpUrl = switchIp.getText().toString();
                    }
                    else{
                        if(!editTextIp.getText().toString().isEmpty()){
                            Toast.makeText(LoginActivity.this,editTextIp.getText().toString(),Toast.LENGTHhhhhhhhhhhhhhhhhz_SHORT).show();
                            IpUrl = editTextIp.getText().toString();
                        }

                        else{
                            Toast.makeText(LoginActivity.this, "Lütfen İP giriniz.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
*/

                /*try {
                    String tokenId = "fvmmcfXAx-o:APA91bGT8oFUve6K0sOTJMMqm3-YS2M5lOxfIhMYlpe7mPmPI1armrUqfegB6sruoay0WzdYbkeIIgP2Iqr-BQvHjz7Hxd8ItxyRhU0F0q1kWTwepegNccO_V7quOB4Wnhv9jnZHQSQZ";

                   /* HashMap<String,String> map = new HashMap<>();
                  //  map.put("data","merhaba notify");
                   // JSONObject info = new JSONObject();
                    map.put("title", "FCM Notificatoin Title"); // PushNotificationRemote title
                    map.put("body", "Hello First Test notification"); // PushNotificationRemote body
                    String tokenId = "fvmmcfXAx-o:APA91bGT8oFUve6K0sOTJMMqm3-YS2M5lOxfIhMYlpe7mPmPI1armrUqfegB6sruoay0WzdYbkeIIgP2Iqr-BQvHjz7Hxd8ItxyRhU0F0q1kWTwepegNccO_V7quOB4Wnhv9jnZHQSQZ";
                   PushNotificationRemote.sendPushToSingleInstance(LoginActivity.this,map,tokenId);


                    //FirebaseInstanceIDService.sendAndroidNotification(tokenId,"merhba hadi ama","sonunda inşaAllah");

//                    PushNotifHttp.HttpPostNotif();


                    PushNotifHttp not = new PushNotifHttp();
                    not.execute("a","b");
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/
               /* try {
                    pushFCMNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        });


    }



    private boolean validateLogin(String email, String password) {
        if(email == null || email.trim().length() == 0){
            Toast.makeText(this,"Email Giriniz!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this,"Şifre Giriniz!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    private void doLogin(final String email, final String password) {
        progressDialog.setMessage("Giriş yapılıyor Bekleyin...");
        progressDialog.show();

        Call<UserModel> call = userService.login(email,password);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if(response.isSuccessful()){


                    final UserModel userModelObj = response.body();
                    try {
                        //if(userModelObj.getUserEmail().equals(email) && userModelObj.getUserPassword().equals(password)){
                        if(userModelObj != null){

                     /*       mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
*/

                                                String token_id = FirebaseInstanceId.getInstance().getToken();//.getToken();
                                                //String current_id = mAuth.getCurrentUser().getUid();

                                                Map<String, Object> tokenMap = new HashMap<>();
                                                tokenMap.put("token_id", token_id);
                                               /* DatabaseReference databaseReference = mfirebaseDatabase.getReference();
                                                databaseReference.child("kullanici")
                                                        .child(current_id).updateChildren(tokenMap);
                                                //   .setValue(tokenMap);*/

                                        Intent intent = new Intent(LoginActivity.this,MainFragmentActivity.class);
                                        intent.putExtra("email",email);
                                        userId = userModelObj.getUserId();
                                        intent.putExtra("userId", userModelObj.getUserId());
                                        intent.putExtra("IpUrl",IpUrl);
                                        session.setUserId(String.valueOf(userId));

                                        session.setUserMail(email);

                                        UpdateTokenId(session.getUserId(),token_id);

                                        startActivity(intent);
                                        Toast.makeText(LoginActivity.this,"Giriş Başarılı...",Toast.LENGTH_SHORT).show();

                                    /*    mFireStore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
*/
                                        progressDialog.dismiss();
                                /*    }else{
                                        Toast.makeText(LoginActivity.this, "HATA: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();}
                                }
                            });
*/
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Giriş Red!!",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }catch (Exception e){
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Hatalı! Terar Deneyin."+t.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
    }

    private void UpdateTokenId(String userId, String token_id) {

        UserTokenModel userToken = new UserTokenModel(userId,token_id);
       // userToken.setUserId(userId);
        //userToken.setTokenId(token_id);
        Call<String> call = userService.UpdateTokenId(userToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    String result = response.body();
                    if(result.equals("OK")){
                        Toast.makeText(LoginActivity.this, "Başarılı", Toast.LENGTH_SHORT).show();

                    }else Toast.makeText(LoginActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    // Method to send Notifications from server to client end.
       public final static String AUTH_KEY_FCM = "API_KEY_HERE";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public static void pushFCMNotification( ) throws Exception {

        String authKey = "AAAAQj4kfdA:APA91bFafkX6Ad1YKw9jZBnf_xPKZllXcNk7H7LSCOfO8S6pEXXbdq1smFdIZOU9mQfDBOCIDsKASGsRoay76O3joIpKljeFV1L2skLRaTP5tuuTaB5g3MgfCeqdKveVml09M21ggY0T";//AUTH_KEY_FCM; // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + authKey);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject data = new JSONObject();
        data.put("to","fvmmcfXAx-o:APA91bGT8oFUve6K0sOTJMMqm3-YS2M5lOxfIhMYlpe7mPmPI1armrUqfegB6sruoay0WzdYbkeIIgP2Iqr-BQvHjz7Hxd8ItxyRhU0F0q1kWTwepegNccO_V7quOB4Wnhv9jnZHQSQZ");//"/topics/foo-bar");
        JSONObject info = new JSONObject();
        info.put("title", "FCM Notificatoin Title"); // PushNotificationRemote title
        info.put("body", "Hello First Test notification"); // PushNotificationRemote body
        data.put("data", info);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data.toString());
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }

}