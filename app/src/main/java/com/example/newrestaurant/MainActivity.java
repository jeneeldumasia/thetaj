package com.example.newrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.ScrollView;


public class MainActivity extends AppCompatActivity {

    private List<MenuItem> menuItems;
    private List<OrderItem> orderedItems;
    private List<View> menuItemViews;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        // Initialize the menu and ordered items
        menuItems = createMenu();
        orderedItems = new ArrayList<>();
        menuItemViews = new ArrayList<>();
        decimalFormat = new DecimalFormat("0.00");

        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the menu when the "Menu" button is clicked
                showMenuDialog();
            }
        });
    }

    // Define the MenuItem class to represent menu items
    private class MenuItem {
        String name;
        String description;
        double price;

        MenuItem(String name, String description, double price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }
    }

    // Define the OrderItem class to represent ordered items
    private class OrderItem {
        MenuItem menuItem;
        int quantity;

        OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        double getTotalPrice() {
            return menuItem.price * quantity;
        }
    }

    // Create the menu items
    private List<MenuItem> createMenu() {
        List<MenuItem> menu = new ArrayList<>();

        menu.add(new MenuItem("Mediterranean Mezze Platter - ₹450", "Hummus, Baba Ghanoush, Tzatziki, Pita Bread, Olives", 450));
        menu.add(new MenuItem("Japanese Sushi Sampler - ₹650", "Assorted Nigiri and Maki Rolls, Wasabi, Pickled Ginger, Soy Sauce", 650));
        menu.add(new MenuItem("Indian Chaat Trio - ₹350", "Aloo Tikki, Papdi Chaat, Bhel Puri", 350));
        menu.add(new MenuItem("Spanish Tapas Selection - ₹420", "Patatas Bravas, Gambas al Ajillo, Chorizo", 420));
        menu.add(new MenuItem("Mexican Guacamole - ₹280", "Freshly Made Guacamole, Tortilla Chips", 280));
        menu.add(new MenuItem("Thai Spring Rolls - ₹320", "Crispy Vegetable Spring Rolls, Sweet Chili Sauce", 320));

        // Add more items here in the same format

        return menu;
    }

    // Display the menu in a custom dialog
    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Menu");

        // Create a ScrollView to hold menu items dynamically
        ScrollView scrollView = new ScrollView(this);
        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);

        // Create checkboxes and quantity input fields for each menu item
        for (final MenuItem item : menuItems) {
            View menuItemView = LayoutInflater.from(this).inflate(R.layout.menu_item, null);
            TextView itemNameTextView = menuItemView.findViewById(R.id.menuItemNameTextView);
            TextView itemDescriptionTextView = menuItemView.findViewById(R.id.menuItemDescriptionTextView);
            final CheckBox itemCheckBox = menuItemView.findViewById(R.id.menuItemCheckBox);
            final EditText itemQuantityEditText = menuItemView.findViewById(R.id.menuItemQuantityEditText);

            itemNameTextView.setText(item.name);
            itemDescriptionTextView.setText(item.description); // Set description

            // Add the menu item view to the list
            menuItemViews.add(menuItemView);

            // Add the menu item to the ordered items list when checked
            itemCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemCheckBox.isChecked()) {
                        // Add the selected item to the orderedItems list
                        int quantity = 0;
                        try {
                            quantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                        } catch (NumberFormatException e) {
                            // Handle invalid input
                        }
                        if (quantity > 0) {
                            orderedItems.add(new OrderItem(item, quantity));
                        }
                    }
                }
            });

            menuLayout.addView(menuItemView);
        }

        scrollView.addView(menuLayout);
        builder.setView(scrollView);

        builder.setPositiveButton("Generate Bill", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Calculate and display the bill when the "Generate Bill" button is clicked
                generateBill();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog without taking any action
            }
        });

        builder.show();
    }

    // Calculate and display the bill in a custom AlertDialog
    private void generateBill() {
        double totalAmount = 0;
        double cgst = 0;
        double sgst = 0;

        StringBuilder billMessage = new StringBuilder("Ordered Items:\n");

        for (View menuItemView : menuItemViews) {
            TextView itemNameTextView = menuItemView.findViewById(R.id.menuItemNameTextView);
            CheckBox itemCheckBox = menuItemView.findViewById(R.id.menuItemCheckBox);
            EditText itemQuantityEditText = menuItemView.findViewById(R.id.menuItemQuantityEditText);

            if (itemCheckBox.isChecked()) {
                MenuItem item = menuItems.get(menuItemViews.indexOf(menuItemView));
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(itemQuantityEditText.getText().toString());
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }
                if (quantity > 0) {
                    double itemTotal = item.price * quantity;
                    totalAmount += itemTotal;
                    billMessage.append(item.name)
                            .append(" x ")
                            .append(quantity)
                            .append(" - ₹")
                            .append(decimalFormat.format(itemTotal))
                            .append("\n");
                }
            }
        }

        // Calculate CGST and SGST (2.5% each)
        cgst = totalAmount * 0.025;
        sgst = totalAmount * 0.025;

        totalAmount += cgst + sgst;

        billMessage.append("\nCGST (2.5%): ₹")
                .append(decimalFormat.format(cgst))
                .append("\nSGST (2.5%): ₹")
                .append(decimalFormat.format(sgst))
                .append("\nTotal: ₹")
                .append(decimalFormat.format(totalAmount));

        // Start the BillActivity and pass bill details
        Intent intent = new Intent(MainActivity.this, BillActivity.class);
        intent.putExtra("bill_details", billMessage.toString());
        startActivity(intent);
    }
}
