<!DOCTYPE html>
<html>
<head lang="pl">
    <meta charset="UTF-8">
    <title></title>
</head>
<body>

<?php require_once('./main.php'); ?>


<div>
    <h1>Header</h1>
    <span><?php print_r($a); ?></span>
    <span>
        <h1>Json Encoded</h1>
        <?php echo json_encode($a); ?>
    </span>
</div>

</body>
</html>