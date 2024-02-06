<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    if(isset($_POST['name']) and isset($_POST['propertyType']) and isset($_POST['id'])){
        $db = new DbOperations();
        
        if($db->editProperty($_POST['name'], $_POST['propertyType'], $_POST['id'])){
            $response['error'] = false;
            $response['message'] = "Property updated";
        }else{
            $response['error'] = true;
            $response['message'] = "An error ocurred, please try again";
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