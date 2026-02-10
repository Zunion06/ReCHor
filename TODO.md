# Server

## Fix

Find a way to access the timetable ressources.

No tests when `mvn test` inside rechor-server

## Feature

Implement security measures:
- Rate Limiting
- Defense against "Low-and-Slow" attack
- Defense against Cross-Site Scripting (XSS)

Aunthentification method to practice. Possibly OAuth2 with CFF if have what it takes, or with Google otherwise. You can think about what features to add when there will be authentification.

Create a service that creates an Icalendar file upon request.

Create a service that given a Journey redirect (perhaps there is a better option) to the url with a map.

## Research

Find out why Router is ~3 times slower when executing spring-boot than javaFX. 

# ReCHor

## Improvement

Improve the performance of Router.java as it is the bottleneck for every aspect of the app.

And perhaps improve JourneyExtractor after, but it is of less importance.

## Fix

Find out why we have nonsensical journeys like Paris <-> Lausanne that only takes 1h and 40min. AND FIX THIS NONSENS 

Fix the changement of date in JavaFX (I think it did not work).

There is a problem with StopField in JavaFX, perhaps a linux thing. Needs fixing.

## Feature

Find how to add / create the Relevant Data for an other date.

If you find how to gather a large amount of data, condider using a database for storage (PostgreSQL is a good idea).