<?php
require_once('./controller.php');
require_once('./classes/Model.php');

if(!isset($_REQUEST['json']) {
    echo "niepoprawna akcja";
    die;
}

$action = isset($_REQUEST['json']['action']) ? $_REQUEST['json']['action'] : '';
$a = Controller::handle()->$action();

$zmienna = isset($_POST["json"]) ? $_POST["json"] : 'brak';

//if( isset($_POST["json"]) ) {
//    $data = json_decode($_POST["json"]);
//    $data->msg = strrev($data->msg); // odwrócona kolejność by zauważyć zmianę :D
//    echo json_encode($data);
//}
//
//
try {
    echo json_encode(Controller::handle()->$action());
} catch(Exception $e) {
    echo 'Przekazano niepoprawną akcję';
}