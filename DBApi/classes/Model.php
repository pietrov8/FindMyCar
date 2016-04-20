<?php
require_once('./classes/DBHelper.php');
class Model {

    public static $instance = null;

    public static function handler() {
        if(self::$instance) {
            return self::$instance;
        }
        return new self;
    }

    public function getAllMarkers(){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 ORDER BY data_utworzenia DESC');
    }

    public function getActiveMarkers(){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND aktywny=1 ORDER BY data_utworzenia DESC');
    }

    public function getUsers(){
        return DBHelper::handle()->executeQuery('SELECT * FROM uzytkownicy ORDER BY nick');
    }

    public function getUserActiveMarker($user_name){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND aktywny=1 AND nazwa_uzytkownika='. $user_name);
    }

    public function getUserMarkers($user_id){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND id_wlasciciela='. $user_id);
    }

    public function getMarkerByName($marker_name){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND nazwa='. $marker_name);
    }

    public function updateMarker($marker) {
        $query = "UPDATE znaczniki SET
                       `latitude` = '{$marker->latitude}',
                       `longitude` = '{$marker->longitude}',
                       `aktywny` = '{$marker->aktywny}',
                  WHERE `nazwa` = '{$marker->nazwa}'";
        DBHelper::handle()->executeQuery($query);
        return 'edytowano znacznik';
    }

    public function addMarker ($marker) {
        $query = "INSERT INTO znaczniki (`nazwa`, `latitude`, `longitude`, `data_utworzenia`, `aktywny`, `usuniety`,
                    `data_usuniecia`, `id_wlaciciela`)
                  VALUES ('{$marker->nazwa}', '{$marker->latitude}', '{$marker->longitude}', '{$marker->data_utworzenia}',
                  '{$marker->aktywny}', '{$marker->usuniety}', '0000-00-00 00:00:00', '')";

        DBHelper::handle()->executeQuery($query);
        return 'dodano znacznik';
    }


    public function removeMarker($marker_name) {
        $query = "UPDATE znaczniki SET
                       `usuniety` = 1,
                       `data_usuniecia` = " + date("Y-m-d H:i:s")
            + "WHERE `nazwa` = '{$marker_name}'";
        DBHelper::handle()->executeQuery($query);
        return 'usunieto znacznik';
    }

    public static function savefile($text, $file = 'wtf.txt', $mode = 'a+', $separator = "\n") {
        $h = fopen($file, $mode);
        fwrite($h, $separator);
        if (is_array($text) == true || is_object($text)) {
            fwrite($h, print_r($text, true));
        } else {
            fwrite($h, $text);
        }
        fclose($h);
    }
}