- Zum Installieren wird Composer ben�tigt -> siehe http://getcomposer.org/download
  Nach Installation in diesem Verzeichnis das Kommando "php composer.phar install"
  ausf�hren -> ben�tigte Dependencies werden heruntergeladen!

- Server ben�tigt PHP.ini-Einstellung "allow_url_fopen" auf "On"
  Au�erdem wird ein PHP-SSL-Modul (z.B.: OpenSSL) ben�tigt, damit Google API-Calls
  �ber SSL erfolgreich durchgef�hrt werden k�nnen.

- Der verwendete Google API-Key (siehe resources/config/prod.php) funktioniert
  nur auf freigeschalteten IPs, d.h. nur auf meinen Server.
  
- Livedemo: http://drivebyreminder.truthfactory.tk/
  Passende Service-URLs: Siehe definierte URL-Patterns in der Datei src/controllers.php
