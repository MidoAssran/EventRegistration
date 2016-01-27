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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.ecse321.eventregistration.controller.EventRegistrationController;
import ca.mcgill.ecse321.eventregistration.controller.InvalidInputException;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceEventRegistration;

public class MainActivity extends AppCompatActivity {

    RegistrationManager rm;
    HashMap<Integer, Participant> participants;
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

    public void refreshData() {
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
            TextView tv = (TextView) findViewById(R.id.newparticipant_name);
            tv.setText("");
        }
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

    public void addParticipant(View v) {
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
}
