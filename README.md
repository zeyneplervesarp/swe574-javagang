# Software Development Practice
This is a repository for the SWE 574 Software Development as a Team class.

## Deployed Application
The application is deployed and can be viewed at URL's below.

Application URL: [http://34.88.198.59:8080/#/](http://34.88.198.59:8080/#/)

Backend Rest API URL: [http://34.88.198.59:8081/#/](http://34.88.198.59:8081/#/)

## Installation
To run the system, either the repo must first be cloned to the local machine or the release version should be downloaded. 

### System Requirements

SocialHub is developed using Spring Boot and Java 11.

The deployment is done using Docker 20.10.11

Java 11  can be downloaded [here](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).

To properly install all the dependencies and create the environment of the application, the host system must have maven installed. Apache Maven 4.0.0 has been used in the development of SocialHub.

### Git Clone
* Open Git Bash
* Go to the directory of where the files will be installed
* Enter command  `git clone https://github.com/zeyneplervesarp/swe574-javagang.git`
* Press enter

### Release download
* Go to [https://github.com/zeyneplervesarp/swe574-javagang/releases/tag/v0.9](https://github.com/zeyneplervesarp/swe574-javagang/releases/tag/v0.9)
* Go to the assets tab
* Click on one of the release files provided
* Extract the downloaded file to your computer

### Docker Compose 
After successfully getting the code, docker-compose can be run to deploy the system on docker. The steps to deploy on docker are as follows:
* Open the terminal in the host machine and go to the root of the project folder.
* Go to the backend folder on your terminal
*  ` cd swe574-javagang/backend` if you've cloned the repo
*  `cd backend` if you've downloaded the release
* Enter the command ` docker-compose up --build`

After following all the steps, the system should successfully run on your computer. Frontend application that is visible to users will be at [localhost:8080](http://localhost:8080/#/).

## Documentation
Detailed documentation can be found at the [wiki](https://github.com/zeyneplervesarp/swe574-javagang/wiki)
 page
