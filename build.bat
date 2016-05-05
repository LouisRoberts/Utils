mkdir bin
cd bin
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar -sourcepath ..\src  ..\src\org\lorob\exception\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/j2ee.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/common/poi-3.0.1-FINAL-20070705.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/j2ee.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/common/poi-3.0.1-FINAL-20070705.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\excel\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\config\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\gui\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\report\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;c:/eclipseworkspace/Common/com.ibm.mq-5.3.jar;c:/eclipseworkspace/Common/crimson.jar;c:/eclipseworkspace/Common/jaxp.jar;c:/eclipseworkspace/Common/xalan.jar;c:/eclipseworkspace/Common/ftpbean.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\org\lorob\sql\*.java -d .
C:\j2sdk1.4.2_06\bin\javac -deprecation -classpath  c:/eclipseworkspace/common/commons-lang-2.1.jar;C:/eclipseworkspace/Utils/bin  -sourcepath ..\src ..\src\uk\co\rc\gui\*.java -d .
C:\j2sdk1.4.2_06\bin\jar -cvf0 lorobutils.jar org uk
xcopy lorobutils.jar .. /R /Y
xcopy lorobutils.jar ..\..\common /R /Y
xcopy lorobutils.jar ..\..\dist\common /R /Y
del lorobutils.jar
cd..
pause