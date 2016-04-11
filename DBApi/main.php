<?php
require_once('./controller.php');
require_once('./classes/Model.php');

$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : '';
$a = Controller::handle()->$action();

try {
    echo json_encode(Controller::handle()->$action());
} catch(Exception $e) {
    echo 'Przekazano niepoprawną akcję';
}