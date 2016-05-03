<?php
require_once('./controller.php');
require_once('./classes/Model.php');

$mark = [
    'nazwa' => 'Samochod',
    'latitude' => 14,
    'longitude' => 16,
    'opis' => 'jakis opis2',
];
$obj = (object)$mark;
Controller::handle($obj)->removeMarker();
$a = 'dodano';