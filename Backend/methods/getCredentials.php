<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='GET'){
    $db = new DbOperations();
    $users = $db->getUsernameAndPassword();
    $response['error'] = false;
    $response['users'] = $users;

}else{
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);