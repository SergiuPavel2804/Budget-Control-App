<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){
    
    if(isset($_POST['propertyId'])){
        $db = new DbOperations();
        $products = $db->getProductsByPropertyId($_POST['propertyId']);
        $response['error'] = false;
        $response['products'] = $products;
        
    }else{
        $response['error'] = true;
        $response['message'] = "Not all params";      
    }


}else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

echo json_encode($response);