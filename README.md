### About

Task Manager GUI application client for Task Manager web
server application

### Requirements

- installed JRE version 11 or later
- installed maven CLI application

### Usage

For first launch build project jar archive:

```bash
mvn package
```

To launch application use commands:

 - linux/unix:

```bash
bash task-manager.sh
```

 - windows:

```cmd
task-manager.cmd
```

### Configuration

For first launch application creates configuration file 'config.json'
in project folder. Change this file to configure connection to Task
Manager API server. Configuration file example:

```json
{
  "scheme": "https",
  "host": "localhost",
  "port": 8443,
  "secondsTimeout": 5,
  "allowRedirects": false,
  "allowSelfSigned": true
}
```
