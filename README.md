# Trailiva-backend
Trailiva is a project management tool that you may use to keep track of your personal, professional, and open source projects.

## Technologies
- Springboot (Framework used to develop the APIs)
- Postgresql (Database for datastorage)
- Sendgrid (SMTP server for mail sending)
- Cloudinary (File server for user pictures and videos)
- Jwt (Library for authentication)

 How To Run The Project
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






