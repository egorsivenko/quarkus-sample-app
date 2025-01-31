@param String action
@param String link

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation Email</title>
</head>
<body>
    <p>
        You received this email because of a ${action} attempt.
    </p>
    <p>
        If it wasn't you, then just ignore this email.
    </p>
    <p>
        To confirm your ${action}, simply click the following link: <a href="${link}">Confirm</a>
    </p>
    <p>
        This is an automated email, you should not reply to it: your mail will be ignored.
    </p>
</body>
</html>