#!/bin/bash

if [ "$1" == "" ]; then
   echo "Usage: $0 machine_name"
   exit
fi


echo "Killing all $1 processes..."

# killing machine vstart process
v1=`ps -feaww | grep "vstart" | grep " $1 " | awk '{ print $2}'`

if [ -n "$v1" ]; then
    echo "   ... killing $1 vstart process"
    kill -9 $v1
fi

# killing machine xterm process

v2=`ps -feaww | grep "xterm" | grep "name=$1" | awk '{ print $2}'`

if [ -n "$v2" ]; then
    echo "   ... killing $1 xterm process"
    kill -9 $v2
fi


# killing machine netkit-kernel processes
v3=`ps -feaww | grep netkit-kernel | grep "name=$1" | awk '{ print $2}'`

if [ -n "$v3" ]; then
    echo "   ... killing $1 netkit-kernel processes"
    kill -9 $v3
fi

if [ -z "$v1" ] && [ -z "$v2" ] && [ -z "$v3" ]; then
            echo "   ... nothing to kill"
fi

