package com.jens.collicounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
	private int totalColliCount;
	private int newTicketColli;
	private int finishedColli;

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
			totalColliCount = savedInstanceState.getInt("totalColliCount");
			newTicketColli = savedInstanceState.getInt("newTicketColli");
			finishedColli = savedInstanceState.getInt("finishedColli");
			text2.setText(String.valueOf(newTicketColli)); // Show current
															// ticket
			text3.setText(String.valueOf(finishedColli)); // Show finished colli
			float timeWorked = calcWorkTime();
			// Print hours worked with only 1 decimal
			text4.setText(String.format("%.1f", Float.valueOf(timeWorked)));

			// Print colli per hour with only 1 decimal
			float colliPerHour = finishedColli / timeWorked;
			text5.setText(String.format("%.1f", Float.valueOf(colliPerHour)));
			Log.d("DBG", "onCreate was called");
		} else {
			totalColliCount = 0;
			newTicketColli = 0;
			finishedColli = 0;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("totalColliCount", totalColliCount);
		outState.putInt("newTicketColli", newTicketColli);
		outState.putInt("finishedColli", finishedColli);
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
			Toast.makeText(this, "Action refresh selected", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.action_settings:

			Intent intent = new Intent(MainActivity.this,
				      Preferences.class);
				      startActivity(intent);
			break;

		default:
			break;
		}

		return true;
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

	// This method is called when the process button is clicked
	public void onProcess(View view) {
		hideKeypad();
		if (!inputIsValid(userInput))
			return;

		// Save user input to variable
		newTicketColli = Integer.parseInt(userInput.getText().toString());
		// Add the new ticket to the totalColliCount
		totalColliCount += newTicketColli;
		// Calculate the finished collis
		finishedColli = totalColliCount - newTicketColli;

		text2.setText(String.valueOf(newTicketColli)); // Show current ticket
		text3.setText(String.valueOf(finishedColli)); // Show finished colli
		userInput.setText(""); // Empty the input field

		Toast.makeText(this,
				"Nieuwe bon met " + newTicketColli + " colli toegevoegd.",
				Toast.LENGTH_LONG).show();

		float timeWorked = calcWorkTime();
		// Print hours worked with only 1 decimal
		text4.setText(String.format("%.1f", Float.valueOf(timeWorked)));

		// Print colli per hour with only 1 decimal
		float colliPerHour = finishedColli / timeWorked;
		text5.setText(String.format("%.1f", Float.valueOf(colliPerHour)));
	}

	private float calcWorkTime() {
		// Current date and current time
		GregorianCalendar datetimestampNow = new GregorianCalendar();
		// Current date and 7am
		GregorianCalendar datetimestampStart = new GregorianCalendar(
				datetimestampNow.get(Calendar.YEAR),
				datetimestampNow.get(Calendar.MONTH),
				datetimestampNow.get(Calendar.DAY_OF_MONTH), 7, 0, 0);
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

	// This method is called when the refresh button is clicked
	public void onRefresh(View view) {
		userInput.setText("0");
		onProcess(view);
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
