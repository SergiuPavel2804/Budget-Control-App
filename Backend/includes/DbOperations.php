<?php

    class DbOperations {
        private $connection;

        function __construct(){

            require_once dirname(__FILE__).'/DbConnect.php';
            $db = new DbConnect();

            $this->connection = $db->connect();
        }

        function createUser($username, $email, $password, $firstName, $lastName, $adress, $telephone) {
            $query = $this->connection->prepare("INSERT INTO `users` (`id`, `username`, `email`, `password`, `firstName`, `lastName`, `adress`, `telephone`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?);");
            $query->bind_param("sssssss", $username, $email, $password, $firstName, $lastName, $adress, $telephone);

            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

        function getUsernameAndPassword(){
            $query = $this->connection->prepare("SELECT id, username, password FROM users;");
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function getUsernames() {
            $query = $this->connection->prepare("SELECT username FROM users;");
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function createProperty($name, $propertyType, $userId){
            $query = $this->connection->prepare("INSERT INTO `properties` (`id`, `name`, `propertyType`, `userId`) VALUES (NULL, ?, ?, ?);");
            $query->bind_param("ssi", $name, $propertyType, $userId);

            if($query->execute()){
                $lastInsertedId = $this->connection->insert_id;
                return $lastInsertedId;
            }else{
                return false;
            }
        }

        function editProperty($name, $propertyType, $id){
            $query = $this->connection->prepare("UPDATE `properties` SET `name` = ?, `propertyType` = ? WHERE `properties`.`id` = ?;");
            $query->bind_param("ssi", $name, $propertyType, $id);

            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

        function deleteProperty($id){
            $query = $this->connection->prepare("DELETE FROM `properties` WHERE `properties`.`id` = ?;");
            $query->bind_param("i", $id);

            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

        function getPropertiesByUserId($userId){
            $query = $this->connection->prepare("SELECT * FROM `properties` WHERE `userId` = ?;");
            $query->bind_param("i", $userId);
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function getProductsFromMarket(){
            $query = $this->connection->prepare("SELECT * FROM `productsmarket`;");
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function getProductsByPropertyId($propertyId){
            $query = $this->connection->prepare("SELECT * FROM `productsmyproperty` WHERE `propertyId` = ?;");
            $query->bind_param("i", $propertyId);
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function addProductToProperty($name, $type, $quantity, $price, $power, $isAvailable, $imageUrl, $propertyId){
            $query = $this->connection->prepare("INSERT INTO `productsmyproperty` (`id`, `name`, `type`, `quantity`, `price`, `power`, `isAvailable`, `imageUrl`, `propertyId`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?);");
            $query->bind_param("ssiddssi", $name, $type, $quantity, $price, $power, $isAvailable, $imageUrl, $propertyId);
            if($query->execute()){
                $lastInsertedId = $this->connection->insert_id;
                return $lastInsertedId;
            }else{
                return false;
            }
        }

        function deleteProduct($id){
            $query = $this->connection->prepare("DELETE FROM `productsmyproperty` WHERE `productsmyproperty`.`id` = ?;");
            $query->bind_param("i", $id);
            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

        function getReportsFromUser($userId){
            $query = $this->connection->prepare("SELECT reports.id, reports.name, reports.value, reports.supplier, reports.propertyId FROM properties INNER JOIN reports ON properties.id = reports.propertyId WHERE properties.userId = ?;");
            $query->bind_param("i", $userId);
            $query->execute();
            return $query->get_result()->fetch_all();
        }

        function deleteReport($id){
            $query = $this->connection->prepare("DELETE FROM `reports` WHERE `reports`.`id` = ?;");
            $query->bind_param("i", $id);
            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

        function createReport($name, $value, $supplier, $propertyId){
            $query = $this->connection->prepare("INSERT INTO `reports` (`id`, `name`, `value`, `supplier`, `propertyId`) VALUES (NULL, ?, ?, ?, ?);");
            $query->bind_param("sdsi", $name, $value, $supplier, $propertyId);
            if($query->execute()){
                return true;
            }else{
                return false;
            }
        }

    }