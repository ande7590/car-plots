<?php
	require_once("ScraperDB.php");

	function fetch_model($outfile, $make, $model) {
		$query = "";
		$res = mysql_query($query) or die("Unable to fetch_model: " . mysql_error());
	}

	function main($args) {
		
		$outfile = $args[1];
		$make = $args[2];
		$model = $args[3];

		initDB();
		$data = fetch_model(

		exit(0);
	}

	main($argv);
?>
