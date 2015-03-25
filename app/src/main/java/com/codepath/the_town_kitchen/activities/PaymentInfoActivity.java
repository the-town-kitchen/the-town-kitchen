package com.codepath.the_town_kitchen.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.the_town_kitchen.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

public class PaymentInfoActivity extends TheTownKitchenBaseActivity {
    Button bSubmitPaymentInfo;
    EditText etCreditCardNum;
    EditText etExpirationMonthYear;
    EditText etCvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        setupViewComponents();

        bSubmitPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String creditCardNumber = etCreditCardNum.getText().toString();

                if(creditCardNumber.length() == 16){
                    String new_string;

                    new_string = creditCardNumber.substring(0, 4) + "-";
                    new_string += creditCardNumber.substring(4, 8) + "-";
                    new_string += creditCardNumber.substring(8, 12) + "-";
                    new_string += creditCardNumber.substring(12, 16);


                    creditCardNumber = new_string;

                }

                String cvc = etCvc.getText().toString();

                int cardExpMonth = 0;
                int cardExpYear = 0;
                if(!etExpirationMonthYear.getText().toString().matches("")) {
                    String[] monthYear = etExpirationMonthYear.getText().toString().split("/");
                    if (!(monthYear.length < 2)) {
                        cardExpMonth = Integer.parseInt(monthYear[0]);
                        cardExpYear = Integer.parseInt(monthYear[1]);
                    }
                }

                if(verifyCreditCard(creditCardNumber,cardExpMonth,cardExpYear,cvc)) {

                    Intent i = new Intent(PaymentInfoActivity.this, OrderSummaryActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

                }
            }
        });
    }

    private void setupViewComponents(){
        bSubmitPaymentInfo = (Button) findViewById(R.id.bSubmitPaymentInfo);
        etCreditCardNum = (EditText) findViewById(R.id.etCreditCardNum);
        etExpirationMonthYear = (EditText) findViewById(R.id.etExpirationMonthYear);
        etCvc = (EditText) findViewById(R.id.etCvc);
    }

    private boolean verifyCreditCard(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC){
        Card card = new Card(
                cardNumber,
                cardExpMonth,
                cardExpYear,
                cardCVC
        );

        card.validateNumber();
        card.validateCVC();

        if ( !card.validateCard() ) {
            Toast.makeText(this, "card not valid", Toast.LENGTH_LONG).show();
            return false;
        }

        Stripe stripe = null;
        try {
            stripe = new Stripe("pk_test_6pRNASCoBOKtIshFeQd4XMUh");
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                    }
                    public void onError(Exception error) {
                        // Show localized error message
                        Toast.makeText(getBaseContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
