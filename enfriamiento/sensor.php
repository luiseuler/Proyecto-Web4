<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Authorization, Access-Control-Allow-Methods, Access-Control-Allow-Headers, Allow, Access-Control-Allow-Origin");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, HEAD");
header("Allow: GET, POST, PUT, DELETE, OPTIONS, HEAD");
require_once "conexion.php";
require_once "jwt.php";

date_default_timezone_set('America/Mexico_City');

if ($_SERVER["REQUEST_METHOD"] == "OPTIONS") {
    exit();
}
$header = apache_request_headers();
$jwt = $header['Authorization'];
if (JWT::verify($jwt, "qwertyuiop") != 0) {
    header("HTT/1.1 401 Unauthorized");
    exit();
}
$data = JWT::get_data($jwt, Config::SECRET);

$metodo = $_SERVER["REQUEST_METHOD"];

switch ($metodo) {
    case "GET":
        if (isset($_GET['id'])) {
            $c = conexion();
            $s = $c->prepare("SELECT * FROM sensor WHERE id=:id");
            $s->bindValue(":id", $_GET['id']);
            $s->execute();
            $s->setFetchMode(PDO::FETCH_ASSOC);
            $r = $s->fetch();
        } else {
            $c = conexion();
            $s = $c->prepare("SELECT * FROM sensor");
            $s->execute();
            $s->setFetchMode(PDO::FETCH_ASSOC);
            $r = $s->fetchAll();
        }
        echo json_encode($r);
        break;
    case "POST":
        if (isset($_POST['tipo']) && isset($_POST['valor'])) {
            $fecha = date('y-m-d');
            $hora = date('h:i:s');

            $c = conexion();
            $s = $c->prepare("INSERT INTO sensor(user,tipo,valor,fecha, hora) VALUES(:u,:t,:v,:f, :h)");
            $s->bindValue(":u", $data['user']);
            $s->bindValue(":t", $_POST['tipo']);
            $s->bindValue(":v", $_POST['valor']);
            $s->bindValue(":f", $fecha);
            $s->bindValue(":h", $hora);
            $s->execute();
            if ($s->rowCount()) {
                $id = $c->lastInsertId();
                $r = array("add" => "y", "id" => $id);
            } else {
                $r = array("add" => "n");
            }
            header("HTTP/1.1 200 OK");
            echo json_encode($r);
        } else {
            header("HTT/1.1 400 Bad Request");
        }
        break;
    case "DELETE":
        if (isset($_GET['id'])) {
            $c = conexion();
            $s = $c->prepare("DELETE FROM sensor WHERE id = :id");
            $s->bindValue(":id", $_GET['id']);

            $s->execute();
            if ($s->rowCount()) {
                $r = array("del" => "y");
            } else {
                $r = array("del" => "n");
            }
            header("HTTP/1.1 200 OK");
            echo json_encode($r);
        } else {
            header("HTT/1.1 400 Bad Request");
        }
        break;
    default:
        header("HTT/1.1 400 Bad Request");
}
