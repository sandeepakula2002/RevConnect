# AWS Deployment

This project can be deployed on AWS with:

- Backend on EC2: `54.146.222.104`
- Database on Amazon RDS MySQL: `revconnectdb.ceh62g2e4ks8.us-east-1.rds.amazonaws.com`
- Frontend on S3 static hosting: `revconnect-frontend`

## Architecture

- Angular frontend is built locally and uploaded to the S3 bucket.
- Spring Boot backend runs on the EC2 instance in Docker.
- MySQL runs on Amazon RDS.

## 1. Backend configuration

The backend already supports environment-variable overrides in [application.properties](/d:/projects%20of%20revature/RevConnect-p2/backend/src/main/resources/application.properties).

Create an AWS env file on the EC2 server:

```bash
cp .env.aws.example .env.aws
```

Then update:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://revconnectdb.ceh62g2e4ks8.us-east-1.rds.amazonaws.com:3306/revconnect?useSSL=true&requireSSL=true&sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=your-real-rds-password
APP_JWT_SECRET=your-long-random-production-secret
APP_CORS_ALLOWED_ORIGINS=http://revconnect-frontend.s3-website-us-east-1.amazonaws.com,http://54.146.222.104:4200
```

## 2. Build the backend jar

Run this before deploying:

```bash
cd backend
mvn clean package -DskipTests
```

## 3. Deploy backend to EC2

Copy the repo to the EC2 instance, then from the project root run:

```bash
docker compose -f docker-compose.aws.yml up -d --build
```

To check logs:

```bash
docker compose -f docker-compose.aws.yml logs -f backend
```

The backend will be available at:

```text
http://54.146.222.104:8080/api
```

## 4. Frontend on S3

The production frontend already points to:

```text
http://54.146.222.104:8080/api
```

Build and upload the Angular app:

```bash
cd frontend
npm install
npm run build
```

Upload the contents of `frontend/dist/frontend/browser/` if that folder exists, otherwise upload `frontend/dist/frontend/` to the `revconnect-frontend` bucket.

## 5. AWS security groups

Make sure these inbound rules exist:

- EC2 security group: allow `22` from your IP for SSH
- EC2 security group: allow `8080` from `0.0.0.0/0` or from your frontend/load balancer
- RDS security group: allow `3306` from the EC2 security group
- S3 bucket: enable static website hosting and allow public read for frontend assets

## 6. RDS SSL note

Your MySQL CLI command uses the AWS CA bundle:

```bash
mysql -h revconnectdb.ceh62g2e4ks8.us-east-1.rds.amazonaws.com -P 3306 -u admin -p'<Enter_DB_Password>' --ssl-verify-server-cert --ssl-ca=/certs/global-bundle.pem mysql
```

That is valid for manual MySQL client access. For the Spring Boot app, this repo uses the JDBC URL from `.env.aws`. If you want strict CA or hostname verification in Java too, import the AWS RDS global bundle into the JVM trust store on the EC2 host and switch the JDBC URL to `sslMode=VERIFY_IDENTITY`.

## 7. Suggested EC2 deploy flow

```bash
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -aG docker ec2-user
```

Then install Docker Compose plugin if it is not already present, reconnect your SSH session, and run the backend compose file.
