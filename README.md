# Spring Post
This project was generated with [Spring Boot](https://spring.io/projects/spring-boot) version 2.4.0-SNAPSHOT

## Servlet Engine
> Apache Tomcat version 9.0.38 <br />
> Tomcat started on port(s): 8092

## Database
> Hibernate with MySQL database

## Briefly Descriptions
> Some main functions that Spring Post do

### Register
* Save user <br /> 												 
* Generate verification token <br />
### Send Account Activation Email
* Get token from url <br />
* Find token in database <br />
* Get user from token <br />
### Login
* Find user in database <br />
* Save authentication in security context <br />
* Generate JWT authenticated token <br />
* Wrap the token and send back to front end <br />
### Create New Post
* Pasrse and validate JWT token <br />
* Get current user (author) <br />
* Map post from container provided by front end
	to container provided by back end <br />
### Refresh Token
* Find refresh token in database <br />
* Generate new JWT authenticated token <br />
* Wrap the token and send back to front end <br />
### Logout
* Delete refresh token <br />
