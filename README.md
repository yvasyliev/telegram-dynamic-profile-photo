![Build status](https://github.com/yvasyliev/telegram-deezer-client/actions/workflows/build-maven-project.yml/badge.svg?branch=main)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=bugs)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=yvasyliev_telegram-deezer-client&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=yvasyliev_telegram-deezer-client)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/yvasyliev/telegram-deezer-client/blob/main/LICENSE)

# Telegram Dynamic Profile Photo

A custom Telegram client that displays [Deezer](https://deezer.com)'s last listened track on profile photo.

## Dependencies

This project uses [TDLight Java](https://github.com/tdlight-team/tdlight-java) as implementation of Telegram Client API.

Also, [Deezer API Java Library](https://github.com/yvasyliev/deezer-api) is used to communicate with Deezer.

## Requirements

1. JDK 8 (or higher)
2. Maven

## Quickstart

1. Download `telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar` from the
   last [Release](https://github.com/yvasyliev/telegram-dynamic-profile-photo/releases).
2. Create `app.properties` file in the same folder
   as `telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar`.
   ```shell
   touch app.properties
   ```
3. Open `app.properties` and add the following content:
   ```properties
   #To get telegram.api_hash see https://core.telegram.org/api/obtaining_api_id
   telegram.api_hash=your_api_hash
   #To get telegram.api_id see https://core.telegram.org/api/obtaining_api_id
   telegram.api_id=XXXXXXX
   #To get deezer.app_id see https://developers.deezer.com/myapps
   deezer.app_id=XXXXXX
   #deezer.redirect_uri must be the same as 'Application domain' in https://developers.deezer.com/myapps
   deezer.redirect_uri=https://google.com
   #To get deezer.secret see https://developers.deezer.com/myapps
   deezer.secret=deezer_app_secret_key
   ```
4. Login into Deezer.
   ```shell
   java -jar telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar deezer.login
   ```
5. Login into Telegram client.
   ```shell
   java -jar telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar telegram.login
   ```
6. Update profile photo.
   ```shell
   java -jar telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar telegram.change_photo
   ```
7. Logout from Telegram. `(Optional)`
   ```shell
   java -jar telegram-dynamic-profile-photo-${version}-jar-with-dependencies.jar telegram.logout
   ```
   