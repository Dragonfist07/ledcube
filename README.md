# ledcube

## Raspeberry Pi einrichten:
Auf dem Pi muss Raspian laufen und eine funktionierende Netzwerkverbindung aufgebaut sein

### Update Raspian
Raspian auf den neusten Stand bringen. Die Reihenfolge der Befehle ist wichtig!
```
sudo apt-get upgrade
sudo apt-get update
```

### Java Installation
Installiert Java 8 Development Kit von Orcale.
```
sudo apt-get install oracle-java8-jdk
```
Nach erfolgreicher Installation sollte die Version mit folgendem Befehl angezeigt werden
```
java -version
```

### pi4j Installation
Installiert die nötigen Bibliotheken um GPIOs aus Java anzusprechen
http://pi4j.com/#
```
wget http://get.pi4j.com/download/pi4j-1.2-SNAPSHOT.deb
sudo dpkg -i pi4j-1.2-SNAPSHOT.deb
rm pi4j-1.2-SNAPSHOT.deb
```
Nach erfolgreicher Installation sollte die Version mit folgendem Befehl angezeigt werden
```
pi4j -v
```
