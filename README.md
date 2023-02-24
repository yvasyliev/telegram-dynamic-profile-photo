![Build status](https://github.com/yvasyliev/telegram-deezer-client/actions/workflows/build-maven-project.yml/badge.svg?branch=main)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/yvasyliev/telegram-deezer-client/blob/main/LICENSE)
# Telegram Dynamic Profile Photo
A custom Telegram client that displays [Deezer](https://deezer.com)'s last listened track on profile photo.

## Dependencies
This project uses [TDLight Java](https://github.com/tdlight-team/tdlight-java) as implementation of Telegram Client API.<br/>
Also [Deezer API Java Library](https://github.com/yvasyliev/deezer-api) is used to communicate with Deezer.
## Requirements
1. JDK 8 (or higher)
2. Maven

## Quickstart
1. Clone this project.<br/>
   `git clone https://github.com/yvasyliev/telegram-dynamic-profile-photo.git`
2. Build the application.<br/>
   `mvn clean package`
3. Find in `target` folder a `telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar` file.<br/>
4. Create `app.properties` file in the same folder as `telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar`.<br/>
5. Put the following content into `app.properties` file:<br/>
```properties
#To get telegram.api_hash see https://core.telegram.org/api/obtaining_api_id
telegram.api_hash=your_api_hash
#To get telegram.api_id see https://core.telegram.org/api/obtaining_api_id
telegram.api_id=XXXXXXX
#Your phone number
telegram.phone_number=+XXXXXXXXXXXX
#To get deezer.app_id see https://developers.deezer.com/myapps
deezer.app_id=XXXXXX
#deezer.redirect_uri must be the same as 'Application domain' in https://developers.deezer.com/myapps
deezer.redirect_uri=https://your.domain.com
#To get deezer.secret see https://developers.deezer.com/myapps
deezer.secret=deezer_app_secret_key
```
6. Login to Deezer. You must follow the link which will be printed to console and accept app permissions.<br/>
   `java -jar telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar deezer.login`
7. Login to your custom Telegram client. You will be prompted to enter authentication code.<br/>
   `java -jar telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar telegram.login`
8. Update your profile photo.<br/>
   `java -jar telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar telegram.change_photo`
9. (Optional) Logout from the client.<br/>
   `java -jar telegram-dynamic-profile-photo-x.y.z-jar-with-dependencies.jar telegram.logout`