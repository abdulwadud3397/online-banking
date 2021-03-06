package com.example.onlinebank.project.banking.atm;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.onlinebank.R;
import com.example.onlinebank.database.android.DriverHelper;
import com.example.onlinebank.project.banking.InputValidator;

import java.math.BigDecimal;
import java.util.List;

public class AtmCheckBalanceActivity extends AppCompatActivity {

  EditText accountIdView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_atm_check_balance);

    // get customer id from intent
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when check button is clicked
    Button check = (Button) findViewById(R.id.button_check);
    check.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Get the views
        accountIdView = (EditText) findViewById(R.id.account_id);
        messageView = (TextView) findViewById(R.id.message);

        // Check whether the text is empty
        if (InputValidator.isEmpty(accountIdView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get the account id
          int accountId = Integer.parseInt(accountIdView.getText().toString());

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // Get the list of account id from the user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;

          // check whether account id for the user exists
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          // If exists, display the message
          if (hasAccess) {
            BigDecimal balance = db.getBalance(accountId);
            messageView.setText(getResources().getString(R.string.existed_bal_msg) +
                balance.setScale(2, BigDecimal.ROUND_HALF_UP));
          } else {
            messageView.setText(R.string.not_existed_bal_msg);
          }
        }
      }
    });
  }
}
