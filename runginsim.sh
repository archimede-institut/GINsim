#!/bin/sh

# change the following line to set java used memory (in Mb)
JAVAMAXMEM=300

# find the dir in which GINsim is installed
GINSIM_DIR=`dirname $0`

# uncomment this to write log message to a file instead of stdout
#LOGFILE="data/log.txt"

# set the classpath to load all required library and plugins
GINSIM_CLASSPATH="$GINSIM_DIR"
for i in `ls $GINSIM_DIR/lib/* $GINSIM_DIR/plugins/*`
do
	GINSIM_CLASSPATH="$GINSIM_CLASSPATH:$i"
done

GsArgs="-cp $GINSIM_CLASSPATH fr.univmrs.ibdm.GINsim.global.GsMain --ginsimdir $GINSIM_DIR $*"
test $JAVAMAXMEM && GsArgs="-Xmx"$JAVAMAXMEM"M $GsArgs"

test $LOGFILE && java $GsArgs > $LOGFILE 2>&1 || java $GsArgs
