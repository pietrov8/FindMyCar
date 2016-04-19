<?php
require_once('./classes/DBHelper.php');
require_once('./classes/Model.php');

Class Controller {

    /**
     * @var DBHelper
     */
    public static $instance = null;

    private $model = null;

    /**
     * @var PDO
     */
    public static $db;

    private function __construct(){
        $this->model = Model::handler();
    }

    public static function handle (){
        if(self::$instance) {
            return self::$instance;
        }
        return new self;
    }


    public function getAllMarkers() {
        return $this->model->getAllMarkers();

    }

    public function getActiveMarkers() {
        return $this->model->getActiveMarkers();
    }

    public function getUsers() {
        return $this->model->getUsers();
    }

    public function getUserActiveMarker() {
        return $this->getUserMarkersFromModel(true);
    }

    public function getUserMarkers() {
        return $this->getUserMarkersFromModel(false);
    }

    public function getMarkerByName($marker_name) {
        if(empty($marker_name)) return 'niepoprawna nazwa';
        return $this->model->getMarkerByName($marker_name);
    }

    public function getUserMarkersFromModel($is_active) {
        if(empty($_REQUEST['user_id'])) return 'niepoprawne ID uzytkownika';

        $user_id = $_REQUEST['user_id'];
        if($is_active) {
            return $this->model->getUserActiveMarker($user_id);
        }
        return $this->model->getUserMarkers($user_id);
    }

    public function saveMarker() {
        if(empty($_REQUEST['marker'])) return 'blad przekazanych danych znacznika';

        $marker = $_REQUEST['marker'];
        $marker = json_decode($marker);
        $this->model->saveMarker($marker);
        return 'dodano znacznik!';
    }

    public function updateMarker() {
        if(empty($_REQUEST['marker'])) return 'blad przekazanych danych znacznika';

        $marker = $_REQUEST['marker'];
        $marker = json_decode($marker);
        $this->model->updateMarker($marker);
        return 'zaktualizowano dane znacznika!';
    }
}