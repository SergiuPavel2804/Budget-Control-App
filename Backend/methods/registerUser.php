<?php

require_once '../includes/DbOperations.php';
$response = array();    

if($_SERVER['REQUEST_METHOD']=='POST'){

    if(isset($_POST['username']) and isset($_POST['email']) and isset($_POST['password']) and 
    isset($_POST['firstName']) and isset($_POST['lastName']) and isset($_POST['adress']) and isset($_POST['telephone'])) {

            $db = new DbOperations();
            if($db ->createUser($_POST['username'], $_POST['email'], $_POST['password'], 
            $_POST['firstName'], $_POST['lastName'], $_POST['adress'], $_POST['telephone'])){
                $response['error'] = false;
                $response['message'] = "User added";
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