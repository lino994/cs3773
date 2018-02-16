# cs3773-android-secure-messaging-app
Created for : Software Engineering Course
Created on : Fall 2016

This is a simple Android messging application that applies different type of encryptions to the messages being sent
as chosen by the user.

Prerequisites:
  - Requires missing PHP files (where lost at some point will add if found)
  - Requires a server to execute PHP files for sending and recieveing of messages
  - User's with an Android Phone to install application on
  
Features:
  - User's can only be added by the System Administrator role which generates a password to be given to the user.
  - Messages are set to a timer and are deleted after 5 minutes from being read (nothing is stored on the server)
  - User's can decide if they want a Pattern based encryption or keyword based encryption use SHA256 encryption methods 
    sha256 obtained from: https://github.com/simbiose/Encryption
  - User's need to set security questions to be able to change their password later.
  
Design Decisions & Problems:
  - Due to lack of time, the UI is mostly just functional and isn't really up to par with what it should be.
  - Some comments detailling errors to be fixed where never removed. 
 
 
 Created with:
  - Java using Android Studio as the IDE. 

