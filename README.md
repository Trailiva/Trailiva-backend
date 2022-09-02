# Trailiva-backend
Trailiva is a project management tool that you may use to keep track of your personal, professional, and open source projects.

## Technologies
- Springboot (Framework used to develop the APIs)
- Postgresql (Database for datastorage)
- Sendgrid (SMTP server for mail sending)
- Cloudinary (File server for user pictures and videos)
- Jwt (Library for authentication)

 ## How To Run The Project
1. Clone project
2. create a database named `trailiva` on PGAdmin or any PostgreSql platform
3. check out to dev with command `git checkout dev`
4. Add this configuration to `application.properties file`:
```
    server.port=9000
    **------ Postgresql configuration ------**
    spring.datasource.url=jdbc:postgresql://localhost:5432/trailiva
    spring.datasource.username=enter_db_username
    spring.datasource.password=enter_db_password
    
    **------ Hibernate configuration -----**
    spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.hibernate.ddl-auto=create
    
     **------ Cloudinary config ------**
    CLOUD_NAME= `enter_cloudinary_name`
    API_KEY= enter `cloudinary_api_key`
    API_SECRET= `enter_cloudinary_secret_key`
```
5. Run project with command `mvn spring-boot:run`

# Requirements

## Functional
- Users can sign up, sign in, verify account, refresh authentication token, forget password, reset password and update profile picture.
- Users can add contributors to workspace
- Users can add moderators to workspace
- Users can create project
- Users can create task
- Contributor can work on task, request for task
- Moderator can assign task to contributor, create task, review task


## Non-Functional
- Application should be secure
- The application should be available

## API Design
[![view - Documentation](https://img.shields.io/badge/view-Documentation-blue?style=for-the-badge)](https://documenter.postman.com/preview/18385063-ff9175c4-8d85-48b4-9e61-ad09e1f4a4d8?environment=18385063-ffc454ad-94dc-4063-9942-93254a215c9f&versionTag=latest&apiName=CURRENT&version=latest&documentationLayout=classic-single-column&right-sidebar=303030&top-bar=FFFFFF&highlight=EF5B25)

## Badges
_Social buttons_

[![Whalewalker - trailiva](https://img.shields.io/static/v1?label=Whalewalker&message=trailiva&color=blue&logo=github)](https://github.com/Whalewalker/trailiva "Go to GitHub repo")
[![stars - trailiva](https://img.shields.io/github/stars/Whalewalker/trailiva?style=social)](https://github.com/Whalewalker/trailiva)
[![forks - trailiva](https://img.shields.io/github/forks/Whalewalker/trailiva?style=social)](https://github.com/Whalewalker/trailiva)


_Repo metadata_


[![GitHub tag](https://img.shields.io/github/tag/Whalewalker/trailiva?include_prereleases=&sort=semver&color=blue)](https://github.com/Whalewalker/trailiva/releases/)
[![License](https://img.shields.io/badge/License-MIT-blue)](#license)
[![issues - trailiva](https://img.shields.io/github/issues/Whalewalker/trailiva)](https://github.com/Whalewalker/trailiva/issues)

## License

Released under [MIT](/LICENSE) by [@Whalewalker](https://github.com/Whalewalker).




