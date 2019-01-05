### Repository auf dem Pi aktualisieren
```
sudo git pull
```

### Java Programm Compilieren
```
sudo pi4j --compile [SourceName].java
```

### Java Programm Ausführen
```
sudo pi4j --run [ProgrammName]
```

### Tipp
Zum schnellen testen alle befehle auf einmal ausf�hren: z.B.:
```
sudo git pull; sudo pi4j --compile TimedAnimationTest.java; sudo pi4j --run TimedAnimationTest
```