@echo off
TITLE GINsim
REM modify path here if needed
SET GINSIM_DIR=.

SET GINSIM_CLASSPATH=.
FOR %%F IN (lib\*.jar plugins\*.jar) DO CALL makeclasspath.bat %%F


java -cp %GINSIM_CLASSPATH% fr.univmrs.tagc.GINsim.global.GsMain --ginsimdir %GINSIM_DIR% %* >data\log.txt

rem For high mem support...: comment the above line and uncomment/adapt the following one
rem java -Xmx300M -cp %GINSIM_CLASSPATH% fr.univmrs.tagc.GINsim.global.GsMain --ginsimdir %GINSIM_DIR% %* >data\log.txt
