package com.example.newrestaurant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            ((Window) window).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        // Retrieve bill details from the intent
        String billDetails = getIntent().getStringExtra("bill_details");

        // Display bill details in a TextView
        TextView billTextView = findViewById(R.id.billTextView);
        billTextView.setText(billDetails);

        // Find the "Home" button and set a click listener
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Home" button click
                Intent intent = new Intent(BillActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Close all activities on top
                startActivity(intent);
            }
        });
    }
}
