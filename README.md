# Thoughts : Blog Application

Welcome to the **Thoughts Blog Application**! 

# Environment variable setting guide
- Create `.env` file and define these properties as directed
```shell
MYSQL_DATABASE_NAME=thoughts
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_USER=thoughts_app
MYSQL_PASSWORD=pass
MYSQL_URI="jdbc:mysql://localhost:3306"

SECRET_KEY=<32 byte secter key base64 encoded command to generate random 32 byte secret given in next code block>
SENDGRID_MAIL_API_KEY=<sendgrid_api_key>
SENDGRID_SENDER_MAIL=<sendgrid_verified_sender_email>
SENDGRID_SENDER_NAME="admin"
```

- Generate `32 bytes` long secret key for environment variable
```shell
openssl rand -base64 32 
```