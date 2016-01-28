package ca.mcgill.ecse321.eventregistration;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Time;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.ecse321.eventregistration.controller.EventRegistrationController;
import ca.mcgill.ecse321.eventregistration.controller.InvalidInputException;
import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceEventRegistration;

public class MainActivity extends AppCompatActivity {

    RegistrationManager rm;
    HashMap<Integer, Participant> participants;
    HashMap<Integer, Event> events;
    String error = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PersistenceEventRegistration.setFilename(getFilesDir().getAbsolutePath() + "eventregistration.xml");

        // load model
        PersistenceEventRegistration.loadEventRegistrationModel();
        rm = RegistrationManager.getInstance();
        refreshData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void refreshData() {

        // Set error message if any errors were discovered
        TextView etv = (TextView) findViewById(R.id.errormessage);
        etv.setText(this.error);

        if (error == null || error.length() == 0) {

            // Initialize the data in the participant spinner
            Spinner spinner = (Spinner) findViewById(R.id.participantspinner);
            ArrayAdapter<CharSequence> participantAdapter = new
                    ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
            participantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.participants = new HashMap<Integer, Participant>();
            int i = 0;
            for (Iterator<Participant> participants = rm.getParticipants().iterator();
                 participants.hasNext(); i++) {
                Participant p = participants.next();
                participantAdapter.add(p.getName());
                this.participants.put(i, p);
            }
            spinner.setAdapter(participantAdapter);

            // Reset add participant text
            TextView tv = (TextView) findViewById(R.id.newparticipant_name);
            tv.setText("");

            // Reset add event text
            TextView eventname_tv = (TextView) findViewById(R.id.newevent_name);
            eventname_tv.setText("");
            setTime(R.id.newevent_start_time, 12, 0);
            setTime(R.id.newevent_end_time, 13, 0);
            setDate(R.id.newevent_date, 1, 1, 2016);

            // Initialize the data in the event spinner
            Spinner espinner = (Spinner) findViewById(R.id.eventspinner);
            ArrayAdapter<CharSequence> eventAdapter = new
                    ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
            eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.events = new HashMap<Integer, Event>();
            int j = 0;
            for (Iterator<Event> events = rm.getEvents().iterator();
                 events.hasNext(); j++) {
                Event e = events.next();
                eventAdapter.add(e.getName());
                this.events.put(j, e);
            }
            espinner.setAdapter(eventAdapter);
            TextView tve = (TextView) findViewById(R.id.newevent_name);
            tve.setText("");

        }
    }

    public void showDatePickerDialog(View v) {
        // Show date picker view

        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        // Show time picker view

        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());

        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void register(View v) {
        // Validate, then try registering participant to event

        error = "";
        if (participants.size() > 0 && events.size() > 0) {

            // Get user selected participant and event values
            Spinner pspinner = (Spinner) findViewById(R.id.participantspinner);
            String pname = pspinner.getSelectedItem().toString();
            Spinner espinner = (Spinner) findViewById(R.id.eventspinner);
            String ename = espinner.getSelectedItem().toString();

            if (pname == null || pname.length() <= 0)
                error = error + "Participant needs to be selected for registration! ";
            if (ename == null || ename.length() <= 0)
                error = error + "Event needs to be selected for registration!";
            error = error.trim();


            if (error.length() == 0) {
                // try registering

                // Get participant object
                Participant participant = null;
                for (int i = 0; i < participants.size(); i++) {
                    Participant p = participants.get(i);
                    if (p.getName().equals(pname)) {
                        participant = p;
                        break;
                    }
                }

                // Get event object
                Event event = null;
                for (int i = 0; i < events.size(); i++) {
                    Event e = events.get(i);
                    if (e.getName().equals(ename)) {
                        event = e;
                        break;
                    }
                }

                // Call controller
                if (participant != null && event != null) {
                    try {
                        EventRegistrationController erc = new EventRegistrationController();
                        erc.register(participant, event);
                    } catch (InvalidInputException e) {
                        error = e.getMessage();
                    }
                }
            }
        } else {
            error += "Participant needs to be selected for registration! " +
                    "Event needs to be selected for registration!";
            error.trim();
        }
        // update visuals
        refreshData();
    }

    public void addParticipant(View v) {
        // Validate, then try adding a participant

        TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        EventRegistrationController pc = new EventRegistrationController();
        error = null;
        try {
            pc.createParticipant(tv.getText().toString());
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }

        refreshData();
    }

    public void addEvent(View v) {
        // Validate, then try adding an event

        TextView nametv = (TextView) findViewById(R.id.newevent_name);
        TextView datetv = (TextView) findViewById(R.id.newevent_date);
        TextView starttv = (TextView) findViewById(R.id.newevent_start_time);
        TextView endtv = (TextView) findViewById(R.id.newevent_end_time);

        Bundle dateb = getDateFromLabel(datetv.getText());
        int year = (int) dateb.get("year") - 1900; // years since 1900 used in java.sql.Date date
        Date date = new Date(year, (int)  dateb.get("month"), (int) dateb.get("day"));

        Bundle stimeb = getTimeFromLabel(starttv.getText());
        Time startTime = new Time((int) stimeb.get("hour"), (int) stimeb.get("minute"), 0);

        Bundle etimeb = getTimeFromLabel(endtv.getText());
        Time endTime = new Time((int) etimeb.get("hour"), (int) etimeb.get("minute"), 0);

        EventRegistrationController ec = new EventRegistrationController();
        error = null;
        try {
            ec.createEvent(nametv.getText().toString(), (java.sql.Date) date, startTime, endTime);
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }

        refreshData();
    }


    private Bundle getTimeFromLabel(CharSequence text) {
        // Get bundle time object from text

        Bundle rtn = new Bundle();
        String comps[] = text.toString().split(":");
        int hour = 12;
        int minute = 0;
        if (comps.length == 2) {
            hour = Integer.parseInt(comps[0]);
            minute = Integer.parseInt(comps[1]);
        }

        rtn.putInt("hour", hour);
        rtn.putInt("minute", minute);
        return rtn;
    }

    private Bundle getDateFromLabel(CharSequence text) {
        // Get bundle date object from text

        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;
        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]);
            month = Integer.parseInt(comps[1]);
            year = Integer.parseInt(comps[2]);
        }

        rtn.putInt("day", day);
        rtn.putInt("month", month-1);
        rtn.putInt("year", year);
        return rtn;
    }


    //------------------------Helpers--------------------------------//

    public void setTime(int id, int h, int m) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d:%02d", h, m));
    }

    public void setDate(int id, int d, int m, int y) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }
}
