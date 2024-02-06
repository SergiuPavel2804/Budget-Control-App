<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    $db = new DbOperations();
    $reports = $db->getReportsFromUser($_POST['userId']);
    $response['error'] = false;
    $response['reports'] = $reports;

}else{
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);