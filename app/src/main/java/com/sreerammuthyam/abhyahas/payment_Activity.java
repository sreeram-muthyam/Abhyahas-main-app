package com.sreerammuthyam.abhyahas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sreerammuthyam.abhyahas.R;

import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import static java.lang.String.valueOf;

public class payment_Activity extends AppCompatActivity  implements PaymentResultListener {

    ProgressDialog pd;
    String price, mon, userID, courseName, CollId, from, rzpkey;

    ImageView image;
    TextView text;
    Button rzp;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_);

        Checkout.preload(payment_Activity.this);

        pd = new ProgressDialog(payment_Activity.this);

        image = (ImageView) findViewById(R.id.sucorfail);
        text = (TextView) findViewById(R.id.text);
        rzp = (Button) findViewById(R.id.rzp);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("ADMIN").document("Razorpay").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                rzpkey = documentSnapshot.getString("rzpkey");
            }
        });

        image.setImageResource(R.drawable.rzpimage);

        firebaseFirestore.collection("ADMIN").document("payment").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Double pay = documentSnapshot.getDouble("pay");
                if(pay==1.00) {
                    rzp.setVisibility(View.VISIBLE);
                } else {
                    rzp.setVisibility(View.INVISIBLE);
                    text.setText("Currently we are not accepting any payments. Sorry for the inconvenience !");
                }
            }
        });

        rzp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (isOnline()) {
                 firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                         from = documentSnapshot.getString("from");
                         if (from.equals("upload")) {
                             price = "10";
                             startPayment(price);
                         } else {
                             price = documentSnapshot.getString("price");
                             startPayment(price);
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         rzp.setVisibility(View.INVISIBLE);
                         image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                         text.setText("Error in connecting the servers. Try after sometime.");
                     }
                 });
             } else {
                 Toast.makeText(payment_Activity.this, " check your INTERNET connection ", Toast.LENGTH_SHORT).show();
             }
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void startPayment(String price) {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Checkout co = new Checkout();
        co.setKeyID(rzpkey);

//        try {
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", 50000); // amount in the smallest currency unit
//            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "order_rcptid_11");
//
//            Order order = razorpay.Orders.create(orderRequest);
//        } catch (RazorpayException e) {
//            // Handle Exception
//            System.out.println(e.getMessage());
//        }
//
//        RazorpayClient razorpay = new RazorpayClient("<YOUR_KEY_ID>", "<YOUR_SECRET>");
//
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", 50000); // amount in the smallest currency unit
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_11");
//
//        Order order = razorpay.Orders.create(orderRequest);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Abhyahas");
            options.put("description", "Network among the students");
            //You can omit the image option to fetch the image from dashboard
            // options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            //  rootNode = FirebaseDatabase.getInstance();
            //  reference = rootNode.getReference("POST/orders");
            //  reference.setValue(options.put("order_id"))
            // options.put("order_id", "order_DBJOWzybf0sJbb");
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            double total = Double.parseDouble(price);
            total = total * 100;
            options.put("amount", total);
            options.put("prefill.email", "");
            options.put("prefill.contact","");
            co.open(payment_Activity.this, options);
        } catch (Exception e) {
            Toast.makeText(payment_Activity.this, "Error while connecting servers ! Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        rzp.setVisibility(View.INVISIBLE);
        image.setImageResource(R.drawable.ic_baseline_cloud_done_24);
        Toast.makeText(payment_Activity.this, "Payment successfully done! ", Toast.LENGTH_SHORT).show();
        try {
            Double cash = Double.valueOf(price);
            if (cash>50.00) {
                pd.setTitle("Adding Course...");
                pd.setMessage("Wait for a while until course is added to your access list");
                pd.setCancelable(false);
                pd.show();
                courseacces(s);
            } else {
                pd.setTitle("Giving Permission");
                pd.setMessage("Wait for a while until you get 3 chances for uploading");
                pd.setCancelable(false);
                pd.show();
                if (userID!=null) {
                    firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").update("upload_status",3.00).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseFirestore.collection("ADMIN").document("upload").update("d",1.00).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String l = String.valueOf(Calendar.getInstance().getTime());
                                    text.setText("Payment successful and the Payment Transaction ID is "+s+"\n\n Now you can start uploading the new course. But remember you will be left with three attempts to upload the course. In case you can't upload in three attempts, you need to pay 10 Rs again to get next three attempts.\n\n\n"+l);
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                                    text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 6. Contact Team Abhyahas");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                            text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 7. Contact Team Abhyahas");
                        }
                    });
                } else {
                    pd.dismiss();
                    image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                    text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 8. Contact Team Abhyahas");
                }
            }
        } catch (Exception e) {
            pd.dismiss();
            image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
            text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 8. Contact Team Abhyahas");
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        rzp.setVisibility(View.INVISIBLE);
        image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
        text.setText("Payment failed with the error code "+i+". Try after sometime.");
        Toast.makeText(payment_Activity.this, "Error while performing payment ! Try again later", Toast.LENGTH_SHORT).show();
    }

    public void courseacces(String s) {
        String l = String.valueOf(Calendar.getInstance().getTime());
        firebaseFirestore.collection("HOME").document(CollId).collection("LISTITEM").document(courseName).update(userID+"+transaction","Transaction ID : "+s).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(mon!=null&&userID!=null&&CollId!=null&&courseName!=null) {
                    if(mon.equals("true")) {
                        text.setText("Payment successful with the Transaction ID : "+s+"\n\nNow you can access "+courseName+" again from your access list. Have a happy learning "+l);
                        File AccFile = new File(valueOf(payment_Activity.this.getExternalFilesDir("/Purchased Courses/"+userID+courseName+"Purchased")));
                        try {
                            AccFile.createNewFile();
                        } catch (IOException e) {
                            image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                            text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError while adding course into your access list with error code -1. Contact Team Abhyahas");
                        }
                        pd.dismiss();
                    } else {
                        // 1) Amount generated
                        // 2) Amount pending
                        // 3) Amount status
                        // 4) Enrolments
                        // 5) Total Enrolments
                        // 6) User access course num
                        // 7) Processing the data
                        // 8) Set Amount generated
                        // 9) Set Amount Pending
                        // 10) Setting enrolments
                        // 11) Setting user id true
                        // 12) Remaining

                        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentReference userref = firebaseFirestore.collection("USERS").document(userID);
                                DocumentSnapshot usersnap = transaction.get(userref);
                                DocumentReference clgref = firebaseFirestore.collection("HOME").document(CollId);
                                DocumentSnapshot clgsnap = transaction.get(clgref);
                                DocumentReference courseref = firebaseFirestore.collection("HOME").document(CollId).collection("LISTITEM").document(courseName);
                                DocumentSnapshot coursesnap = transaction.get(courseref);
                                Double useracces = clgsnap.getDouble(userID+"access");
                                Double amtsts = clgsnap.getDouble("amount_status");
                                Double amtgnr = coursesnap.getDouble("amount_generated");
                                Double amtpen = coursesnap.getDouble("amount_pending");
                                String enr = coursesnap.getString("Enrolments");
                                String toenr = coursesnap.getString("Total_Enrolments");
                                String courses_accessed = usersnap.getString("courses_accessed");
                                if (useracces==null) {
                                    useracces = 0.00;
                                }
                                if (amtgnr==null) {
                                    amtgnr = 0.00;
                                }
                                if (amtpen==null) {
                                    amtpen = 0.00;
                                }
                                if (enr==null) {
                                    enr = "0";
                                }
                                if (toenr==null) {
                                    toenr = "0";
                                }

                                int en = Integer.parseInt(enr);
                                int to_en = Integer.parseInt(toenr);

                                HashMap<String, String> crsstr = new HashMap<>();
                                crsstr.put(userID,"true");
                                crsstr.put("Enrolments",String.valueOf(en+1));
                                crsstr.put("Total_Enrolments",String.valueOf(to_en+1));
                                HashMap<String, Double> crsdbl = new HashMap<>();
                                crsdbl.put("amount_generated",amtgnr+(Double.valueOf(price)-20.00));
                                crsdbl.put("amount_pending",amtpen+(Double.valueOf(price)-20.00));

                                if (amtsts==null) {
                                    HashMap<String, Double> clghash = new HashMap<>();
                                    clghash.put("amount_status",0.00);
                                    clghash.put(userID+"access",useracces+1.00);
                                    transaction.set(clgref,clghash,SetOptions.merge());
                                } else {
                                    transaction.update(clgref,userID+"access",useracces+1.00);
                                }

                                if (amtpen==0.00) {
                                    if(amtsts==null)
                                    {
                                        transaction.update(clgref,"amount_status",1.00);
                                    }
                                    else
                                        {
                                            transaction.update(clgref,"amount_status",amtsts+1.00);
                                    }
                                }
                                transaction.set(courseref,crsstr,SetOptions.merge());
                                transaction.set(courseref,crsdbl,SetOptions.merge());
                                transaction.update(userref,"courses_accessed",String.valueOf(Integer.parseInt(courses_accessed)+1));
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                File AccFile = new File(valueOf(getApplication().getExternalFilesDir("/Purchased Courses/"+userID+courseName+"Purchased")));
                                try {
                                    AccFile.createNewFile();
                                    text.setText("Payment successful and the Payment Transaction ID is "+s+"\n\n"+courseName+" is added to your Access list. Now you can start learning it from your access section.\n\n\n"+l);
                                    pd.dismiss();
                                } catch (IOException e) {
                                    text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError while adding course into your access list with error code 1. Contact Team Abhyahas");
                                    pd.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                                text.setText(e.getMessage());
                                //text.append("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError while adding course into your access list with error code 0. Contact Team Abhyahas");
                            }
                        });
                    }
                } else {
                    pd.dismiss();
                    image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                    text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 10. Contact Team Abhyahas");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                image.setImageResource(R.drawable.ic_baseline_sms_failed_24);
                text.setText("\n\n(TAKE THIS SCREEN SCREENSHOT FOR REFERENCE)\n\nError in giving access to upload with an error code 9. Contact Team Abhyahas");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                from = documentSnapshot.getString("from");
                if (from.equals("upload")) {
                    price = "10";
                } else {
                    price = documentSnapshot.getString("price");
                    courseName = documentSnapshot.getString("coursename");
                    CollId = documentSnapshot.getString("collid");
                    mon = documentSnapshot.getString("mon");
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                from = documentSnapshot.getString("from");
                if (from.equals("upload")) {
                    price = "10";
                } else {
                    price = documentSnapshot.getString("price");
                    courseName = documentSnapshot.getString("coursename");
                    CollId = documentSnapshot.getString("collid");
                    mon = documentSnapshot.getString("mon");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                from = documentSnapshot.getString("from");
                if (from.equals("upload")) {
                    price = "10";
                } else {
                    price = documentSnapshot.getString("price");
                    courseName = documentSnapshot.getString("coursename");
                    CollId = documentSnapshot.getString("collid");
                    mon = documentSnapshot.getString("mon");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pd.dismiss();
        Intent intent = new Intent(payment_Activity.this,com.sreerammuthyam.abhyahas.Container_Activity.class);
        startActivity(intent);
        finish();
    }
}
