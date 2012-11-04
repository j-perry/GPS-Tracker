<?php
require_once("MySQL.inc.php");

db_connect();

$strUserName = (isset($_GET['u']) ? db_escape($_GET['u']) : "");
$strPassword = (isset($_GET['p']) ? db_escape($_GET['p']) : "");
$strFirstName = (isset($_GET['f']) ? db_escape($_GET['f']) : "");
$strSurName = (isset($_GET['s']) ? db_escape($_GET['s']) : "");
$strEmail = (isset($_GET['e']) ? db_escape($_GET['e']) : "");

if($strUserName == "" || $strPassword == "" || $strFirstName == "" || $strSurName == "" || $strEmail == ""){
	echo "Some data is missing";
	exit;
} 

//Check to see if the actual user exists
db("SELECT User_id FROM users WHERE UserName = '".$strUserName."' AND Firstname = '".$strFirstName."' AND Surname = '".$strSurName."' AND Email = '".$strEmail."'");
$user_exists = dbr();

if (empty($user_exists) == true){
	
	//Check to see if the username is in use
	db("SELECT User_id FROM users WHERE UserName = '".$strUserName."'");
	$username_exists = dbr();
	
	if (empty($username_exists) == true){
		
		//Check to see if the email address is in use
		db("SELECT User_id FROM users WHERE Email = '".$strEmail."'");
		$Email_exists = dbr();
		
		if (empty($Email_exists) == true){
			//Insert our new user
			dbn("INSERT INTO users (UserName, Password, Firstname, Surname, Email) VALUES ('".$strUserName."', '".$strPassword."', '".$strFirstName."', '".$strSurName."', '".$strEmail."')");
			
			db("SELECT User_id FROM users WHERE UserName = '".$strUserName."' AND Firstname = '".$strFirstName."' AND Surname = '".$strSurName."' AND Email = '".$strEmail."'");
			$user_registered = dbr();
			if (empty($user_registered) == false){ 
				echo "User has successfully registered";
				exit;
			} else {
				echo "User was not registered";
				exit;			
			}

		} else {
			echo "Email Address is already in use";
			exit;
		}
		
	} else {
		echo "Username is already in use";
		exit;
	}
	
} else {
	echo "User already exists";
	exit;
}
?>