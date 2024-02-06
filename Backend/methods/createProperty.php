<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    
    if(isset($_POST['name']) and isset($_POST['propertyType']) and isset($_POST['userId'])){
        $db = new DbOperations();
        
       $lastInsertedId = $db->createProperty($_POST['name'], $_POST['propertyType'], $_POST['userId']);
        if($lastInsertedId != false){
            $response['error'] = false;
            $response['message'] = 'Property added';
            $response['lastId'] = $lastInsertedId;

        }else{
            $response['error'] = true;
            $response['message'] = 'An error ocurred, please try again';
        }

    }else{
        $response['error'] = true;
        $response['message'] = "Not all params";      
    }


}else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

echo json_encode($response);