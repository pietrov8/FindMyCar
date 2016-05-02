<?php
/**
 * Created by PhpStorm.
 * User: RadNowacki
 * Date: 20.11.15
 * Time: 10:26
 */

class DBHelper {

    /**
     * @var DBHelper
     */
    public static $instance = null;

    /**
     * @var PDO
     */
    public static $db;

    private function __construct(){
        try {
            self::$db = new PDO('mysql:host=localhost;dbname=piotrmp1_findmycar', 'piotrmp1_fmc', 'findmycar');
//            self::$db = new PDO('mysql:host=localhost;dbname=FMC', 'root', '');
            self::$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $e) {
            echo 'blad polaczenia z baza danych';
        }
    }

    public static function handle (){
        if(self::$instance) {
            return self::$instance;
        }
        return new DBHelper();
    }

    public function getInstance(){
        return self::$db;
    }

    /**
     * Metoda wykonuje query i zwraca tablice
     *
     * @param $query string
     * @return array
     */
    public function executeQuery ($query) {
        $result = self::$db->query($query);
        return $result->fetchAll(PDO::FETCH_ASSOC);
    }
}