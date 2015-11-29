# lzgg
## Brief Explanation of the Idea

We are making a web application which stores both URLs and the annotations added to the web page. We are going to use cloud platform to store the annotations to support large amount of data and also provide high extensibility for more future functions.


## Idea: 
Making the entire web editable by annotations: 	
Take notes on a web page, save them in cloud, share with our friends, where notes can be text, picture, videos etc.

Platform: Web application, also possible to make it a mobile (Android, iOS) app.

Data Storage: AWS RDS or DynamoDB

Functionalities: 
Add comments to web pages.
Add Friends/Share the comments with friends/See friends’ comments.
Take a screenshot of the webpage and write comments/draw shapes directly on the screenshot. 
Making the entire web editable by annotations		

1. Platform: 
Android application.
2. Function:  
Users can create lots of events, topics, groups and create, get access to and shared notes (text, pictures, audio and video etc.) within the events, topics, groups. Users can 
follow other the events, topics and groups created by other users.
3. Technology: 
  User-Topic relation is stored in Cloud.
	Large amounts of picture, audio, video are stored in Cloud.
	Client-Cache.
	Message-Notification.

## Stretch goals:
In the app, we can create a user by username, password and verified email (using SES).
people can use GPS to find some interesting topics nearby and add into like list and comment on it. People can also create topics (which can be commented by others). 
people can add friends with others and see what topic their friends are following.
In the recommendation button, we can recommend topics to users according to what users like and geolocation.
