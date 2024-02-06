package com.example.aplicatie_licenta.utils;

public class DBConstants {
    private static final String ROOT_URL = "http://192.168.100.17/Android/methods/";

    public static final String URL_REGISTER_USER = ROOT_URL + "registerUser.php";
    public static final String URL_GET_CREDENTIALS = ROOT_URL + "getCredentials.php";
    public static final String URL_GET_USERNAMES = ROOT_URL + "getUsernames.php";
    public static final String URL_CREATE_PROPERTY = ROOT_URL + "createProperty.php";
    public static final String URL_EDIT_PROPERTY = ROOT_URL + "editProperty.php";
    public static final String URL_DELETE_PROPERTY = ROOT_URL + "deleteProperty.php";
    public static final String URL_GET_PROPERTIES = ROOT_URL + "getPropertiesByUserId.php";
    public static final String URL_GET_PRODUCTS_MARKET = ROOT_URL + "getProductsFromMarket.php";
    public static final String URL_GET_MY_PRODUCTS = ROOT_URL + "getProductsByPropertyId.php";
    public static final String URL_ADD_PRODUCT_TO_PROPERTY = ROOT_URL + "addProductToProperty.php";
    public static final String URL_DELETE_PRODUCT = ROOT_URL + "deleteProduct.php";
    public static final String URL_GET_REPORTS_FROM_USER = ROOT_URL + "getReportsFromUser.php";
    public static final String URL_DELETE_REPORT = ROOT_URL + "deleteReport.php";
    public static final String URL_CREATE_REPORT = ROOT_URL + "createReport.php";
}
