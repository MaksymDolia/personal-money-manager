[![Build Status](https://travis-ci.com/MaksymDolia/personal-money-manager.svg?branch=master)](https://travis-ci.com/MaksymDolia/personal-money-manager)
[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=me.dolia.pmm%3Apersonal-money-manager&metric=alert_status)](https://sonarcloud.io/api/project_badges/measure?project=me.dolia.pmm%3Apersonal-money-manager&metric=alert_status)

# personal-money-manager
Personal money manager app that helps control budget and expenses.

## How to run (in development mode)
To run project in dev mode clone it and type:

```
mvn clean package verify jetty:run -Dspring.profiles.active="dev"
```

This command will start embedded Jetty server, port 8080. After you can access up through the link: http://localhost:8080/
