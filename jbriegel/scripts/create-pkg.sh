
if [ ! "$BRIEGEL_CMDLIB" ]; then
    echo "Missing BRIEGEL_CMDLIB"
    exit 23
fi

MSGPIPE=/var/run/message.pipe

JBRIEGEL_CMD_CLASS=metux.briegel.cmd.build

. $BRIEGEL_CMDLIB/system-cmd-common.sh

if [ "$result" == "ok" ] ; then
    echo "BRIEGEL-CREATE-PKG ($BRIEGEL_SYSTEM) -> DONE" >> $MSGPIPE
else
    echo "BRIEGEL-CREATE-PKG ($BRIEGEL_SYSTEM) -> FAILED" >> $MSGPIPE
fi

exit ;

/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
/usr/share/shortcuts/echolot ; 
sleep 1 ; 
