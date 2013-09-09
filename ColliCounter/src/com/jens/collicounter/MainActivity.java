package com.jens.collicounter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements
		OnItemSelectedListener {

	private EditText userInput;
	private TextView text2;
	private TextView text3;
	private TextView text4;
	private TextView text5;
	public int colliTotal;
	public int colliCurrentTicket;
	public int colliFinished;
	ArrayList<Integer> arrayColli = new ArrayList<Integer>(); // Create an arraylist to store the user input collis

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		userInput = (EditText) findViewById(R.id.editText1);
		text2 = (TextView) findViewById(R.id.textView6);
		text3 = (TextView) findViewById(R.id.textView7);
		text4 = (TextView) findViewById(R.id.textView8);
		text5 = (TextView) findViewById(R.id.textView9);

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			colliTotal = savedInstanceState.getInt("colliTotal");
			colliCurrentTicket = savedInstanceState.getInt("colliCurrentTicket");
			colliFinished = savedInstanceState.getInt("colliFinished");
			text2.setText(String.valueOf(colliCurrentTicket)); // Show current ticket
			text3.setText(String.valueOf(colliFinished)); // Show finished colli
			float timeWorked = calcWorkTime();
			// Print hours worked with only 1 decimal
			text4.setText(String.format("%.1f", Float.valueOf(timeWorked)));

			// Print colli per hour with only 1 decimal
			float colliPerHour = colliFinished / timeWorked;
			text5.setText(String.format("%.1f", Float.valueOf(colliPerHour)));
			Log.d("DBG", "onCreate was called");
		} else {
			colliTotal = 0;
			colliCurrentTicket = 0;
			colliFinished = 0;
		} 
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("colliTotal", colliTotal);
		outState.putInt("colliCurrentTicket", colliCurrentTicket);
		outState.putInt("colliFinished", colliFinished);
		Log.d("DBG", "onSaveInstanceState was called");
	}

	// NEW
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// NEW
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh:
				updateFields();
				Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.action_settings:
	
				Intent intent = new Intent(MainActivity.this,
					      Preferences.class);
					      startActivity(intent);
				break;
			case R.id.action_undo:
				if(arrayColli.size() != 0)
					ShowAlertBox("Let op!", "Weet u zeker dat u uw laatste bon met " + colliCurrentTicket +" colli wilt verwijderen?");
				else
					Toast.makeText(this, "Er valt niks te verwijderen", Toast.LENGTH_SHORT).show();
				break;
				
			default:
				break;
		}

		return true;
	}

	private void ShowAlertBox(String title, String desc) {
		new AlertDialog.Builder(this) // confirmation dialog
		.setTitle(title)
		.setMessage(desc)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int whichButton) {
		        arrayRemoveLast();
		    }})
		 .setNegativeButton(android.R.string.no, null).show();
	}

	public void arrayRemoveLast() {
		if(arrayColli.size() <= 1) {   	// alternative procedure when the array is almost empty or empty
			arrayColli.clear();
			Toast.makeText(this, "Removed all collis", Toast.LENGTH_SHORT)
			.show();
		} else {   						// remove the last input
			Toast.makeText(this, "Removed last ticket with " + getColliCurrentTicket(arrayColli) + " colli", Toast.LENGTH_SHORT)
			.show();
			arrayColli.remove(arrayColli.size() - 1);
		}
		updateFields();
	}

	// Hides the keypad
	private void hideKeypad() {
		((InputMethodManager) getSystemService("input_method"))
				.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	// Check to see if the input is not blank
	private boolean inputIsValid(EditText in) {
		if (in.getText().length() == 0) {
			Toast.makeText(this, getString(R.string.toast_no_user_input),
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private int getColliTotal(ArrayList<Integer> list) {
		int sum = 0;
		for(Integer listValue : list){
		    sum += listValue;
		}
		return sum;
	}
	
	private int getColliCurrentTicket(ArrayList<Integer> list) {
		int last = 0;
		last = list.get(list.size() - 1);
		return last;
	}
	
	private void updateFields() {
		if (arrayColli.size() == 0) {
			colliCurrentTicket = 0;
			colliTotal = 0;
			colliFinished = 0;
		} else {
			colliCurrentTicket = getColliCurrentTicket(arrayColli);
			colliTotal = getColliTotal(arrayColli);
			colliFinished = colliTotal - colliCurrentTicket;
		}
		text2.setText(String.valueOf(colliCurrentTicket));
		text3.setText(String.valueOf(colliFinished));
		float timeWorked = calcWorkTime();
		text4.setText(String.format("%.1f", Float.valueOf(timeWorked))); // Print hours worked with only 1 decimal
		float colliPerHour = colliFinished / timeWorked; 
		text5.setText(String.format("%.1f", Float.valueOf(colliPerHour))); // Print colli per hour with only 1 decimal
	}
	
	// This method is called when the process button is clicked
	public void onProcess(View view) {
		hideKeypad();
		if (!inputIsValid(userInput))
			return;

		// Save user input in the Colli array and put function result into variables
		arrayColli.add(Integer.parseInt(userInput.getText().toString()));
		updateFields();
		userInput.setText(""); 								// Empty the input field

		Toast.makeText(this,
				"Nieuwe bon met " + colliCurrentTicket + " colli toegevoegd.",
				Toast.LENGTH_LONG).show();
	}

	private float calcWorkTime() {
		// Current date and current time
		GregorianCalendar datetimestampNow = new GregorianCalendar();
		// Load preferences
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Save preference start work time to an array
		String[] prefStartTime = sharedPrefs.getString("timeStartWork", "7:00").split(":"); // default start at 07:00
		// Current date and the start of work time loaded from preferences
		GregorianCalendar datetimestampStart = new GregorianCalendar(
				datetimestampNow.get(Calendar.YEAR),
				datetimestampNow.get(Calendar.MONTH),
				datetimestampNow.get(Calendar.DAY_OF_MONTH),
				Integer.parseInt(prefStartTime[0]),
				Integer.parseInt(prefStartTime[1]), 0);
		// Calculates the time difference between now and 7am
		float workTimeGross = (datetimestampNow.getTime().getTime() - datetimestampStart
				.getTime().getTime()) / 1000.0F / 60.0F / 60.0F;
		// Initialize the resulting variable
		float workTimeNet = 0;

		// Subtract break times where due (hard coded)
		if (workTimeGross <= 2.0F)
			workTimeNet = workTimeGross;
		if ((workTimeGross > 2.0F) && (workTimeGross <= 2.25D)) {
			workTimeNet = workTimeGross - (workTimeGross - 2.0F);
		}
		if ((workTimeGross >= 2.25D) && (workTimeGross <= 5.0F)) {
			workTimeNet = workTimeGross - 0.25F;
		}
		if ((workTimeGross >= 5.0F) && (workTimeGross <= 5.5D)) {
			workTimeNet = workTimeGross - (workTimeGross - 5.0F) - 0.25F;
		}
		if ((workTimeGross >= 5.5D) && (workTimeGross <= 7.75D)) {
			workTimeNet = workTimeGross - 0.75F;
		}
		if ((workTimeGross >= 7.75D) && (workTimeGross <= 8.0F)) {
			workTimeNet = workTimeGross - (workTimeGross - 7.75F - 0.75F);
		}
		boolean bool = workTimeGross > 8.0F;
		if (bool)
			workTimeNet = workTimeGross - 1.0F;
		return workTimeNet;
	}
	
	@Override
	public void onItemSelected(IcsAdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(IcsAdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
