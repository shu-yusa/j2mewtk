#!/bin/sh -e
#
# This batch file builds and preverifies the code for the demos.
# it then packages them in a JAR file appropriately.
#
DEMO=$1
LIB_DIR=/home/yusa/usr/WTK2.5.2/lib
CLDCAPI=${LIB_DIR}/cldcapi11.jar
MIDPAPI=${LIB_DIR}/midpapi21.jar
PREVERIFY=/home/yusa/usr/WTK2.5.2/bin/preverify1.1

PATHSEP=":"

JAVAC=javac
JAR=jar

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

#
# Make possible to run this script from any directory'`
#
cd `dirname $0`

echo "Creating directories..."
mkdir -p ../tmpclasses
mkdir -p ../classes

echo "Compiling source files..."

${JAVAC} -bootclasspath ${CLDCAPI}${PATHSEP}${MIDPAPI} \
    -J-Dfile.encoding=UTF8 \
    -source 1.3 \
    -target 1.3 \
    -d ../tmpclasses \
    -classpath ../tmpclasses \
    `find ../src -name '*'.java`

echo "Preverifying class files..."

${PREVERIFY} \
    -classpath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}../tmpclasses \
    -d ../classes \
    ../tmpclasses

echo "Jaring preverified class files..."
${JAR} cmf MANIFEST.MF ${DEMO}.jar -C ../classes .

if [ -d ../res ] ; then
  ${JAR} uf ${DEMO}.jar -C ../res .
fi

echo
echo "Don't forget to update the JAR file size in the JAD file!!!"
echo
