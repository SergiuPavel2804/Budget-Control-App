<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    
    if(isset($_POST['userId'])){
        $db = new DbOperations();
        $properties = $db->getPropertiesByUserId($_POST['userId']);
        $response['error'] = false;
        $response['properties'] = $properties;
        
    }else{
        $response['error'] = true;
        $response['message'] = "Not all params";      
    }


}else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

echo json_encode($response);