package com.dmcadmson.dmc.MissionDMCmcq;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sslcommerz.library.payment.Classes.PayUsingSSLCommerz;
import com.sslcommerz.library.payment.Listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.Util.ConstantData.BankName;
import com.sslcommerz.library.payment.Util.ConstantData.CurrencyType;
import com.sslcommerz.library.payment.Util.ConstantData.ErrorKeys;
import com.sslcommerz.library.payment.Util.ConstantData.SdkCategory;
import com.sslcommerz.library.payment.Util.ConstantData.SdkType;
import com.sslcommerz.library.payment.Util.JsonModel.TransactionInfo;
import com.sslcommerz.library.payment.Util.Model.AdditionalFieldModel;
import com.sslcommerz.library.payment.Util.Model.CustomerFieldModel;
import com.sslcommerz.library.payment.Util.Model.MandatoryFieldModel;
import com.sslcommerz.library.payment.Util.Model.ShippingFieldModel;


public class PaymentActivity extends AppCompatActivity {
    TextView tv1,tv2;
    private String amount="0";

    /*Mandatory Field*/
    MandatoryFieldModel mandatoryFieldModel;
    /*Mandatory Field For Specific Bank Page*/
 //   MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel("missi5b1bf9e7c190f","missi5b1bf9e7c190f@ssl","10", "1012", CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_PAGE, BankName.DBBL_VISA);

    /*Optional Fields*/
    CustomerFieldModel customerFieldModel;
    ShippingFieldModel shippingFieldModel = new ShippingFieldModel("Shipping Name", "Shipping Address 1","Shipping Address 2","Shipping City", "Shipping State", "Shipping Post Code", "Shipping Country" );

    AdditionalFieldModel additionalFieldModel = new AdditionalFieldModel();
    //additionalFieldModel.setValueA("Value Option 1");
    //additionalFieldModel.setValueB("Value Option 1");
    //additionalFieldModel.setValueC("Value Option 1");
    //additionalFieldModel.setValueD("Value Option 1");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            amount = extras.getString("amount");
        }

        tv1=(TextView) findViewById(R.id.textView);
        tv1.setText("পেমেন্ট");
        tv2=(TextView) findViewById(R.id.textView_notice);
        tv2.setText("রিচার্জ সফল হয়েছে কিনা দয়াকরে আপনার প্রোফাইলে চেক করুন।\n" +
                "\n" +
                "\n" +
                "*আপনার রিচার্জ সুসম্পন্ন হলে, " +
                "আপনি চাইলে প্যাকেজ কিনতে পারবেন এবং ডেইলি এক্সাম এ পরীক্ষা দিতে পারবেন।" +
                "\n\n\n **না হলে আরেক বার মনযোগ দিয়ে চেষ্টা করুন।\n" +
                "                       ধন্যবাদ৷");

        //findCurrentUserCredentials From Firebase

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mandatoryFieldModel = new MandatoryFieldModel("missiondmcadmissionaidlive","5B29E1782861136981",amount, user.getUid(), CurrencyType.BDT, SdkType.LIVE, SdkCategory.BANK_LIST);
        //mandatoryFieldModel = new MandatoryFieldModel("missi5b1bf9e7c190f","missi5b1bf9e7c190f@ssl",amount, user.getUid(), CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST);
        customerFieldModel = new CustomerFieldModel(user.getDisplayName(), user.getEmail(), "Customer Address 1", "Customer Address 2", "Customer City", "Customer State", "Customer Post Code", "Customer Country", " Customer Phone", "Customer Fax");

        /*Call for the PaymentActivity*/
        PayUsingSSLCommerz.getInstance().setData(this,mandatoryFieldModel,customerFieldModel,shippingFieldModel,additionalFieldModel,new OnPaymentResultListener() {
            public static final String TAG = "tag" ;

            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {
                // If PaymentActivity is success and risk label is 0.
                if(transactionInfo.getRiskLevel().equals("0")) {


                    Log.d(TAG, "Transaction Successfully completed");
                }
                // Payment is success but PaymentActivity is not complete yet. Card on hold now.
                else{
                    Log.d(TAG, "Transaction in risk. Risk Title : "+transactionInfo.getRiskTitle().toString());
                }
            }

            @Override
            public void transactionFail(TransactionInfo transactionInfo) {
                // Transaction failed
                Log.e(TAG, "Transaction Fail");
            }

            @Override
            public void error(int errorCode) {
                switch (errorCode){
                    // Your provides information is not valid.
                    case ErrorKeys.USER_INPUT_ERROR :
                        Log.e(TAG, "User Input Error" );break;
                    // Internet is not connected.
                    case ErrorKeys.INTERNET_CONNECTION_ERROR :
                        Log.e(TAG, "Internet Connection Error" );break;
                    // Server is not giving valid data.
                    case ErrorKeys.DATA_PARSING_ERROR :
                        Log.e(TAG, "Data Parsing Error" );break;
                    // User press back button or canceled the transaction.
                    case ErrorKeys.CANCEL_TRANSACTION_ERROR :
                        Log.e(TAG, "User Cancel The Transaction" );break;
                    // Server is not responding.
                    case ErrorKeys.SERVER_ERROR :
                        Log.e(TAG, "Server Error" );break;
                    // For some reason network is not responding
                    case ErrorKeys.NETWORK_ERROR :
                        Log.e(TAG, "Network Error" );break;
                }
            }
        });


    }
}
