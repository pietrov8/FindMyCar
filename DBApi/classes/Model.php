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

    public function getUserActiveMarker($user_id){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND aktywny=1 AND id_wlasciciela='. $user_id);
    }

    public function getUserMarkers($user_id){
        return DBHelper::handle()->executeQuery('SELECT * FROM znaczniki WHERE usuniety=0 AND id_wlasciciela='. $user_id);
    }

    public function updateMarker($marker) {
        $query = "UPDATE znaczniki SET
                       `nazwa` = '{$marker->name}',
                       `latitude` = '{$marker->latitude}',
                       `longitude` = '{$marker->longitude}',
                       `data_utworzenia`= '{$marker->date_created}',
                       `aktywny` = '{$marker->active}',
                       `usuniety` = '{$marker->deleted}',
                       `data_usuniecia` = '{$marker->date_deleted}',
                       `id_wlasciciela` = '{$marker->user_id}',
                  WHERE `id` = '{$marker->id}'";
        return $query;
        DBHelper::handle()->executeQuery($query);
    }

    public function saveMarker ($marker) {
        $query = "INSERT INTO bieznie (`id`, `nazwa`, `latitude`, `longitude`, `data_utworzenia`, `aktywny`, `usuniety`,
                    `data_usuniecia`, `id_wlaciciela`)
                  VALUES ('{$marker->id}', '{$marker->name}', '{$marker->latitude}', '{$marker->longitude}', '{$marker->date_created}',
                  '{$marker->active}', '{$marker->deleted}', '{$marker->date_deleted}', '{$marker->user_id}')";

        return $query;
        DBHelper::handle()->executeQuery($query);
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