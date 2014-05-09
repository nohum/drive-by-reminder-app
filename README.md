drive-by-reminder-app
=====================

An Android application designed to remind you of tasks if you are nearby a user-defined 
location.

This application has been developed for a university project. All the geo-location 
calculation for user notifications is done by hand. As Google did present the geofencing 
feature at I/O 2013, the application is not using up-to-date (and possibly battery-saving) 
technology anymore. Nevertheless, the application still works as usual.

Usage
-----

To use the server application inside of the Android application, open up the class 
`at.fhj.itm10.mobcomp.drivebyreminder.helper.DownloadLocationDataAsyncTask` and change the 
`API_LOCATION_MORE` constant.
