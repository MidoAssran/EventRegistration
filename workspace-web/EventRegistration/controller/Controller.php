<?php
require_once "controller/InputValidator.php";
require_once "persistence/PersistenceEventRegistration.php";
require_once "model/Participant.php";
require_once "model/Event.php";
require_once "model/Registration.php";
require_once "model/RegistrationManager.php";

class Controller
{
	public function __construct()
	{
	}
	
	public function createParticipant($participant_name) {
		// 1. Validate Input
		$name = InputValidator::validate_input($participant_name);
		if ($name == null || strlen($name) == 0) {
			throw new Exception("Participant name cannot be empty!");
		} else {
			// 2. Load all of the data
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();

			// 3. Add the new participant
			$participant = new Participant($name);
			$rm->addParticipant($participant);

			// 4. Write all of the data
			$pm->writeDataToStore($rm);
		}
	}
	
	public function createEvent($event_name, $event_date, $starttime, $endtime) {
		// 1. Validate Input
		$name = InputValidator::validate_input($event_name);

		// Reformat Input
		date('Y-m-d', strtotime($event_date));
		date('H:i', strtotime($starttime));
		date('H:i', strtotime($endtime));

		// Check Validity
		if ($name == null || strlen($name) == 0) {
			throw new Exception("Event name cannot be empty!");
		} elseif (strtotime($event_date) == false) {
			throw new Exception("Invalid Date!");
		} elseif (strtotime($starttime) == false) {
			throw new Exception("Invalid Start Time!");
		} elseif (strtotime($endtime) == false) {
			throw new Exception("Invalid End Time!");
		} else {
			// 2. Load all of the data
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();

			// 3. Add the new event
			$event = new Event($name, $event_date, $starttime, $endtime);
			$rm->addEvent($event);

			// 4. Write all of the data
			$pm->writeDataToStore($rm);
		}
	}
	
	public function register($aParticipant, $aEvent) {
		// 1. Load all of the data
		$pm = new PersistenceEventRegistration();
		$rm = $pm->loadDataFromStore();

		// 2. Find the participant
		$myParticipant = NULL;
		foreach ($rm->getParticipants() as $participant) {
			if (strcmp($participant->getName(), $aParticipant) == 0) {
				$myParticipant = $participant;
				break;
			}
		}

		// 3. Find the event
		$myEvent = NULL;
		foreach ($rm->getEvents() as $event) {
			if (strcmp($event->getName(), $aEvent) == 0) {
				$myEvent = $event;
				break;
			}
		}

		// 4. Register for the event
		$error = "";
		if ($myParticipant != NULL && $myEvent != NULL) {
			$myRegistration = new Registration($myParticipant, $myEvent);
			$rm->addRegistration($myRegistration);
			$pm->writeDataToStore($rm);
		} else {
			if ($myParticipant == NULL) {
				$error .= "@1Participant ";
				if ($aParticipant != NULL) {
					$error .= $aParticipant;
				}
				$error .= " not found! ";
			}
			if ($myEvent == NULL) {
				$error .= "@2Event ";
				if ($aEvent != NULL) {
					$error .= $aEvent;
				}
				$error .= " not found! ";
			}
			throw new Exception(trim($error));
			
		}
	}
}
?>