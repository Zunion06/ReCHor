# ReCHor 

Semester project for the **CS-108 – Object-Oriented Programming (Pratique de la programmation orientée-objet)** course at **EPFL**.

## Description

A Java application similar to the SBB itinerary planning app.

(SBB is the Swiss public transportation company for trains, buses, and other transit services.)

## ReCHor Web Server

### Deployment

*(Status as of 13/10/2026)*

The application is not deployed anymore.

## Authors

- Matthias Gibon
- Guillaume Pommier

## Timetable

Due to their large size, the main timetable files are not included in this repository.

However you can download them at https://cs108.epfl.ch/archive/25/p/

## Utilisation

Run the javaFX application: 
```bash
mvn javafx:run -pl rechor-desktop
```

Run the HTTP server:
```bash
mvn spring-boot:run -pl rechor-server
```
