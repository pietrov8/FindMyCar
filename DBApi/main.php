<?php
require_once('./controller.php');
require_once('./classes/Model.php');

if(!isset($_REQUEST['json'])) {
    echo 'brak przekazanych danych';
    die;
}
$data = json_decode($_POST['json']);

$action = isset($data->action) ? $data->action : '';
Model::savefile($action);
if(empty($action)) {
    echo 'błąd przekazanej akcji';
    die;
}

try {
    echo json_encode(Controller::handle($data)->$action());
} catch(Exception $e) {
    echo 'Przekazano niepoprawną akcję';
}