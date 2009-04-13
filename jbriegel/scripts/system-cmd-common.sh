
if [ ! "$BRIEGEL_CONFDIR" ]; then
    echo "Missing BRIEGEL_CONFDIR"
    exit 23
fi
if [ ! "$BRIEGEL_CMDLIB" ]; then
    echo "Missing BRIEGEL_CMDLIB"
    exit 23
fi
if [ ! "$BRIEGEL_SYSTEM" ]; then 
    echo "$0: missing BRIEGEL_SYSTEM"; 
    exit 1;
fi
if [ ! "$BRIEGEL_ROOT" ]; then
    echo "$0: missing BRIEGEL_ROOT";
    exit 2;
fi
if [ ! "$JBRIEGEL_CMD_CLASS" ]; then
    if [ ! "$JBRIEGEL_COMMAND" ]; then
	echo "$0 missing JBRIEGEL_CMD_CLASS / JBRIEGEL_COMMAND"
	exit 3
    else
	JBRIEGEL_CMD_CLASS=metux.briegel.cmd.$JBRIEGEL_COMMAND
    fi
fi
if [ ! "$JLIB_METUX" ]; then
    echo "$0: missing JLIB_METUX"
    exit 4
fi
if [ ! "$JLIB_UNITOOL" ]; then
    echo "$0: missing JLIB_UNITOOL"
    exit 5
fi
if [ ! "$JLIB_PI" ]; then
    echo "$0: missing JLIB_PI"
    exit 6
fi


export JLIB_PI_CLASSPATH=$JLIB_PI/.build
export JLIB_UNITOOL_CLASSPATH=$JLIB_UNITOOL/.build
export JLIB_METUX_CLASSPATH=$JLIB_METUX/.build
export JLIB_BRIEGEL_CLASSPATH=$BRIEGEL_ROOT/.build

export BRIEGEL_CONF=$BRIEGEL_CONFDIR/systems/$BRIEGEL_SYSTEM/etc/briegel/briegel.conf
export CLASSPATH=$CLASSPATH:$JLIB_BRIEGEL_CLASSPATH:$JLIB_METUX_CLASSPATH:$JLIB_UNITOOL_CLASSPATH:$JLIB_PI_CLASSPATH:
export JBRIEGEL_CMD_DIR=$BRIEGEL_ROOT/cmd/
export JVM=java

result=failed
cd $JBRIEGEL_CMD_DIR && $JVM $JBRIEGEL_CMD_CLASS $* && result=ok

