<?php

$con = mysqli_connect("localhost", "root", "root", "db_tree_store");

if (mysqli_connect_errno()) {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

if (isset($_GET['apis'])) {

    if ($_GET['apis'] == 'payments_detail') {

        $response = [];
        $strCmd = "
                SELECT *, goods.picture AS goods_picture, payment.id AS payment_id, payment.picture AS payment_picture FROM payment 
                LEFT JOIN goods ON goods.id = payment.goods_id
                WHERE payment.id = '{$_GET['id']}' 
                ORDER BY payment.id DESC
        ";
        $result = $con->query($strCmd);
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }
        if ($result->num_rows != 0) {
            $response['data'] = $data[0];
            $response['code'] = 200;
            $response['message'] = "OK";
        } else {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'payments') {

        $response = [];
        $strCmd = "SELECT *, goods.picture AS goods_picture, payment.id AS payment_id, payment.picture AS payment_picture FROM payment 
                LEFT JOIN goods ON goods.id = payment.goods_id 
        ";
        if ($_GET['user_id'] == 'all') {
            $strCmd .= "ORDER BY payment.id DESC";
        } else {
            $strCmd .= "WHERE payment.user_id = '{$_GET['user_id']}' 
                ORDER BY payment.id DESC";
        }
        $result = $con->query($strCmd);
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }
        if ($result->num_rows != 0) {
            $response['data'] = $data;
            $response['code'] = 200;
            $response['message'] = "OK";
        } else {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'confirmPayment') {

        $response = [];
        $strCmd = "UPDATE payment SET status = '2'";
        $strCmd .= " WHERE id = '{$_GET['id']}'";
        $result = $con->query($strCmd);
        if ($result) {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        } else {
            $response['data'] = [];
            $response['code'] = 404;
            $response['message'] = "NOT_FOUND";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'login') {

        $response = [];

        $strIsUser = "SELECT * FROM users WHERE username = '{$_POST['username']}' AND password = '{$_POST['password']}'";
        $result = $con->query($strIsUser);
        if ($result->num_rows != 0) {
            $response['data'] = $result->fetch_assoc();
            $response['code'] = 200;
            $response['message'] = "OK";
        } else {
            $response['data'] = [];
            $response['code'] = 404;
            $response['message'] = "NOT_FOUND";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'register') {


        $response = [];

        $strIsUser = "SELECT * FROM users WHERE username = '{$_POST['username']}'";
        $result = $con->query($strIsUser);
        if ($result->num_rows == 0) {
            $strCmd = "
                INSERT INTO users 
                (username, password, name, tel, facebook, picture, status) VALUES(
                    '{$_POST['username']}', 
                    '{$_POST['password']}', 
                    '{$_POST['name']}', 
                    '{$_POST['tel']}',
                    '{$_POST['facebook']}',
                    '1.jpg', 
                    '1'
                )
            ";
            $result = $con->query($strCmd);
            if ($result) {
                $response['data'] = [];
                $response['code'] = 200;
                $response['message'] = "OK";
            }
        } else {
            $response['data'] = [];
            $response['code'] = 201;
            $response['message'] = "USERED";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'goods') {

        $response = [];
        $strCmd = "SELECT * FROM goods ORDER BY id DESC";
        $result = $con->query($strCmd);
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }
        if ($result->num_rows != 0) {
            $response['data'] = $data;
            $response['code'] = 200;
            $response['message'] = "OK";
        } else {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'postPayment') {

        move_uploaded_file($_FILES["upload"]["tmp_name"], "uploads/" . $_FILES["upload"]["name"]);

        $response = [];
        $strCmd = "INSERT INTO payment 
                (goods_id, user_id, amount, picture, date_time, status) VALUES(
                    '{$_POST['goodsId']}', 
                    '{$_POST['userId']}', 
                    '{$_POST['amount']}', 
                    '{$_FILES['upload']['name']}',
                    '{$_POST['datetime']}',
                    '{$_POST['status']}'
                )
        ";
        $result = $con->query($strCmd);
        if ($result) {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'addGoods') {

        move_uploaded_file($_FILES["upload"]["tmp_name"], "uploads/" . $_FILES["upload"]["name"]);

        $response = [];
        $strCmd = "INSERT INTO goods
                (name, detail, price, picture,contact) VALUES(
                    '{$_POST['name']}',
                    '{$_POST['detail']}',
                    '{$_POST['price']}',
                    '{$_FILES['upload']['name']}'
                )
        ";
        $result = $con->query($strCmd);
        if ($result) {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'editGoods') {

        if ($_FILES["upload"]["name"]) {
            move_uploaded_file($_FILES["upload"]["tmp_name"], "uploads/" . $_FILES["upload"]["name"]);
        }

        $response = [];
        $strCmd = "UPDATE goods SET 
                 name = '{$_POST['name']}', 
                 detail = '{$_POST['detail']}', 
                 price = '{$_POST['price']}'
        ";
        if ($_FILES["upload"]["name"]) {
            $strCmd .= ", picture = '{$_FILES['upload']['name']}'";
        }
        $strCmd .= "WHERE id = '{$_POST['id']}'";
        $result = $con->query($strCmd);
        if ($result) {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }

        echo json_encode($response);

    }

    if ($_GET['apis'] == 'deleteGoods') {
        $response = [];
        $strCmd = "DELETE FROM goods";
        $strCmd .= " WHERE id = '{$_GET['id']}'";
        $result = $con->query($strCmd);
        if ($result) {
            $response['data'] = [];
            $response['code'] = 200;
            $response['message'] = "OK";
        }
        echo json_encode($response);
    }

} else {
    $response['data'] = [];
    $response['code'] = 404;
    $response['message'] = "Not found";
    echo json_encode($response);
}


