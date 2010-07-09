#!/bin/bash

case $BRIEGEL_COMMAND in
    "install-port")
	JCMD=install_port
    ;;
    *)
	echo "unsupported command \"$BRIEGEL_COMMAND\""
	exit 1;
    ;;
esac

if [ ! "$BRIEGEL_SYSTEM" ]; then 
    echo "$0: missing BRIEGEL_SYSTEM"; 
    exit 1;
fi

export BRIEGEL_CONF=/install/config/systems/$BRIEGEL_SYSTEM/etc/briegel/briegel.conf
export CLASSPATH=$CLASSPATH:../.build/:../.metux-lib/.build/
export JBRIEGEL_CMD_DIR=/home/devel/projects/jbriegel/cmd/

cd $JBRIEGEL_CMD_DIR && java metux.briegel.cmd.$JCMD "$@"
