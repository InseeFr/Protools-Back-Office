# Protools POC - Flowable Engine 🦊

[Demo Link 😉](https://protools.dev.insee.io/)

This POC was build using the Flowable Engine. This branch is dedicated to various experiments and tests.

Currently testing event-egistry features with RabbitQM for PQV (Proto Qualité Volaille)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Installation

[Link to swagger](https://protools-flowable-demo.dev.insee.io/)

### Manual Install

```bash
docker pull docker pull inseefr/protools-back-office
docker run -d --name protoolsflowable -p 8080:8080  docker pull inseefr/protools-back-office:experimental
```

### Manual Install

```bash
git clone git@github.com:InseeFr/Protools-Back-Office.git
cd Protools-Back-Office
git checkout event-registry
./mvnw spring-boot:run -Dmaven.test.skip
```
