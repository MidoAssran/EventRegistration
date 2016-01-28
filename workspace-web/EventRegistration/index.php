<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Event Registration</title>
		<style>
			.error {color: #FF0000;}
		</style>
	</head>
	<body>
		<?php
			require_once 'model/Participant.php';
			require_once 'model/Event.php';
			require_once 'model/RegistrationManager.php';
			require_once 'persistence/PersistenceEventRegistration.php';

			session_start();

			//Retreive the data from the model
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();

			echo "<form action='register.php' method='post'>";

			echo "<p>Name? <select name='participantspinner'>";
			foreach ($$rm->getParticipants as $participant) {
				echo "<option>" . $participant->getName() . "</option>";
			}
			echo "</select><span class = 'error'>";
			if (isset($_SESSION['errorRegisterParticipant']) && !empty($_SESSION['errorRegisterParticipant'])) {
				echo " * " . $_SESSION["errorRegisterParticipant"];
			}
			echo "</span></p>";

			echo "<p>Name? <select name='eventspinner'>";
			foreach ($$rm->getEvents as $event) {
				echo "<option>" . $event->getName() . "</option>";
			}
			echo "</select><span class = 'error'>";
			if (isset($_SESSION['errorRegisterEvent']) && !empty($_SESSION['errorRegisterEvent'])) {
				echo " * " . $_SESSION["errorRegisterEvent"];
			}
			echo "</span></p>";

			echo "<p><input type='submit' value='Register /></p>";
			echo "</form>";
		?>
		<form action="addparticipant.php" method="post">
			<p>Name? <input type="text" name="participant_name" />
				<span class="error">
					<?php
					if (isset($_SESSION['errorParticipantName']) && !empty($_SESSION['errorParticipantName'])) {
						echo " * " . $_SESSION["errorParticipantName"];
					}
					?>
				</span>
			</p>
			<input type="submit" value="Add Participant" />
		</form>
	</body>
</html>
