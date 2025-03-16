@import gg.jte.Content

@param String title
@param Content styles = null
@param Content scripts = null
@param Content body

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="turbo-refresh-method" content="morph">
    <meta name="turbo-refresh-scroll" content="preserve">
    <title>${title}</title>

    <link rel="shortcut icon" href="https://picocss.com/favicon.ico"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"/>
    <link rel="stylesheet" href="/css/styles.css"/>
    ${styles}

    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
    ${scripts}
</head>
<body>
    ${body}
</body>
</html>