### Repository auf dem Pi aktualisieren
```
sudo git pull
```

### Java Programm Compilieren
```
sudo pi4j --compile [SourceName].java
```

### Java Programm AusfÃ¼hren
```
sudo pi4j --run [ProgrammName]
```

### Tipp
Zum schnellen testen alle befehle auf einmal ausführen: z.B.:
```
sudo git pull; sudo pi4j --compile TimedAnimationTest.java; sudo pi4j --run TimedAnimationTest
```