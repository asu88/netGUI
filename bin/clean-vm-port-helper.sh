#!/bin/bash

if [ "$1" == "" ]; then
   echo "Usage: $0 nombre_de_la_máquina"
   exit
fi

echo "Matando todos los procesos de la máquina $1 ..."

var=`ps -feaww | grep port-helper | grep "$1" | awk '{ print $2}'`
v2=`echo $var`
if [ -n "$v2" ]; then
   kill -9 $v2
fi

var=`ps -feaww | grep netkit | grep "$1" | awk '{ print $2}'`
v1=`echo $var`

if [ -n "$v1" ]; then
   kill -9 $v1
fi

if [ -z "$v1" ]; then
    if [ -z "$v2" ]; then
        echo "Nothing to kill"
    fi
fi

