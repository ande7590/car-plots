<?php

	function initDB() {
		$mysqlLink = mysql_connect("localhost", "root", "nail!goat") or 
			die("FAILED TO CONNECT TO MYSQLDB");
	}

	function getSQLError($mysqlError, $q){
		return "\nSQL ERROR:" . $mysqlError . "\n\n" . $q . "\n\n";
	}

	function getScraperBatch($ScraperBatchID){

		$q = "SELECT ScraperBatchSearchID, S.* FROM `Scraper`.`ScraperBatchSearch` SBS
				 INNER JOIN `Scraper`.`Search` S 
				  ON S.SearchID = SBS.SearchID
				WHERE SBS.ScraperBatchID = " . mysql_escape_string($ScraperBatchID); 

		$res = mysql_query($q) or trigger_error(getSQLError(mysql_error(), $q));

		$batch = array();	//store batch data

		//build ScraperBatchSearchID indexed field of URLs
		while($r = mysql_fetch_assoc($res)){
			$id = strval($r["ScraperBatchSearchID"]);
			if(!is_numeric($id)){
				trigger_error("Warning: skipping non-numeric batchID!");
				continue;
			}
			$batch[$id]= $r;
			$urls[$id] = getSearchURL($r["MakeID"], $r["ModelID"], $r["Zipcode"], $r["Radius"]);
		}

		return $batch;

	}

	function insertScraperRun($scraperBatchID){

		//create a new record corresponding to this execution of Scraper (runID)
		$scraperBatchID = mysql_escape_string($scraperBatchID);
		$q = "INSERT INTO `Scraper`.`ScraperRun`(ScraperRunDT, ScraperBatchID) VALUES(NOW(), {$scraperBatchID});";
		$res = mysql_query($q) or trigger_error(getSQLError(mysql_error(), $q));

		return mysql_insert_id();

	}

	function insertScraperSearchResultsStatus($batch, $urls, $runID, $status){
		
		$q = "";
		$numUrls = count($urls);
		for($i=0; $i<$numUrls; $i++){
			if($i == 0){
				$q = "INSERT INTO `Scraper`.`ScraperBatchSearchResults` 
					   (ScraperBatchSearchID, ScraperRunID, ScraperBatchSearchResultsStatus, 
						  ScraperBatchSearchResultsLog) 
						VALUES ({$id}, {$runID}, '{$status}', '{$urls[$id]}')\n";
			}
			else {
				$q.=  ",({$id}, {$runID}, 'FAIL', '{$urls[$id]}')\n";
			}
		}

		$res = mysql_query($q) or trigger_error(getSQLError(mysql_error(), $q));

		return mysql_affected_rows();
	}

	function updateScraperSearchResultsStatus($ids, $runID, $status){

		$q = "UPDATE `Scraper`.`ScraperBatchSearchResults` SET ScraperBatchSearchResultsStatus = '{$status}' WHERE ";
		
		$numIDs = count($ids);
		for($i=0; $i<$numIDs; $i++){

			$q .= ($i > 0)? " OR ":"";
			$q .= " (ScraperBatchID = {$id} AND ScraperRunID = {$runID})";

		}

		mysql_query($q) or trigger_error(getSQLError(mysql_error(), $q));

		return mysql_affected_rows();
	}

?>

