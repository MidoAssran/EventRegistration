<?php
require_once ('controller/Controller.php';)

session_start();

$c = new Controller();
try {
	$c->createEvent($_POST['Event_name']);
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