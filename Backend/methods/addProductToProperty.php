<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    
    if(isset($_POST['name']) and isset($_POST['type']) and isset($_POST['quantity']) and isset($_POST['price']) and isset($_POST['power']) 
    and isset($_POST['isAvailable']) and isset($_POST['imageUrl']) and isset($_POST['propertyId'])){
        $db = new DbOperations();
        
       $lastInsertedId = $db->addProductToProperty($_POST['name'], $_POST['type'], $_POST['quantity'], $_POST['price'], $_POST['power'], 
       $_POST['isAvailable'], $_POST['imageUrl'], $_POST['propertyId']);
        if($lastInsertedId != false){
            $response['error'] = false;
            $response['message'] = 'Product added';
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