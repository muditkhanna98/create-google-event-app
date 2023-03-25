package com.mudit.implicitintentlab;

import static android.Manifest.permission.CALL_PHONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText title;
    EditText startDate;
    EditText startTime;
    EditText endDate;
    EditText endTime;
    EditText emailsEditText;
    EditText descriptionEditText;
    Button clickPicture;
    Button sos;
    Button addEvent;
    Boolean isAllDay = false;
    private static final int PHOTO_REQUEST = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.titleEditText);
        startDate = findViewById(R.id.startDateEditText);
        startTime = findViewById(R.id.startTimeEditText);
        endDate = findViewById(R.id.endDateEditText);
        endTime = findViewById(R.id.endTimeEditText);
        emailsEditText = findViewById(R.id.emailsEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addEvent = findViewById(R.id.addEventBtn);
        clickPicture = findViewById(R.id.clickPicBtn);
        sos = findViewById(R.id.call911);
    }

    public void openDateDialog(View view) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd/");
        LocalDateTime now = LocalDateTime.now();
        String date[] = dtf.format(now).split("/");

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (view.getId() == R.id.startDateEditText) {
                    startDate.setText(getMonthFormat(month) + " " + day + " " + year);
                } else {
                    endDate.setText(getMonthFormat(month) + " " + day + " " + year);
                }
            }
        }, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        datePickerDialog.show();
    }

    public void openTimeDialog(View view) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH/mm/ss");
        LocalDateTime now = LocalDateTime.now();
        String time[] = dtf.format(now).split("/");

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (view.getId() == R.id.startTimeEditText) {
                    startTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                } else {
                    endTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }
            }
        }, Integer.parseInt(time[0]), Integer.parseInt(time[1]), true);
        timePickerDialog.show();
    }

    public void photoClicked(View view) {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //To save to file: photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename));
        startActivityForResult(photoIntent, PHOTO_REQUEST);
    }

    public void callSos(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:0612312312"));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            //Get the photo into a Bitmap object and display it in the imageView
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(R.id.imageView);
            imageview.setImageBitmap(image);
        }
    }

    public void addEvent(View view) {
        if (!title.getText().toString().isEmpty() && !startDate.getText().toString().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, title.getText().toString());
            String[] emailArr = emailsEditText.getText().toString().split(",");
            intent.putExtra(Intent.EXTRA_EMAIL, emailArr);
            if (isAllDay) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
            } else {
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime().getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime().getTimeInMillis());
            }
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
    }

    public Calendar startDateTime() {
        String string_date = startDate.getText().toString();
        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        String staring_time = startTime.getText().toString();
        String[] starting_time = staring_time.split(":");
        try {
            Date date = format.parse(string_date);
            System.out.println(date);
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(date);
            calDate.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), Integer.parseInt(starting_time[0]), Integer.parseInt(starting_time[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calDate;
    }

    public Calendar endDateTime() {
        String string_date = endDate.getText().toString();
        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        String staring_time = endTime.getText().toString();
        String[] starting_time = staring_time.split(":");
        try {
            Date date = format.parse(string_date);
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(date);
            calDate.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), Integer.parseInt(starting_time[0]), Integer.parseInt(starting_time[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calDate;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void allDayClicked(View view) {
        if (isAllDay == false) {
            String startingDate = startDate.getText().toString();
            startTime.setEnabled(false);
            endTime.setEnabled(false);
            endDate.setEnabled(false);
            if (startingDate != null) {
                endDate.setText(startingDate);
            }
            isAllDay = true;
        } else {
            isAllDay = false;
            startTime.setEnabled(true);
            endTime.setEnabled(true);
            endDate.setEnabled(true);
        }
    }
}