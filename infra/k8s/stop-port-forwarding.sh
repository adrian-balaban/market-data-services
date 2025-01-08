#!/bin/bash
set -e

ps aux | grep -i "kubectl port-forward" | grep -v grep | awk {'print $2'} | xargs kill