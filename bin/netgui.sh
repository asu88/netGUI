#!/bin/sh

#####################################################
# Modify these variables based on your installation
# Notice that if your environment already sets these
# variables they are preserved for NetGUI too
#

# Location for Netkit (root dir):
if [ -z "$NETKIT_HOME" ]; then
    NETKIT_HOME=/usr/local/netkit
fi

# Location for the NetGUI application (root dir):
if [ -z "$NETLAB_HOME" ]; then
    NETLAB_HOME=$NETKIT_HOME/netgui/
fi

# Define a location for our JAVA installation (unless the
# user has defined one already)
if [ -z "$JAVA_HOME" ] ; then
    if [ -d /usr/lib/jvm/java-6-openjdk-amd64 ] ; then
	# Ubuntu 12.04 64bits with openjdk-6-{jdk,jre}
	JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
    elif [ -d /usr/lib/jvm/java-6-openjdk-i386 ] ; then
	# Ubuntu 12.04 32bits with openjdk-6-{jdk,jre}
	JAVA_HOME=/usr/lib/jvm/java-6-openjdk-i386
    elif [ -d /usr/lib/jvm/java-6-openjdk ] ;  then
	# Ubuntu 10.04 with openjdk-6-{jdk,jre}
	JAVA_HOME=/usr/lib/jvm/java-6-openjdk
    elif [ -d /usr/lib/jvm/java-6-sun ] ;  then
	# Ubuntu 8.04 with sun-java6-{bin,jre}
	JAVA_HOME=/usr/lib/jvm/java-6-sun
    elif [ -d /usr/lib/j2se/1.4 ] ;  then
	# Debian/Ubuntu old with j2re1.4
	JAVA_HOME=/usr/lib/j2se/1.4/    
    fi
fi
CLASSPATH=${JAVA_HOME}/lib:${JAVA_HOME}/jre/lib/
JAVA=${JAVA_HOME}/bin/java

#
######################################################

export NETKIT_HOME NETLAB_HOME
export JAVA_HOME CLASSPATH

######################################################
# Do not modify any lines from here on
######################################################

# Add NETKIT_HOME to the PATH if not already there
if ! echo $PATH |grep -q $NETKIT_HOME; then
     export PATH=$NETKIT_HOME/bin:$PATH
fi

# Sanity checks
if [ ! -e "$JAVA" ] ; then
    echo "ERROR: Cannot find java interpreter at $JAVA, aborting"
    exit 1
fi
if [ ! -d "$NETKIT_HOME" ] ; then
    echo "ERROR: Cannot find Netkit HOME dir: $NETKIT_HOME"
    echo "Please define a proper value by modifying $0"
    exit 1
fi
if [ ! -d "$NETLAB_HOME" ] ; then
    echo "ERROR: Cannot find Netlab HOME dir: $NETLAB_HOME"
    echo "Please define a proper value by modifying $0"
    exit 1
fi
if [ ! -e "$NETLAB_HOME/classes/NetKitViewer.class" ] ; then
    echo "ERROR: Cannot find compiled classes in Netlab's classes dir"
    echo "Have you compiled the application?"
    exit 1
fi
if [ -z "$DISPLAY" ] ; then
    echo "ERROR: Undefined DISPLAY, are you running a graphical environment?"
    echo "(trying to continue anyway...)"
fi

CURPATH=`pwd`
EXITV=0
cd $NETKIT_HOME
echo "Starting NetkitViewer..."
$JAVA -DNETKIT_HOME=$NETKIT_HOME -DNETLAB_HOME=$NETLAB_HOME -cp $NETLAB_HOME/classes/:$NETLAB_HOME/libs/piccolo2d-core-1.3.1.jar:$NETLAB_HOME/libs/piccolo2d-extras-1.3.1.jar:$NETLAB_HOME/libs/commons-net-3.3.jar:$NETLAB_HOME/libs/jakarta-oro-2.0.8.jar:. NetKitViewer 
if [ $? -ne 0 ]; then
    EXITVAL=$?
    echo "... there was an error executing NetKitViewer"
else
    echo "... NetkitViewer finished"
fi
cd $CURPATH

exit $EXITVAL