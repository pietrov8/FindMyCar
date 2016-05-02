<?php
require_once('./controller.php');
require_once('./classes/Model.php');

$mark = [
    'nazwa' => 'znacznik testowy2',
    'latitude' => 12,
    'longitude' => 13,
    'opis' => 'jakis opis2',
    'data_utworzenia' => '0000-00-00 00:00:00',
];
$obj = (object)$mark;
Controller::handle($obj)->addMarker();
$a = 'dodano';