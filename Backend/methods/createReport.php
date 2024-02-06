<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){

    if(isset($_POST['name']) and isset($_POST['value']) and isset($_POST['supplier']) and isset($_POST['propertyId'])) {

            $db = new DbOperations();
            if($db ->createReport($_POST['name'], $_POST['value'], $_POST['supplier'], 
            $_POST['propertyId'])){
                $response['error'] = false;
                $response['message'] = "Report added";
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