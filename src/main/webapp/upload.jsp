<html>
<head>
    <title>File Uploading Form</title>
</head>
<body>
<h3>File Upload:</h3>
Select a file to upload: <br />
<form action="uploadfile" method="post"
      enctype="multipart/form-data">
    <input type="text" name="title" />
    <input type="description" name="title" />
    <input type="lat" name="title" />
    <input type="lon" name="title" />
    <input type="file" name="file" size="50" />
    <br />
    <input type="submit" value="Upload File" />
</form>
</body>
</html>
