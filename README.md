# Software Development Practice
This is a repository for the SWE 574 Software Development as Practice class.

## Deployed Application
The application is deployed and can be viewed at URL's below.

Application URL: http://34.125.127.109:8080/

Backend Rest API URL: http://34.125.127.109:8081/

## Installation
To run the system, either the repo must first be cloned to the local machine or the release version should be downloaded. 

### Git Clone
* Open Git Bash
* Go to the directory of where the files will be installed
* Enter command  `git clone https://github.com/ecesari/software-development-practice.git`
* Press enter

### Release download
* Go to https://github.com/ecesari/software-development-practice/releases/tag/v.09
* Go to the assets tab
* Click on one of the release files provided
* Extract the downloaded file to your computer

### Docker Compose 
After successfully getting the code, docker-compose can be run to deploy the system on docker. The steps to deploy on docker are as follows:
* Go to the backend folder on your terminal
*  `cd software-development-practice/backend` if you've cloned the repo
*  `cd backend` if you've downloaded the release
* Enter the command `docker-compose up –-build`

After following all the steps, the system should successfully run on your computer. Frontend application that is visible to users will be at port 8080 and the backend api will be at port 8081 

## Documentation
Detailed documentation can be found at the [wiki](https://github.com/ecesari/software-development-practice/wiki)
 page
