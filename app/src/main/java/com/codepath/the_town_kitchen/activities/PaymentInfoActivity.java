package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.the_town_kitchen.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

public class PaymentInfoActivity extends ActionBarActivity {
    Button bSavePayment;
    EditText etCreditCardNum;
    EditText etExpirationYear;
    EditText etExpirationMonth;
    EditText etCvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        setupViewComponents();

        bSavePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String creditCardNumber = etCreditCardNum.getText().toString();
                String cvc = etCvc.getText().toString();
                int cardExpMonth = Integer.parseInt(etExpirationMonth.getText().toString());
                int cardExpYear = Integer.parseInt(etExpirationYear.getText().toString());

                if(verifyCreditCard(creditCardNumber,cardExpMonth, cardExpYear, cvc)) {

                    Intent i = new Intent(PaymentInfoActivity.this, OrderSummaryActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void setupViewComponents(){
        bSavePayment = (Button) findViewById(R.id.bSavePayment);
        etCreditCardNum = (EditText) findViewById(R.id.etCreditCardNum);
        etExpirationYear = (EditText) findViewById(R.id.etExpirationYear);
        etExpirationMonth = (EditText) findViewById(R.id.etExpirationMonth);
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
