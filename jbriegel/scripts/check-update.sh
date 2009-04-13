
if [ ! "$BRIEGEL_CMDLIB" ]; then
    echo "Missing BRIEGEL_CMDLIB"
    exit 23
fi

JBRIEGEL_CMD_CLASS=metux.briegel.cmd.check_update

. $BRIEGEL_CMDLIB/system-cmd-common.sh
