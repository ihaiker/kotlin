#!/usr/bin/env bash

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME=
SERVER_PORT=
LOGS_FILE=

if [ -x "$DEPLOY_DIR/bin/env.sh" ]; then
    source $DEPLOY_DIR/bin/env.sh
fi

if [ ! -n "$SERVER_NAME" ]; then
    SERVER_NAME=`basename $DEPLOY_DIR`
fi

LOGS_DIR=""
if [ -n "$LOGS_FILE" ]; then
    LOGS_DIR=`dirname $LOGS_FILE`
else
    LOGS_DIR=$DEPLOY_DIR/logs
fi
if [ ! -d $LOGS_DIR ]; then
    mkdir -p $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

start(){
    PIDS=`ps -f | grep java | grep "$CONF_DIR" |awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo "ERROR: The $SERVER_NAME already started!"
        echo "PID: $PIDS"
        exit 1
    fi

    if [ -n "$SERVER_PORT" ]; then
        SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
        if [ $SERVER_PORT_COUNT -gt 0 ]; then
            echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
            exit 1
        fi
    fi

    LIB_DIR=$DEPLOY_DIR/lib
    LIB_JARS=`ls $LIB_DIR | grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

    JAVA_OPTS="$JAVA_OPTS"
    BOOT_OPTS="$BOOT_OPTS"
    if [ -f "$CONF_DIR/java.options" ]; then
        JAVA_OPTS=`cat $CONF_DIR/java.options | tr "\n" " "`
    else
        JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
        JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
        JAVA_OPTS="$JAVA_OPTS -server"
        JAVA_OPTS="$JAVA_OPTS -Xmx512m"
        JAVA_OPTS="$JAVA_OPTS -Xms512m"
        JAVA_OPTS="$JAVA_OPTS -Xmn256m"
        JAVA_OPTS="$JAVA_OPTS -Xss256k"
        JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
        JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
        JAVA_OPTS="$JAVA_OPTS -XX:LargePageSizeInBytes=128m"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
        JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=70"
    fi

    echo -e "Starting the $SERVER_NAME ...\c"
    nohup $JAVACMD $JAVA_OPTS -classpath $CONF_DIR:$LIB_JARS [MAIN_CLASS] $BOOT_OPTS > $STDOUT_FILE 2>&1 &

    COUNT=0
    while [ $COUNT -lt 100 ]; do
        echo -e ".\c"
        sleep 1
        if [ -n "$SERVER_PORT" ]; then
            RUN_COUNT=`netstat -an | grep $SERVER_PORT | wc -l`
        else
            RUN_COUNT=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
        fi
        if [ $RUN_COUNT -gt 0 ]; then
            break
        fi
        COUNT=$[COUNT+1]
        if [ $COUNT -gt 20 ]; then
            echo "ERROR"
            if [ -n $STDOUT_FILE ]; then
                cat $STDOUT_FILE
            fi
            exit 1
        fi
    done

    echo "OK!"
    PID=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
    echo $PID > $LOGS_DIR/$SERVER_NAME.pid
    echo "PID: $PID"
    echo "STDOUT: $STDOUT_FILE"
}

stop(){
    PID=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
    if [ -z "$PID" ]; then
        echo "ERROR: The $SERVER_NAME does not started!"
        exit 0
    fi

    if [ "$1" == "skip" ]; then
        dump
    fi

    echo -e "Stopping the $SERVER_NAME ...\c"
    kill $PID > /dev/null 2>&1

    COUNT=0
    while [ $COUNT -lt 100 ]; do
        echo -e ".\c"
        sleep 1

        if [ -n "`ps -f -p $PID | grep java`" ]; then
            COUNT=$[COUNT+1]
        else
            break
        fi

        if [ $COUNT -gt 20 ]; then
            echo "STOP ERROR: `ps -f -p $PID | grep java`"
            exit 0
        fi
    done
    echo "OK! PID: $PID"
}

dump(){
    PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
    if [ -z "$PIDS" ]; then
        echo "ERROR: The $SERVER_NAME does not started!"
        exit 1
    fi

    DUMP_DIR=$LOGS_DIR/dump
    if [ ! -d $DUMP_DIR ]; then
        mkdir $DUMP_DIR
    fi
    DUMP_DATE=`date +%Y%m%d%H%M%S`
    DATE_DIR=$DUMP_DIR/$DUMP_DATE
    if [ ! -d $DATE_DIR ]; then
        mkdir $DATE_DIR
    fi

    echo -e "Dumping the $SERVER_NAME ...\c"
    for PID in $PIDS ; do
        jstack $PID > $DATE_DIR/jstack-$PID.dump 2>&1
        echo -e ".\c"
        jinfo $PID > $DATE_DIR/jinfo-$PID.dump 2>&1
        echo -e ".\c"
        jstat -gcutil $PID > $DATE_DIR/jstat-gcutil-$PID.dump 2>&1
        echo -e ".\c"
        jstat -gccapacity $PID > $DATE_DIR/jstat-gccapacity-$PID.dump 2>&1
        echo -e ".\c"
        jmap $PID > $DATE_DIR/jmap-$PID.dump 2>&1
        echo -e ".\c"
        jmap -heap $PID > $DATE_DIR/jmap-heap-$PID.dump 2>&1
        echo -e ".\c"
        jmap -histo $PID > $DATE_DIR/jmap-histo-$PID.dump 2>&1
        echo -e ".\c"
        if [ -r /usr/sbin/lsof ]; then
            /usr/sbin/lsof -p $PID > $DATE_DIR/lsof-$PID.dump
            echo -e ".\c"
        fi
    done

    if [ -r /bin/netstat ]; then
        /bin/netstat -an > $DATE_DIR/netstat.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/iostat ]; then
        /usr/bin/iostat > $DATE_DIR/iostat.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/mpstat ]; then
        /usr/bin/mpstat > $DATE_DIR/mpstat.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/vmstat ]; then
        /usr/bin/vmstat > $DATE_DIR/vmstat.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/free ]; then
        /usr/bin/free -t > $DATE_DIR/free.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/sar ]; then
        /usr/bin/sar > $DATE_DIR/sar.dump 2>&1
        echo -e ".\c"
    fi
    if [ -r /usr/bin/uptime ]; then
        /usr/bin/uptime > $DATE_DIR/uptime.dump 2>&1
        echo -e ".\c"
    fi

    echo "OK!"
    echo "DUMP: $DATE_DIR"
}

status(){
    PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
    if [ -z "$PIDS" ]; then
        echo "Note: The $SERVER_NAME does not started!"
    else
        echo "Note: The $SERVER_NAME is running! PID $PIDS"
    fi
    exit 0
}

case "$1" in
  status)
        status
        ;;
  start)
        start
        ;;
  stop)
        stop $2
        ;;
  restart)
        stop
        start
        ;;
  dump)
        dump
        ;;
  *)
        echo $"Usage: $0 {state|start|stop|restart|dump|tail}"
esac