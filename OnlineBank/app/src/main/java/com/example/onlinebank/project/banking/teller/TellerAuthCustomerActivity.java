package com.example.onlinebank.project.banking.teller;

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
import com.example.onlinebank.generics.Roles;
import com.example.onlinebank.project.banking.InputValidator;
import com.example.onlinebank.security.PasswordHelpers;

import java.util.EnumMap;
import java.util.List;

public class TellerAuthCustomerActivity extends AppCompatActivity {

  EditText customerIdView;
  EditText customerPasswordView;
  TextView messageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teller_auth_customer);

    // when user presses button
    Button loginCustomer = (Button) findViewById(R.id.button_teller_auth_cus);
    loginCustomer.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // get views
        customerIdView = (EditText) findViewById(R.id.teller_get_cus_id);
        customerPasswordView = (EditText) findViewById(R.id.teller_get_cus_pw);
        messageView = (TextView) findViewById(R.id.display_result);

        // check if edit text view is empty for either of them
        if (InputValidator.isEmpty(customerIdView) || InputValidator
            .isEmpty(customerPasswordView)) {
          // pop message so fields aren't left empty
          messageView.setText(R.string.null_info);

        } else {
          // get the customerid integer and password
          int customerId = Integer.parseInt(customerIdView.getText().toString());
          String customerPassword = customerPasswordView.getText().toString();

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // check if user id exists
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (customerId == i) {
              exists = true;
            }
          }

          boolean isCustomer = false;
          boolean authenticated = false;

          if (exists) {
            // check if id is customer
            EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
            int customerRoleId = roles.get(Roles.CUSTOMER);
            int roleId = db.getUserRole(customerId);
            if (roleId == customerRoleId) {
              isCustomer = true;
            }

            // compare passwords and authenticate if customer
            String password = db.getPassword(customerId);
            if (isCustomer) {
              authenticated = PasswordHelpers.comparePassword(password, customerPassword);
            }
          }

          if (authenticated) {
            // send customer id back to menu
            Intent intent = new Intent();
            intent.putExtra("customerId", customerId);
            setResult(RESULT_OK, intent);
            finish();

          } else if (!exists) {
            // user id doesn't exist
            messageView.setText(R.string.invalid_userid);

          } else if (!isCustomer) {
            // user id isn't a customer
            messageView.setText(R.string.not_customer);

          } else {
            // not authenticated so wrong password
            messageView.setText(R.string.incorrect_password);
          }
        }
      }
    });

  }
}
