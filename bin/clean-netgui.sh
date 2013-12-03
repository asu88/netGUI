#!/bin/bash
# Kill NetGUI and Netkit related processes

# Location for Netkit (root dir):
if [ -z "$NETKIT_HOME" ]; then
    export NETKIT_HOME=/usr/local/netkit/
fi

# Add NETKIT_HOME to the PATH if not already there
if ! echo $PATH |grep -q $NETKIT_HOME; then
     export PATH=$NETKIT_HOME/bin:$PATH
fi

[ -z "$USER" ] && {
	echo "USER undefined, error in the environment" >&2
	exit 1
}

echo "Starting cleanup of Netkit / NetGUI"

if [ "$1" == "-s" ]; then

    systems=`vlist | grep ^$USER | awk '{ print $2}'`
    for i in $systems; do
	vhalt $i
    done
    exit 0
fi
	
# Now kill processes if they exist
PROCLIST="awk uml_switch java netkit-kernel xterm vstart port-helper"

echo -e "\tKilling netkit related processes"
killall $PROCLIST 2>/dev/null || true
sleep 2
echo -e "\tForce-Killing netkit related processes"
killall -9 $PROCLIST 2>/dev/null || true

# Cleaning netkit directories

# First, copy the netkit.conf file if it exists
CONF=~/.netkit/netkit.conf 
TMPFILE=""
if [ -e $CONF ] ; then
	TMPFILE=`tempfile` || { echo "Cannot create tempfile!" >&2; exit 1; }
	echo -e "\tCopying configuration file $CONF to $TMPFILE"
	cp $CONF $TMPFILE
fi
# Now clean the directories, that way the consoles and hubs are clean
rm -rf $HOME/.netkit/ 2> /dev/null
rm -rf $HOME/.netkitgui/ 2> /dev/null
# Now restore the netkit.conf file
if [ -s "$TMPFILE" ] ;then
	echo -e "\tRestoring configuration file $CONF from $TMPFILE"
	mkdir -p `dirname $CONF`
	cp $TMPFILE $CONF
fi

echo "Cleaning finished."

exit 0
