- Zum Installieren wird Composer benötigt -> siehe http://getcomposer.org/download
  Nach Installation in diesem Verzeichnis das Kommando "php composer.phar install"
  ausführen -> benötigte Dependencies werden heruntergeladen!

- Server benötigt PHP.ini-Einstellung "allow_url_fopen" auf "On"
  Außerdem wird ein PHP-SSL-Modul (z.B.: OpenSSL) benötigt, damit Google API-Calls
  über SSL erfolgreich durchgeführt werden können.

- Der verwendete Google API-Key (siehe resources/config/prod.php) funktioniert
  nur auf freigeschalteten IPs, d.h. nur auf meinen Server.
  
- Livedemo: http://drivebyreminder.truthfactory.tk/
  Passende Service-URLs: Siehe definierte URL-Patterns in der Datei src/controllers.php
