<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='GET'){
    $db = new DbOperations();
    $products = $db->getProductsFromMarket();
    $response['error'] = false;
    $response['products'] = $products;

}else{
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);