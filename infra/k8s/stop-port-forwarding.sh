#!/bin/bash
set -e # exit immediately if any command within the script returns a non-zero exit status
set -o xtrace

ps aux | grep -i "kubectl port-forward" | grep -v grep | awk {'print $2'} | xargs kill