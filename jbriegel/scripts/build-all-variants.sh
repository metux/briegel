
if [ ! "$BRIEGEL_CMDLIB" ]; then
    echo "Missing BRIEGEL_CMDLIB"
    exit 23
fi

JBRIEGEL_CMD_CLASS=metux.briegel.cmd.build_all_variants

. $BRIEGEL_CMDLIB/system-cmd-common.sh
