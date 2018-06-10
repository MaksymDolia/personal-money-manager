# personal-money-manager
Personal money manager app that helps control budget and expenses.

## How to run (in development mode)
To run project in dev mode clone it and type:

```
mvn clean package verify jetty:run -Dspring.profiles.active="dev"
```

This command will start embedded Jetty server, port 8080. After you can access up through the link: http://localhost:8080/
