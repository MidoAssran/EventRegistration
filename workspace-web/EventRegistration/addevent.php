<?php
require_once ('controller/Controller.php');

session_start();

$c = new Controller();
try {
	$c->createEvent($_POST['event_name'], $_POST['event_date'], $_POST['starttime'], $_POST['endtime']);
	
	// Clearing unrelated errors
	$_SESSION["errorRegisterEvent"] = "";
	$_SESSION["errorRegisterParticipant"] = "";
	$_SESSION["errorParticipantName"] = "";
	$_SESSION["errorEventName"] = "";	
} catch (Exception $e) {
       $_SESSION["errorEventName"] = $e->getMessage();
}
?>

<!DOCTYPE html>
<html>
       <head>
	<meta http-equiv="refresh" content="0; url=/EventRegistration/" />
	</head>
</html>